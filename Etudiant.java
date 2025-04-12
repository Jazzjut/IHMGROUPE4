import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Classe représentant un étudiant avec ses informations personnelles.
 * 
 * @author Iman
 * @version 26/03/2025
 */
public class Etudiant {
    private int id;
    private String nom;
    private String prenom;
    private String dateDeNaissance;
    private Parcours parcours;
    private Promotion promotion;
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    // Enumérations pour le parcours et la promotion
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

    // Constructeur sans paramètres
    public Etudiant() {
        this.id = 0;
        this.nom = "";
        this.prenom = "";
        this.dateDeNaissance = "";
        this.parcours = Parcours.GPHY;
        this.promotion = Promotion.M1;
    }

    // Constructeur avec paramètres (sans id)
    public Etudiant(String nom, String prenom, String dateDeNaissance, Parcours parcours, Promotion promotion) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateDeNaissance = dateDeNaissance;
        this.parcours = parcours;
        this.promotion = promotion;
    }

    // Constructeur avec tous les paramètres (lecture depuis la BDD)
    public Etudiant(int id, String nom, String prenom, String dateDeNaissance, Parcours parcours, Promotion promotion) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateDeNaissance = dateDeNaissance;
        this.parcours = parcours;
        this.promotion = promotion;
    }

    // ✅ Constructeur de copie
    public Etudiant(Etudiant autre) {
        this.id = autre.id;
        this.nom = autre.nom;
        this.prenom = autre.prenom;
        this.dateDeNaissance = autre.dateDeNaissance;
        this.parcours = autre.parcours;
        this.promotion = autre.promotion;
        this.selected = new SimpleBooleanProperty(autre.isSelected());
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getDateDeNaissance() {
        return dateDeNaissance;
    }

    public Parcours getParcours() {
        return parcours;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setDateDeNaissance(String dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public void setParcours(Parcours parcours) {
        this.parcours = parcours;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public void setSelected(boolean value) {
        this.selected.set(value);
    }
}