import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;

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

    // TableView
    @FXML private TableView<Etudiant> tableView;
    @FXML private TableColumn<Etudiant, Integer> idTC;
    @FXML private TableColumn<Etudiant, String> nomTC;
    @FXML private TableColumn<Etudiant, String> prenomTC;
    @FXML private TableColumn<Etudiant, String> ddnTC;
    @FXML private TableColumn<Etudiant, Etudiant.Parcours> parcoursTC;
    @FXML private TableColumn<Etudiant, Etudiant.Promotion> promotionTC;

    private Etudiant etudiantCourant;
    private EtudiantDAO etudiantDAO = new EtudiantDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des ComboBox avec les enums
          System.out.println("parcoursCombo = " + parcoursCombo);
    System.out.println("promotionCombo = " + promotionCombo);
        parcoursCombo.getItems().setAll(Etudiant.Parcours.values());
        promotionCombo.getItems().setAll(Etudiant.Promotion.values());

        // Configuration du tableau
        idTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomTC.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomTC.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        ddnTC.setCellValueFactory(new PropertyValueFactory<>("dateDeNaissance"));
        parcoursTC.setCellValueFactory(new PropertyValueFactory<>("parcours"));
        promotionTC.setCellValueFactory(new PropertyValueFactory<>("promotion"));

        // Chargement des données
        tableView.getItems().addAll(etudiantDAO.getAllEtudiants());
        System.out.println("Table chargée avec " + tableView.getItems().size() + " étudiants.");
    }

    @FXML
    public void handleEnregistrer(ActionEvent event) {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        LocalDate dateNaissance = dateNaissancePicker.getValue();
        Etudiant.Parcours parcours = parcoursCombo.getValue();
        Etudiant.Promotion promotion = promotionCombo.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || parcours == null || promotion == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (etudiantCourant == null) {
            // Création
            Etudiant nouvelEtudiant = new Etudiant(nom, prenom, dateNaissance.toString(), parcours, promotion);
            etudiantDAO.ajouterEtudiant(nouvelEtudiant);
        } else {
            // Modification
            etudiantCourant.setNom(nom);
            etudiantCourant.setPrenom(prenom);
            etudiantCourant.setDateDeNaissance(dateNaissance.toString());
            etudiantCourant.setParcours(parcours);
            etudiantCourant.setPromotion(promotion);
            etudiantDAO.modifierEtudiant(etudiantCourant);
        }

        viderFormulaire();
        tableView.setItems(FXCollections.observableArrayList(etudiantDAO.getAllEtudiants()));
 // rafraîchit la table
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
