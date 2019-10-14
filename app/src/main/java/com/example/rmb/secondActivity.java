package com.example.rmb;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class secondActivity extends AppCompatActivity {
    private final String TAG = "second";

    TextView score;
    TextView score2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_second );
        Log.i( TAG,"onCreate:" );

        score = (TextView)findViewById( R.id.score );
        score2 = (TextView)findViewById( R.id.score2 );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState( outState );
        String scorea = ((TextView)findViewById( R.id.score )).getText().toString();
        String scoreb = ((TextView)findViewById( R.id.score2 )).getText().toString();

        Log.i( TAG,"onSaveInstanceState:" );
        outState.putString( "teama_score",scorea );
        outState.putString( "teamb_score",scoreb );
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState( savedInstanceState );
        String scorea = savedInstanceState.getString( "teama_score" );
        String scoreb = savedInstanceState.getString( "teamb_score" );

        Log.i( TAG,"onRestoreInstanceState:" );
        ((TextView)findViewById( R.id.score )).setText( scorea );
        ((TextView)findViewById( R.id.score2 )).setText( scoreb );
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i( TAG,"onStart:" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i( TAG,"onResume:" );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i( TAG,"onRestart:" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i( TAG,"onPause:" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i( TAG,"onStop:" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i( TAG,"onDestroy:" );
    }

    public void btnAdd3(View btn) {
        if(btn.getId()==R.id.btn_3){
            //showScore(3);
            show(3);
        }else {
            //showScore2(3);
            showb(3);
        }
    }
    public void btnAdd2(View btn) {
        if(btn.getId()==R.id.btn_2){
            show(2);
        }else {
            showb(2);
        }
    }
    public void btnAdd1(View btn) {
        if(btn.getId()==R.id.btn_1){
            show(1);
        }else {
            showb(1);
        }
    }
    public void btnReset(View v) {
        TextView out = (TextView)findViewById( R.id.score );
        score.setText( "0" );
        ((TextView)findViewById( R.id.score2 )).setText( 0 );
        //score2.setText( "0" );
    }
    /*private void showScore(int inc){
        Log.i("show","inc=" + inc);
        String oldScore = (String)score.getText();
        int newScore = Integer.parseInt( oldScore ) + inc;
        score.setText( "" + newScore );
    }

    private void showScore2(int inc){
        Log.i("show","inc=" + inc);
        String oldScore = (String)score2.getText();
        int newScore = Integer.parseInt( oldScore ) + inc;
        score2.setText( "" + newScore );
    }*/
    private void show(int i){
        TextView out = (TextView) findViewById( R.id.score );
        String oldScore = (String)out.getText();
        String newScore = String.valueOf( Integer.parseInt( oldScore ) + i );
        out.setText( newScore );
    }

    private void showb(int i){
        TextView out = (TextView) findViewById( R.id.score2 );
        String oldScore = (String)out.getText();
        String newScore = String.valueOf( Integer.parseInt( oldScore ) + i );
        out.setText( newScore );
    }
}
