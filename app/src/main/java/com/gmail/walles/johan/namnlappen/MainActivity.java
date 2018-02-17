package com.gmail.walles.johan.namnlappen;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Random;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String[] PRAISE = { "bra", "fint", "utmärkt" };

    @Nullable
    private TextToSpeech textToSpeech;
    private int ttsStatus = TextToSpeech.ERROR;

    private Challenge challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.answer1).setOnClickListener(this);
        findViewById(R.id.answer2).setOnClickListener(this);
        findViewById(R.id.answer3).setOnClickListener(this);

        textToSpeech =
                new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        ttsStatus = i;
                        if (ttsStatus == TextToSpeech.SUCCESS) {
                            updateChallenge();
                        } else {
                            Timber.e("Error setting up TTS: status=%d", i);
                        }
                    }
                });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                // This method intentionally left blank
            }

            @Override
            public void onDone(String s) {
                // This method intentionally left blank
            }

            @Override
            public void onError(String s) {
                Timber.e("Speech failed: id=<%s>", s);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void speak(String phrase, boolean flush) {
        if (textToSpeech == null) {
            Timber.e("Text to speech not initialized");
            return;
        }

        if (ttsStatus != TextToSpeech.SUCCESS) {
            Timber.e("Text to speech status unsuccessful: %d", ttsStatus);
            return;
        }

        int queueMode = flush ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD;
        int status = textToSpeech.speak(phrase, queueMode, null);
        if (status == TextToSpeech.SUCCESS) {
            Timber.i("Spoken: flush=%s, phrase=<%s>", flush, phrase);
        } else {
            Timber.e("Speech failed: flush=%s, phrase=<%s>", flush, phrase);
        }
    }

    private boolean ttsInitializing() {
        if (textToSpeech == null) {
            return true;
        }

        if (ttsStatus != TextToSpeech.SUCCESS) {
            return true;
        }

        return false;
    }

    /**
     * Get a new challenge, update the buttons and speak the new phrase to the user.
     */
    private void updateChallenge() {
        if (ttsInitializing()) {
            Timber.w("TTS initializing, not picking any new challenge");
            return;
        }

        Timber.i("Picking new challenge...");

        challenge = new Challenge();
        ((Button)findViewById(R.id.answer1)).setText(challenge.options[0]);
        ((Button)findViewById(R.id.answer2)).setText(challenge.options[1]);
        ((Button)findViewById(R.id.answer3)).setText(challenge.options[2]);
        speak(challenge.question, false);
    }

    @Override
    public void onClick(View view) {
        if (!(view instanceof Button)) {
            return;
        }

        Button button = (Button)view;
        if (TextUtils.equals(button.getText(), challenge.answer)) {
            // Praise the user and pick a new letter
            String praise = PRAISE[new Random().nextInt(PRAISE.length)];
            speak("\"" + challenge.answer + "\", " + praise + "!", true);
            updateChallenge();
        } else {
            // Prompt the user to try again
            speak("\"" + button.getText() + "\" var fel, försök hitta \"" + challenge.answer + "\"!", true);
        }
    }
}
