package com.example.grocerylist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String qfilename = "questionFile", afilename = "answerFile", sfilename = "setTrackerFile";
    int newSet = 0;
    FileOutputStream outputStream;//creates a 'stream' to save data to file;
    FileInputStream inputStream;
    TextView questionView;
    File qfile, afile, sfile;
    EditText txtQuestion, txtAnswer;
    ArrayList<String> questionList, answerList;
    Button practiceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializes the question TextView where the questions will be displayed as inputted by the user (simply to make it easier for the user to see which questions they've inputted already)
        questionView = findViewById(R.id.questionView);

        txtQuestion = findViewById(R.id.txtQuestion); //EditView for question input
        txtAnswer = findViewById(R.id.txtAnswer); //EditView for answer input
        practiceBtn = findViewById(R.id.practiceBtn);

        //array of strings that saves the items in the list
        questionList = new ArrayList<>();
        answerList = new ArrayList<>();

        qfile = new File(getApplicationContext().getFilesDir(), qfilename);//creates a new file in that directory under filename
        afile = new File(getApplicationContext().getFilesDir(), afilename);
        sfile = new File(getApplicationContext().getFilesDir(), sfilename);

        //saveQuestions();//have to save data at the very beginning in order to have actual data to load later
        loadQuestions();//call loadData at the start to immediately show the previous data
        //saveAnswers();
        loadAnswers();
        //saveSetTracker();
        loadSetTracker();

        questionView.setText(getDisplay());
    }

    //handles when the enter btn is clicked
    public void handleEnter(View view){
        //adds in the inputted question and answer into the question and answer arrayLists, respectively.
        questionList.add(txtQuestion.getText().toString());
        answerList.add(txtAnswer.getText().toString());

        questionView.setText(getDisplay()); //puts in the items of questionList into questionView.

        txtQuestion.setText("");
        txtAnswer.setText("");

        saveQuestions();
        saveAnswers();

        if (questionList.size() == 5 && answerList.size() == 5){ //once 5 questions and answers have been inputted, the user is able to practice!
            practiceBtn.setEnabled(true);
        }
    }

    public void newSet(View view){ //If New Set Button is clicked, the set number (newSet) is incremented by 1 and saved in a file. All prior set info and displays are cleared.
        practiceBtn.setEnabled(false);

        newSet++;
        saveSetTracker();
        questionList.clear();
        answerList.clear();
        questionView.setText("");
    }

    //adds "\n" between the items of questionList in order to organize the list with one question per row when displayed in questionView (only for display purposes)
    public String getDisplay(){
        String list = "";
        for(int i = 0; i < questionList.size(); i++){
            list += questionList.get(i) + "\n";
        }
        return list;
    }

    //saves the questions to the file
    public void saveQuestions(){
        try {
            outputStream = openFileOutput(qfilename, Context.MODE_PRIVATE);//creates a 'stream' to save data to file
            for(int i = 0; i < questionList.size(); i++){
                outputStream.write(questionList.get(i).getBytes());//writes data to the file
                if(i < questionList.size() - 1){
                    outputStream.write("\n".getBytes());//indents the writing to make new line
                }
            }

            outputStream.close();//closes the 'stream' to stop saving
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //loads the questions from the file
    public void loadQuestions(){
        try{
            inputStream = openFileInput(qfilename);//creates a 'stream' of data in bytes
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStreamReader inputReader = new InputStreamReader(inputStream);//converts the byte stream to characters
        BufferedReader bufferedReader = new BufferedReader(inputReader);//acts as a buffer for the info; processes it?

        try {
            String line = bufferedReader.readLine();//saves the line read to a temp variable to avoid reading next line during check
            while (line != null){
                questionList.add(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //saves the answers to the file
    public void saveAnswers(){
        try {
            outputStream = openFileOutput(afilename, Context.MODE_PRIVATE);//creates a 'stream' to save data to file
            for(int i = 0; i < answerList.size(); i++){
                outputStream.write(answerList.get(i).getBytes());//writes data to the file
                if(i < answerList.size() - 1){
                    outputStream.write("\n".getBytes());//indents the writing to make new line
                }
            }

            outputStream.close();//closes the 'stream' to stop saving
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //loads the answers from the file
    public void loadAnswers(){
        try{
            inputStream = openFileInput(afilename);//creates a 'stream' of data in bytes
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStreamReader inputReader = new InputStreamReader(inputStream);//converts the byte stream to characters
        BufferedReader bufferedReader = new BufferedReader(inputReader);//acts as a buffer for the info; processes it?

        try {
            String line = bufferedReader.readLine();//saves the line read to a temp variable to avoid reading next line during check
            while (line != null){
                answerList.add(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSetTracker(){ //saves the set tracker number, or newSet, to the file
        try {
            outputStream = openFileOutput(sfilename, Context.MODE_PRIVATE);//creates a 'stream' to save data to file
            outputStream.write(Integer.toString(newSet).getBytes());//writes data to the file
            outputStream.close();//closes the 'stream' to stop saving
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //loads the set tracker number, or newSet, from the file
    public void loadSetTracker(){
        try{
            inputStream = openFileInput(sfilename);//creates a 'stream' of data in bytes
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStreamReader inputReader = new InputStreamReader(inputStream);//converts the byte stream to characters
        BufferedReader bufferedReader = new BufferedReader(inputReader);//acts as a buffer for the info; processes it?

        try {
            String line = bufferedReader.readLine();//saves the line read to a temp variable to avoid reading next line during check
            while (line != null){
                newSet = Integer.parseInt(line);
                line = bufferedReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void doPractice(View view){
        Intent intent = new Intent(MainActivity.this, LearnActivity.class);
        intent.putExtra("questions", questionList);
        intent.putExtra("answers", answerList);
        intent.putExtra("new", newSet);

        startActivity(intent);
    }
}
