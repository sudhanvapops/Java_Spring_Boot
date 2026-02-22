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

        String query = "select * from student where id = 1";

        // load and register
        try {
            // But modern JDBC doesn't require this anymore.
            // Class.forName("org.postgresql.Driver");

            // create connection
            // connect your app to DB
            Connection conn = DriverManager.getConnection(url, user, dbPassword);

            System.out.print("\nConnection Established: ");
            System.out.println(conn);

            // create statemnt
            Statement st = conn.createStatement();
            // and execute and store it in rs
            // i got it from ctrl + click and see what return type it has
            ResultSet rs = st.executeQuery(query); // fetch

            // System.out.print("Result: ");
            // If got data returns True else False
            // System.out.println(rs.next());

            System.out.println("----Result----");
            // while (rs.next()) {
            // String name = rs.getString("name");
            // System.out.println("Name: "+name);
            // }

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getObject(i) + " ");
                }
                System.out.println();
            }

            rs.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Exeption: \n" + e.getMessage());
            e.printStackTrace();
        }

    }
}