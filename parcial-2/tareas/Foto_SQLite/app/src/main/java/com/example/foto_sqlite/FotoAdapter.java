package com.example.foto_sqlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FotoAdapter extends BaseAdapter {
  private Context context;
  private ArrayList<FotoItem> photoItems;

  public FotoAdapter(Context context, ArrayList<FotoItem> photoItems) {
    this.context = context;
    this.photoItems = photoItems;
  }

  @Override
  public int getCount() {
    return photoItems.size();
  }

  @Override
  public Object getItem(int position) {
    return photoItems.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    ImageView imageView = convertView.findViewById(R.id.imageView);
    TextView descriptionView = convertView.findViewById(R.id.descriptionView);

    FotoItem photoItem = (FotoItem) getItem(position);
    Bitmap imageBitmap = photoItem.getImageBitmap();
    String description = photoItem.getDescripcion();

    imageView.setImageBitmap(imageBitmap);
    descriptionView.setText(description);

    return convertView;
  }
}
