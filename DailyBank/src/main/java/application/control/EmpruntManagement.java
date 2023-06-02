package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.EmpruntManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Emprunt;
import model.orm.Access_BD_Emprunt;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmpruntManagement {

	private Stage primaryStage;
	private EmpruntManagementController emcViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;

	/**
	 * Constructeur de la classe EmpruntManagement
	 * @author hugob
	 * @param _parentStage
	 * @param _dbstate
	 * @param client
	 */
	public EmpruntManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmpruntManagementController.class.getResource("empruntsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des emprunts");
			this.primaryStage.setResizable(false);

			this.emcViewController = loader.getController();
			this.emcViewController.initContext(this.primaryStage, this, _dbstate, client);
			this.emcViewController.configure();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Méthode permettant de recuperer les emprunts d'un client
	 * @return liste des comptes d'un client
	 * @author hugob
	 */
	public ArrayList<Emprunt> getEmprunts() {
		ArrayList<Emprunt> listeCpt = new ArrayList<>();
		
		try {
			Access_BD_Emprunt ace = new Access_BD_Emprunt();
			listeCpt = ace.getEmprunts(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}
	
	/**
	 * Permet de supprimer un emprunt
	 * @author hugob 
	 * @param em
	 * @return l'emprunt supprimé
	 */
	public Emprunt supprimerEmprunt(Emprunt em) {
		if(em != null) {
			try {
				Access_BD_Emprunt ae = new Access_BD_Emprunt();
				ae.deleteEmprunt(em);
				
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				em = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				em = null;
			}
		}
		return em;
	}
	
	/**
	 * Méthode permettant d'afficher la fenêtre de gestion des emprunts
	 */
	public void doEmpruntManagementDialog() {
		this.emcViewController.displayDialog();
	}
}