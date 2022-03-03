package com.tchoutchou.model;

import android.os.Bundle;
import android.util.Log;

import com.tchoutchou.util.JDBCUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Trip {

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

    public static List<Trip> getTrips(Bundle infos){
        List<Trip> tripList = new ArrayList<>();
        String req = "SELECT trip_id,DATE(departureTime),DATE_FORMAT(departureTime,\"%H:%i\"),departureTown,DATE_FORMAT(arrivalTime,\"%H:%i\"),arrivalTown," +
                "price,TIMESTAMPDIFF(MINUTE,departureTime,arrivalTime) " +
                "FROM trips " +
                "WHERE departureTime >=\""+JDBCUtils.dateToSQLFormat((String) infos.get("tripDay"))+" "+infos.get("departureHour")+"\""+
                " AND departureTown=\""+infos.get("departureTown")+"\""+
                " AND arrivalTown=\""+infos.get("arrivalTown")+"\""+
                " ORDER BY departureTime ASC";
        Connection connection = JDBCUtils.getConnection();
        Statement st;
        ResultSet rs;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery(req);

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



    @Override
    public String toString() {
        return "Trip{" +
                "tripDay='" + tripDay + '\'' +
                ", departureHour='" + departureHour + '\'' +
                ", departureTown='" + departureTown + '\'' +
                ", arrivalHour='" + arrivalHour + '\'' +
                ", arrivalTown='" + arrivalTown + '\'' +
                ", price=" + price +
                '}';
    }
}
