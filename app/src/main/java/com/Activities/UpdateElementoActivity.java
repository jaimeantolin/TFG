package com.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.DB_Objects.Elemento;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateElementoActivity extends AppCompatActivity {

    public static final int All_PERMS_CODE = 1101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALERIA_REQUEST_CODE = 105;

    private EditText editTextNombre, editTextDesc;
    private TextView textViewLabel;
    private ImageView image;

    private FirebaseFirestore db;
    FirebaseFunctions mFunctions;

    private Elemento element;
    StorageReference storageReference;

    String imageFileName;
    String currentPhotoPath;
    Uri contentUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Elimina barra con titulo
        setContentView(R.layout.activity_update_elemento);

        element = (Elemento) getIntent().getSerializableExtra("elemento");
        db = FirebaseFirestore.getInstance();

        imageFileName = element.getFoto();

        editTextNombre = findViewById(R.id.edittext_Nombre2);
        editTextDesc = findViewById(R.id.edittext_Descripcion2);
        textViewLabel = findViewById(R.id.textview_sensorID2);
        image = findViewById(R.id.imageView3);

        editTextNombre.setText(element.getNombre());
        editTextDesc.setText(element.getDesc());
        textViewLabel.setText(element.getLabel());
        Picasso.get().load(element.getFoto()).into(image);

        storageReference = FirebaseStorage.getInstance().getReference();

        mFunctions = FirebaseFunctions.getInstance();

        findViewById(R.id.btnCamara2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateElementoActivity.this, "Boton de Camara presionado", Toast.LENGTH_SHORT).show();
                pedirPermisos();
            }
        });

        findViewById(R.id.btnGaleria2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateElementoActivity.this, "Boton de Galería presionado", Toast.LENGTH_SHORT).show();
                Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galeria, GALERIA_REQUEST_CODE);
            }
        });

        findViewById(R.id.btnActualizar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateElemento();
            }
        });

        findViewById(R.id.button_eliminar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateElementoActivity.this);
                builder.setTitle("¿Estas seguro?");
                builder.setMessage("Eliminar es permanente");

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarElemento();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });

    }

    private void eliminarElemento() {
        db.collection("Conocimiento").document(element.getId()).delete().addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(UpdateElementoActivity.this, "Elemento Eliminado", Toast.LENGTH_LONG).show();
                    finish();
                    storageReference.child("images/" + URLUtil.guessFileName(imageFileName, "filename.jpg", ".jpg" )).delete(); // Should delete old image from storage
                    startActivity(new Intent(getApplicationContext(), CreadorActivity.class));
                }
            }
        });
    }

    private boolean hasValidationErrors(String nombre, String desc, String sensorID, String foto) {
        if (nombre.isEmpty()) {
            editTextNombre.setError("Name required");
            editTextNombre.requestFocus();
            return true;
        }

        if (desc.isEmpty()) {
            editTextDesc.setError("Brand required");
            editTextDesc.requestFocus();
            return true;
        }

        if (sensorID.isEmpty()) {
            textViewLabel.setError("Description required");
            textViewLabel.requestFocus();
            return true;
        }

        if (foto.isEmpty()) {
            Toast.makeText(UpdateElementoActivity.this, "Error con URI de la foto", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    private void updateElemento() {

        if(contentUri != null && !contentUri.toString().equals(element.getFoto())){
            uploadImageToFirebase(imageFileName, contentUri);

        }else {
            String sImage = element.getFoto();
            String sNombre = editTextNombre.getText().toString().trim();
            String sDesc = editTextDesc.getText().toString().trim();
            String sLabel = textViewLabel.getText().toString().trim();

            if(!hasValidationErrors(sNombre, sDesc, sLabel, sImage)){

                CollectionReference dbConocimiento = db.collection("Conocimiento");

                Elemento elemento = new Elemento(sNombre, sDesc, sLabel, sImage);

                dbConocimiento.document(element.getId()).set(elemento).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(UpdateElementoActivity.this, "Base de conocimiento modificada", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateElementoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
             }
            startActivity(new Intent(getApplicationContext(), CreadorActivity.class));
        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        final StorageReference image = storageReference.child("images/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image Uri is " +  uri.toString());

                        String sImage= uri.toString();
                        String sNombre = editTextNombre.getText().toString().trim();
                        String sDesc = editTextDesc.getText().toString().trim();
                        String sLabel = textViewLabel.getText().toString().trim();

                        if(!hasValidationErrors(sNombre, sDesc, sLabel, sImage)){

                            CollectionReference dbConocimiento = db.collection("Conocimiento");

                            Elemento elemento = new Elemento(sNombre, sDesc, sLabel, sImage);

                            dbConocimiento.document(element.getId()).set(elemento).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Toast.makeText(UpdateElementoActivity.this, "Base de conocimiento modificada", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateElementoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        startActivity(new Intent(getApplicationContext(), CreadorActivity.class));
                    }

                });
                Toast.makeText(UpdateElementoActivity.this, "Imagen subida", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateElementoActivity.this, "Error subiendo imagen", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                image.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute URI of image is " + Uri.fromFile(f));

                //Saving image to gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                storageReference.child("images/" + URLUtil.guessFileName(imageFileName, "filename.jpg", ".jpg" )).delete(); // Should delete old image from storage
                imageFileName = f.getName();

                labelImage();
            }
        }

        if(requestCode == GALERIA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                storageReference.child("images/" + URLUtil.guessFileName(imageFileName, "filename.jpg", ".jpg" )).delete(); // Should delete old image from storage
                imageFileName = "JPEG_" + timeStamp +"."+ getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " +  imageFileName);
                image.setImageURI(contentUri);

                labelImage();
            }
        }
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
            ActivityCompat.requestPermissions(UpdateElementoActivity.this, permissions, All_PERMS_CODE);
        }
    }


    private String getFileExt(Uri contentUri) { // Extracts extension of the image
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == All_PERMS_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(UpdateElementoActivity.this, "Permisos de Camara y almacenamiento requeridos", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(UpdateElementoActivity.this, "Failed to classify image", Toast.LENGTH_SHORT).show();
                            } else {
                                // Task completed successfully
                                // ...
                                String bestLabel = "";
                                float highestScore = 0;

                                for (JsonElement label : task.getResult().getAsJsonArray().get(0).getAsJsonObject().get("labelAnnotations").getAsJsonArray()) {
                                    JsonObject labelObj = label.getAsJsonObject();
                                    String text = labelObj.get("description").getAsString();
                                    String entityId = labelObj.get("mid").getAsString();
                                    float score = labelObj.get("score").getAsFloat();

                                    if(score > highestScore){
                                        bestLabel = text;
                                        highestScore = score;
                                    }
                                }

                                textViewLabel.setText(bestLabel);
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
