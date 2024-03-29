package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.EmpruntManagement;
import application.control.PrelevementsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;

public class ComptesManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ComptesManagementController
	private ComptesManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> oListCompteCourant;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}
	/**
	 * Configuration de la fenêtre
	 */
	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;
	@FXML
	private Button btnCloturerCompte;
	@FXML
	private Button btnVoirPrel;
	@FXML
	private Button btnSimulEmpr;

	/**
	 * Fermeture de la fenêtre
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Affichage des opérations du compte sélectionné
	 */
	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			this.cmDialogController.gererOperationsDUnCompte(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}

	/**
	 * Modification du compte sélectionné
	 */
	@FXML
	private void doModifierCompte() {
	}
	
	/**
	 * Voir prélèvements du compte sélectionné
	 * @author Bernat
	 */
	@FXML
	private void doVoirPrelevement() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		PrelevementsManagement cm = new PrelevementsManagement(this.primaryStage, this.dailyBankState,this.clientDesComptes, this.oListCompteCourant.get(selectedIndice));
		cm.doPrelevementsManagementDialog();
	}
	
	@FXML
	private void doVoirEmprunts() {
		
		EmpruntManagement  em = new EmpruntManagement(primaryStage, dailyBankState, clientDesComptes); 
		em.doEmpruntManagementDialog();
	}

	/**
	 * suppression du compte sélectionné
	 */
	@FXML
	private void doSupprimerCompte() {
	}

	/** JavaDoc of doSupprimerCompte()
	* Suppression du compte sélectionné
	*crée par jimmy
	*/
	@FXML
	private void doCloturerCompte() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
		
		Access_BD_CompteCourant cc = new Access_BD_CompteCourant();
		if (selectedIndice >= 0) {
			try {
				cc.cloturerCompteCourant(cpt);
				this.lvComptes.getSelectionModel();
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		this.loadList();
	}
/**
 * Ouverture d'une fenêtre de création d'un nouveau compte
 */
	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cmDialogController.creerNouveauCompte();
		if (compte != null) {
			this.oListCompteCourant.add(compte);
		}
	}
	

	/**
	 * Chargement de la liste des comptes du client 
	 */
	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cmDialogController.getComptesDunClient();
		this.oListCompteCourant.clear();
		this.oListCompteCourant.addAll(listeCpt);
	}
	
	/**
	 * Mise à jour des informations des comptes
	 */
	
	/**
	 * Affichage des comptes du client
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}



	/**
	 * Validation de l'état des composants
	 */
	private void validateComponentState() {
		//modfifié par jimmy
		this.btnModifierCompte.setDisable(true);
		this.btnSupprCompte.setDisable(true);
		this.btnCloturerCompte.setDisable(true);
		this.btnVoirOpes.setDisable(true);
		this.btnVoirPrel.setDisable(true);
		
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			
			
			this.btnVoirOpes.setDisable(false);
			if (cpt.estCloture.equals("O")) {
				btnCloturerCompte.setDisable(true);
				btnVoirPrel.setDisable(true);
			}
			else {
				btnVoirPrel.setDisable(false);
				
				if (cpt.solde != 0) {
					this.btnCloturerCompte.setDisable(true);
				}
				else {
					btnCloturerCompte.setDisable(false);
				}
			}
		} else {
			this.btnVoirOpes.setDisable(true);
		}
	}
}
