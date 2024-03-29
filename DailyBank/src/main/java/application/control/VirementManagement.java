package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.VirementManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class VirementManagement {

	private Stage primaryStage;
	private VirementManagementController vmcViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;
	private CompteCourant compteConcerne;

	/**
	 * Constructeur de la classe ComptesManagement
	 * 
	 * @param _parentStage
	 * @param _dbstate
	 * @param client
	 */
	public VirementManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.compteConcerne = compte;
		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(VirementManagementController.class.getResource("virementmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);

			this.vmcViewController = loader.getController();
			this.vmcViewController.initContext(this.primaryStage, this, _dbstate, client, compte);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Méthode permettant d'afficher la fenêtre de gestion des comptes
	 */
	public void doVirementManagementDialog() {
		this.vmcViewController.displayDialog();
	}

	/**
	 * Méthode permettant de recuperer les comptes d'un client
	 * @return liste des comptes d'un client
	 */
	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
			System.out.println("array1");
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
			System.out.println("array2");
		}
		return listeCpt;
	}


}
