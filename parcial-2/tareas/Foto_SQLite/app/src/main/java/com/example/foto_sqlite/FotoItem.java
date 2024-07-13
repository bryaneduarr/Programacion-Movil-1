package com.example.foto_sqlite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class FotoItem {
  private String imageBase64;
  private String descripcion;

  public FotoItem(String imageBase64, String descripcion) {
    this.imageBase64 = imageBase64;
    this.descripcion = descripcion;
  }

  public String getImageBase64() {
    return imageBase64;
  }

  public void setImageBase64(String imageBase64) {
    this.imageBase64 = imageBase64;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public Bitmap getImageBitmap() {
    byte[] decodeString = android.util.Base64.decode(imageBase64, Base64.DEFAULT);

    return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
  }
}
