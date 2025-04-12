import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List; 
import java.util.ArrayList;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.transformation.FilteredList;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.CheckBoxTableCell;
import java.util.Stack;
import javafx.animation.PauseTransition;
import javafx.util.Duration;


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
    @FXML private Button btnUndo;

    // TableView
    @FXML private TableView<Etudiant> tableView;
    @FXML private TableColumn<Etudiant, Boolean> selectCol;
    @FXML private CheckBox selectAllCheckBox;
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
    
    private Stack<Action> historiqueActions = new Stack<>();

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
        
        // Colonne checkbox (liaison à la propriété selected)
        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setEditable(true);
        tableView.setEditable(true);
        
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
    System.out.println("✔️ Bouton Enregistrer cliqué");
    effacerMessages();

    // 1. Récupération des champs
    String nom = nomField.getText();
    String prenom = prenomField.getText();
    LocalDate dateNaissance = dateNaissancePicker.getValue();
    Etudiant.Parcours parcours = parcoursCombo.getValue();
    Etudiant.Promotion promotion = promotionCombo.getValue();

    // 2. Validation
    if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || parcours == null || promotion == null) {
        afficherMessageTemporaire(messageLabel,"❌ Veuillez remplir tous les champs.", "red");        return;
    }

    // 3. Mode AJOUT
    if (etudiantCourant == null) {
        Etudiant nouvelEtudiant = new Etudiant(nom, prenom, dateNaissance.toString(), parcours, promotion);
        

       nouvelEtudiant = etudiantDAO.ajouterEtudiant(nouvelEtudiant);
historiqueActions.push(new AjoutAction(etudiantDAO, nouvelEtudiant));

       afficherMessageTemporaire(messageLabel, "✅ Étudiant ajouté !", "green");
    }
    // 4. Mode MODIFICATION
    else {
        Etudiant ancien = new Etudiant(etudiantCourant); // copie avant modification

        // Modification en base
        etudiantCourant.setNom(nom);
        etudiantCourant.setPrenom(prenom);
        etudiantCourant.setDateDeNaissance(dateNaissance.toString());
        etudiantCourant.setParcours(parcours);
        etudiantCourant.setPromotion(promotion);

        etudiantDAO.modifierEtudiant(etudiantCourant);

        // 🔁 Historique Undo
        historiqueActions.push(new ModificationAction(etudiantDAO, ancien, etudiantCourant));

        messageLabel.setText("✅ Étudiant modifié !");
       afficherMessageTemporaire(messageLabel, "✅ Étudiant modifié !", "green");
    }

    // 5. Nettoyage final
    viderFormulaire();
    etudiantCourant = null;
    setFormulaireActif(false);
    rafraichirTable();
    tableView.getSelectionModel().clearSelection();
}


@FXML
public void handleAnnuler(ActionEvent event) {
    effacerMessages();
    viderFormulaire();              // Vide tous les champs
    setFormulaireActif(false);      // Grise le formulaire
    etudiantCourant = null;         // Annule le mode modification
 
    afficherMessageTemporaire(messageLabel, "❌ Modification annulée.", "gray");
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
        effacerMessages();
        viderFormulaire();
        etudiantCourant = null;
        setFormulaireActif(true); // active le formulaire
    }
    
    @FXML
public void handleSupprimer(ActionEvent event) {
    effacerMessages();
    var selectionnes = tableView.getItems().filtered(Etudiant::isSelected);

    if (selectionnes.isEmpty()) {
        afficherMessageTemporaire(messageLabel, "Aucun étudiant sélectionné.", "gray");
        return;
    }

    // 🔁 Ajouter à l'historique pour Undo
    historiqueActions.push(new SuppressionMultipleAction(etudiantDAO, new ArrayList<>(selectionnes)));

    // Suppression réelle
    for (Etudiant e : selectionnes) {
        etudiantDAO.supprimerEtudiant(e.getId());
    }

    // Feedback
    afficherMessageTemporaire(messageLabel1, "Étudiants supprimés !", "green");

    // Ces lignes DOIVENT être exécutées après suppression
    selectAllCheckBox.setSelected(false);
    rafraichirTable();
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
    // System.out.println(">>> rafraichirTable() APPELÉ !");
    // System.out.println("Début de rafraichirTable()");
    // ObservableList<Etudiant> nouvelleListe = FXCollections.observableArrayList(etudiantDAO.getAllEtudiants());
    // System.out.println("?Liste récupérée : " + nouvelleListe.size() + " étudiants");

    // etudiantData.clear();
    // etudiantData.addAll(nouvelleListe);
    // tableView.refresh();
    // System.out.println("Table rafraîchie !");
    
    System.out.println(">>> rafraichirTable() APPELÉ !");
    
    // 🔁 Met à jour le total
    totalEtudiants = etudiantDAO.getNombreTotalEtudiants();

    // 🔁 Recharge la page actuelle
    // Si on est à la page 4 mais qu’il ne reste plus que 3 pages après suppression, on recule d’une page
    int maxPages = (int) Math.ceil((double) totalEtudiants / pageSize);
    if (currentPage > maxPages && maxPages > 0) {
        currentPage = maxPages;
    } else if (maxPages == 0) {
        currentPage = 1;
    }

    chargerPage(currentPage);
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

@FXML
private void handleSelectAll(ActionEvent event) {
    boolean isSelected = selectAllCheckBox.isSelected();
    for (Etudiant e : tableView.getItems()) {
        e.setSelected(isSelected);
    }
}
@FXML
public void handleUndo(ActionEvent event) {
    effacerMessages();
    if (!historiqueActions.isEmpty()) {
        Action derniereAction = historiqueActions.pop(); // retire la dernière action
        derniereAction.undo();                            // annule cette action
        rafraichirTable();                                // met à jour la table
       
        afficherMessageTemporaire(messageLabel, "↩️ Action annulée !", "orange");
        
    } else {
        
         afficherMessageTemporaire(messageLabel, "Aucune action à annuler.", "gray");
    }
}
private void afficherMessageTemporaire(Label label, String message, String color) {
    label.setText(message);
    label.setStyle("-fx-text-fill: " + color + ";");
    label.setVisible(true);

    PauseTransition pause = new PauseTransition(Duration.seconds(3));
    pause.setOnFinished(e -> label.setVisible(false));
    pause.play();
}
private void effacerMessages() {
    messageLabel.setVisible(false);
    messageLabel1.setVisible(false);
}
}
