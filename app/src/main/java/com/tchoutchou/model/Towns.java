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

public class Towns {

    private Towns(){}

    public static List<String> getAllTowns() throws NoConnectionException {
        List<String> townList = new ArrayList<>();
        String req = "select * from towns";
        Connection connection = JDBCUtils.getConnection();
        Statement st = null;
        ResultSet rs = null;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery(req);

            while (rs.next()){
                townList.add(rs.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(connection);
        }
        return townList;
    }
}


