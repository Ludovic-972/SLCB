package com.tchoutchou.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtils {

    public static Connection getConnection() {
        Connection  connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://mysql-mathurin.alwaysdata.net:3306/mathurin_slcb"  // Lien vers la base de données
                    + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
            String login = "mathurin_slcb";
            String pwd = "slcb+-*/+-*/";
            connection= DriverManager.getConnection(url,login,pwd);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return connection;
    }

    public static void close(Connection connection){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String dateToSQLFormat(String date) {
        String[] tab = date.split("-");
        return tab[2]+'-'+tab[1]+'-'+tab[0];
    }

}
