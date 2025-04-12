public class AjoutAction implements Action {
    private EtudiantDAO dao;
    private Etudiant etudiant;

    public AjoutAction(EtudiantDAO dao, Etudiant etudiant) {
        this.dao = dao;
        this.etudiant = etudiant;
    }

    @Override
    public void undo() {
        System.out.println("Suppression de l'étudiant ID : " + etudiant.getId());
        dao.supprimerEtudiant(etudiant.getId());
    }
    //jkjk
}