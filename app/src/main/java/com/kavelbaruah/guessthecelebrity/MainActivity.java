package com.kavelbaruah.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {

    ImageView question;
    Button option_1;
    Button option_2;
    Button option_3;
    Button option_4;
    String html;
    int correctIndex;
    String[] questionURLs = new String[100];
    String[] Options = new String[100];
    Integer[] correctAnswer = {0,0,0,0};
    Button[] buttons = new Button[4];

    public class getHTML extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                int data = reader.read();
                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();


                }

                return result;
            }

            catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("Info", "Error3 tapped");
            }

            catch (IOException e) {
                e.printStackTrace();
                Log.i("Info", "Error4 tapped");
            }


            return null;
        }

    }
    public class QuestionGetter extends AsyncTask<String, Object, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap downloadedImage = BitmapFactory.decodeStream(in);

                return downloadedImage;
            }

            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public int randomGenerator(int range){

        Random random = new Random();
        int x = random.nextInt(range);
        return x;
    }
    public void questionSetter(){

        correctIndex = randomGenerator(100);
        Log.i("Current index: ", String.valueOf(correctIndex));
        QuestionGetter task = new QuestionGetter();
        Bitmap questionImage;

        try {

            questionImage = task.execute(questionURLs[correctIndex]).get();
            question.setImageBitmap(questionImage);
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void optionSetter() {

        correctAnswer[0] = 0;
        correctAnswer[1] = 0;
        correctAnswer[2] = 0;
        correctAnswer[3] = 0;

        int x = randomGenerator(4) + 1;

        Log.i("Value of x: ", String.valueOf(x));
        Log.i("Current index: ", String.valueOf(correctIndex));
        Log.i("Answer: ", String.valueOf(Options[(correctIndex-4)]));

        if( x == 1){

            buttons[0].setText(Options[(correctIndex-4)]);
            correctAnswer[0] = 1;

        }

        else if( x == 2){

            buttons[1].setText(Options[(correctIndex-4)]);
            correctAnswer[1] = 1;
        }

        else if( x == 3){

            buttons[2].setText(Options[(correctIndex-4)]);
            correctAnswer[2] = 1;
        }

        else if( x == 4){

            buttons[3].setText(Options[(correctIndex-4)]);
            correctAnswer[3] = 1;
        }

        for (int i = 0; i < 4; i++){

            if(correctAnswer[i] != 1){

                int randomNumber = randomGenerator(96);
                buttons[i].setText(Options[randomNumber]);
            }
        }
    }
    public void onClick(View view){

        String checkAnswer = String.valueOf(view.getTag());
        int i = Integer.parseInt(checkAnswer);
        Log.i("CheckAnswer = ", checkAnswer);
        if(correctAnswer[i] == 1){

            Toast.makeText(this,"Correct answer!", Toast.LENGTH_SHORT).show();
            questionSetter();
            optionSetter();
        }

        else{

            Toast.makeText(this, "Wrong Answer! ", Toast.LENGTH_SHORT).show();
            questionSetter();
            optionSetter();
        }



    }
    public void htmlGetter() {

        getHTML task = new getHTML();
        html = null;

        try {

            html = task.execute("http://www.posh24.se/kandisar").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("Info", "Error1 tapped");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.i("Info", "Error2 tapped");
        }

        Pattern p = Pattern.compile("src=\"(.*?)\"");
        Matcher m = p.matcher(html);

        for(int i = 0;i < 100 ;i++){

            m.find();
            questionURLs[i] = m.group(1);
        }

        Pattern k = Pattern.compile("alt=\"(.*?)\"");
        Matcher l = k.matcher(html);

        for(int i = 0; i < 96; i++){

            l.find();
            Options[i] = l.group(1);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = (ImageView) findViewById(R.id.questionView);
        buttons[0] = (Button) findViewById(R.id.option_1);
        buttons[1] = (Button) findViewById(R.id.option_2);
        buttons[2] = (Button) findViewById(R.id.option_3);
        buttons[3] = (Button) findViewById(R.id.option_4);
        htmlGetter();
        questionSetter();
        optionSetter();





    }

}