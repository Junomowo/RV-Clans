package de.rovedmc.clans.util;

import de.rovedmc.clans.Clans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
public class MySQL {
        private static String HOST = Clans.HOST;
        private static String DATABASE = Clans.DATABASE;
        private static String USER = Clans.USER;
        private static String PASSWORD = Clans.PASSWORD;
       
        private static Connection con;
       
        @SuppressWarnings("static-access")
		public MySQL(String host, String database, String user, String password) {
                this.HOST = host;
                this.DATABASE = database;
                this.USER = user;
                this.PASSWORD = password;
               
                connect();
        }
 
        public static void connect() {
                try {
                        con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":3306/" + DATABASE + "?autoReconnect=true", USER, PASSWORD);
                } catch (SQLException e) {
                        System.out.println("[MySQL] Die Verbindung zur MySQL ist fehlgeschlagen! Fehler: " + e.getMessage());
                }
        }
       
        public static void close() {
                try {
                        if(con != null) {
                                con.close();
                                System.out.println("[MySQL] Die Verbindung zur MySQL wurde Erfolgreich beendet!");
                        }
                } catch (SQLException e) {
                        System.out.println("[MySQL] Fehler beim beenden der Verbindung zur MySQL! Fehler: " + e.getMessage());
                }
        }
       
        public static void update(String qry) {
                try {
                        Statement st = con.createStatement();
                        st.executeUpdate(qry);
                        st.close();
                } catch (SQLException e) {
                        connect();
                        System.err.println(e);
                }
        }
       
        public static ResultSet query(String qry) {
                ResultSet rs = null;
               
                try {
                        Statement st = con.createStatement();
                        rs = st.executeQuery(qry);
                } catch (SQLException e) {
                        connect();
                        System.err.println(e);
                }
                return rs;
        }
        
        public static void createTable() {
    		MySQL.update("CREATE TABLE IF NOT EXISTS Stats ("
    				+ "UUID VARCHAR(80), "
    				+ "Name VARCHAR(20), "
    				+ "Kills INT(20), "
    				+ "Deaths INT(20), "
    				+ "HightLevel INT(20), "
    				+ "FirstPlay VARCHAR(80))");
    	}
}