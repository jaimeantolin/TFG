package com.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Adapters.TestsAdapter;
import com.DB_Objects.Elemento;
import com.DB_Objects.Test;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JuegoSelecActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TestsAdapter adapter;
    private List<Test> testList;
    private FirebaseFirestore db;
    ArrayList<Elemento> elementList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Elimina barra con titulo
        setContentView(R.layout.activity_juego_selec);

        Button todo = findViewById(R.id.todoBtn);
        elementList = new ArrayList<Elemento>();

        todo.setOnClickListener(new View.OnClickListener() { // Coger todos los elementos de la base de conocimiento y pasarselos como un arraylist a la actividad del juego
            @Override
            public void onClick(View v) {

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
                                Intent intent = new Intent(getApplicationContext(), JuegoActivity.class);
                                intent.putExtra("Test", elementList);
                                startActivity(intent);
                            }
                        });


            }
        });


        recyclerView = findViewById(R.id.recyclerview_tests);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        testList = new ArrayList<>();
        adapter = new TestsAdapter(this, testList);

        recyclerView.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();


        db.collection("Tests").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        if(!queryDocumentSnapshots.isEmpty()){

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){

                                Test u = d.toObject(Test.class);
                                u.setId(d.getId());
                                testList.add(u);

                            }

                            adapter.notifyDataSetChanged();

                        }


                    }
                });
    }

}

