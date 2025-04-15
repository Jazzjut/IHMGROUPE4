import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) pour gérer les opérations de création, lecture, mise à jour et suppression d'étudiants.
 * Permet l'accès à la base SQLite pour ajouter, lire, modifier et supprimer des étudiants.
 * 
 * @author Iman, Kenza, Jacinthe
 * @version 26/04/2025
 */

public class EtudiantDAO {
    private final Db db = new Db();


    /**
     * Récupère tous les étudiants depuis la base de données.
     *
     * @return Une liste d'étudiants.
     */
    public List<Etudiant> getAllEtudiants() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Parcours des résultats de la requête
            while (rs.next()) {
                // Création d'un objet Etudiant à partir des données de la base
                Etudiant e = new Etudiant(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("date_naissance"),
                    Etudiant.Parcours.valueOf(rs.getString("parcours")),
                    Etudiant.Promotion.valueOf(rs.getString("promotion"))
                );
                etudiants.add(e); // ajout de l'étudiant à la liste
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return etudiants;
    }

    /**
     * Ajoute un étudiant à la base de données.
     *
     * @param e L'étudiant à ajouter.
     * @return L'étudiant avec l'ID généré automatiquement.
     */
    public Etudiant ajouterEtudiant(Etudiant e) {
        String sql = "INSERT INTO etudiants (nom, prenom, date_naissance, parcours, promotion) VALUES (?, ?, ?, ?, ?)";
    
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    
            // Remplissage des paramètres
            pstmt.setString(1, e.getNom());
            pstmt.setString(2, e.getPrenom());
            pstmt.setString(3, e.getDateDeNaissance());
            pstmt.setString(4, e.getParcours().name());
            pstmt.setString(5, e.getPromotion().name());
    
            pstmt.executeUpdate(); // Exécution de l'insertion
    
            // Récupération de l'ID généré automatiquement
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                e.setId(rs.getInt(1)); // affecte l'ID généré par la BDD
            }
    
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
        return e;
    }
    

    /**
     * Modifie les informations d’un étudiant existant.
     *
     * @param e L'étudiant à modifier (doit contenir un ID valide).
     */
    public void modifierEtudiant(Etudiant e) {
        String sql = "UPDATE etudiants SET nom=?, prenom=?, date_naissance=?, parcours=?, promotion=? WHERE id=?";

        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Remplissage des nouveaux champs
            pstmt.setString(1, e.getNom());
            pstmt.setString(2, e.getPrenom());
            pstmt.setString(3, e.getDateDeNaissance());
            pstmt.setString(4, e.getParcours().name());
            pstmt.setString(5, e.getPromotion().name());
            pstmt.setInt(6, e.getId());
            
            pstmt.executeUpdate(); // Mise à jour de l’enregistrement
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Supprime un étudiant à partir de son identifiant.
     *
     * @param id L'identifiant de l'étudiant à supprimer.
     */
    public void supprimerEtudiant(int id) {
        String sql = "DELETE FROM etudiants WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id); // Définir l'ID à supprimer
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    

    /**
     * Récupère une page d’étudiants avec un offset et une limite.
     *
     * @param limit Le nombre maximum d'étudiants à récupérer.
     * @param offset Le décalage à appliquer.
     * @return Une liste d’étudiants correspondant à la page demandée.
     */
    public List<Etudiant> getEtudiantsParPage(int limit, int offset) {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants LIMIT ? OFFSET ?";
    
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Applique la pagination : LIMIT = nombre max de résultats, OFFSET = point de départ
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
    
            // Parcours des résultats
            while (rs.next()) {
                Etudiant e = new Etudiant(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("date_naissance"),
                    Etudiant.Parcours.valueOf(rs.getString("parcours")),
                    Etudiant.Promotion.valueOf(rs.getString("promotion"))
                );
                etudiants.add(e);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return etudiants;
    }

    /**
     * Retourne le nombre total d'étudiants dans la base.
     *
     * @return Le nombre total d'étudiants.
     */
    public int getNombreTotalEtudiants() {
        String sql = "SELECT COUNT(*) FROM etudiants";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            if (rs.next()) {
                return rs.getInt(1); // Retourne le résultat du COUNT
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Par défaut si erreur ou aucun résultat
        
    }
    
    /**
     * Réinsère un étudiant avec un ID précis (cas d'import ou restauration).
     *
     * @param e L’étudiant à insérer (avec ID).
     */
    public void reajouterEtudiantAvecID(Etudiant e) {
        String sql = "INSERT INTO etudiants (id, nom, prenom, date_naissance, parcours, promotion) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 
            pstmt.setInt(1, e.getId());
            pstmt.setString(2, e.getNom());
            pstmt.setString(3, e.getPrenom());
            pstmt.setString(4, e.getDateDeNaissance());
            pstmt.setString(5, e.getParcours().name());
            pstmt.setString(6, e.getPromotion().name());
            
            pstmt.executeUpdate();// Insertion forcée avec ID fourni
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
