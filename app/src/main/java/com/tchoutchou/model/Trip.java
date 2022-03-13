package com.tchoutchou.model;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.tchoutchou.util.JDBCUtils;
import com.tchoutchou.util.NoConnectionException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Trip implements Serializable {

    private final int tripId;
    private final String tripDay;
    private final String departureHour;
    private final String departureTown;
    private final String arrivalHour;
    private final String arrivalTown;
    private final float price;
    private final int tripTime;

    public Trip(int tripId,String tripDay, String departureHour,
                String departureTown, String arrivalHour,
                String arrivalTown, float price,int tripTime) {
        this.tripId = tripId;
        this.tripDay = tripDay;
        this.departureHour = departureHour;
        this.departureTown = departureTown;
        this.arrivalHour = arrivalHour;
        this.arrivalTown = arrivalTown;
        this.price = price;
        this.tripTime = tripTime;
    }

    public int getTripId() { return tripId; }

    public String getTripDay() { return tripDay; }

    public String getDepartureHour() {
        return departureHour;
    }

    public String getDepartureTown() {
        return departureTown;
    }

    public String getArrivalHour() {
        return arrivalHour;
    }

    public String getArrivalTown() {
        return arrivalTown;
    }

    public float getPrice() {
        return price;
    }

    public int getTripTime() {
        return tripTime;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Trip> getTrips(Bundle infos) throws NoConnectionException {
        List<Trip> tripList = new ArrayList<>();
        String req = "SELECT trip_id,DATE(departureTime),DATE_FORMAT(departureTime,\"%H:%i\"),departureTown,DATE_FORMAT(arrivalTime,\"%H:%i\"),arrivalTown," +
                "price,TIMESTAMPDIFF(MINUTE,departureTime,arrivalTime) " +
                "FROM trips " +
                "WHERE DATE(departureTime) =\""+JDBCUtils.dateToSQLFormat((String) infos.get("tripDay"))+"\""+
                " AND DATE_FORMAT(departureTime,\"%H:%i\") >= \""+infos.get("departureHour")+"\""+
                " AND departureTown=\""+infos.get("departureTown")+"\""+
                " AND arrivalTown=\""+infos.get("arrivalTown")+"\""+
                " ORDER BY DATE_FORMAT(departureTime,\"%H:%i\") ASC";

        Log.d("ExtDb",req);
        Connection connection = JDBCUtils.getConnection();


        try {
            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery(req);

            while (rs.next()){
                tripList.add(
                        new Trip(
                                rs.getInt(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getString(5),
                                rs.getString(6),
                                rs.getInt(7),
                                rs.getInt(8))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }

        return tripList;
    }


    public static List<Trip> getUserTrips(int userId) throws NoConnectionException {
        List<Trip> tripList = new ArrayList<>();
        String req = "SELECT T.trip_id,DATE(departureTime),DATE_FORMAT(departureTime,\"%H:%i\"),departureTown,DATE_FORMAT(arrivalTime,\"%H:%i\"),arrivalTown," +
                "price,TIMESTAMPDIFF(MINUTE,departureTime,arrivalTime) " +
                "FROM trips T,tickets U " +
                "WHERE T.trip_id = U.trip_id AND U.user_id =" +userId+
                " ORDER BY DATE(departureTime),DATE_FORMAT(departureTime,\"%H:%i\") ASC";
        Connection connection = JDBCUtils.getConnection();

        try {
            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery(req);

            while (rs.next()){
                tripList.add(
                        new Trip(
                                rs.getInt(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getString(5),
                                rs.getString(6),
                                rs.getInt(7),
                                rs.getInt(8))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }

        return tripList;
    }


    public static void clean() throws NoConnectionException {
        Connection connection = JDBCUtils.getConnection();
        String req = "DELETE T,U FROM trips T JOIN tickets U ON U.trip_id = T.trip_id WHERE T.arrivalTime < NOW()";
        String req2 = "DELETE FROM trips WHERE arrivalTime < NOW()";
        Statement st;
        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            st.executeUpdate(req);
            st.execute(req2);
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }
    }
}
