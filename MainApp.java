import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;

public class MainApp extends Application {
    private Db db = new Db();
    @Override
    public void start(Stage stage) throws Exception {
        URL fxmlurl = (getClass().getResource("/resources/main.fxml"));
            if (fxmlurl == null) {
            System.out.println("ERREUR : fichier introuvable fxml");
            return;
            }
        Parent root = FXMLLoader.load(fxmlurl);
        Scene scene = new Scene(root);
        stage.setTitle("Gestion des Étudiants");
        stage.setScene(scene);
        stage.show();
        
        Connection conn = db.getConnection();
        if (conn != null) {
            System.out.println("Connexion réussie !");
        } else {
            System.out.println("ERREUR : Connexion à la base de données échouée.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
