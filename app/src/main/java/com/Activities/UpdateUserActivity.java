package com.Activities;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Adapters.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextFullName;
    private EditText editTextUserEmail;
    private EditText editTextIsValidated;

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


        editTextFullName.setText(user.getFullName());
        editTextUserEmail.setText(user.getUserEmail());
        editTextIsValidated.setText(user.getIsValidated());



        findViewById(R.id.button_update).setOnClickListener(this);
    }

    private boolean hasValidationErrors(String fullName, String userEmail, String isValidated) {
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


        return false;
    }


    private void updateProduct() {
        String fullName = editTextFullName.getText().toString().trim();
        String userEmail = editTextUserEmail.getText().toString().trim();
        String isValidated = editTextIsValidated.getText().toString().trim();


        if (!hasValidationErrors(fullName, userEmail, isValidated)) {

            User u = new User(fullName, userEmail, isValidated);


            db.collection("Users").document(user.getId())
                    .set(u)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UpdateUserActivity.this, "Product Updated", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_update:
                updateProduct();
                break;
        }
    }
}