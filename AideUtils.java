import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AideUtils {

    public static void afficherAide() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Centre d’aide");
        alert.setHeaderText("Questions fréquentes");

        String contenu =
            "❓ Comment ajouter un étudiant ?\n" +
            "➡️ Cliquez sur 'Ajouter', remplissez le formulaire, puis cliquez sur 'Soumettre'.\n\n" +
            "❓ Comment supprimer plusieurs étudiants ?\n" +
            "➡️ Cochez les étudiants, puis cliquez sur 'Supprimer'.\n\n" +
            "❓ Comment modifier un étudiant ?\n" +
            "➡️ Cliquez sur 'Modifier' à droite d’un étudiant.\n\n" +
            "❓ Comment réinitialiser les filtres ?\n" +
            "➡️ Cliquez sur 'Refresh'.\n";

        alert.setContentText(contenu);
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(450);

        // IMPORTANT : s'affiche au-dessus de tout sans changer la scène principale
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initModality(Modality.APPLICATION_MODAL); // bloquant jusqu'à fermeture

        alert.showAndWait();
    }
}
