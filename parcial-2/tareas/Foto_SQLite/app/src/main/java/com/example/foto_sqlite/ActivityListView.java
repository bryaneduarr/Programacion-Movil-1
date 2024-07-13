package com.example.foto_sqlite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ActivityListView extends AppCompatActivity {
  private ListView listView;
  private ArrayList<FotoItem> fotoItems;
  private Photograph db;
  private Button regresarButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_list_view);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });
    regresarButton = findViewById(R.id.regresarButton);
    listView = findViewById(R.id.listView);
    db = new Photograph(this);
    fotoItems = db.getAllFotos();

    FotoAdapter adapter = new FotoAdapter(this, fotoItems);

    listView.setAdapter(adapter);

    regresarButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(ActivityListView.this, MainActivity.class);

        startActivity(intent);
      }
    });
  }
}