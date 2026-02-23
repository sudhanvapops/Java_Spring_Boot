package com.orm;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static DbConfig loadConfig(){

        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        return new DbConfig(url, user, dbPassword);
    }

    public static void main(String[] args) {

        DbConfig db = loadConfig();
        System.out.println(db.getUrl());

        Student s1 = new Student();

        s1.setName("Sudhanva S");
        s1.setRollNo(1);
        s1.setsAge(21);


        System.out.println(s1);

    }
}