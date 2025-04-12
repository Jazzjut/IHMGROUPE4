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
    public Etudiant ajouterEtudiant(Etudiant e) {
    String sql = "INSERT INTO etudiants (nom, prenom, date_naissance, parcours, promotion) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = db.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        pstmt.setString(1, e.getNom());
        pstmt.setString(2, e.getPrenom());
        pstmt.setString(3, e.getDateDeNaissance());
        pstmt.setString(4, e.getParcours().name());
        pstmt.setString(5, e.getPromotion().name());

        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            e.setId(rs.getInt(1)); // ✅ on attribue l’ID retourné par la base
            System.out.println("✔️ Étudiant ajouté avec ID : " + e.getId());
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        System.out.println("❌ ERREUR : Étudiant pas ajouté.");
    }

    return e;
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
    
    //Charger étudiant
    public List<Etudiant> getEtudiantsParPage(int limit, int offset) {
    List<Etudiant> etudiants = new ArrayList<>();
    String sql = "SELECT * FROM etudiants LIMIT ? OFFSET ?";

    try (Connection conn = db.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, limit);
        pstmt.setInt(2, offset);
        ResultSet rs = pstmt.executeQuery();

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

public int getNombreTotalEtudiants() {
    String sql = "SELECT COUNT(*) FROM etudiants";
    try (Connection conn = db.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}
}
