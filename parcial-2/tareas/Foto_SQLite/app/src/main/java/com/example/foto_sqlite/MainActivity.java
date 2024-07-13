package com.example.foto_sqlite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
  private static final int REQUEST_CAMERA_PERMISSION = 100;
  private static final int REQUEST_IMAGE_CAPTURE = 101;
  private Button guardarButton, verFotosButton;
  private EditText descripcionEditText;
  private String currentPhotoPath;
  private ImageView imageView;
  private Photograph db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
    db = new Photograph(this);

    descripcionEditText = findViewById(R.id.descripcionTextInputEditText);
    verFotosButton = findViewById(R.id.verFotosButton);
    guardarButton = findViewById(R.id.guardarButton);
    imageView = findViewById(R.id.imageView);

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        permisosCamara();
      }
    });

    verFotosButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, ActivityListView.class);

        startActivity(intent);
      }
    });

    guardarButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        guardarFotoDB();
      }
    });
  }

  private void guardarFotoDB() {
    String description = descripcionEditText.getText().toString().trim();
    String base64Image = convertImageBase64(currentPhotoPath);

    if (!description.isEmpty() && !base64Image.isEmpty()) {
      db.addPhoto(base64Image, description);

      Toast.makeText(this, "Foto guardada en la base de datos.", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, "Llene todos los campos.", Toast.LENGTH_SHORT).show();
    }
  }

  private void permisosCamara() {
    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
    } else {
      abrirCamara();
    }
  }

  private void abrirCamara() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      File photoFile = null;

      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        Log.e("MainActivity", "Error al crear el archivo de la imagen.", ex);
      }

      if (photoFile != null) {
        Uri photoURI = FileProvider.getUriForFile(this, "com.example.foto_sqlite.fileprovider", photoFile);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      }
    }
  }

  private File createImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    String imageFileName = "JPEG_" + timeStamp + "_";

    File storageDir = getExternalFilesDir(null);

    File image = File.createTempFile(imageFileName, ".jpg", storageDir);

    currentPhotoPath = image.getAbsolutePath();

    return image;
  }

  private String convertImageBase64(String path) {
    Bitmap bitmap = BitmapFactory.decodeFile(path);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

    byte[] imagearray = byteArrayOutputStream.toByteArray();

    return Base64.encodeToString(imagearray, Base64.DEFAULT);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

      imageView.setImageBitmap(bitmap);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == REQUEST_CAMERA_PERMISSION) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        abrirCamara();
      } else {
        Toast.makeText(this, "Permiso de camara denegado.", Toast.LENGTH_SHORT).show();
      }
    }
  }
}