import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List; 

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.transformation.FilteredList;
import javafx.collections.ObservableList;

public class EtudiantController implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private DatePicker filtreDatePicker;
    @FXML private ComboBox<Etudiant.Parcours> parcoursCombo;
    @FXML private ComboBox<Etudiant.Promotion> promotionCombo;
    @FXML private Button enregistrerButton;
    @FXML private Button btnAjouter;
    @FXML private Button annulerButton;
    @FXML private Label messageLabel;
    @FXML private Label messageLabel1;
    @FXML private Button btnSupprimer;
    
    // Pagination
    @FXML private Button btnPrecedent;
    @FXML private Button btnSuivant;
    @FXML private Label pageLabel;
    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalEtudiants = 0;

    // TableView
    @FXML private TableView<Etudiant> tableView;
    @FXML private TableColumn<Etudiant, Integer> idTC;
    @FXML private TableColumn<Etudiant, String> nomTC;
    @FXML private TableColumn<Etudiant, String> prenomTC;
    @FXML private TableColumn<Etudiant, String> ddnTC;
    @FXML private TableColumn<Etudiant, Etudiant.Parcours> parcoursTC;
    @FXML private TableColumn<Etudiant, Etudiant.Promotion> promotionTC;
    @FXML private TableColumn<Etudiant, Void> modifierTC;
    
    //filtre 
    @FXML private TextField filtreNomField;
    @FXML private ComboBox<Etudiant.Parcours> filtreParcoursCombo;
    @FXML private ComboBox<Etudiant.Promotion> filtrePromotionCombo;
    private ObservableList<Etudiant> etudiantData;
    private FilteredList<Etudiant> filteredData;

    //private FilteredList<Etudiant> filteredData;


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
        
        etudiantData = FXCollections.observableArrayList(etudiantDAO.getAllEtudiants());
        filteredData = new FilteredList<>(etudiantData, e -> true);
        tableView.setItems(filteredData);
        filtreDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        //ObservableList<Etudiant> data = FXCollections.observableArrayList(etudiantDAO.getAllEtudiants());
        //filteredData = new FilteredList<>(data, e -> true);
        //tableView.setItems(filteredData);


        // Initialiser les ComboBox de filtre
        filtreParcoursCombo.getItems().setAll(Etudiant.Parcours.values());
        filtrePromotionCombo.getItems().setAll(Etudiant.Promotion.values());
        filtreNomField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filtreParcoursCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filtrePromotionCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
    

        //System.out.println("Table chargée avec " + tableView.getItems().size() + " étudiants.");
        setFormulaireActif(false); // désactive le formulaire au lancement
        ajouterBoutonModifier();
        
        //Charger page
        totalEtudiants = etudiantDAO.getNombreTotalEtudiants();
        chargerPage(1);

    }

    private void updateFilter() {
    String filtreTexte = filtreNomField.getText().toLowerCase().trim();
    Etudiant.Parcours parcoursFiltre = filtreParcoursCombo.getValue();
    Etudiant.Promotion promotionFiltre = filtrePromotionCombo.getValue();
    LocalDate dateFiltre = filtreDatePicker.getValue();

    filteredData.setPredicate(e -> {
        boolean nomMatch = e.getNom().toLowerCase().contains(filtreTexte)
                        || e.getPrenom().toLowerCase().contains(filtreTexte);
        boolean parcoursMatch = (parcoursFiltre == null || e.getParcours() == parcoursFiltre);
        boolean promotionMatch = (promotionFiltre == null || e.getPromotion() == promotionFiltre);
        boolean dateMatch = (dateFiltre == null || LocalDate.parse(e.getDateDeNaissance()).isEqual(dateFiltre));

        return nomMatch && parcoursMatch && promotionMatch && dateMatch;
    });
}

       

    @FXML
public void handleEnregistrer(ActionEvent event) {
    System.out.println(">>> handleEnregistrer() APPELÉ !");
    System.out.println("✔️ Bouton Enregistrer cliqué");
    System.out.println("👤 Mode courant : " + (etudiantCourant == null ? "Ajout" : "Modification"));

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
        // ✅ Ajout
        Etudiant nouvelEtudiant = new Etudiant(nom, prenom, dateNaissance.toString(), parcours, promotion);
        etudiantDAO.ajouterEtudiant(nouvelEtudiant);
        messageLabel.setText("✅ Étudiant ajouté !");
        messageLabel.setStyle("-fx-text-fill: green;");
    } else {
        // ✅ Modification
        System.out.println("🔧 MODIF → ID = " + etudiantCourant.getId());
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
    System.out.println("🔁 J'appelle la méthode manuellement...");
    rafraichirTable();
    tableView.getSelectionModel().clearSelection(); // désélectionne tout
setFormulaireActif(false); // désactive le formulaire comme après un ajout
}


@FXML
public void handleAnnuler(ActionEvent event) {
    viderFormulaire();              // Vide tous les champs
    setFormulaireActif(false);      // Grise le formulaire
    etudiantCourant = null;         // Annule le mode modification
    messageLabel.setText("❌ Modification annulée.");
    messageLabel.setStyle("-fx-text-fill: gray;");
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
    etudiantCourant = null;
    setFormulaireActif(true); // active le formulaire
}
@FXML
public void handleSupprimer(ActionEvent event) {
    Etudiant selection = tableView.getSelectionModel().getSelectedItem();

    if (selection == null) {
        System.out.println("messageLabel1 = " + messageLabel1);
        messageLabel1.setText("❌ Aucun étudiant sélectionné.");
        messageLabel1.setStyle("-fx-text-fill: red;");
        return;
    }

    etudiantDAO.supprimerEtudiant(selection.getId());
    rafraichirTable();

    //tableView.setItems(FXCollections.observableArrayList(etudiantDAO.getAllEtudiants()));
    messageLabel1.setText("🗑️ Étudiant supprimé !");
    messageLabel1.setStyle("-fx-text-fill: green;");
    viderFormulaire();
}
private void ajouterBoutonModifier() {
    modifierTC.setCellFactory(col -> new TableCell<Etudiant, Void>() {
        private final Button btn = new Button("Modifier");

        {
            btn.setOnAction(event -> {
                Etudiant e = getTableView().getItems().get(getIndex());
                remplirFormulaire(e);
                setFormulaireActif(true);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(btn);
            }
        }
    });
}
private void rafraichirTable() {
    System.out.println(">>> rafraichirTable() APPELÉ !");
    System.out.println("🌀 Début de rafraichirTable()");
    ObservableList<Etudiant> nouvelleListe = FXCollections.observableArrayList(etudiantDAO.getAllEtudiants());
    System.out.println("📦 Liste récupérée : " + nouvelleListe.size() + " étudiants");

    etudiantData.clear();
    etudiantData.addAll(nouvelleListe);
    tableView.refresh();
    System.out.println("✅ Table rafraîchie !");
}
    // Méthode pour charger une page spécifique 
    private void chargerPage(int page) {
    int offset = (page - 1) * pageSize;
    List<Etudiant> pageData = etudiantDAO.getEtudiantsParPage(pageSize, offset);
    etudiantData.setAll(pageData);
    tableView.refresh();
    currentPage = page;
    pageLabel.setText("Page " + currentPage);
    updatePaginationButtons();
}
    private void updatePaginationButtons() {
    btnPrecedent.setDisable(currentPage == 1);
    btnSuivant.setDisable(currentPage * pageSize >= totalEtudiants);
}
@FXML
public void handlePrecedent(ActionEvent event) {
    if (currentPage > 1) {
        chargerPage(currentPage - 1);
    }
}

@FXML
public void handleSuivant(ActionEvent event) {
    if ((currentPage * pageSize) < totalEtudiants) {
        chargerPage(currentPage + 1);
    }
}



}
