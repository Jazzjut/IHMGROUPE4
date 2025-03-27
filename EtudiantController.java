import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.transformation.FilteredList;
import javafx.collections.ObservableList;

public class EtudiantController implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private ComboBox<Etudiant.Parcours> parcoursCombo;
    @FXML private ComboBox<Etudiant.Promotion> promotionCombo;
    @FXML private Button enregistrerButton;
    @FXML private Button btnAjouter;
    @FXML private Button annulerButton;
    @FXML private Label messageLabel;
    @FXML private Label messageLabel1;
    @FXML private Button btnSupprimer;

    // TableView
    @FXML private TableView<Etudiant> tableView;
    @FXML private TableColumn<Etudiant, Integer> idTC;
    @FXML private TableColumn<Etudiant, String> nomTC;
    @FXML private TableColumn<Etudiant, String> prenomTC;
    @FXML private TableColumn<Etudiant, String> ddnTC;
    @FXML private TableColumn<Etudiant, Etudiant.Parcours> parcoursTC;
    @FXML private TableColumn<Etudiant, Etudiant.Promotion> promotionTC;
    
    //filtre 
    @FXML private TextField filtreNomField;
    @FXML private ComboBox<Etudiant.Parcours> filtreParcoursCombo;
    @FXML private ComboBox<Etudiant.Promotion> filtrePromotionCombo;
    private FilteredList<Etudiant> filteredData;


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
        
        ObservableList<Etudiant> data = FXCollections.observableArrayList(etudiantDAO.getAllEtudiants());
        filteredData = new FilteredList<>(data, e -> true);
        tableView.setItems(filteredData);


        // Initialiser les ComboBox de filtre
        filtreParcoursCombo.getItems().setAll(Etudiant.Parcours.values());
        filtrePromotionCombo.getItems().setAll(Etudiant.Promotion.values());
        filtreNomField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filtreParcoursCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filtrePromotionCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
    

        //System.out.println("Table chargée avec " + tableView.getItems().size() + " étudiants.");
        setFormulaireActif(false); // désactive le formulaire au lancement
    }

    private void updateFilter() {
        String filtreTexte = filtreNomField.getText().toLowerCase().trim();
        Etudiant.Parcours parcoursFiltre = filtreParcoursCombo.getValue();
        Etudiant.Promotion promotionFiltre = filtrePromotionCombo.getValue();
    
        filteredData.setPredicate(e -> {
            boolean nomMatch = e.getNom().toLowerCase().contains(filtreTexte) || e.getPrenom().toLowerCase().contains(filtreTexte);
            boolean parcoursMatch = (parcoursFiltre == null || e.getParcours() == parcoursFiltre);
            boolean promotionMatch = (promotionFiltre == null || e.getPromotion() == promotionFiltre);
            return nomMatch && parcoursMatch && promotionMatch;
        });
    }
        

    @FXML
public void handleEnregistrer(ActionEvent event) {
    System.out.println("✔️ Bouton Enregistrer cliqué");

    String nom = nomField.getText();
    String prenom = prenomField.getText();
    LocalDate dateNaissance = dateNaissancePicker.getValue();
    Etudiant.Parcours parcours = parcoursCombo.getValue();
    Etudiant.Promotion promotion = promotionCombo.getValue();

    if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || parcours == null || promotion == null) {
        messageLabel.setText("❌ Veuillez remplir tous les champs.");
        messageLabel.setStyle("-fx-text-fill: red;");
        return;
    }

    if (etudiantCourant == null) {
        // Création
        Etudiant nouvelEtudiant = new Etudiant(nom, prenom, dateNaissance.toString(), parcours, promotion);
        etudiantDAO.ajouterEtudiant(nouvelEtudiant);
        messageLabel.setText("✅ Étudiant ajouté !");
        messageLabel.setStyle("-fx-text-fill: green;");
    } else {
        // Modification
        etudiantCourant.setNom(nom);
        etudiantCourant.setPrenom(prenom);
        etudiantCourant.setDateDeNaissance(dateNaissance.toString());
        etudiantCourant.setParcours(parcours);
        etudiantCourant.setPromotion(promotion);
        etudiantDAO.modifierEtudiant(etudiantCourant);
        messageLabel.setText("✅ Étudiant modifié !");
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    viderFormulaire();
    tableView.setItems(FXCollections.observableArrayList(etudiantDAO.getAllEtudiants()));
}

@FXML
    public void handleAnnuler(ActionEvent event) {
        viderFormulaire();
        setFormulaireActif(false);
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
    private void setFormulaireActif(boolean actif) {
    nomField.setDisable(!actif);
    prenomField.setDisable(!actif);
    dateNaissancePicker.setDisable(!actif);
    parcoursCombo.setDisable(!actif);
    promotionCombo.setDisable(!actif);
    enregistrerButton.setDisable(!actif);
    annulerButton.setDisable(!actif);
}
@FXML
public void handleAjouter(ActionEvent event) {
    viderFormulaire();
    setFormulaireActif(true); // active le formulaire
}
@FXML
public void handleSupprimer(ActionEvent event) {
    Etudiant selection = tableView.getSelectionModel().getSelectedItem();

    if (selection == null) {
        messageLabel1.setText("❌ Aucun étudiant sélectionné.");
        messageLabel1.setStyle("-fx-text-fill: red;");
        return;
    }

    etudiantDAO.supprimerEtudiant(selection.getId());

    tableView.setItems(FXCollections.observableArrayList(etudiantDAO.getAllEtudiants()));
    messageLabel1.setText("🗑️ Étudiant supprimé !");
    messageLabel1.setStyle("-fx-text-fill: green;");
    viderFormulaire();
}
}
