import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private Button btnGererEtudiants; // ⚠️ Important : ce champ doit correspondre au fx:id dans le FXML

    @FXML
    private void handleGererEtudiants(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/resources/main.fxml"));
            Stage stage = (Stage) btnGererEtudiants.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Étudiants");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
private void handleAccueil(ActionEvent event) {
    // Rester sur la page actuelle : aucune action nécessaire
    System.out.println("Déjà sur la page d'accueil.");
}

@FXML
private void handleAide(ActionEvent event) {
    AideUtils.afficherAide(); // méthode existante dans ta classe utilitaire
}

@FXML
private void handleQuitter(ActionEvent event) {
    System.exit(0);
}

}

