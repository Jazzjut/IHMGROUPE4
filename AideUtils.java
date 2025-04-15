import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Classe utilitaire pour afficher une fenêtre d'aide (FAQ) à l'utilisateur.
 * Affiche un popup bloquant contenant les questions/réponses les plus fréquentes.
 * 
 * Cette classe est appelée via un item de menu.
 * 
 * @author Kenza
 * @version 14/04/2025
 */

public class AideUtils {
    
    /**
     * Affiche une boîte de dialogue contenant les questions fréquentes (FAQ).
     * La fenêtre est bloquante et reste au premier plan jusqu'à fermeture.
     */
    public static void afficherAide() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Centre d’aide");
        alert.setHeaderText("Questions fréquentes");

        String contenu =
            "? Comment ajouter un étudiant ?\n" +
            "➡️ Cliquez sur 'Ajouter', remplissez le formulaire, puis cliquez sur 'Soumettre'.\n\n" +
            "? Comment supprimer plusieurs étudiants ?\n" +
            "➡️ Cochez les étudiants, puis cliquez sur 'Supprimer'.\n\n" +
            "? Comment modifier un étudiant ?\n" +
            "➡️ Cliquez sur 'Modifier' à droite d’un étudiant.\n\n" +
            "? Comment réinitialiser les filtres ?\n" +
            "➡️ Cliquez sur 'Refresh'.\n";

        alert.setContentText(contenu);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(450);

        // s'affiche au-dessus de tout sans changer la scène principale
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initModality(Modality.APPLICATION_MODAL); 
        
        // Affichage et attente de fermeture
        alert.showAndWait();
    }
}
