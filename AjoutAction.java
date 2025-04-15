/**
 * Représente une action d'ajout d'un étudiant,
 * permettant d'annuler l'ajout en supprimant l'étudiant concerné.
 * Cette classe est utilisée dans le cadre du design pattern Command.
 *
 * @author Iman Jouiad
 * @version 05/04/2025
 */
public class AjoutAction implements Action {
  
    private EtudiantDAO dao; //effectuer les opérations sur les étudiants.
    private Etudiant etudiant; //Étudiant ayant été ajouté.

    /**
     * Crée une action d'ajout d'un étudiant.
     *
     * @param dao       DAO utilisé pour supprimer l'étudiant lors d'un undo
     * @param etudiant  Étudiant ajouté (doit contenir l'ID généré)
     */
    public AjoutAction(EtudiantDAO dao, Etudiant etudiant) {
        this.dao = dao;
        this.etudiant = etudiant;
    }

    /**
     * Annule l'ajout de l'étudiant en le supprimant via le DAO.
     */
    @Override
    public void undo() {
        dao.supprimerEtudiant(etudiant.getId());
    }
}