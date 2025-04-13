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
import java.util.Comparator;


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
    
    @FXML private Label nomErrorLabel;
@FXML private Label prenomErrorLabel;
@FXML private Label dateErrorLabel;
@FXML private Label parcoursErrorLabel;
@FXML private Label promotionErrorLabel;
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
    
        tableView.setItems(filteredData);
      

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

    // ✅ MAJ directe de la table après filtre
    currentPage = 1; // Remettre à la première page pour éviter des erreurs de pagination
    rafraichirTable();
}

       

   @FXML
public void handleEnregistrer(ActionEvent event) {
    effacerMessages();
     if (!validerChamps()) return;

    String nom = nomField.getText();
    String prenom = prenomField.getText();
    LocalDate dateNaissance = dateNaissancePicker.getValue();
    Etudiant.Parcours parcours = parcoursCombo.getValue();
    Etudiant.Promotion promotion = promotionCombo.getValue();

    if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || parcours == null || promotion == null) {
        afficherMessageTemporaire(messageLabel, "❌ Veuillez remplir tous les champs obligatoires.", "red");
        return;
    }

    if (etudiantCourant == null) {
        Etudiant nouvelEtudiant = new Etudiant(nom, prenom, dateNaissance.toString(), parcours, promotion);
        nouvelEtudiant = etudiantDAO.ajouterEtudiant(nouvelEtudiant);
        historiqueActions.push(new AjoutAction(etudiantDAO, nouvelEtudiant));
        afficherMessageTemporaire(messageLabel, "✅ Étudiant enregistré avec succès.", "green");
    } else {
        Etudiant ancien = new Etudiant(etudiantCourant);
        etudiantCourant.setNom(nom);
        etudiantCourant.setPrenom(prenom);
        etudiantCourant.setDateDeNaissance(dateNaissance.toString());
        etudiantCourant.setParcours(parcours);
        etudiantCourant.setPromotion(promotion);
        etudiantDAO.modifierEtudiant(etudiantCourant);
        historiqueActions.push(new ModificationAction(etudiantDAO, ancien, etudiantCourant));
        afficherMessageTemporaire(messageLabel, "✅ Modifications enregistrées.", "green");
    }
    
   
    viderFormulaire();
    etudiantCourant = null;
    setFormulaireActif(false);
    rafraichirTable();
    tableView.getSelectionModel().clearSelection();
}


@FXML
public void handleAnnuler(ActionEvent event) {
    effacerMessages();

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Annulation");
    alert.setHeaderText(null);
    alert.setContentText("Voulez-vous vraiment annuler la modification en cours ?");
    if (alert.showAndWait().get() != ButtonType.OK) {
        afficherMessageTemporaire(messageLabel, "❌ Annulation interrompue.", "gray");
        return;
    }

    viderFormulaire();
    setFormulaireActif(false);
    etudiantCourant = null;
    afficherMessageTemporaire(messageLabel, "✅ Modification annulée.", "gray");
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
        afficherMessageTemporaire(messageLabel, "⚠️ Aucun étudiant sélectionné.", "gray");
        return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmation de suppression");
    alert.setHeaderText(null);
    alert.setContentText("Êtes-vous sûr de vouloir supprimer les étudiants sélectionnés ?");
    if (alert.showAndWait().get() != ButtonType.OK) {
        afficherMessageTemporaire(messageLabel, "❌ Suppression annulée.", "gray");
        return;
    }

    historiqueActions.push(new SuppressionMultipleAction(etudiantDAO, new ArrayList<>(selectionnes)));
    for (Etudiant e : selectionnes) {
        etudiantDAO.supprimerEtudiant(e.getId());
    }

    afficherMessageTemporaire(messageLabel1, "✅ Étudiants supprimés avec succès.", "green");
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
    // Récupérer tous les étudiants depuis la base
    List<Etudiant> all = etudiantDAO.getAllEtudiants();

    // Appliquer les filtres manuellement
    List<Etudiant> filtres = all.stream()
        .filter(e -> {
            String filtreTexte = filtreNomField.getText().toLowerCase().trim();
            Etudiant.Parcours parcoursFiltre = filtreParcoursCombo.getValue();
            Etudiant.Promotion promotionFiltre = filtrePromotionCombo.getValue();
            LocalDate dateFiltre = filtreDatePicker.getValue();

            boolean nomMatch = e.getNom().toLowerCase().contains(filtreTexte)
                            || e.getPrenom().toLowerCase().contains(filtreTexte);
            boolean parcoursMatch = (parcoursFiltre == null || e.getParcours() == parcoursFiltre);
            boolean promotionMatch = (promotionFiltre == null || e.getPromotion() == promotionFiltre);
            boolean dateMatch = (dateFiltre == null || LocalDate.parse(e.getDateDeNaissance()).isEqual(dateFiltre));

            return nomMatch && parcoursMatch && promotionMatch && dateMatch;
        })
        .sorted(
            Comparator.comparing(Etudiant::getNom)
                      .thenComparing(Etudiant::getPrenom)
        )
        .toList();

    // Pagination
    totalEtudiants = filtres.size();
    int maxPages = Math.max(1, (int) Math.ceil((double) totalEtudiants / pageSize));
    if (currentPage > maxPages) currentPage = maxPages;

    List<Etudiant> page = getPageFromList(filtres, currentPage, pageSize);
    etudiantData.setAll(page);
    tableView.setItems(etudiantData);

    pageLabel.setText("Page " + currentPage + " / " + maxPages);
    updatePaginationButtons();
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
        currentPage--;
        rafraichirTable();
    }
}

@FXML
public void handleSuivant(ActionEvent event) {
    currentPage++;
    rafraichirTable();
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
@FXML
public void handleResetFiltre(ActionEvent event) {
    effacerMessages();

    filtreNomField.clear();
    filtreParcoursCombo.getSelectionModel().clearSelection();
    filtrePromotionCombo.getSelectionModel().clearSelection();
    filtreDatePicker.setValue(null);

    // Cela forcera la mise à jour du filtre (Predicate)
    updateFilter();

    afficherMessageTemporaire(messageLabel, "🔄 Filtres réinitialisés.", "blue");
}
private void effacerMessages() {
    messageLabel.setVisible(false);
    messageLabel1.setVisible(false);
    
}

private boolean isFiltrageActif() {
    return !filtreNomField.getText().trim().isEmpty()
        || filtreParcoursCombo.getValue() != null
        || filtrePromotionCombo.getValue() != null
        || filtreDatePicker.getValue() != null;
}

private List<Etudiant> getPageFromList(List<Etudiant> list, int page, int pageSize) {
    int fromIndex = Math.min((page - 1) * pageSize, list.size());
    int toIndex = Math.min(fromIndex + pageSize, list.size());
    return list.subList(fromIndex, toIndex);
}
private boolean validerChamps() {
    boolean valide = true;

    nomErrorLabel.setVisible(false);
    prenomErrorLabel.setVisible(false);
    dateErrorLabel.setVisible(false);
    parcoursErrorLabel.setVisible(false);
    promotionErrorLabel.setVisible(false);

    String nom = nomField.getText().trim();
    String prenom = prenomField.getText().trim();
    LocalDate dateNaissance = dateNaissancePicker.getValue();
    Etudiant.Parcours parcours = parcoursCombo.getValue();
    Etudiant.Promotion promotion = promotionCombo.getValue();

    if (!nom.matches("[a-zA-ZÀ-ÿ\\s\\-']{2,}")) {
        nomErrorLabel.setText("❌ Le nom doit contenir uniquement des lettres.");
        nomErrorLabel.setVisible(true);
        valide = false;
    }

    if (!prenom.matches("[a-zA-ZÀ-ÿ\\s\\-']{2,}")) {
        prenomErrorLabel.setText("❌ Le prénom doit contenir uniquement des lettres.");
        prenomErrorLabel.setVisible(true);
        valide = false;
    }

    if (dateNaissance == null) {
        dateErrorLabel.setText("❌ La date de naissance est obligatoire.");
        dateErrorLabel.setVisible(true);
        valide = false;
    } else if (dateNaissance.isAfter(LocalDate.now())) {
        dateErrorLabel.setText("❌ La date ne peut pas être dans le futur.");
        dateErrorLabel.setVisible(true);
        valide = false;
    } else if (LocalDate.now().getYear() - dateNaissance.getYear() < 16) {
        dateErrorLabel.setText("❌ L'étudiant doit avoir au moins 16 ans.");
        dateErrorLabel.setVisible(true);
        valide = false;
    }

    if (parcours == null) {
        parcoursErrorLabel.setText("❌ Le parcours est requis.");
        parcoursErrorLabel.setVisible(true);
        valide = false;
    }

    if (promotion == null) {
        promotionErrorLabel.setText("❌ La promotion est requise.");
        promotionErrorLabel.setVisible(true);
        valide = false;
    }

    return valide;
}
}
