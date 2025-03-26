import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;


import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EtudiantController implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private ComboBox<Etudiant.Parcours> parcoursCombo;
    @FXML private ComboBox<Etudiant.Promotion> promotionCombo;
    @FXML private Button enregistrerButton;
    @FXML private Button annulerButton;

    // Étudiant courant (null si ajout, non null si modification)
    private Etudiant etudiantCourant;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les ComboBox avec les valeurs de l'enum
        parcoursCombo.getItems().setAll(Etudiant.Parcours.values());
        promotionCombo.getItems().setAll(Etudiant.Promotion.values());
    }
    
    /**
     * Méthode déclenchée lorsqu'on clique sur le bouton "Enregistrer".
     * Elle récupère les données du formulaire, les valide,
     * puis crée un nouvel étudiant ou modifie l'étudiant existant.
     */
    @FXML
    public void handleEnregistrer(ActionEvent event) {
        // Récupération des valeurs du formulaire
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        LocalDate dateNaissance = dateNaissancePicker.getValue();
        Etudiant.Parcours parcours = parcoursCombo.getValue();
        Etudiant.Promotion promotion = promotionCombo.getValue();

        // Validation simple
        if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || parcours == null || promotion == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // Création ou mise à jour d'un étudiant
        if (etudiantCourant == null) {
            // Ajout (id non défini)
            Etudiant nouvelEtudiant = new Etudiant(nom, prenom, dateNaissance.toString(), parcours, promotion);
            // Tu pourras ensuite appeler EtudiantDAO.insert(nouvelEtudiant)
        } else {
            // Modification
            etudiantCourant.setNom(nom);
            etudiantCourant.setPrenom(prenom);
            etudiantCourant.setDateDeNaissance(dateNaissance.toString());
            etudiantCourant.setParcours(parcours);
            etudiantCourant.setPromotion(promotion);
            // Tu pourras ensuite appeler EtudiantDAO.update(etudiantCourant)
        }

        viderFormulaire();
    }

    @FXML
    public void handleAnnuler(ActionEvent event) {
        viderFormulaire();
    }

    public void remplirFormulaire(Etudiant e) {
        this.etudiantCourant = e;
        nomField.setText(e.getNom());
        prenomField.setText(e.getPrenom());
        dateNaissancePicker.setValue(LocalDate.parse(e.getDateDeNaissance()));
        parcoursCombo.setValue(e.getParcours());
        promotionCombo.setValue(e.getPromotion());
    }

    public void viderFormulaire() {
        etudiantCourant = null;
        nomField.clear();
        prenomField.clear();
        dateNaissancePicker.setValue(null);
        parcoursCombo.getSelectionModel().clearSelection();
        promotionCombo.getSelectionModel().clearSelection();
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
