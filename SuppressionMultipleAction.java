import java.util.List;
import java.util.ArrayList;

public class SuppressionMultipleAction implements Action {
    private EtudiantDAO dao;
    private List<Etudiant> etudiantsSupprimes;

    public SuppressionMultipleAction(EtudiantDAO dao, List<Etudiant> etudiantsSupprimes) {
        this.dao = dao;
        this.etudiantsSupprimes = new ArrayList<>(etudiantsSupprimes);
    }

    @Override
    public void undo() {
        for (Etudiant e : etudiantsSupprimes) {
            dao.ajouterEtudiant(e);
        }
    }
}