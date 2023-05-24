package application.view;

import java.util.ArrayList;
import java.util.Locale;

import application.DailyBankState;
import application.control.PrelevementsManagement;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Employe;
import model.data.Prelevement;

public class PrelevementsManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à OperationsManagementController
	private PrelevementsManagement omDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Prelevement> oListPrelevements;
	private Prelevement selectedPrelevement;
	
	

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, PrelevementsManagement _om, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.omDialogController = _om;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();
	}
	
	/**
	 * Configuration de la fenêtre et de ses composants
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		
		this.oListPrelevements = FXCollections.observableArrayList();
		this.lvPrelevements.setItems(this.oListPrelevements);
		this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelevements.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		
		this.updateInfoCompteClient();
		this.validateComponentState();
	}
	
	/**
	 * Affiche la fenêtre de gestion des opérations
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

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private Label lblInfosCompte;
	@FXML
	private ListView<Prelevement> lvPrelevements;
	@FXML
	private Button btnAjout;
	@FXML
	private Button btnModifier;
	@FXML 
	private Button btnSupprimer;

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	// TODO
	@FXML
	private void doAjout() {
		Prelevement prelev;
		prelev = this.omDialogController.enregistrerPrelevement();
		if (prelev != null) {
			this.oListPrelevements.add(prelev);
		}
	}

	// TODO
	@FXML
	private void doModifier() {
		Alert batchAlert = new Alert(AlertType.INFORMATION);
		batchAlert.setHeaderText("Fonction en développement :) (edit "+ this.selectedPrelevement.idPrelev +")");
		batchAlert.showAndWait();
	}

	// TODO
	@FXML
	private void doSupprimer() {
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		
		if (selectedIndice >= 0) {
			Prelevement pvm = this.oListPrelevements.get(selectedIndice);
			Prelevement result = this.omDialogController.supprimerPrelevement(pvm);
			if(result != null) {
				System.out.println("delete prelev");
				//this.oListPrelevements.remove(result);
				this.updateInfoCompteClient();
			}
		}
	}

	/**
	 * Valide l'état des composants de la fenêtre
	*/
	 
	private void validateComponentState() {
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		this.btnAjout.setDisable(false);
		
		if (selectedIndice >= 0) {
			this.selectedPrelevement = this.oListPrelevements.get(selectedIndice);
			this.btnModifier.setDisable(false);
			this.btnSupprimer.setDisable(false);
		} else {
			this.btnModifier.setDisable(true);
			this.btnSupprimer.setDisable(true);
		}
	}
	
	
	/**
	 * Met à jour les informations affichées sur le client et le compte courant
	 */
	
	private void updateInfoCompteClient() {
		PairsOfValue<CompteCourant, ArrayList<Prelevement>> opesEtCompte;
		opesEtCompte = this.omDialogController.operationsEtSoldeDunCompte();

		ArrayList<Prelevement> listeOP;
		this.compteConcerne = opesEtCompte.getLeft();
		listeOP = opesEtCompte.getRight();

		String info;
		info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli
				+ ")";
		this.lblInfosClient.setText(info);

		info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
		this.lblInfosCompte.setText(info);

		this.oListPrelevements.clear();
		this.oListPrelevements.addAll(listeOP);

		this.validateComponentState();
	}
	
}
