package com.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.DB_Objects.Elemento;
import com.DB_Objects.Test;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CreadorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ElementosAdapterCreador adapter;
    private List<Elemento> elementList;
    private FirebaseFirestore db;
    private ArrayList<Elemento> elementosSelec = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Elimina barra con titulo
        setContentView(R.layout.activity_creador);

        Button logout = findViewById(R.id.logoutBtn);
        Button añadir = findViewById(R.id.buttonAñadir);
        Button seleccion = findViewById(R.id.buttonSeleccion);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NewElementActivity.class));
            }
        });

        seleccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elementosSelec = adapter.getElementosSelec();

                if(elementosSelec.isEmpty()){
                    Toast.makeText(CreadorActivity.this,"No hay elementos seleccionados!", Toast.LENGTH_SHORT).show();
                    return;

                } else{
                    seleccionDialog();
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerview_elementos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        elementList = new ArrayList<>();
        adapter = new ElementosAdapterCreador(this, elementList);

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("Conocimiento").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //progressBar.setVisibility(View.GONE);

                        if(!queryDocumentSnapshots.isEmpty()){

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){

                                Elemento e = d.toObject(Elemento.class);
                                e.setId(d.getId());
                                elementList.add(e);

                            }

                            adapter.notifyDataSetChanged();

                        }


                    }
                });
    }

    public void seleccionDialog() {
        final Dialog dialogSeleccion = new Dialog(CreadorActivity.this);
        dialogSeleccion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialogSeleccion.getWindow() != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            dialogSeleccion.getWindow().setBackgroundDrawable(colorDrawable);
        }
        dialogSeleccion.setContentView(R.layout.dialog_seleccion);
        dialogSeleccion.setCancelable(false);
        dialogSeleccion.show();

        Button buttonGuardar = (Button) dialogSeleccion.findViewById(R.id.dialogSeleccion);
        EditText editTextSeleccion = (EditText) dialogSeleccion.findViewById(R.id.editTextSeleccion);



        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String elemBuscar = editTextSeleccion.getText().toString().trim();

                if (elemBuscar.isEmpty()) {
                    editTextSeleccion.setError("Nombre de objeto");
                    editTextSeleccion.requestFocus();
                }
                else {

                    Test test = new Test(elemBuscar, elementosSelec);

                    CollectionReference dbTest = db.collection("Tests");

                    dbTest.add(test).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(CreadorActivity.this, "Añadido a base de tests personalizados", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreadorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    //This will dismiss the dialog
                    dialogSeleccion.dismiss();

                    Intent resetSelec = new Intent(getApplicationContext(), CreadorActivity.class); //reset the activity to reset selections
                    startActivity(resetSelec);


                }

            }
        });
    }
}