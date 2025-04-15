/**
 * Classe responsable de la gestion de la connexion à la base de données SQLite.
 * Elle ouvre une connexion unique à la base locale "etudiants.db" et la réutilise.
 * 
 * Version : 1.0
 * Date : 2025-03-25
 * Auteur : Jacinthe
 * 
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private Connection connection;
    
    /**
     * Retourne une connexion active à la base de données SQLite.
     * Si la connexion est déjà ouverte, elle est réutilisée.
     *
     * @return Connection à la base ou null si échec.
     */
    public Connection getConnection() {
        try {
            // Créer une nouvelle connexion si aucune n'est ouverte
            if(connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:etudiants.db");
                // Connexion établie
            }
        }catch (SQLException e) {
             // Connexion échouée
             e.printStackTrace();
        }
        return connection;
    }
}