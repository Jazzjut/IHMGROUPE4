/**
 * Représente une action de modification d'un étudiant,
 * permettant d'annuler la modification via le design pattern Command.
 * Cette classe conserve l'état de l'étudiant avant et après la modification,
 * ainsi que son identifiant, pour rétablir l'état précédent si nécessaire.
 *
 * @author Iman Jouiad
 * @version 05/04/2025
 */
public class ModificationAction implements Action {
    
    private EtudiantDAO dao; // pour effectuer les opérations sur les étudiants.
    private Etudiant ancien; //État de l'étudiant avant la modification.
    private int id;//Identifiant unique de l'étudiant concerné
    private Etudiant actuel; //État de l'étudiant après la modification

    /**
     * Crée une action de modification en sauvegardant l'état précédent et l'état modifié de l'étudiant.
     *
     * @param dao     DAO utilisé pour appliquer la modification ou l'annuler
     * @param ancien  État de l'étudiant avant modification
     * @param actuel  État de l'étudiant après modification    
    */
    public ModificationAction(EtudiantDAO dao, Etudiant ancien, Etudiant actuel) {
        this.dao = dao;
        this.ancien = new Etudiant(ancien); // l'état avant modification
        this.actuel = new Etudiant(actuel); // l'état après modification
        this.id = ancien.getId();           // ID unique de l'étudiant
        this.ancien.setId(id);              // cohérence de l'ID
        this.actuel.setId(id);
    }

    /**
     * Annule la modification en restaurant l'état précédent de l'étudiant.
     */
    @Override
    public void undo() {
        dao.modifierEtudiant(ancien);
    }
}