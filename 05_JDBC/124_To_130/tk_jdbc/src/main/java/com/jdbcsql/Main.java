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

    public static int insertIntoStudents(Connection conn, int id, int marks, String name) {

        // ! CREATE

        int result = 0;
        try {
            String query2 = "INSERT INTO student (id,name,marks)  VALUES (?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(query2);
            // pstmt.setInt(1, id);
            // pstmt.setInt(2, marks);
            // pstmt.setString(3, name);
            pstmt.setInt(1, id);
            pstmt.setInt(3, marks);
            pstmt.setString(2, name);
            result = pstmt.executeUpdate();
            System.out.println("Inserted: " + result);

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void readResult(Connection conn) {
        try {
            String query = "select * from student";

            // create statemnt
            // and execute and store it in rs
            // i got it from ctrl + click and see what return type it has
            Statement st = conn.createStatement();
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

            // Printing Header Data
            for (int i=1; i<=cols; i++){
                System.out.print(meta.getColumnName(i)+"\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getObject(i) + "\t");
                }
                System.out.println();
            }

            System.out.println("--------------");

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Dotenv dotEnv = Dotenv.load();

        String url = dotEnv.get("DB_URL");
        String user = dotEnv.get("DB_USER");
        String dbPassword = dotEnv.get("DB_PASSWORD");

        try {
            // ! load and register
            // But modern JDBC doesn't require this anymore.
            // Class.forName("org.postgresql.Driver");

            // ! create connection
            // connect your app to DB
            Connection conn = DriverManager.getConnection(url, user, dbPassword);

            System.out.print("\nConnection Established: ");
            System.out.println(conn);

            // ! create
            // insertIntoStudents(conn, 7, 90, "KR");

            // ! Read
            readResult(conn);

            // ! Close Connection
            conn.close();

        } catch (SQLException e) {
            System.out.println("Exeption: \n" + e.getMessage());
            e.printStackTrace();
        }

    }
}