package com.example.jchoi.foodlogger;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecognitionListener {

    // ----- TYPES ----- //
    // Timer task used to reproduce the timeout input error that seems not be called on android 4.1.2
    public class SilenceTimer extends TimerTask {
        @Override
        public void run() {
            Log.e(TAG, "SilenceTimer");
            onError(SpeechRecognizer.ERROR_SPEECH_TIMEOUT);
        }
    }

    private Timer speechTimeout = null;
    private SpeechRecognizer speech = null;
    String TAG = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onDestroy() {
        // prevent memory leaks when activity is destroyed
        super.onDestroy();
    }

    public void init() {
        findViewById(R.id.btn_google).setOnClickListener(this);
        findViewById(R.id.btn_facebook).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google:
                goToGoogle();
                break;
            case R.id.btn_facebook:
//                goToFacebook();

                startVoiceRecognitionCycle();
                break;
        }

    }

    public void goToGoogle() {
        Intent myIntent = new Intent(MainActivity.this, SurveyActivity.class);
        myIntent.putExtra("test1", 10);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToFacebook() {

    }

    /**
     * Fire an intent to start the voice recognition process.
     */
    public void startVoiceRecognitionCycle() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        getSpeechRevognizer().startListening(intent);
    }

    /**
     * Stop the voice recognition process and destroy the recognizer.
     */
    public void stopVoiceRecognition()
    {
        speechTimeout.cancel();
        if (speech != null) {
            speech.destroy();

            speech = null;
        }
    }

	/* RecognitionListener interface implementation */

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG,"onReadyForSpeech");
        // create and schedule the input speech timeout
        speechTimeout = new Timer();
        speechTimeout.schedule(new SilenceTimer(), 3000);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG,"onBeginningOfSpeech");
        // Cancel the timeout because voice is arriving
        speechTimeout.cancel();
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG,"onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG,"onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        String message;
        boolean restart = true;
        switch (error)
        {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                restart = false;
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                restart = false;
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Not recognised";
                break;
        }
        Log.d(TAG,"onError code:" + error + " message: " + message);

        if (restart) {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    getSpeechRevognizer().cancel();
                    startVoiceRecognitionCycle();
                }
            });
        }
    }

    // Lazy instantiation method for getting the speech recognizer
    private SpeechRecognizer getSpeechRevognizer(){
        if (speech == null) {
            speech = SpeechRecognizer.createSpeechRecognizer(this);
            speech.setRecognitionListener(this);
        }

        return speech;
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG,"onEvent");
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG,"onPartialResults");
    }

    @Override
    public void onResults(Bundle results) {
        // Restart new dictation cycle
        startVoiceRecognitionCycle();
        //
        StringBuilder scores = new StringBuilder();
        for (int i = 0; i < results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES).length; i++) {
            scores.append(results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)[i] + " ");
        }
        Log.d(TAG,"onResults: " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) + " scores: " + scores.toString());
        // Return to the container activity dictation results
        if (results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null) {
//            mCallback.onResults(this, results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

}
