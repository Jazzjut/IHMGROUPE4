import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

/**
 * Contrôleur de la page d'accueil de l'application.
 * Permet de naviguer vers la gestion des étudiants, d'afficher l'aide ou de quitter l'application.
 * 
 * @author Kenza Berrada
 * @version 14/04/2025
 */
public class WelcomeController {

    
    @FXML private Button btnGererEtudiants; // Bouton pour accéder à la gestion des étudiants

    /**
     * Ouvre la fenêtre de gestion des étudiants.
     *
     * @param event événement déclenché par le clic sur le bouton
     */
    @FXML
    private void handleGererEtudiants(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/resources/main.fxml"));
            Stage stage = (Stage) btnGererEtudiants.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Étudiants");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Une erreur est survenue");
            alert.setContentText("Impossible d'accéder à la page demandée. Veuillez réessayer plus tard.");
            alert.showAndWait();
        }
    }

    /**
     * Ne fait rien car l'utilisateur est déjà sur la page d'accueil.
     *
     * @param event événement déclenché par le clic
     */
    @FXML
    private void handleAccueil(ActionEvent event) {
        // Aucune action nécessaire
    }

    /**
     * Affiche la fenêtre d'aide via la classe d'aide AideUtils.
     *
     * @param event événement déclenché par le clic
     */
    @FXML
    private void handleAide(ActionEvent event) {
        AideUtils.afficherAide();
    }

    /**
     * Ferme l'application proprement.
     *
     * @param event événement déclenché par le clic
     */
    @FXML
    private void handleQuitter(ActionEvent event) {
        System.exit(0); // fermeture de l'application
    }
}