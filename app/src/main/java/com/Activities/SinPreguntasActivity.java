package com.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SinPreguntasActivity extends AppCompatActivity {

    TextView correct, incorrect, percentage;
    Button playAgain, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Elimina barra con titulo
        setContentView(R.layout.sin_preguntas);

        correct = findViewById(R.id.textViewCorrectTotal);
        incorrect = findViewById(R.id.textViewWrongTotal);
        percentage = findViewById(R.id.textViewPercentage);

        playAgain = findViewById(R.id.buttonPlayAgain);
        back = findViewById(R.id.buttonMenuPaciente);

        Bundle extras = getIntent().getExtras();
        int numCorrect = 0, numIncorrect = 0, numPercent;

        if (extras != null) {
            numCorrect = extras.getInt("Correct");
            numIncorrect = extras.getInt("Incorrect");
            //The key argument here must match that used in the other activity
        }

        correct.setText(String.valueOf(numCorrect));
        incorrect.setText(String.valueOf(numIncorrect));
        int calc = (numCorrect / (numIncorrect + numCorrect))*100;
        Log.d("tag", "percent is " + String.valueOf(calc));
        percentage.setText(String.valueOf(calc) + "%");

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), JuegoActivity.class));
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PacienteActivity.class));
                finish();
            }
        });

    }

}
