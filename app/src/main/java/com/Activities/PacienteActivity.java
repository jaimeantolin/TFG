package com.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.DB_Objects.Elemento;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PacienteActivity extends AppCompatActivity {

    public static final int All_PERMS_CODE = 1101;
    public static final int CAMERA_REQUEST_CODE = 102;

    String currentPhotoPath;
    String imageFileName;
    String labelBusqueda;
    Uri contentUri;

    FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        Button escanear = findViewById(R.id.buttonEscanear);
        Button logout = findViewById(R.id.logoutBtn);

        mFunctions = FirebaseFunctions.getInstance();

        escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirPermisos();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });


    }

    private void pedirPermisos() {
        Log.d("TAG", "VerifyingPermission : Asking for permission ");
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // index 0 = camera, index 1 = readStorage , index 2 = write Storage

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {

            dispatchTakePictureIntent();
        }
        else {
            ActivityCompat.requestPermissions(PacienteActivity.this, permissions, All_PERMS_CODE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.Activities.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File storageDir = Environment.getExternalStorageDirectory(); // HAY QUE DAR STORAGE PERMISSIONS A LA APP
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                Log.d("tag", "Absolute URI of image is " + Uri.fromFile(f));

                //Saving image to gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                imageFileName = f.getName();

                labelImage();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == All_PERMS_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(PacienteActivity.this, "Permisos de Camara y almacenamiento requeridos", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void labelImage(){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentUri);
            // Convert bitmap to base64 encoded string
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            // Create json request to cloud vision
            JsonObject request = new JsonObject();
            // Add image to request
            JsonObject image = new JsonObject();
            image.add("content", new JsonPrimitive(base64encoded));
            request.add("image", image);
            //Add features to the request
            JsonObject feature = new JsonObject();
            feature.add("maxResults", new JsonPrimitive(5));
            feature.add("type", new JsonPrimitive("LABEL_DETECTION"));
            JsonArray features = new JsonArray();
            features.add(feature);
            request.add("features", features);

            annotateImage(request.toString())
                    .addOnCompleteListener(new OnCompleteListener<JsonElement>() {
                        @Override
                        public void onComplete(@NonNull Task<JsonElement> task) {
                            if (!task.isSuccessful()) {
                                // Task failed with an exception
                                // ...
                                Toast.makeText(PacienteActivity.this, "Failed to classify image", Toast.LENGTH_SHORT).show();
                            } else {
                                // Task completed successfully
                                // ...
                                String bestLabel = "";
                                float highestScore = 0;

                                for (JsonElement label : task.getResult().getAsJsonArray().get(0).getAsJsonObject().get("labelAnnotations").getAsJsonArray()) {
                                    JsonObject labelObj = label.getAsJsonObject();
                                    String text = labelObj.get("description").getAsString();
                                    float score = labelObj.get("score").getAsFloat();

                                    if(score > highestScore){
                                        bestLabel = text;
                                        highestScore = score;
                                    }
                                }

                                labelBusqueda = bestLabel;
                                Intent showData = new Intent(getApplicationContext(), DatosPacienteActivity.class);
                                showData.putExtra("Label", labelBusqueda);
                                startActivity(showData);
                            }
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private Task<JsonElement> annotateImage(String requestJson) {
        return mFunctions
                .getHttpsCallable("annotateImage")
                .call(requestJson)
                .continueWith(new Continuation<HttpsCallableResult, JsonElement>() {
                    @Override
                    public JsonElement then(@NonNull Task<HttpsCallableResult> task) {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return JsonParser.parseString(new Gson().toJson(task.getResult().getData()));
                    }
                });
    }
}