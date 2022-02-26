package com.tchoutchou;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UserBD extends SQLiteOpenHelper {

    private final String TABLE_NAME = "user";

    private final String LASTNAME = "lastname";
    private final String FIRSTNAME = "firstname";
    private final String MAIL = "mail";
    private final String BIRTHDAY = "birthday";
    private final String PHONE = "phone";
    private final String PASSWORD = "password";

    public UserBD(Context context) {
        super(context,"User_manager",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (" +
                LASTNAME+" varchar(256)," +
                FIRSTNAME+" varchar(256)," +
                MAIL+" varchar(256)," +
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
        values.put(BIRTHDAY,user.getBirthday());
        values.put(PHONE,user.getPhone());
        values.put(PASSWORD,user.getPassword());

        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public List<User> getAllUsers(){
        ArrayList<User> userList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user",null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setLastname(cursor.getString(0));
                user.setFirstname(cursor.getString(1));
                user.setMail(cursor.getString(2));
                user.setBirthday(cursor.getString(3));
                user.setPhone(cursor.getString(4));
                user.setPassword(cursor.getString(5));

                userList.add(user);
            } while (cursor.moveToNext());
        }
        db.close();

        return userList;
    }

    public void removeAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM user");
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
