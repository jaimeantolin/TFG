package com.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.DB_Objects.Elemento;
import com.DB_Objects.Pregunta;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.stream.Collectors;

public class JuegoActivity extends AppCompatActivity {

    Button buttonA, buttonB, buttonC;
    ImageView image;
    TextView correctos;
    FirebaseFirestore db;
    ArrayList<Elemento> elementList;
    ArrayList<Pregunta> questionList;
    Pregunta preguntaActual;
    int pregInd = 0;
    int correctosCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        buttonA = findViewById(R.id.buttonA);
        buttonB = findViewById(R.id.buttonB);
        buttonC = findViewById(R.id.buttonC);
        image = findViewById(R.id.imageViewJuego);
        correctos = findViewById(R.id.textViewCorrectCounter);

        db = FirebaseFirestore.getInstance();

        elementList = new ArrayList<Elemento>();
        questionList = new ArrayList<Pregunta>();

        // Guardar todos los elementos de la base de conocimiento en una lista
        db.collection("Conocimiento")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()){

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){

                                Elemento e = d.toObject(Elemento.class);
                                e.setId(d.getId());
                                elementList.add(e);

                            }
                        }
                        generateQuestions();

                        preguntaActual = questionList.get(pregInd);

                        updateOptions();
                    }
                });

        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preguntaActual.getOptionA().equals(preguntaActual.getAns())){
                    buttonA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightGreen));
                    correctosCounter++;
                    if(pregInd < questionList.size() - 1){
                        disableButton();
                        correctDialog();
                    }
                    else{
                        // no quedan mas preguntas - pantalla final
                        finalJuego();
                    }
                }
                else{
                    // respuesta incorrecta - incorrect dialog
                    buttonA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    if(pregInd < questionList.size() - 1){
                        disableButton();
                        incorrectDialog();
                    }
                    else{
                        finalJuego();
                    }

                }
            }
        });

        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preguntaActual.getOptionB().equals(preguntaActual.getAns())){
                    buttonB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightGreen));
                    correctosCounter++;
                    if(pregInd < questionList.size() - 1){
                        disableButton();
                        correctDialog();
                    }
                    else{
                        // no quedan mas preguntas - pantalla final
                        finalJuego();
                    }
                }
                else{
                    // respuesta incorrecta - incorrect dialog
                    buttonB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    if(pregInd < questionList.size() - 1){
                        disableButton();
                        incorrectDialog();
                    }
                    else{
                        finalJuego();
                    }

                }
            }
        });

        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preguntaActual.getOptionC().equals(preguntaActual.getAns())){
                    buttonC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.lightGreen));
                    correctosCounter++;
                    if(pregInd < questionList.size() - 1){
                        disableButton();
                        correctDialog();
                    }
                    else{
                        // no quedan mas preguntas - pantalla final
                        finalJuego();
                    }
                }
                else{
                    // respuesta incorrecta - incorrect dialog
                    buttonC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    if(pregInd < questionList.size() - 1){
                        disableButton();
                        incorrectDialog();
                    }
                    else{
                        finalJuego();
                    }

                }
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void generateQuestions(){

        for (Elemento e : elementList){
            String optionA = "";
            String optionB = "";
            String optionC = "";
            Random rand = new Random();
            int ansIndex = rand.nextInt(3);

            ArrayList<Elemento> elementListCopy = (ArrayList<Elemento>) elementList.stream()
                    .map(elem -> new Elemento(elem))
                    .collect(Collectors.toList());

            String[] options = new String[2];

            int numberOfElements = 2;

            for (int i = 0; i < numberOfElements; i++) {
                int randomIndex = rand.nextInt(elementListCopy.size());
                options[i] = elementListCopy.get(randomIndex).getNombre();
                if(options[i] == e.getNombre()) // Si uno de los elegidos aleaatorios es la respuesta repetir asignacion aleatoria
                    i--;
                elementListCopy.remove(randomIndex);
            }


            if(ansIndex == 0) {
                optionA = e.getNombre();
                optionB = options[0];
                optionC = options[1];
            }
            else if(ansIndex == 1) {
                optionA = options[0];
                optionB = e.getNombre();
                optionC = options[1];
            }
            else if (ansIndex == 2){
                optionA = options[0];
                optionB = options[1];
                optionC = e.getNombre();
            }

            Pregunta p = new Pregunta(e.getFoto(), optionA , optionB, optionC , e.getNombre());
            questionList.add(p);
        }

        Collections.shuffle(questionList);
    }

    private void updateOptions(){

        Picasso.get().load(preguntaActual.getFoto()).into(image);
        buttonA.setText(preguntaActual.getOptionA());
        buttonB.setText(preguntaActual.getOptionB());
        buttonC.setText(preguntaActual.getOptionC());
        correctos.setText(String.valueOf(correctosCounter));

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), PacienteActivity.class));
        finish();
    }

    private void finalJuego() {
        Intent intent = new Intent(getApplicationContext(), SinPreguntasActivity.class);
        intent.putExtra("Correct", correctosCounter);
        intent.putExtra("Incorrect", questionList.size() - correctosCounter);
        startActivity(intent);
        finish();
    }

    public void correctDialog() {
        final Dialog dialogCorrect = new Dialog(JuegoActivity.this);
        dialogCorrect.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialogCorrect.getWindow() != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            dialogCorrect.getWindow().setBackgroundDrawable(colorDrawable);
        }
        dialogCorrect.setContentView(R.layout.dialog_correcto);
        dialogCorrect.setCancelable(false);
        dialogCorrect.show();

        Button buttonNext = (Button) dialogCorrect.findViewById(R.id.dialogNext);


        //OnCLick listener to go next que
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This will dismiss the dialog
                dialogCorrect.dismiss();
                //it will increment the question number
                pregInd++;
                //get the que and 4 option and store in the currentQuestion
                preguntaActual = questionList.get(pregInd);
                //Now this method will set the new que and 4 options
                updateOptions();
                //reset the color of buttons back to white
                resetColor();
                //Enable button - remember we had disable them when user ans was correct in there particular button methods
                enableButton();
            }
        });
    }
    public void incorrectDialog() {
        final Dialog dialogIncorrect = new Dialog(JuegoActivity.this);
        dialogIncorrect.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialogIncorrect.getWindow() != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            dialogIncorrect.getWindow().setBackgroundDrawable(colorDrawable);
        }
        dialogIncorrect.setContentView(R.layout.dialog_incorrecto);
        dialogIncorrect.setCancelable(false);
        dialogIncorrect.show();

        Button buttonNext = (Button) dialogIncorrect.findViewById(R.id.dialogNext);


        //OnCLick listener to go next que
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This will dismiss the dialog
                dialogIncorrect.dismiss();
                //it will increment the question number
                pregInd++;
                //get the que and 4 option and store in the currentQuestion
                preguntaActual = questionList.get(pregInd);
                //Now this method will set the new que and 4 options
                updateOptions();
                //reset the color of buttons back to white
                resetColor();
                //Enable button - remember we had disable them when user ans was correct in there particular button methods
                enableButton();
            }
        });
    }

    //This method will make button color white again since our one button color was turned green
    public void resetColor() {
        buttonA.setBackgroundResource(android.R.drawable.btn_default);
        buttonB.setBackgroundResource(android.R.drawable.btn_default);
        buttonC.setBackgroundResource(android.R.drawable.btn_default);
    }

    //This method will disable all the option button
    public void disableButton() {
        buttonA.setEnabled(false);
        buttonB.setEnabled(false);
        buttonC.setEnabled(false);
    }

    //This method will all enable the option buttons
    public void enableButton() {
        buttonA.setEnabled(true);
        buttonB.setEnabled(true);
        buttonC.setEnabled(true);
    }
}
