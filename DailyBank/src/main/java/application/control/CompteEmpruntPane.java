package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.CompteEmpruntController;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Emprunt;
import model.orm.Access_BD_Emprunt;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class CompteEmpruntPane {
	
	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;
	private CompteEmpruntController cecViewController;
	
	/**
	 * @param _parentStage
	 * @param _dbstate
	 * @param client
	 */
	
	public CompteEmpruntPane(Stage _parentStage, DailyBankState _dbstate, Client client) {
		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(CompteEmpruntController.class.getResource("compteparametreemprunt.fxml"));
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

			this.cecViewController = loader.getController();
			this.cecViewController.initContext(this.primaryStage, this, _dbstate, client);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doEmpruntDialog() {
		this.cecViewController.displayDialog();
	}
	
	/**
	 * Méthode permettant de simuler un emprunt 
	 * @author hugob
	 */
	public Emprunt simulerEmprunt(Emprunt emp) {
		try {
			Access_BD_Emprunt ac = new Access_BD_Emprunt();
			ac.nouveauEmprunt(emp);
			
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			
		}
		return emp;
	}
}