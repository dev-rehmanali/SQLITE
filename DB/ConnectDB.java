package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Rehmanali
 * This is connection class that creates a connection
 * with the database
 */
public class ConnectDB {

    private Connection conn;
    private static final String SQLITECONN = "jdbc:sqlite:C:/Users/rehma/Downloads/JavaCodebase/Personbook.db";


    public ConnectDB(){
        try {
            conn = DriverManager.getConnection(SQLITECONN);

        } catch (SQLException e) {
            System.out.println("0 " + e.getMessage());
        }

    }

    public Connection getConnection() {

        return this.conn;
    }

    @Override
    protected void finalize() throws Throwable
    {
        conn.close();
    }




}
