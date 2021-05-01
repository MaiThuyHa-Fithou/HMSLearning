package com.mtha.findmyfriends.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ContactDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "ContactDbHelper";
    private static final String DATABASE_NAME = "mycontact.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CONTACT = "contact";

    public ContactDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Create table");
        String queryCreateTable = "CREATE TABLE " + TABLE_CONTACT + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fullname VARCHAR (255) NOT NULL, " +
                "phoneNumb VARCHAR (255) NOT NULL, " +
                "email VARCHAR (255) NOT NULL, " +
                "address VARCHAR (255) NOT NULL " +
                ")";

        db.execSQL(queryCreateTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //Xoá bảng cũ
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        //Tiến hành tạo bảng mới
        onCreate(db);
    }

    public ArrayList<Contact> getAllContacts(){
        ArrayList<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_CONTACT, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String fullName = cursor.getString(0);
            String phoneNumb = cursor.getString(1);
            String email = cursor.getString(2);
            String address = cursor.getString(3);
            contacts.add(new Contact(fullName,phoneNumb,email,address));
            cursor.moveToNext();
        }
        return contacts;
    }

    public void insContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_CONTACT + " ( fullname, phoneNumb, email, address ) " +
                "VALUES (?,?,?,?) " , new String[]{contact.getFullName(), contact.getPhoneNumb(), contact.getEmail(), contact.getAddress()});
    }
}
