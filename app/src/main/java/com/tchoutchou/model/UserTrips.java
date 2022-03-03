package com.tchoutchou.model;

import com.tchoutchou.util.JDBCUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserTrips {

    private UserTrips(){}

    public static void addUserTrip(int userId,int tripId){
        Connection connection = JDBCUtils.getConnection();

        String req = "Insert into userTrips (user_id,trip_id) values ("+
                userId+","+
                tripId
                +")";
        Statement st;
        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            st.executeUpdate(req);
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }

    }
}
