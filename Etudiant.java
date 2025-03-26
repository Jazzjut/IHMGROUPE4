/**
 * Décrivez votre classe Etudiant ici.
 * Classe représentant un étudiant avec ses informations personnelles.
 * 
 * @author Iman
 * @version 26/03/2025
 */
public class Etudiant
{
    // Enumérations pour le parcours et la promotion
    public enum Parcours {
        ECMPS, GCELL, GPHY
    }

    public enum Promotion {
        M1, M2
    }

    // Attributs privés
    private int id;
    private String nom;
    private String prenom;
    private String dateDeNaissance;
    private Parcours parcours;
    private Promotion promotion;

    // Constructeur sans paramètres
    public Etudiant()
    {
        this.nom = "";
        this.prenom = "";
        this.dateDeNaissance = "";
        this.parcours = Parcours.GPHY;
        this.promotion = Promotion.M1;
    }

    // Constructeur avec paramètres
    public Etudiant(String nom, String prenom, String dateDeNaissance, Parcours parcours, Promotion promotion)
    {
        this.id = id; 
        this.nom = nom;
        this.prenom = prenom;
        this.dateDeNaissance = dateDeNaissance;
        this.parcours = parcours;
        this.promotion = promotion;
    }
    
    // Getters
    public int getId(){
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

    // Setters
    public void setId (int id){
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
}