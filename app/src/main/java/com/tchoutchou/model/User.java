package com.tchoutchou.model;

import static com.tchoutchou.MainActivity.getAppContext;

import android.util.Log;

import com.tchoutchou.database.UserBD;

import java.util.List;

public class User {

    private String lastname;
    private String firstname;
    private String mail;
    private String birthdate;
    private String phoneNumber;
    private String password;

    public User(){

    }

    public User(String _lastname,String _firstname,String _mail,String _birthdate,String _phoneNumber,String _password){
        this.lastname = _lastname;
        this.firstname = _firstname;
        this.mail = _mail;
        this.birthdate = _birthdate;
        this.phoneNumber = _phoneNumber;
        this.password = _password;
    }

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
    public String toString() {
        return "User{" +
                "lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", mail='" + mail + '\'' +
                ", birthday='" + birthdate + '\'' +
                ", phone='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User){
            User user = (User) o;
            return this.mail.equals(user.getMail()) && this.password.equals(user.getPassword());
        }
        return false;
    }


    public static boolean isRegistered(String loginText, String passwordText) {
        UserBD userBD = new UserBD(getAppContext());
        List<User> users = userBD.getAllUsers();
        for(User user : users){
            if (user.getMail().equals(loginText) && user.getPassword().equals(passwordText))
                return true;
        }
        return false;
    }

    public static User getInformations(String loginText, String passwordText) {
        UserBD userBD = new UserBD(getAppContext());
        List<User> users = userBD.getAllUsers();
        User res = new User();
        for(User user : users){
            if (user.getMail().equals(loginText) && user.getPassword().equals(passwordText)){
                res.setFirstname(user.getFirstname());
                res.setLastname(user.getLastname());
                res.setBirthdate(user.getBirthdate());
                res.setMail(user.getMail());
                res.setPassword(user.getPassword());
                res.setPhoneNumber(user.getPhoneNumber());
            }
        }
        return  res;
    }
}
