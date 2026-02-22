package com.jdbcsql;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

// import pacakge
// load and register
// create connection
// create statement
// execute statement
// process the result 
// close

public class Main {
    public static void main(String[] args) {

        Dotenv dotEnv = Dotenv.load();

        String url = dotEnv.get("DB_URL");
        String user = dotEnv.get("DB_USER");
        String dbPassword = dotEnv.get("DB_PASSWORD");

        // load and register
        try {
            // But modern JDBC doesn't require this anymore.
            // Class.forName("org.postgresql.Driver");

            // create connection
            // connect your app to DB
            Connection conn = DriverManager.getConnection(url, user, dbPassword);

            System.out.println("Connection Established: ");
            System.out.println(conn);

            conn.close();

        } catch (SQLException  e) {
            System.out.println("Exeption: \n" + e.getMessage());
            e.printStackTrace();
        }

    }
}