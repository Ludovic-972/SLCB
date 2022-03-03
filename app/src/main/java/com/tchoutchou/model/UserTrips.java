package com.tchoutchou.model;

import com.tchoutchou.util.JDBCUtils;

import java.sql.Connection;

public class UserTrips {

    private UserTrips(){}

    public static void addUserTrip(int userId,int tripId){
        Connection connection = JDBCUtils.getConnection();


    }
}
