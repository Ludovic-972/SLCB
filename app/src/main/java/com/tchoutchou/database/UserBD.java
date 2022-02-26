package com.tchoutchou.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tchoutchou.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class UserBD extends SQLiteOpenHelper {

    private final String TABLE_NAME = "users";

    private final String LASTNAME = "lastname";
    private final String FIRSTNAME = "firstname";
    private final String MAIL = "mail";
    private final String BIRTHDAY = "birthdate";
    private final String PHONE = "phoneNumber";
    private final String PASSWORD = "password";

    public UserBD(Context context) {
        super(context,"User_manager",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+
                TABLE_NAME+" (" +
                LASTNAME+" varchar(256)," +
                FIRSTNAME+" varchar(256)," +
                MAIL+" varchar(256) PRIMARY KEY," +
                BIRTHDAY+" varchar(256)," +
                PHONE+" varchar(10)," +
                PASSWORD+" varchar(256))");
    }

    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LASTNAME,user.getLastname());
        values.put(FIRSTNAME,user.getFirstname());
        values.put(MAIL,user.getMail());
        values.put(BIRTHDAY,dateToSQLFormat(user.getBirthdate()));
        values.put(PHONE,user.getPhoneNumber());
        values.put(PASSWORD,user.getPassword());

        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public User getUser(String mail,String password){
        User user = null;
        if (!mail.equals("") && !password.equals("")) {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM users " +
                    "WHERE mail =\"" + mail +
                    "\" AND password =\"" + password+"\"", null);
            if (cursor.moveToFirst()) {
                user.setLastname(cursor.getString(0));
                user.setFirstname(cursor.getString(1));
                user.setMail(cursor.getString(2));
                user.setBirthdate(cursor.getString(3));
                user.setPhoneNumber(cursor.getString(4));
                user.setPassword(cursor.getString(5));
            }
        }
        return user;
    }

    public List<User> getAllUsers(){
        ArrayList<User> userList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users",null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setLastname(cursor.getString(0));
                user.setFirstname(cursor.getString(1));
                user.setMail(cursor.getString(2));
                user.setBirthdate(cursor.getString(3));
                user.setPhoneNumber(cursor.getString(4));
                user.setPassword(cursor.getString(5));

                userList.add(user);
            } while (cursor.moveToNext());
        }
        db.close();

        return userList;
    }

    public void removeAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM users");
        db.close();
    }

    String dateToSQLFormat(String date) {
        String[] tab = date.split("-");
        return tab[2]+'-'+tab[1]+'-'+tab[0];
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
