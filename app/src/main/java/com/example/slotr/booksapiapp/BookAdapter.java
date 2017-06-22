package com.example.slotr.booksapiapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by SlotR on 21/06/2017.
 */

public class BookAdapter extends ArrayAdapter {
    public BookAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Book> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, parent, false);


        final Book currentBook = (Book) getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_image_view);
        imageView.setImageBitmap(currentBook.bmp);

        TextView titleView = (TextView) convertView.findViewById(R.id.list_book_title);
        titleView.setText(currentBook.title);

        TextView authorView = (TextView) convertView.findViewById(R.id.list_book_author);
        authorView.setText(currentBook.author);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentBook.infoLink));
                startActivity(getContext(), i, null);
            }
        });

        return convertView;

    }
}
