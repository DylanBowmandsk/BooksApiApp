package com.example.slotr.booksapiapp;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SlotR on 21/06/2017.
 */

public class Book implements Parcelable {
    String title;
    String author;
    Bitmap bmp;
    String infoLink;

    public Book(String title, String author, Bitmap bmp, String infoLink) {
        this.title = title;
        this.author = author;
        this.bmp = bmp;
        this.infoLink = infoLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeString(infoLink);
        parcel.writeValue(bmp);
    }

    public static final Parcelable.Creator<Book> CREATOR
            = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private Book(Parcel in) {
        title = in.readString();
        author = in.readString();
        infoLink = in.readString();
        bmp = in.readParcelable(Bitmap.class.getClassLoader());
    }
}
