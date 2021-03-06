package com.smallacademy.userroles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText fullName,email,password;
    Button registerBtn,goToLogin;
    CheckBox isCreador, isPaciente;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        registerBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.gotoLogin);
        isCreador = findViewById(R.id.isCreador);
        isPaciente = findViewById(R.id.isPaciente);

        // checkbox logic

        isCreador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    isPaciente.setChecked(false);
                }
            }
        });

        isPaciente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    isCreador.setChecked(false);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkField(fullName);
                checkField(email);
                checkField(password);

                // checkbox validation
                if(!(isCreador.isChecked() || isPaciente.isChecked())){
                    Toast.makeText(Register.this,"Select the Account type", Toast.LENGTH_SHORT).show();
                    return;
                }



                if (valid) {
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("FullName", fullName.getText().toString());
                            userInfo.put("UserEmail", email.getText().toString());
                            // specify if the user is admin
                            if(isCreador.isChecked()){
                                userInfo.put("isCreador", "1");
                            }
                            if(isPaciente.isChecked()){
                                userInfo.put("isPaciente", "1");
                            }

                            df.set(userInfo);

                            if(isCreador.isChecked()){
                                startActivity(new Intent(getApplicationContext(), CreadorActivity.class));
                                finish(); // prevents going back to registration page
                            }
                            if(isPaciente.isChecked()){
                                startActivity(new Intent(getApplicationContext(), PacienteActivity.class));
                                finish(); // prevents going back to registration page
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
}