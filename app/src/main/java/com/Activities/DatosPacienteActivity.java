package com.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.DB_Objects.Elemento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class DatosPacienteActivity extends AppCompatActivity {

    Button atras;
    TextView textViewNombre, textViewDesc;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_paciente);

        Bundle extras = getIntent().getExtras();
        String label = "";
        if (extras != null) {
            label = extras.getString("Label");
            //The key argument here must match that used in the other activity
        }

        atras = findViewById(R.id.buttonAtras);
        textViewNombre = findViewById(R.id.textViewName);
        textViewDesc = findViewById(R.id.textViewDesc);

        db = FirebaseFirestore.getInstance();

        db.collection("Conocimiento")
                .whereEqualTo("label", label)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){

                                Elemento e = d.toObject(Elemento.class);
                                e.setId(d.getId());

                                textViewNombre.setText(e.getNombre());
                                textViewDesc.setText(e.getDesc());

                            }
                        }
                    }
                });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PacienteActivity.class));
            }
        });


    }
}
