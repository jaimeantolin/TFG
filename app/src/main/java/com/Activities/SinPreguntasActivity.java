package com.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.DB_Objects.Elemento;

import java.util.ArrayList;

public class SinPreguntasActivity extends AppCompatActivity {

    TextView correct, incorrect, percentage;
    Button playAgain, back;
    ArrayList<Elemento> elementList;

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
        elementList = new ArrayList<Elemento>();
        int numCorrect = 0, numIncorrect = 0;
        double numPercent = 0;

        if (extras != null) {
            numCorrect = extras.getInt("Correct");
            numIncorrect = extras.getInt("Incorrect");
            numPercent = extras.getDouble("Percent");
            elementList = (ArrayList<Elemento>) extras.get("Test");
            //The key argument here must match that used in the other activity
        }

        correct.setText(String.valueOf(numCorrect));
        incorrect.setText(String.valueOf(numIncorrect));
        percentage.setText(String.valueOf(numPercent) + "%");

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JuegoActivity.class);
                intent.putExtra("Test", elementList);
                startActivity(intent);
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
