import java.util.List;

/**
 * Classe représentant une action de suppression d’un étudiant dans l'application.
 * Cette action permet d'annuler la suppression en réinsérant l’étudiant
 * dans la base de données à l’aide du DAO.
 *
 * @version 05/04/2025
 * @author Iman Jouiad
 */
public class SuppressionAction implements Action {
    
    private EtudiantDAO dao;
    private Etudiant etudiant;

    /**
     * Constructeur de l'action de suppression.
     *
     * @param dao Le Data Access Object utilisé pour accéder aux données des étudiants.
     * @param etudiant L'étudiant qui a été supprimé.
     */
    public SuppressionAction(EtudiantDAO dao, Etudiant etudiant) {
        this.dao = dao;
        this.etudiant = etudiant;
    }

    /**
     * Annule l'action de suppression en réinsérant l'étudiant dans la base de données.
     */
    @Override
    public void undo() {
        dao.ajouterEtudiant(etudiant);
    }
}