import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;

/**
 * Classe principale de l'application JavaFX.
 * Initialise l'interface graphique et établit la connexion à la base de données.
 * 
 * @author Jacinthe, Iman, Kenza
 * @version 26/03/2025
 */

public class MainApp extends Application {
    private Db db = new Db();   // Accès à la base de données SQLite

    @Override
    public void start(Stage stage) throws Exception {
        // Chargement du fichier FXML de la page d'accueil
        URL fxmlurl = (getClass().getResource("/resources/welcome.fxml"));
        if (fxmlurl == null) {
        return;
        // Si le fichier FXML est introuvable, arrêt de l'application
        }
        
        Parent root = FXMLLoader.load(fxmlurl);
        Scene scene = new Scene(root);
        
        // Chargement du fichier CSS de style
        URL cssURL = getClass().getResource("/css/style.css");
        if (cssURL != null) {
            scene.getStylesheets().add(cssURL.toExternalForm());
            //css appliqué
        }
        
        // Configuration de la fenêtre principale
        stage.setTitle("Bienvenue - Application de Gestion des Étudiants");
        stage.setScene(scene);
        stage.show();
        
        // Test de connexion à la base (facultatif en production)
        Connection conn = db.getConnection();
    }

    /**
     * Point d'entrée principal de l'application JavaFX.
     *
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
