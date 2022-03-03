package com.tchoutchou.model;


import android.util.Log;

import com.tchoutchou.util.InexistantUserException;
import com.tchoutchou.util.JDBCUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class User {

    private int id;
    private String lastname;
    private String firstname;
    private String mail;
    private String birthdate;
    private String phoneNumber;
    private String password;

    public User(){

    }

    public User(int _id,String _lastname,String _firstname,String _mail,String _password,String _birthdate,String _phoneNumber){
        this.id = _id;
        this.lastname = _lastname;
        this.firstname = _firstname;
        this.mail = _mail;
        this.birthdate = _birthdate;
        this.phoneNumber = _phoneNumber;
        this.password = _password;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User){
            User user = (User) o;
            return this.mail.equals(user.getMail()) && this.password.equals(user.getPassword());
        }
        return false;
    }

    public static User addUser(String lastname,String firstname,String mail,
                               String birthdate,String phoneNumber,String password){
        User user = null;
        Connection connection = JDBCUtils.getConnection();
        String req = "Insert into users (`lastname`, `firstname`, `mail`, `password`, `birthday`, `phoneNumber`) values (" +
                "\""+lastname+"\"," +
                "\""+firstname+"\"," +
                "\""+mail+"\"," +
                "\""+password+"\"," +
                "\""+JDBCUtils.dateToSQLFormat(birthdate)+"\"," +
                "\""+phoneNumber+"\")";
        Statement st;
        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            st.executeUpdate(req);
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }

        try {
            user = getInformationsFromDB(mail,password);
        } catch (InexistantUserException e) {
            e.printStackTrace();
        }

        return user;
    }


    public static User getInformationsFromDB(String mail, String password) throws InexistantUserException {
        List<User> users = getAllUsers();
        User res = null;
        for(User user : users){
            if (user.getMail().equals(mail) && user.getPassword().equals(password)){
                Log.d("ExtDB",user.toString());
                res = new User();
                res.setId(user.getId());
                res.setFirstname(user.getFirstname());
                res.setLastname(user.getLastname());
                res.setBirthdate(user.getBirthdate());
                res.setMail(user.getMail());
                res.setPassword(user.getPassword());
                res.setPhoneNumber(user.getPhoneNumber());
                break;
            }
        }
        if (res == null)
            throw new InexistantUserException();
        else
            return res;
    }

    public static List<User> getAllUsers(){
        List<User> usersList = new ArrayList<>();
        String req = "select * from users";
        Connection connection = JDBCUtils.getConnection();
        Statement st;
        ResultSet rs;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery(req);

            while (rs.next()){
                usersList.add(new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }
        return usersList;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", mail='" + mail + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
