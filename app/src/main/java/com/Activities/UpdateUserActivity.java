package com.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.DB_Objects.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextFullName;
    private EditText editTextUserEmail;
    private EditText editTextIsValidated;
    private EditText editTextIsAdmin;
    private EditText editTextIsPaciente;
    private EditText editTextIsCreador;

    private FirebaseFirestore db;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        user = (User) getIntent().getSerializableExtra("user");
        db = FirebaseFirestore.getInstance();

        editTextFullName = findViewById(R.id.edittext_fullName);
        editTextUserEmail = findViewById(R.id.edittext_email);
        editTextIsValidated = findViewById(R.id.edittext_isValidated);
        editTextIsAdmin = findViewById(R.id.edittext_isAdmin);
        editTextIsPaciente = findViewById(R.id.edittext_isPaciente);
        editTextIsCreador = findViewById(R.id.edittext_isCreador);


        editTextFullName.setText(user.getfullName());
        editTextUserEmail.setText(user.getuserEmail());
        editTextIsValidated.setText(user.getIsValidated());
        editTextIsAdmin.setText(user.getIsAdmin());
        editTextIsPaciente.setText(user.getIsPaciente());
        editTextIsCreador.setText(user.getIsCreador());

        findViewById(R.id.button_update).setOnClickListener(this);
    }

    private boolean hasValidationErrors(String fullName, String userEmail, String isValidated, String isAdmin, String isPaciente, String isCreador) {
        if (fullName.isEmpty()) {
            editTextFullName.setError("Name required");
            editTextFullName.requestFocus();
            return true;
        }

        if (userEmail.isEmpty()) {
            editTextUserEmail.setError("Email required");
            editTextUserEmail.requestFocus();
            return true;
        }

        if (isValidated.isEmpty()) {
            editTextIsValidated.setError("Validation field required");
            editTextIsValidated.requestFocus();
            return true;
        }
    /*    if (isAdmin.isEmpty()) {
            editTextIsAdmin.setError("isAdmin field required");
            editTextIsAdmin.requestFocus();
            return true;
        }
        if (isPaciente.isEmpty()) {
            editTextIsPaciente.setError("isPaciente field required");
            editTextIsPaciente.requestFocus();
            return true;
        }
        if (isCreador.isEmpty()) {
            editTextIsCreador.setError("isCreador field required");
            editTextIsCreador.requestFocus();
            return true;
        }
*/

        return false;
    }


    private void updateUser() {
        String FullName = editTextFullName.getText().toString().trim();
        String UserEmail = editTextUserEmail.getText().toString().trim();
        String isValidated = editTextIsValidated.getText().toString().trim();
        String isAdmin = editTextIsAdmin.getText().toString().trim();
        String isPaciente = editTextIsPaciente.getText().toString().trim();
        String isCreador = editTextIsCreador.getText().toString().trim();


        if (!hasValidationErrors(FullName, UserEmail, isValidated, isAdmin, isPaciente, isCreador)) {

            User u = new User(FullName, UserEmail, isValidated, isAdmin, isPaciente, isCreador);


            db.collection("Users").document(user.getId())
                    .set(u)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UpdateUserActivity.this, "User Updated", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_update:
                updateUser();
                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                break;
        }
    }
}