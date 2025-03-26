import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {
    private final Db db = new Db();

    // Lire tous les étudiants
    public List<Etudiant> getAllEtudiants() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants";

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

    // Ajouter un étudiant
    public void ajouterEtudiant(Etudiant e) {
        String sql = "INSERT INTO etudiants (nom, prenom, date_naissance, parcours, promotion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, e.getNom());
            pstmt.setString(2, e.getPrenom());
            pstmt.setString(3, e.getDateDeNaissance());
            pstmt.setString(4, e.getParcours().name());
            pstmt.setString(5, e.getPromotion().name());
            pstmt.executeUpdate();
            System.out.println("Étudiant ajouté avec succès.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("ERREUR : Étudiant pas ajouté.");
        }
    }

    // Modifier un étudiant
    public void modifierEtudiant(Etudiant e) {
        String sql = "UPDATE etudiants SET nom=?, prenom=?, date_naissance=?, parcours=?, promotion=? WHERE id=?";

        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, e.getNom());
            pstmt.setString(2, e.getPrenom());
            pstmt.setString(3, e.getDateDeNaissance());
            pstmt.setString(4, e.getParcours().name());
            pstmt.setString(5, e.getPromotion().name());
            pstmt.setInt(6, e.getId());
            pstmt.executeUpdate();
            System.out.println("Étudiant modifié avec succès.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("ERREUR : Étudiant pas modifié.");
        }
    }

    // Supprimer un étudiant par ID
    public void supprimerEtudiant(int id) {
        String sql = "DELETE FROM etudiants WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Étudiant supprimé avec succès.");

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("ERREUR : Étudiant pas supprimé.");
        }
    }
}
