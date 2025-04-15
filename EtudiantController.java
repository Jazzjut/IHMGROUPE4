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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Contrôleur principal gérant l’interface de gestion des étudiants :
 * affichage, filtres, ajout, modification, suppression, pagination, annulation, aide.
 * 
 * Ce contrôleur est lié au fichier FXML de la vue principale.
 * 
 * @author Iman, Kenza, Jacinthe 
 * @version 15/04/2025
 */
public class EtudiantController implements Initializable {

    // Champs du formulaire d'ajout/modification et bouton ajout/supp/undo
    @FXML private TextField nomField, prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private ComboBox<Etudiant.Parcours> parcoursCombo;
    @FXML private ComboBox<Etudiant.Promotion> promotionCombo;
    @FXML private Button enregistrerButton, btnAjouter, annulerButton, btnUndo, btnSupprimer;;
    @FXML private Label messageLabel;
    @FXML private Label messageLabel1;
    @FXML private Label messageLabel2;
    @FXML private Label nomErrorLabel;
    @FXML private Label prenomErrorLabel;
    @FXML private Label dateErrorLabel;
    @FXML private Label parcoursErrorLabel;
    @FXML private Label promotionErrorLabel;
    
    // Champ pour les filtres 
    @FXML private DatePicker filtreDatePicker;
    @FXML private TextField filtreNomField;
    @FXML private ComboBox<Etudiant.Parcours> filtreParcoursCombo;
    @FXML private ComboBox<Etudiant.Promotion> filtrePromotionCombo;
    
    //TableView + pagination
    @FXML private TableView<Etudiant> tableView;
    @FXML private CheckBox selectAllCheckBox;
    @FXML private TableColumn<Etudiant, Boolean> selectCol;
    @FXML private TableColumn<Etudiant, Integer> idTC;
    @FXML private TableColumn<Etudiant, String> nomTC;
    @FXML private TableColumn<Etudiant, String> prenomTC;
    @FXML private TableColumn<Etudiant, String> ddnTC;
    @FXML private TableColumn<Etudiant, Etudiant.Parcours> parcoursTC;
    @FXML private TableColumn<Etudiant, Etudiant.Promotion> promotionTC;
    @FXML private TableColumn<Etudiant, Void> modifierTC;
    
    //pagination
    @FXML private Button btnPrecedent, btnSuivant;
    @FXML private Label pageLabel;
    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalEtudiants = 0;

    //Données
    private ObservableList<Etudiant> etudiantData;
    private FilteredList<Etudiant> filteredData;
    private Etudiant etudiantCourant;
    private EtudiantDAO etudiantDAO = new EtudiantDAO();
    private Stack<Action> historiqueActions = new Stack<>();

    /**
     * Initialise les composants de l’interface au lancement du contrôleur.
     * 
     * Cette méthode configure :
     *  - Les listes déroulantes (ComboBox) avec les valeurs des énumérations {@code Parcours} et {@code Promotion}
     *  - Les colonnes de la {@code TableView} avec les propriétés des objets {@code Etudiant}
     *  - La colonne de sélection avec des cases à cocher
     *  - Les filtres : champ texte, date, parcours, promotion
     *  - Les écouteurs qui déclenchent le filtrage dynamique
     *  - Le formulaire (désactivé par défaut)
     *  - L'ajout dynamique du bouton "Modifier" dans chaque ligne du tableau
     * 
     * @param location L'URL utilisée pour résoudre les chemins relatifs.
     * @param resources Le bundle de ressources localisées.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des ComboBox avec les valeurs d'enum
        parcoursCombo.getItems().setAll(Etudiant.Parcours.values());
        promotionCombo.getItems().setAll(Etudiant.Promotion.values());

        // Lier les colonnes de la TableView aux propriétés de l'objet Etudiant
        idTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomTC.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomTC.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        ddnTC.setCellValueFactory(new PropertyValueFactory<>("dateDeNaissance"));
        parcoursTC.setCellValueFactory(new PropertyValueFactory<>("parcours"));
        promotionTC.setCellValueFactory(new PropertyValueFactory<>("promotion"));
        
        // Configuration de la colonne checkbox (liaison à la propriété selected)
        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setEditable(true);
        tableView.setEditable(true);
        
        // Chargement initial des données
        etudiantData = FXCollections.observableArrayList(etudiantDAO.getAllEtudiants());
        filteredData = new FilteredList<>(etudiantData, e -> true);
       
        //Initialisation des filtres
        //getItem().setAll => remplit les combobox avec les parcours et promotions possibles 
        //addlistener => execute updateFilter dès utilisateur change un filtre
        //updateFilter => applique tous les filtres en même temps 
        filtreDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filtreParcoursCombo.getItems().setAll(Etudiant.Parcours.values());
        filtrePromotionCombo.getItems().setAll(Etudiant.Promotion.values());
        filtreNomField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filtreParcoursCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filtrePromotionCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
    
        tableView.setItems(filteredData);
      

        // Désactiver le formulaire au démarrage
        setFormulaireActif(false);
        
        // Ajouter les boutons "Modifier" dans la TableView
        ajouterBoutonModifier();
        
        // Mise à jour initiale de la table
        rafraichirTable();

    }

    
    /**
     * Met à jour de la liste filtrée des étudiants affichés 
     * dans la table.
     *
     * Cette méthode applique un prédicat de filtre combinant :
     * - Le champ de recherche (nom ou prénom partiel, insensible à la casse)
     * - Le parcours sélectionné (ou tous si aucun sélectionné)
     * - La promotion sélectionnée (ou toutes si aucune sélectionnée)
     * - La date de naissance exacte (ou toutes si aucune date choisie)
     *
     * Une fois les filtres appliqués, elle recharge la table à la page 1.
     */
        private void updateFilter() {
        // Récupère les valeurs de filtres
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
    
        //MAJ directe de la table après filtre
        currentPage = 1; // Remettre à la première page pour éviter des erreurs de pagination
        rafraichirTable();
    }

       

    @FXML
    /**
     * Gère l'enregistrement d'un nouvel étudiant ou la modification d'un étudiant existant.
     * - Vérifie la validité des champs et affiche des messages d'erreur si nécessaire.
     * - Ajoute un nouvel étudiant s'il s'agit d'une création.
     * - Modifie l'étudiant sélectionné s'il s'agit d'une édition.
     * - Enregistre l'action dans l’historique pour permettre l’annulation.
     * - Rafraîchit la table, vide le formulaire et désactive le mode édition.
     *
     * @param event L'événement généré par le clic sur le bouton "Enregistrer".
     */
        public void handleEnregistrer(ActionEvent event) {
        effacerMessages(); // Cache les anciens messages d'erreur
        if (!validerChamps()) return; // Arrête si les champs ne sont pas valides
    
        // Récupération des valeurs saisies
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        LocalDate dateNaissance = dateNaissancePicker.getValue();
        Etudiant.Parcours parcours = parcoursCombo.getValue();
        Etudiant.Promotion promotion = promotionCombo.getValue();
    
        // Vérifie que tous les champs sont bien remplis
        if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || parcours == null || promotion == null) {
            afficherMessageTemporaire(messageLabel, "Veuillez remplir tous les champs obligatoires.", "red");
            return;
        }
    
        // Cas 1 : ajout d’un nouvel étudiant
        if (etudiantCourant == null) {
            Etudiant nouvelEtudiant = new Etudiant(nom, prenom, dateNaissance.toString(), parcours, promotion);
            nouvelEtudiant = etudiantDAO.ajouterEtudiant(nouvelEtudiant);
            historiqueActions.push(new AjoutAction(etudiantDAO, nouvelEtudiant));
            afficherMessageTemporaire(messageLabel, "Étudiant enregistré avec succès.", "green");
        } else {     // Cas 2 : modification d’un étudiant existant
            Etudiant ancien = new Etudiant(etudiantCourant); // copie pour annulation
            etudiantCourant.setNom(nom);
            etudiantCourant.setPrenom(prenom);
            etudiantCourant.setDateDeNaissance(dateNaissance.toString());
            etudiantCourant.setParcours(parcours);
            etudiantCourant.setPromotion(promotion);
            etudiantDAO.modifierEtudiant(etudiantCourant);
            historiqueActions.push(new ModificationAction(etudiantDAO, ancien, etudiantCourant));
            afficherMessageTemporaire(messageLabel, "Modifications enregistrées.", "green");
        }
        
        // Réinitialisation de l’état
        viderFormulaire();
        etudiantCourant = null;
        setFormulaireActif(false);
        rafraichirTable();
        tableView.getSelectionModel().clearSelection();
    }


    @FXML
    /**
     * Gère l'action d'annulation d'une modification en cours.
     *
     * Affiche une boîte de confirmation à l'utilisateur.
     * Si l'utilisateur confirme :
     * - Le formulaire est vidé
     * - Le mode édition est désactivé
     * - L'étudiant courant est réinitialisé
     * 
     * Si l'utilisateur annule, un message d'interruption est affiché.
     *
     * @param event L'événement déclenché par le clic sur le bouton "Annuler".
     */
    public void handleAnnuler(ActionEvent event) {
        effacerMessages(); // Cache les anciens messages
    
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Annulation");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment annuler la modification en cours ?");
        
        // Si l'utilisateur annule la confirmation, ne rien faire
        if (alert.showAndWait().get() != ButtonType.OK) {
            afficherMessageTemporaire(messageLabel, "Annulation interrompue.", "gray");
            return;
        }
    
        // Si l'utilisateur confirme : réinitialisation de l'état
        viderFormulaire();
        setFormulaireActif(false);
        etudiantCourant = null;
        
        afficherMessageTemporaire(messageLabel, "Modification annulée.", "gray");
    }

    /**
     * Remplit le formulaire de saisie avec les informations d'un étudiant donné.
     * 
     * Cette méthode est appelée lorsqu'on souhaite modifier un étudiant existant.
     * Elle initialise tous les champs du formulaire avec les données de l'étudiant sélectionné.
     *
     * @param e L'étudiant dont les données doivent être affichées dans le formulaire.
     */
    public void remplirFormulaire(Etudiant e) {
        this.etudiantCourant = e; // On garde une référence à l'étudiant en cours de modification
        
        // Remplissage des champs du formulaire avec les données de l'étudiant
        nomField.setText(e.getNom());
        prenomField.setText(e.getPrenom());
        dateNaissancePicker.setValue(LocalDate.parse(e.getDateDeNaissance()));
        parcoursCombo.setValue(e.getParcours());
        promotionCombo.setValue(e.getPromotion());
    }

    /**
     * Vide tous les champs du formulaire et réinitialise l'étudiant courant.
     * 
     * Cette méthode est utilisée après un enregistrement, une annulation ou une suppression,
     * afin de réinitialiser complètement le formulaire à un état vide.
     */
    public void viderFormulaire() {
        etudiantCourant = null; // Plus aucun étudiant en cours d'édition
        
        // Réinitialiser tous les champs de saisie
        nomField.clear();
        prenomField.clear();
        dateNaissancePicker.setValue(null);
        parcoursCombo.getSelectionModel().clearSelection();
        promotionCombo.getSelectionModel().clearSelection();
    }

    /**
     * Affiche une alerte de type WARNING avec un titre et un message personnalisés.
     * 
     * Cette boîte de dialogue est bloquante jusqu'à ce que l'utilisateur la ferme.
     *
     * @param titre   Le titre de la fenêtre d'alerte.
     * @param message Le message à afficher dans le corps de l'alerte.
     */
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING); // Création d'une alerte de type WARNING
        alert.setTitle(titre); // Titre personnalisé
        alert.setHeaderText(null); // Pas de sous-titre
        alert.setContentText(message); // Corps du message affiché à l'utilisateur
        alert.showAndWait(); // Affichage et attente de confirmation
    }
    
    /**
     * Active ou désactive l'ensemble des champs et boutons du formulaire d'édition.
     * 
     * Cette méthode permet de bloquer ou débloquer le formulaire selon le contexte :
     * - Lorsqu'on clique sur "Ajouter" → actif = true
     * - Lorsqu'on annule ou termine une action → actif = false
     *
     * @param actif true pour activer le formulaire, false pour le désactiver.
     */
    private void setFormulaireActif(boolean actif) {
        // Active ou désactive les champs en fonction du paramètre
        nomField.setDisable(!actif);
        prenomField.setDisable(!actif);
        dateNaissancePicker.setDisable(!actif);
        parcoursCombo.setDisable(!actif);
        promotionCombo.setDisable(!actif);
        // Active ou désactive les boutons liés à l'édition
        enregistrerButton.setDisable(!actif);
        annulerButton.setDisable(!actif);
    }

    @FXML
    /**
     * Gère le clic sur le bouton "Ajouter".
     * 
     * Cette méthode prépare le formulaire pour l'ajout d'un nouvel étudiant :
     * - Efface les anciens messages
     * - Vide les champs du formulaire
     * - Réinitialise l'étudiant courant
     * - Active le formulaire pour la saisie
     *
     * @param event L'événement déclenché par le clic sur le bouton "Ajouter".
     */
    public void handleAjouter(ActionEvent event) {
        effacerMessages();
        viderFormulaire();
        etudiantCourant = null; // On passe en mode "ajout" (pas de sélection)
        setFormulaireActif(true); // active le formulaire -> saisie
    }
    
    @FXML
    /**
     * Gère la suppression d’un ou plusieurs étudiants sélectionnés dans la table.
     *
     *   - Affiche un message si aucun étudiant n’est sélectionné
     *   - Demande confirmation avant la suppression
     *   - Supprime chaque étudiant sélectionné de la base
     *   - Ajoute l’action à l’historique pour permettre l’annulation
     *   - Rafraîchit la table et réinitialise le formulaire
     *
     * @param event L’événement déclenché par le clic sur le bouton "Supprimer".
     */
    public void handleSupprimer(ActionEvent event) {
        effacerMessages();
        
        // Récupère les étudiants cochés dans la table
        var selectionnes = tableView.getItems().filtered(Etudiant::isSelected);
    
        // Si aucun étudiant sélectionné, afficher un message d'avertissement
        if (selectionnes.isEmpty()) {
            afficherMessageTemporaire(messageLabel1, "Aucun étudiant sélectionné.", "gray");
            return;
        }
    
        // Demande de confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer les étudiants sélectionnés ?");
        if (alert.showAndWait().get() != ButtonType.OK) {
            afficherMessageTemporaire(messageLabel2, "Suppression annulée.", "gray");
            return;
        }
    
        // Enregistre l'action dans l'historique pour un éventuel undo
        historiqueActions.push(new SuppressionMultipleAction(etudiantDAO, new ArrayList<>(selectionnes)));
        
        // Supprime les étudiants sélectionnés
        for (Etudiant e : selectionnes) {
            etudiantDAO.supprimerEtudiant(e.getId());
        }
    
        // Mise à jour de l’interface
        afficherMessageTemporaire(messageLabel1, "Étudiants supprimés avec succès.", "green");
        selectAllCheckBox.setSelected(false); // Décoche "tout sélectionner"
        rafraichirTable(); // Recharge les données
        viderFormulaire();
    }
    
    /**
     * Ajoute un bouton "Modifier" dans chaque ligne de la colonne {@code modifierTC} de la table.
     *
     * Lorsque l'utilisateur clique sur ce bouton :
     *   - Les données de l'étudiant correspondant sont chargées dans le formulaire
     *   - Le formulaire est activé pour permettre la modification
     *
     * Cette méthode utilise une {@code TableCell} personnalisée pour insérer un bouton dans chaque ligne.
     */
    private void ajouterBoutonModifier() {
        modifierTC.setCellFactory(col -> new TableCell<Etudiant, Void>() {
            private final Button btn = new Button("Modifier");// Bouton affiché dans chaque ligne
    
            {
                // Action déclenchée lorsqu'on clique sur le bouton "Modifier"
                btn.setOnAction(event -> {
                    Etudiant e = getTableView().getItems().get(getIndex());
                    remplirFormulaire(e); // Remplit les champs avec les données de l'étudiant
                    setFormulaireActif(true); // Active le formulaire pour édition
                });
            }
    
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                // Si la ligne est vide, ne rien afficher
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn); // Affiche le bouton dans la cellule
                }
            }
        });
    }
        
    /**
     * Rafraîchit le contenu de la table en appliquant manuellement les filtres et la pagination.
     * 
     * Étapes :
     * - Récupération de tous les étudiants depuis la base
     * - Filtrage selon les critères actifs (nom, parcours, promotion, date)
     * - Tri alphabétique (nom puis prénom)
     * - Application de la pagination
     * - Affichage des résultats dans la TableView
     * 
     */
    private void rafraichirTable() {
        // 1. Charger tous les étudiants depuis la base
        List<Etudiant> all = etudiantDAO.getAllEtudiants();
    
        // 2. Appliquer les filtres sur la liste complète
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
    
        // 3. Calcul du nombre de pages et ajustement de la page courante si nécessaire
        totalEtudiants = filtres.size();
        int maxPages = Math.max(1, (int) Math.ceil((double) totalEtudiants / pageSize));
        if (currentPage > maxPages) currentPage = maxPages;
    
        // 4. Récupération de la sous-liste correspondant à la page en cours
        List<Etudiant> page = getPageFromList(filtres, currentPage, pageSize);
        
        // 5. Mise à jour des données affichées
        etudiantData.setAll(page);
        tableView.setItems(etudiantData);
    
        // 6. Mise à jour de l'indicateur de page
        pageLabel.setText("Page " + currentPage + " / " + maxPages);
        updatePaginationButtons();
    }
    
    /**
     * Charge et affiche une page spécifique d'étudiants depuis la base de données.
     * 
     * @param page Numéro de page à afficher (1-indexé).
     */
    private void chargerPage(int page) {
        int offset = (page - 1) * pageSize;// Calcule l’index de départ
        List<Etudiant> pageData = etudiantDAO.getEtudiantsParPage(pageSize, offset);// Récupère la sous-liste
        etudiantData.setAll(pageData); // Remplace les données visibles
        tableView.refresh();
        currentPage = page;
        pageLabel.setText("Page " + currentPage);
        updatePaginationButtons(); // Active/désactive les flèches
    }
    
    /**
     * Active ou désactive les boutons de navigation selon la page actuelle.
     */
    private void updatePaginationButtons() {
        btnPrecedent.setDisable(currentPage == 1);
        btnSuivant.setDisable(currentPage * pageSize >= totalEtudiants);
    }

    /**
     * Passe à la page précédente dans la pagination.
     */
    @FXML
    public void handlePrecedent(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            rafraichirTable();
        }
    }

    /**
     * Passe à la page suivante dans la pagination.
     */
    @FXML
    public void handleSuivant(ActionEvent event) {
        currentPage++;
        rafraichirTable();
    }

    /**
     * Coche ou décoche tous les étudiants visibles dans la table.
     */
    @FXML
    private void handleSelectAll(ActionEvent event) {
        boolean isSelected = selectAllCheckBox.isSelected();
        for (Etudiant e : tableView.getItems()) {
            e.setSelected(isSelected);
        }
    }
    
    /**
     * Annule la dernière action effectuée (ajout, suppression, modification).
     */
    @FXML
    public void handleUndo(ActionEvent event) {
        effacerMessages();
        if (!historiqueActions.isEmpty()) {
            Action derniereAction = historiqueActions.pop(); 
            derniereAction.undo();                             // Exécute l'annulation
            rafraichirTable();                                // met à jour la table
           
            afficherMessageTemporaire(messageLabel2, "Action annulée !", "orange");
            
        } else {
            
             afficherMessageTemporaire(messageLabel2, "Aucune action à annuler.", "gray");
        }
    }
    
    /**
     * Affiche un message temporaire coloré pendant 3 secondes dans un Label.
     *
     * @param label   Le label à remplir
     * @param message Le texte à afficher
     * @param color   La couleur (ex: "red", "green", "gray", etc.)
     */
    private void afficherMessageTemporaire(Label label, String message, String color) {
        label.setText(message);
        label.setStyle("-fx-text-fill: " + color + ";");
        label.setVisible(true);
    
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> label.setVisible(false));
        pause.play();
        
    }
    
    /**
     * Réinitialise tous les filtres de recherche et recharge les données.
     */
    @FXML
    public void handleResetFiltre(ActionEvent event) {
        effacerMessages();
    
        filtreNomField.clear();
        filtreParcoursCombo.getSelectionModel().clearSelection();
        filtrePromotionCombo.getSelectionModel().clearSelection();
        filtreDatePicker.setValue(null);
    
        //Recharge après réinitialisation
        updateFilter();
    
        afficherMessageTemporaire(messageLabel1, "Filtres réinitialisés.", "blue");
    }
    
    /**
     * Masque tous les messages temporaires visibles.
     */
    private void effacerMessages() {
        messageLabel.setVisible(false);
        messageLabel1.setVisible(false);
        messageLabel2.setVisible(false);
        
        nomErrorLabel.setVisible(false);
        prenomErrorLabel.setVisible(false);
        dateErrorLabel.setVisible(false);
        parcoursErrorLabel.setVisible(false);
        promotionErrorLabel.setVisible(false);
        
        nomField.setStyle(null);
        prenomField.setStyle(null);
        dateNaissancePicker.setStyle(null);
        parcoursCombo.setStyle(null);
        promotionCombo.setStyle(null);
    }

    /**
     * Vérifie si au moins un filtre est actif (nom, parcours, promotion ou date).
     *
     * @return true si un filtre est actif, false sinon
     */
    private boolean isFiltrageActif() {
        return !filtreNomField.getText().trim().isEmpty()
            || filtreParcoursCombo.getValue() != null
            || filtrePromotionCombo.getValue() != null
            || filtreDatePicker.getValue() != null;
    }

    /**
     * Extrait une sous-liste correspondant à une page donnée.
     *
     * @param list      Liste complète à paginer
     * @param page      Numéro de page (1-indexé)
     * @param pageSize  Nombre d'éléments par page
     * @return Sous-liste correspondant à la page demandée
     */
    private List<Etudiant> getPageFromList(List<Etudiant> list, int page, int pageSize) {
        int fromIndex = Math.min((page - 1) * pageSize, list.size());
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return list.subList(fromIndex, toIndex);
    }
    
    /**
     * Valide tous les champs du formulaire de saisie.
     *
     * Vérifie le format des noms/prénoms, la date de naissance, le parcours et la promotion.
     * Affiche les messages d'erreur si nécessaire.
     *
     * @return true si tous les champs sont valides, false sinon.
     */
    private boolean validerChamps() {
    boolean valide = true;

    // Masquer tous les messages d’erreur
    nomErrorLabel.setVisible(false);
    prenomErrorLabel.setVisible(false);
    dateErrorLabel.setVisible(false);
    parcoursErrorLabel.setVisible(false);
    promotionErrorLabel.setVisible(false);

    // Récupération des valeurs
    String nom = nomField.getText().trim();
    String prenom = prenomField.getText().trim();
    LocalDate dateNaissance = dateNaissancePicker.getValue();
    Etudiant.Parcours parcours = parcoursCombo.getValue();
    Etudiant.Promotion promotion = promotionCombo.getValue();

    // Validation du nom
    if (nom.isEmpty()) {
        afficherErreur(nomErrorLabel, "Le nom est obligatoire.");
        valide = false;
    } else if (!nom.matches("[a-zA-ZÀ-ÿ\\s\\-']+")) {
        afficherErreur(nomErrorLabel, "Le nom ne doit contenir que des lettres.");
        valide = false;
    }

    // Validation du prénom
    if (prenom.isEmpty()) {
        afficherErreur(prenomErrorLabel, "Le prénom est obligatoire.");
        valide = false;
    } else if (!prenom.matches("[a-zA-ZÀ-ÿ\\s\\-']+")) {
        afficherErreur(prenomErrorLabel, "Le prénom ne doit contenir que des lettres.");
        valide = false;
    }

    // Validation de la date de naissance
    if (dateNaissance == null) {
        afficherErreur(dateErrorLabel, "La date de naissance est obligatoire.");
        valide = false;
    } else if (dateNaissance.isAfter(LocalDate.now())) {
        afficherErreur(dateErrorLabel, "La date ne peut pas être dans le futur.");
        valide = false;
    } else if (LocalDate.now().getYear() - dateNaissance.getYear() < 16) {
        afficherErreur(dateErrorLabel, "L'étudiant doit avoir au moins 16 ans.");
        valide = false;
    }

    // Validation du parcours
    if (parcours == null) {
        afficherErreur(parcoursErrorLabel, "Le parcours est requis.");
        valide = false;
    }

    // Validation de la promotion
    if (promotion == null) {
        afficherErreur(promotionErrorLabel, "La promotion est requise.");
        valide = false;
    }

    return valide;
    }
    
    private void afficherErreur(Label label, String message) {
    label.setText(message);
    label.setStyle("-fx-text-fill: red;");
    label.setVisible(true);
    }
    
    /**
     * Retourne à la scène d’accueil (welcome.fxml).
     */
    @FXML
    public void handleRetourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/welcome.fxml"));
            Parent root = loader.load();
    
            // Récupère la scène à partir d’un élément de l’interface
            Stage stage = (Stage) tableView.getScene().getWindow();  // ou un autre élément de ton interface
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil - Application Étudiants");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Ferme complètement l'application.
     */
    @FXML
    public void handleQuitterApplication(ActionEvent event) {
        System.exit(0); // Ferme proprement l'application JavaFX
    }
    
    /**
     * Affiche la fenêtre d’aide contextuelle (FAQ).
     */
    @FXML
    public void handleAide(ActionEvent event) {
         AideUtils.afficherAide();
    }
}
