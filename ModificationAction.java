public class ModificationAction implements Action {
    private EtudiantDAO dao;
    private Etudiant ancien;   // état AVANT modification
    private int id;            // ID fixe
    private Etudiant actuel;   // état APRÈS modification

    public ModificationAction(EtudiantDAO dao, Etudiant ancien, Etudiant actuel) {
        this.dao = dao;
        this.ancien = new Etudiant(ancien); // copie de l'état avant modif
        this.actuel = new Etudiant(actuel); // copie de l'état après modif
        this.id = ancien.getId();           // ID unique pour être sûr
        this.ancien.setId(id);
        this.actuel.setId(id);
    }

    @Override
    public void undo() {
        dao.modifierEtudiant(ancien);
        System.out.println("↩️ Modification annulée : retour à l'ancien état pour ID " + id);
    }
}