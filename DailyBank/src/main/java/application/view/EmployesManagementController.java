package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.EmployesManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

public class EmployesManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ClientsManagementController
	private EmployesManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> oListEmployes;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, EmployesManagement _cm, DailyBankState _dbstate) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListEmployes = FXCollections.observableArrayList();
		this.lvEmployes.setItems(this.oListEmployes);
		this.lvEmployes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvEmployes.getFocusModel().focus(-1);
		this.lvEmployes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		this.validateComponentState();
	}

	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private TextField txtNum;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private ListView<Employe> lvEmployes;
	@FXML
	private Button btnEffacerEmploye;
	@FXML
	private Button btnModifEmploye;

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * doRechercher : recherche des clients en BD et affichage dans la liste
	 */
	@FXML
	private void doRechercher() {
		int numEmploye;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numEmploye = -1;
			} else {
				numEmploye = Integer.parseInt(nc);
				if (numEmploye < 0) {
					this.txtNum.setText("");
					numEmploye = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			numEmploye = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (numEmploye != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}

		// Recherche des clients en BD. cf. AccessClient > getClients(.)
		// numCompte != -1 => recherche sur numCompte
		// numCompte != -1 et debutNom non vide => recherche nom/prenom
		// numCompte != -1 et debutNom vide => recherche tous les clients
		
		ArrayList<Employe> listeCli;
		listeCli = this.cmDialogController.getlisteEmployes(numEmploye, debutNom, debutPrenom);

		this.oListEmployes.clear();
		this.oListEmployes.addAll(listeCli);
		
		this.validateComponentState();
	}


	/**
	 * doEffacerEmploye : efface l'employé sélectionné dans la liste
	 */
	@FXML
	private void doModifierEmploye() {
		int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe cliMod = this.oListEmployes.get(selectedIndice);
			Employe result = this.cmDialogController.modifierEmploye(cliMod);
			if (result != null) {
				this.oListEmployes.set(selectedIndice, result);
			}
		}
	}

	@FXML
	private void doSupprimerEmploye() {

		int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe emp = this.oListEmployes.get(selectedIndice);
			Employe result = this.cmDialogController.supprimerEmploye(emp);
			if(result != null) {
				this.oListEmployes.remove(result);
			}
		}
	}

	/**
	 * doNouveauEmploye : crée un nouveau employé
	 */
	@FXML
	private void doNouveauEmploye() {
		Employe client;
		client = this.cmDialogController.nouveauEmploye();
		if (client != null) {
			this.oListEmployes.add(client);
		}
	}
	
	private void validateComponentState() {
		// Non implémenté => désactivé
		/**
		this.btnDesactClient.setDisable(true);
		int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifClient.setDisable(false);
			this.btnComptesClient.setDisable(false);
		} else {
			this.btnModifClient.setDisable(true);
			this.btnComptesClient.setDisable(true);
		}
		*/
	}
}
