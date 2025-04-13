import java.util.List;
import java.util.ArrayList;

public class SuppressionMultipleAction implements Action {
    private EtudiantDAO dao;
    private List<Etudiant> etudiantsSupprimes;

    public SuppressionMultipleAction(EtudiantDAO dao, List<Etudiant> liste) {
        this.dao = dao;
        this.etudiantsSupprimes = new ArrayList<>(liste); // on copie
    }

    @Override
    public void undo() {
        for (Etudiant e : etudiantsSupprimes) {
            dao.reajouterEtudiantAvecID(e); // Utilisation de la méthode avec ID fixe
        }
        System.out.println("↩️ Étudiants restaurés !");
    }
}