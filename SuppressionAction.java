import java.util.List;

public class SuppressionAction implements Action {
    private EtudiantDAO dao;
    private Etudiant etudiant;

    public SuppressionAction(EtudiantDAO dao, Etudiant etudiant) {
        this.dao = dao;
        this.etudiant = etudiant;
    }

    @Override
    public void undo() {
        dao.ajouterEtudiant(etudiant);
    }
}