import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private Connection connection;
    
    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:etudiants.db");
               System.out.println("Connected to database");
            }
        }catch (SQLException e) {
             e.printStackTrace();
        }
        return connection;
    }
}