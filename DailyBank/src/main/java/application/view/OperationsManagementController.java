package application.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import application.DailyBankState;
import application.control.OperationsManagement;
import application.control.VirementManagement;
import application.tools.GeneratePDF;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

public class OperationsManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à OperationsManagementController
	private OperationsManagement omDialogController;

	private VirementManagement vmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Operation> oListOperations;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, OperationsManagement _om, DailyBankState _dbstate, Client client,
			CompteCourant compte) {
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

		this.oListOperations = FXCollections.observableArrayList();
		this.lvOperations.setItems(this.oListOperations);
		this.lvOperations.setSelectionModel(new NoSelectionModel<Operation>());
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
	private ListView<Operation> lvOperations;
	@FXML
	private Button btnDebit;
	@FXML
	private Button btnCredit;
	@FXML
	private Button btnAutre;
	@FXML
	private Button btnDebitExep;
	@FXML
	private Button btnRelevePdf;

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Enregistre un débit sur le compte courant
	 */
	@FXML
	private void doDebit() {

		Operation op = this.omDialogController.enregistrerDebit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	/**
	 * Enregistre un crédit sur le compte courant
	 * 
	 * @author SOLDEVILA Bernat
	 */
	@FXML
	private void doCredit() {

		Operation op = this.omDialogController.enregistrerCredit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	// Non implémenté => désactivé
	@FXML
	private void doAutre() {
		this.vmDialogController = new VirementManagement(this.primaryStage, this.dailyBankState, this.clientDuCompte,
				this.compteConcerne);
		this.vmDialogController.doVirementManagementDialog();
	}

	@FXML
	private void doDebitExep() {

		Operation op = this.omDialogController.enregistrerDebitExeptionnel();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	@FXML
	private void doRelevePdf() {
		LocalDate currentdate = LocalDate.now();
		int currentMonth = currentdate.getMonthValue();
		int currentYear = currentdate.getYear();
		
		GeneratePDF.genererRelevePdf(clientDuCompte, compteConcerne, currentMonth, currentYear);
	}

	/**
	 * valide l'état des composants de la fenêtre
	 */
	private void validateComponentState() {
		this.btnCredit.setDisable(false);
		this.btnDebit.setDisable(false);
		this.btnDebitExep.setDisable(true);

		// modifié par jimmy
		if (compteConcerne.estCloture.equals("O")) {
			this.btnCredit.setDisable(true);
			this.btnDebit.setDisable(true);
			this.btnAutre.setDisable(true);
			
		} else {
			this.btnDebit.setDisable(false);
			this.btnCredit.setDisable(false);
			if (this.dailyBankState.isChefDAgence()) {
				this.btnDebitExep.setDisable(false);
			}
		}

	}

	/**
	 * Met à jour les informations affichées sur le client et le compte courant
	 */
	private void updateInfoCompteClient() {

		PairsOfValue<CompteCourant, ArrayList<Operation>> opesEtCompte;

		opesEtCompte = this.omDialogController.operationsEtSoldeDunCompte();

		ArrayList<Operation> listeOP;
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

		this.oListOperations.clear();
		this.oListOperations.addAll(listeOP);

		this.validateComponentState();
	}
}
