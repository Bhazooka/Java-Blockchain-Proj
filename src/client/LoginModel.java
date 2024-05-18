package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbUtil.dbConnection;

public class LoginModel {
    Connection connection;

    public LoginModel(){
        try{
            this.connection = dbConnection.getConnection();
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        if(this.connection == null){
            System.exit(1);
        }
    }

    public boolean isDatabaseConnected(){
        return this.connection != null;
    }

    public boolean isLoginAdmin(String user, String pass) throws Exception{
        PreparedStatement pr = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM login WHERE username = ? AND password = ? AND division = 'Receiver'";

        try {
            pr = this.connection.prepareStatement(sql);
            pr.setString(1, user);
            pr.setString(2, pass);

            rs = pr.executeQuery();

            return rs.next();
        } catch (SQLException ex){
            return false;
        } finally {
            if (pr != null) pr.close();
            if (rs != null) rs.close();
        }
    }

    public boolean isLoginStudent(String fname, String pass) throws Exception{
        PreparedStatement pr = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM students WHERE fname = ? AND pword = ?";

        try {
            pr = this.connection.prepareStatement(sql);
            pr.setString(1, fname);
            pr.setString(2, pass);

            rs = pr.executeQuery();

            return rs.next();
        } catch (SQLException ex){
            return false;
        } finally {
            if (pr != null) pr.close();
            if (rs != null) rs.close();
        }
    }
}
