package com.mtha.findmyfriends.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
                "image VARCHAR (255) NOT NULL, " +
                "latitude double,  "+
                "longtitude double "+
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

    public List<Contact> getAllContacts(){
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_CONTACT, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            String fullName = cursor.getString(1);
            String phoneNumb = cursor.getString(2);
            String email = cursor.getString(3);
            String image = cursor.getString(4);
            double latitude = cursor.getDouble(5);
            double longtitude = cursor.getDouble(6);
            contacts.add(new Contact(fullName,phoneNumb,email,image,latitude,longtitude));

        }
        return contacts;
    }

    public void insContact(@NotNull Contact contact){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_CONTACT + " ( fullname, phoneNumb, email, image, latitude, longtitude ) " +
                "VALUES (?,?,?,?,?,?) " , new String[]{contact.getFullName(),
                contact.getPhoneNumb(), contact.getEmail(),contact.getImage()
        ,contact.getLatitude()+"", contact.getLongitude()+""});
    }

    public JSONArray getJsonContacts() throws JSONException {
        JSONArray listContacts = new JSONArray();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_CONTACT, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            String fullName = cursor.getString(1);
            String phoneNumb = cursor.getString(2);
            String email = cursor.getString(3);
            String image = cursor.getString(4);
            double latitude = cursor.getDouble(5);
            double longtitude = cursor.getDouble(6);
            JSONObject contact = new JSONObject();
            contact.put("fullname", fullName);
            contact.put("email", email);
            contact.put("phone", phoneNumb);
            contact.put("image", image);
            contact.put("latitude", latitude);
            contact.put("longtitude", longtitude);
            listContacts.put(contact);
        }
        return listContacts;
    }
}
