package com.tchoutchou.model;

import android.app.Activity;

import com.tchoutchou.util.JDBCUtils;
import com.tchoutchou.util.NoConnectionException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Tickets {



    private Tickets(){}


    public static void addTickets(int userId, int tripId) throws NoConnectionException {
        Connection connection = JDBCUtils.getConnection();
        String req = "Insert into tickets (user_id,trip_id) values ("+
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

    public static List<Integer> getAllUserTickets(int userId) throws NoConnectionException {
        List<Integer> ticketList = new ArrayList<>();

        String req = "SELECT trip_id FROM tickets WHERE user_id ="+userId;

        Connection connection = JDBCUtils.getConnection();
        try{
            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery(req);

            while (rs.next()){
                ticketList.add(rs.getInt(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }

        return  ticketList;
    }

    public static boolean ticketExists(int userId,int tripId) throws NoConnectionException {
        return getAllUserTickets(userId).contains(tripId);
    }

    public static void deleteTicket(int userId,int tripId) throws NoConnectionException {
        Connection connection = JDBCUtils.getConnection();
        String req = "DELETE FROM tickets WHERE"+
                " user_id = "+userId+
                " AND trip_id = "+tripId;
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
