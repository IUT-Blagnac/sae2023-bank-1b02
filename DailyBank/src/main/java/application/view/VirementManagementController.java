package application.view;

import java.rmi.server.Operation;
import java.util.ArrayList;
import java.util.Locale;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.VirementManagement;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
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
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;

public class VirementManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ComptesManagementController
	private VirementManagement vmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	private CompteCourant compteSource;
	private CompteCourant compteTarget;
	private Integer montant;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> oListCompteCourant;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, VirementManagement _vm, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.vmDialogController = _vm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.clientDesComptes = client;
		this.compteSource = compte;
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

		info = "Cpt. : " + this.compteSource.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteSource.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteSource.debitAutorise);
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}

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

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVirement;
	@FXML
	private TextArea textMontant;

	/**
	 * Fermeture de la fenêtre
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
		
	}

	/**
	 * doVirement
	 * @throws DataAccessException 
	 * @throws ManagementRuleViolation 
	 * @throws DatabaseConnexionException 
	 */
	@FXML
	private void doVirement() throws DatabaseConnexionException, ManagementRuleViolation, DataAccessException {
		this.loadList();
		this.validateComponentState();
		// this.montant = Integer.parseInt(this.textMontant.getText());
		// System.out.println(this.compteTarget.solde);
		Access_BD_Operation ao = new Access_BD_Operation();
		if (this.compteTarget == null) {
			System.out.println("Erreur lors de la sélection du compte cible");
		}
		else {
			System.out.println(this.compteSource.solde);
			try {
				ao.insertDebit(this.compteSource.idNumCompte, this.montant, ConstantesIHM.TYPE_OP_7);	
				try {
					ao.insertCredit(this.compteTarget.idNumCompte, this.montant, ConstantesIHM.TYPE_OP_7);
					System.out.println(this.compteSource.solde);
					this.updateInfoCompteClient();
				} catch (Exception e) {
					System.out.println("Erreur lors de l'insertion du crédit");
				}
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("Erreur lors de l'insertion du débit");
				// 	public static void showAlert(Stage _fen, String _title, String _message, String _content, AlertType _at) {
				// AlertUtilities.showAlert(this.primaryStage, "Erreur", "Erreur lors de l'insertion du débit", "Erreur lors de l'insertion du débit", AlertType.ERROR);
			}
		}
		this.primaryStage.close();
		return;
	}

	/**
	 * Chargement de la liste des comptes du client
	 */
	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.vmDialogController.getComptesDunClient();
		// ici
		this.oListCompteCourant.clear();
		for (CompteCourant cpt : listeCpt) {
			if (this.compteSource.idNumCompte != cpt.idNumCompte && cpt.estCloture.equals("N") ){
				this.oListCompteCourant.add(cpt);
			}
		}
		// this.oListCompteCourant.addAll(listeCpt);
	}
	
	/**
	 * Validation de l'état des composants
	 */
	private void validateComponentState() {
		//modfifié par jimmy
		this.montant = Math.abs(Integer.parseInt(this.textMontant.getText()));

		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.compteTarget = this.oListCompteCourant.get(selectedIndice);
			this.btnVirement.setDisable(false);
		} else {
			this.btnVirement.setDisable(true);
		}
	}

	private void updateInfoCompteClient() {
		ArrayList<CompteCourant> Compte;
		String info;

		Compte = this.vmDialogController.getComptesDunClient();
		for (CompteCourant compteCourant : Compte) {
			if (compteCourant.idNumCompte == this.compteSource.idNumCompte) {
				this.compteSource = compteCourant;
			}
		}

		info = "Cpt. : " + this.compteSource.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteSource.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteSource.debitAutorise);
		this.lblInfosClient.setText(info);
		
		this.loadList();
		this.validateComponentState();
	}


}
