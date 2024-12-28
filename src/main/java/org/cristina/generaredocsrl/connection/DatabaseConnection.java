package org.cristina.generaredocsrl.connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String DBNAME = "test_person";
        String DRIVER = "org.postgresql.Driver";
        String DBURL = "jdbc:postgresql://localhost:5432/test_person";
        String USER = "cristina";
        String PASS = "adrianaC14";

        try {
            Class.forName(DRIVER);
            databaseLink = DriverManager.getConnection(DBURL,USER,PASS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return databaseLink;
    }
}

