import java.util.List;
import java.util.ArrayList;

/**
 * Classe représentant une action de suppression multiple d’étudiants.
 * Cette action permet d’annuler la suppression en réinsérant chaque étudiant
 * supprimé dans la base de données, en conservant leurs identifiants d'origine.
 *
 * @version 05/04/2025
 * @author Iman Jouiad
 */
public class SuppressionMultipleAction implements Action {

    private EtudiantDAO dao;
    private List<Etudiant> etudiantsSupprimes;

    /**
     * Constructeur de l'action de suppression multiple.
     *
     * @param dao   Le Data Access Object utilisé pour les opérations sur les étudiants.
     * @param liste La liste des étudiants à supprimer.
     */
    public SuppressionMultipleAction(EtudiantDAO dao, List<Etudiant> liste) {
        this.dao = dao;
        this.etudiantsSupprimes = new ArrayList<>(liste); // on copie la liste
    }

    /**
     * Annule la suppression multiple en réinsérant chaque étudiant avec son ID d'origine.
     */
    @Override
    public void undo() {
        for (Etudiant e : etudiantsSupprimes) {
            dao.reajouterEtudiantAvecID(e); // Réinsertion avec ID fixe ( pour restaurer l'étudiant(s) avec son ID)
        }
    }
}