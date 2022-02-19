package com.tchoutchou.utils;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

/*
* Gère les trajets de l'application et les communes
* */
public class ExternalDB {

    private static Connection DB;

    public static boolean init(){
        if(DB == null){
            String nom_driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://mysql-mathurin.alwaysdata.net:3306/mathurin_slcb"  // Lien vers la base de données
                    + "?serverTimezone=UTC"; // Fuseau horaire (UTC = Universal Time Coordinated)
            String login = "mathurin_slcb";
            String pwd = "slcb+-*/+-*/";
            try {
                /* Connexion avec la base */
                Class.forName(nom_driver); // Chargement du pilote JDBC + instance de la classe Driver
                DB = DriverManager.getConnection(url,login,pwd);  // Connexion à la base de données
                return true;
            } catch (ClassNotFoundException e) {
                Log.e("ExternalDatabase","Driver not found");
            }catch (SQLException sqle){
                Log.e("ExternalDatabase","Connection to the database failed");
            }
            return false;
        }
        return true;
    }


    public static List<String> getAllTowns()  {
        List<String> towns = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("SELECT * FROM towns");
            while(rs.next()){
                towns.add(rs.getString("name"));
            }
        }catch(SQLException e){
            Log.e("ExternalDatabase","Problème d'exécution de requête de récupération de communes");
        }

        return towns;
    }
}
