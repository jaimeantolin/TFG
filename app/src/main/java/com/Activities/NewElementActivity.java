package com.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.DB_Objects.Elemento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.grpc.Context;

public class NewElementActivity extends AppCompatActivity{

    public static final int All_PERMS_CODE = 1101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALERIA_REQUEST_CODE = 105;
    ImageView selectedImage;
    Button btnCamara, btnGaleria, btnUpload;
    EditText editTextNombre, editTextDesc, editTextSensorID;
    String currentPhotoPath;
    StorageReference storageReference;

    String imageFileName;
    Uri contentUri;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_element);

        selectedImage = findViewById(R.id.imageView);
        btnCamara = findViewById(R.id.btnCamara);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnUpload = findViewById(R.id.btnUpload);

        editTextNombre = findViewById(R.id.edittext_Nombre);
        editTextDesc = findViewById(R.id.edittext_Descripcion);
        editTextSensorID = findViewById(R.id.edittext_sensorID);

        db = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewElementActivity.this, "Boton de Camara presionado", Toast.LENGTH_SHORT).show();
                pedirPermisos();
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewElementActivity.this, "Boton de Galería presionado", Toast.LENGTH_SHORT).show();
                Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galeria, GALERIA_REQUEST_CODE);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewElementActivity.this, "Upload presionado", Toast.LENGTH_SHORT).show();
                uploadImageToFirebase(imageFileName, contentUri);

            }
        });
    }

    private boolean validateInputs(String nombre, String desc, String sensorID, String foto) {
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
            editTextSensorID.setError("Description required");
            editTextSensorID.requestFocus();
            return true;
        }

        if (foto.isEmpty()) {
            Toast.makeText(NewElementActivity.this, "Error con URI de la foto", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
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
            ActivityCompat.requestPermissions(NewElementActivity.this, permissions, All_PERMS_CODE);
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute URI of image is " + Uri.fromFile(f));

                //Saving image to gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                imageFileName = f.getName();
            }
        }

        if(requestCode == GALERIA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timeStamp +"."+ getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " +  imageFileName);
                selectedImage.setImageURI(contentUri);
            }
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
                        String nombre = editTextNombre.getText().toString().trim();
                        String desc = editTextDesc.getText().toString().trim();
                        String sensorID = editTextSensorID.getText().toString().trim();
                        String image = uri.toString();

                        if(!validateInputs(nombre, desc, sensorID, image)){

                            CollectionReference dbConocimiento = db.collection("Conocimiento");

                            Elemento elemento = new Elemento(nombre, desc, sensorID, image);

                            dbConocimiento.add(elemento).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(NewElementActivity.this, "Añadido a base de conocimiento", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NewElementActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), CreadorActivity.class));
                        }
                    }

                });
                Toast.makeText(NewElementActivity.this, "Imagen subida", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewElementActivity.this, "Error subiendo imagen", Toast.LENGTH_SHORT).show();
            }
        });

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
                Toast.makeText(NewElementActivity.this, "Permisos de Camara y almacenamiento requeridos", Toast.LENGTH_SHORT).show();
            }

        }
    }


}