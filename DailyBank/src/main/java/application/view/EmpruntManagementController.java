package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.CompteEmpruntPane;
import application.control.ComptesManagement;
import application.control.EmpruntManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Emprunt;

public class EmpruntManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ComptesManagementController
	private EmpruntManagement emDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<Emprunt> oListEmprunts;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, EmpruntManagement _cm, DailyBankState _dbstate, Client client) {
		this.emDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	/**
	 * Configuration de la fenêtre
	 */
	public void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListEmprunts = FXCollections.observableArrayList();
		this.lvEmprunts.setItems(this.oListEmprunts);
		this.lvEmprunts.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvEmprunts.getFocusModel().focus(-1);
		//this.lvEmprunts.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		//this.validateComponentState();
	}

	/**
	 * Attributs FXML de la page
	 */
	@FXML
	private ListView<Emprunt> lvEmprunts;
	@FXML
	private Label lblInfosClient;
	@FXML
	private Button btnSimulerEmprunt;
	@FXML
	private Button btnSupprimerEmprunt;
	@FXML
	private Button btnActualiser;


	/**
	 * Fermeture de la fenêtre
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doActualiser() {
		this.loadList();
	}

	/**
	 * Simule un emprunt d'une somme définie par le client et l'ajoute dans la BD
	 * par Hugo Berdinel
	 */
	@FXML
	private void doSimulerEmprunt() {
		CompteEmpruntPane emp = new CompteEmpruntPane(this.primaryStage, this.dailyBankState,this.clientDesComptes);
		emp.doEmpruntDialog();
	}

	/**
	 * Supprime un emprunt choisi par le client
	 */
	@FXML
	private void doSupprimerEmprunt() {
		int selectedIndice = this.lvEmprunts.getSelectionModel().getSelectedIndex();
		if(selectedIndice >= 0) {
			Emprunt emp = this.oListEmprunts.get(selectedIndice);
			Alert alt = new Alert(AlertType.CONFIRMATION);
			alt.setTitle("Suppression d'un emprunt");
			alt.setContentText("Vous vous apprêtez à supprimer un emprunt. Voulez-vous continuer ?");
			alt.showAndWait();
			if(alt.getResult().equals(ButtonType.OK)) {
				Emprunt result = this.emDialogController.supprimerEmprunt(emp);
				System.out.println("ok");
				if(result != null) {
					this.oListEmprunts.remove(emp);
				}
			}
		}
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}


	/**
	 * Affiche la fenêtre de gestion des opérations
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	/**
	 *Chargement de la liste des comptes du client 
	 */
	private void loadList() {
		ArrayList<Emprunt> listeCpt;
		listeCpt = this.emDialogController.getEmprunts();
		this.oListEmprunts.clear();
		this.oListEmprunts.addAll(listeCpt);
	}
}