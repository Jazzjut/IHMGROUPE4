import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Classe représentant un étudiant avec ses informations personnelles : identifiant, 
 * nom, prénom, date de naissance, parcours, promotion, et état de sélection.
 * 
 * @author Iman/Jacinthe
 * @version 26/03/2025
 */
public class Etudiant {
    // Attributs de l'étudiant
    private int id;
    private String nom;
    private String prenom;
    private String dateDeNaissance;
    private Parcours parcours;
    private Promotion promotion;
    /** Propriété utilisée pour savoir si l'étudiant est sélectionné dans 
     * l'interface graphique (ex. case à cocher dans notre TableView). */
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    /**
     * Enumération des parcours disponibles.
     */
    public enum Parcours {
        ECMPS("ECMPS"),
        GCELL("GCell"),
        GPHY("GPhy");

        private final String label;

        Parcours(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
    
     /**
     * Enumération des promotions disponibles.
     */
    public enum Promotion {
        M1("Master 1"),
        M2("Master 2");

        private final String label;

        Promotion(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    // ----- Constructeurs -----
    /**
     * Constructeur vide par défaut.
     */
    public Etudiant() {
        this.id = 0;
        this.nom = "";
        this.prenom = "";
        this.dateDeNaissance = "";
        this.parcours = Parcours.GPHY;
        this.promotion = Promotion.M1;
    }

    /**
     * Construit un nouvel étudiant sans identifiant, utilisé notamment lors de l’ajout manuel
     * d’un étudiant par l’utilisateur avant insertion en base de données.
     * @param nom Le nom de l’étudiant.
     * @param prenom Le prénom de l’étudiant.
     * @param dateDeNaissance La date de naissance.
     * @param parcours Le parcours choisi (ECMPS, GCELL, GPHY).
     * @param promotion La promotion choisie (M1 ou M2).
    */
    public Etudiant(String nom, String prenom, String dateDeNaissance, Parcours parcours, Promotion promotion) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateDeNaissance = dateDeNaissance;
        this.parcours = parcours;
        this.promotion = promotion;
    }

    /**
     * Construit un étudiant complet avec identifiant, utilisé généralement
     * lors du chargement d’un étudiant depuis la base de données.
     *
     * @param id L’identifiant unique de l’étudiant.
     * @param nom Le nom de l’étudiant.
     * @param prenom Le prénom de l’étudiant.
     * @param dateDeNaissance La date de naissance.
     * @param parcours Le parcours suivi par l’étudiant.
     * @param promotion La promotion à laquelle appartient l’étudiant.
    */
    public Etudiant(int id, String nom, String prenom, String dateDeNaissance, Parcours parcours, Promotion promotion) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateDeNaissance = dateDeNaissance;
        this.parcours = parcours;
        this.promotion = promotion;
    }

    /**
     * Construit un nouvel étudiant en copiant toutes les données d’un autre étudiant.
     *
     * @param autre L’étudiant à copier.
    */
    public Etudiant(Etudiant autre) {
        this.id = autre.id;
        this.nom = autre.nom;
        this.prenom = autre.prenom;
        this.dateDeNaissance = autre.dateDeNaissance;
        this.parcours = autre.parcours;
        this.promotion = autre.promotion;
        // Copie la valeur de sélection sans lier la propriété.
        this.selected = new SimpleBooleanProperty(autre.isSelected()); 
    }

    // ----- Getters -----
    /** @return l'identifiant unique de l'étudiant (utilisé en base de données). */
    public int getId() {
        return id;
    }

    /** @return le nom de famille de l'étudiant. */
    public String getNom() {
        return nom;
    }

    /** @return le prénom de l'étudiant. */
    public String getPrenom() {
        return prenom;
    }

    /** @return la date de naissance de l'étudiant (format texte, ex: "2000-12-31"). */
    public String getDateDeNaissance() {
        return dateDeNaissance;
    }

    /** @return le parcours suivi par l'étudiant (GPHY, GCELL, ECMPS). */
    public Parcours getParcours() {
        return parcours;
    }
    
    /** @return la promotion de l'étudiant (M1 ou M2). */
    public Promotion getPromotion() {
        return promotion;
    }

    /** @return true si l'étudiant est sélectionné dans l'interface, false sinon. */
    public boolean isSelected() {
        return selected.get();
    }

    /** @return la propriété JavaFX associée à l'état de sélection de l'étudiant. */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    // ----- Setters -----
    /** @param id l'identifiant unique de l'étudiant (défini depuis la base de données). */
    public void setId(int id) {
        this.id = id;
    }

    /** @param nom le nom de famille à affecter à l'étudiant. */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /** @param prenom le prénom à affecter à l'étudiant. */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /** @param promotion la promotion à affecter à l'étudiant. */
    public void setDateDeNaissance(String dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    /** @param parcours le parcours à affecter à l'étudiant. */
    public void setParcours(Parcours parcours) {
        this.parcours = parcours;
    }
    
    /** @param promotion la promotion à affecter à l'étudiant. */
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    /** @param value true si l'étudiant est sélectionné dans l'interface, false sinon. */
    public void setSelected(boolean value) {
        this.selected.set(value);
    }
}