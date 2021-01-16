package com.example.grocerylist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LearnActivity extends AppCompatActivity {

    private ArrayList<String> questions, answers, questionsTemp, answersTemp, scoreList;
    private TextView q1View, q2View, q3View, q4View, q5View, scoreView, resultsView, avgView;
    private EditText a1Text, a2Text, a3Text, a4Text, a5Text;
    private double correctCount, score, setCounter;
    private Button subButton;
    private int newSet = 1;
    private String filename = "dataFile", cfilename = "setCounterFile";
    private FileOutputStream outputStream;//creates a 'stream' to save data to file;
    private FileInputStream inputStream;
    File file, cfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        subButton = findViewById(R.id.subButton);

        //initalizes all the display elements (textViews and editTexts)
        q1View = findViewById(R.id.q1View);
        q2View = findViewById(R.id.q2View);
        q3View = findViewById(R.id.q3View);
        q4View = findViewById(R.id.q4View);
        q5View = findViewById(R.id.q5View);
        scoreView = findViewById(R.id.scoreView);
        resultsView = findViewById(R.id.resultsView);
        avgView = findViewById(R.id.avgView);

        a1Text = findViewById(R.id.a1Text);
        a2Text = findViewById(R.id.a2Text);
        a3Text = findViewById(R.id.a3Text);
        a4Text = findViewById(R.id.a4Text);
        a5Text = findViewById(R.id.a5Text);

        questions = getIntent().getStringArrayListExtra("questions"); //gets questionList from other activity
        answers = getIntent().getStringArrayListExtra("answers"); //gets answerList from other activity
        newSet = getIntent().getIntExtra("new", 0); //used to know whether or not the user has started a new set or is still using the former set (the old questions)

        questionsTemp = new ArrayList<>(questions); //copies elements of questions into questionTemp (is not the same object reference as questions, is independent of that arrayList)
        answersTemp = new ArrayList<>(answers);
        scoreList = new ArrayList<>(); //list of all the scores thus far

        //randomly shuffles the questionsTemp and answersTemp array elements (but each corresponding question and answer still have the same index in their respective arrays) so that the questions are displayed on screen in a different order.
        int randNum;
        for (int i = 0; i < questions.size()-1; i++){
            randNum = (int)(Math.random()*(questionsTemp.size()-i));
            questionsTemp.add(questionsTemp.remove(randNum));
            answersTemp.add(answersTemp.remove(randNum));
        }
        //sets the question textViews up with the rearranged questions from the arrayList.
        q1View.setText(questionsTemp.get(0));
        q2View.setText(questionsTemp.get(1));
        q3View.setText(questionsTemp.get(2));
        q4View.setText(questionsTemp.get(3));
        q5View.setText(questionsTemp.get(4));

        file = new File(getApplicationContext().getFilesDir(), filename);//creates a new file in that directory under filename
        cfile = new File(getApplicationContext().getFilesDir(), cfilename);
        //save();
        load();
        //saveCounterTracker();
        loadCounterTracker();
        resultsView.setText(getDisplay()); //adds in previous scores into the textView for display

        subButton.setOnClickListener(new View.OnClickListener() { //if user clicks the submit button, checks if the answers inputted match the answers the user provided before starting the practice round
            @Override
            public void onClick(View v) {
                if (a1Text.getText().toString().equals(answersTemp.get(0))){
                    correctCount++;
                }
                if (a2Text.getText().toString().equals(answersTemp.get(1))){
                    correctCount++;
                }
                if (a3Text.getText().toString().equals(answersTemp.get(2))){
                    correctCount++;
                }
                if (a4Text.getText().toString().equals(answersTemp.get(3))){
                    correctCount++;
                }
                if (a5Text.getText().toString().equals(answersTemp.get(4))){
                    correctCount++;
                }

                if (newSet != setCounter){ //if the set number has been increased from the previous (newSet is one number larger than setCounter), a new set has been created.
                    scoreList.add("New Set " + newSet);
                }

                //calculates score percentage and displays it
                score = (correctCount/5)*100;
                scoreList.add(score + "");
                scoreView.setText("Your Score: " + score + " %");
                save(); //puts in recent score into the file

                resultsView.setText(getDisplay()); //displays previous scores in textView
                calcStats(); //calculates average score for that particular set and compares the user's recent score to their average for the set, so they know where they stand (improvement or not).
            }
        });
    }

    //adds "\n" between the items of scoreList in order to organize the list when displayed
    public String getDisplay(){
        String list = "";
        for(int i = 0; i < scoreList.size(); i++){
            list += scoreList.get(i) + "\n";
        }
        return list;
    }

    //saves the previous scores to the file
    public void save(){
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);//creates a 'stream' to save data to file
            //can comment out for loop below and run project to clear out the file
            for(int i = 0; i < scoreList.size(); i++){
                outputStream.write(scoreList.get(i).getBytes());//writes data to the file
                if(i < scoreList.size() - 1){
                    outputStream.write("\n".getBytes());//indents the writing to make new line
                }
            }

            outputStream.close();//closes the 'stream' to stop saving
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //loads the previous scores from the file
    public void load(){
        try{
            inputStream = openFileInput(filename);//creates a 'stream' of data in bytes
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStreamReader inputReader = new InputStreamReader(inputStream);//converts the byte stream to characters
        BufferedReader bufferedReader = new BufferedReader(inputReader);//acts as a buffer for the info; processes it?

        try {
            String line = bufferedReader.readLine();//saves the line read to a temp variable to avoid reading next line during check
            while (line != null){
                scoreList.add(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCounterTracker(){ //saves the set tracker number, or setCounter, to the file
        try {
            outputStream = openFileOutput(cfilename, Context.MODE_PRIVATE);//creates a 'stream' to save data to file
            outputStream.write(Double.toString(setCounter).getBytes());//writes data to the file
            outputStream.close();//closes the 'stream' to stop saving
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //loads the set tracker number, or setCounter, from the file
    public void loadCounterTracker(){
        try{
            inputStream = openFileInput(cfilename);//creates a 'stream' of data in bytes
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStreamReader inputReader = new InputStreamReader(inputStream);//converts the byte stream to characters
        BufferedReader bufferedReader = new BufferedReader(inputReader);//acts as a buffer for the info; processes it?

        try {
            String line = bufferedReader.readLine();//saves the line read to a temp variable to avoid reading next line during check
            while (line != null){
                setCounter = Double.parseDouble(line);
                line = bufferedReader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double sum = 0;
    private double avg = 0;

    public void calcStats(){
        if (newSet != setCounter){ //if it's a new set, then the average score of the set is the current score
            setCounter = newSet;
            saveCounterTracker();
            avg = score;
        }
        else{ //if not a new set, then average is calculated for all the scores in that particular set (all the numbers in the list after the words "New Set")
            int counter = 0;
            for (int i = scoreList.size() - 2; i > scoreList.lastIndexOf("New Set " + newSet); i--){
                sum += Double.parseDouble(scoreList.get(i));
                counter++;
            }
            avg = sum/counter;
            //round avg to the hundredths
            avg = Math.floor(avg * 100);
            avg /= 100;
        }

        //display's based on how the user's recent score compares to their average score.
        if (score > avg){
            avgView.setText("Good job! You scored higher than your average of " + avg + " %");
        }
        else if (score == avg){
            avgView.setText("Nice! You're right where you should be, with an average of " + avg + " %");
        }
        else{
            avgView.setText("Come on, you slacker! You need to beat your average of " + avg + " %");
        }

    }

    public void backToMain(View view){ //exits out of this activity back into the parent activity
        finish();
    }
}
