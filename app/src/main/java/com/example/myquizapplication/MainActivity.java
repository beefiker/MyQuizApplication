package com.example.myquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.res.AssetManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    String QuizAnswer = "";
    int quizCount, correctCnt, incorrectCnt, quizSize;
    StringBuffer incorrect_arr = new StringBuffer();
    TextView theQuestion, progText, incorrectTv, questionNo, qType;
    EditText editWord, editSub;
    Button btnAnswer1, btnAnswer2, btnAnswer3,checkSub;
    ImageButton btnGo, btnBack;
    ProgressBar progBar;
    ViewFlipper vFlipAnswers, vFlipWebView;
    WebView web;

    void initialize(){
        qType.setVisibility(View.VISIBLE);
        questionNo.setVisibility(View.VISIBLE);
        incorrect_arr.setLength(0);
        quizCount = 0;
        correctCnt = 0;
        incorrectCnt = 0;
        setQuiz(quizCount);
        btnAnswer1.setClickable(true);
        btnAnswer2.setClickable(true);
        btnAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAnswer = btnAnswer3.getText().toString();
                check(userAnswer);
            }
        });
        btnAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAnswer = btnAnswer3.getText().toString();
                check(userAnswer);
            }
        });
    }

    void setQuiz(int count){
        try{
            AssetManager asm = getAssets();
            InputStream inputStr = asm.open("quiz.json");
            InputStreamReader inputStrReader = new InputStreamReader(inputStr);
            BufferedReader reader = new BufferedReader(inputStrReader);

            StringBuffer wordBuffer = new StringBuffer();
            String line = reader.readLine();
            while(line!=null){
                wordBuffer.append(line+"\n");
                line=reader.readLine();
                quizSize++;
            }
            String jsonData = wordBuffer.toString();

            JSONArray jsonArr = new JSONArray(jsonData);
            quizSize = jsonArr.length();
            JSONObject jsonObj = jsonArr.getJSONObject(count);

            String type = jsonObj.getString("type");
            String word = jsonObj.getString("word");

            QuizAnswer = jsonObj.getString("answer");

            if(!type.equals("Subjective")){
                JSONObject answers = jsonObj.getJSONObject("answerlist");
                String a1 = answers.getString("a1");
                String a2 = answers.getString("a2");
                String a3 = answers.getString("a3");
                qType.setText(type);

                theQuestion.setText(word);

                questionNo.setText("- Q"+(quizCount +1)+" -");

                btnAnswer1.setText(a1);
                btnAnswer2.setText(a2);
                btnAnswer3.setText(a3);

                btnAnswer1.setVisibility(View.VISIBLE);
                btnAnswer2.setVisibility(View.VISIBLE);
                btnAnswer3.setVisibility(View.VISIBLE);
                checkSub.setVisibility(View.GONE);
                editSub.setVisibility(View.GONE);

            }else{
                qType.setText(type);

                theQuestion.setText(word);

                questionNo.setText("- Q"+(quizCount +1)+" -");

                btnAnswer1.setVisibility(View.GONE);
                btnAnswer2.setVisibility(View.GONE);
                btnAnswer3.setVisibility(View.GONE);
                checkSub.setVisibility(View.VISIBLE);
                editSub.setVisibility(View.VISIBLE);
            }

            progBar.setProgress(quizCount +1);
            progBar.setMax(quizSize);
            progText.setText((quizCount +1)+"/"+quizSize);

        }catch(IOException e){
            e.printStackTrace();
        }catch(JSONException e){
            e.printStackTrace();
        }

    }
    void check(String userAnswer){
        String QuizWord = theQuestion.getText().toString();
        if(quizCount < quizSize){
            if(userAnswer.equals(QuizAnswer)){
                Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_SHORT).show();
                correctCnt++;
            }
            else{
                Toast.makeText(getApplicationContext(),"Incorrect",Toast.LENGTH_SHORT).show();
                incorrect_arr.append("- Q"+(quizCount +1)+" -\n"+QuizWord + " : " +QuizAnswer+"\n\n");
                incorrectCnt++;
            }
        }

        if(quizCount +1 == quizSize){

            theQuestion.setText("Completed");
            qType.setVisibility(View.INVISIBLE);
            questionNo.setVisibility(View.INVISIBLE);
            progText.setText("done");
            btnAnswer1.setText("맞힌 개수 : "+ correctCnt);
            btnAnswer1.setClickable(false);
            btnAnswer2.setText("틀린 개수 : "+ incorrectCnt);
            btnAnswer2.setClickable(false);
            btnAnswer3.setText("틀린 문제 확인");

            btnAnswer3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    vFlipAnswers.showNext();
                    incorrectTv.setText(incorrect_arr.toString());
                    btnAnswer3.setText("재도전");
                    btnAnswer2.setText("-");
                    btnAnswer1.setText("단어 검색");

                    btnAnswer3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            vFlipAnswers.showPrevious();
                            initialize();
                        }
                    });
                    btnAnswer1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            vFlipWebView.showNext();
                        }
                    });

                }
            });
        }
        quizCount++;
        setQuiz(quizCount);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(" Perfect Score Project");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.score);
        qType = (TextView) findViewById(R.id.Qtype);
        theQuestion = (TextView) findViewById(R.id.question);
        questionNo = (TextView) findViewById(R.id.questionNo);
        progText = (TextView) findViewById(R.id.progressText);
        incorrectTv = (TextView) findViewById(R.id.incorrectTv);
        btnAnswer1 = (Button) findViewById(R.id.answer1);
        btnAnswer2 = (Button) findViewById(R.id.answer2);
        btnAnswer3 = (Button) findViewById(R.id.answer3);
        progBar = (ProgressBar) findViewById(R.id.progress);
        vFlipAnswers = (ViewFlipper) findViewById(R.id.viewFlip1);
        vFlipWebView = (ViewFlipper) findViewById(R.id.viewFlip2);
        btnGo = (ImageButton) findViewById(R.id.btnGo);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        web = (WebView) findViewById(R.id.webView);
        editWord = (EditText) findViewById(R.id.editWord);
        checkSub = (Button) findViewById(R.id.checkSub);
        editSub = (EditText) findViewById(R.id.editSub);

        setQuiz(quizCount);

        btnAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAnswer = btnAnswer1.getText().toString();
                check(userAnswer);
            }
        });

        btnAnswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAnswer = btnAnswer2.getText().toString();
                check(userAnswer);
            }
        });

        btnAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAnswer = btnAnswer3.getText().toString();
                check(userAnswer);
            }
        });
        checkSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAnswer = editSub.getText().toString();
                check(userAnswer);
                editSub.setText(null);
            }
        });

        web.setWebViewClient(new WordSearchWeb());
        WebSettings webset = web.getSettings();
        webset.setBuiltInZoomControls(true);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchURL = "https://m.dic.daum.net/search.do?q=";
                String query = editWord.getText().toString();
                web.loadUrl(searchURL + query);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vFlipWebView.showPrevious();
            }
        });

    }

    class WordSearchWeb extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
