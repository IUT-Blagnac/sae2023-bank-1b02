package application.control;

import java.time.LocalDate;
import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Prelevement;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.Access_BD_Prelevements;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class BatchManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	
	public BatchManagement() {
		
	}

	/**
	 * Constructeur de la classe BatchManagement
	 * 
	 * @param _parentStage
	 * @param _dbstate
	 */
	public BatchManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState = _dbstate;
		
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
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

			//this.cmcViewController = loader.getController();
			//this.cmcViewController.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void efectuerPrelevAutomatiques () {
		ArrayList<Prelevement> alPrelevements;
		LocalDate currentdate = LocalDate.now();
		int cptModifCounter = 0;
		int currentDay = currentdate.getDayOfMonth();
		
		try {
			Access_BD_Prelevements ao = new Access_BD_Prelevements();
			Access_BD_Operation op = new Access_BD_Operation();
			alPrelevements = ao.getPrelevementsParJour(currentDay);
			int currentClient = -1;
			int debitRealises = 0;
			
			for (int i=0; i<alPrelevements.size(); i++) {
				Prelevement prelev = alPrelevements.get(i);
			
				if ((currentClient == -1) || !(currentClient == prelev.idNumCompte)) {
					currentClient = prelev.idNumCompte;
					cptModifCounter++;
				}
				
				try {
					op.insertDebit(prelev.idNumCompte, prelev.montant, "Prélèvement automatique");
					debitRealises++;
					System.out.println(prelev.idNumCompte + ", " + prelev.montant + ", Prélèvement automatique : "+ prelev.beneficiaire);
					
				} catch (DatabaseConnexionException e) {
					ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
					ed.doExceptionDialog();
					this.primaryStage.close();
					
				} catch (ApplicationException ae) {
					Alert batchAlert = new Alert(AlertType.ERROR);
					batchAlert.setHeaderText("Prélèvement automatique NON EFECTUÉ pour le compte "+ prelev.idNumCompte);
					batchAlert.setContentText("Découvert autorisé dépassé, le prélèvement "+ prelev.idPrelev +" ("+ prelev.beneficiaire +", "+ prelev.montant +"€) n'a pas été efectué.");
					System.err.println("ERR : "+ prelev.idNumCompte + ", " + prelev.montant + ", Prélèvement automatique : "+ prelev.beneficiaire);
					batchAlert.showAndWait();
					
				}
			}
			
			Alert batchAlert = new Alert(AlertType.INFORMATION);
			batchAlert.setHeaderText("Prélèvements automatiques pour le jour "+ currentDay);
			batchAlert.setContentText("Dans cette exécution, "+ debitRealises +"/"+ alPrelevements.size() + " prélèvements ont été réalisés sur "+ cptModifCounter +" comptes différentes.");
			batchAlert.showAndWait();
			
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();

		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
		}
		
	}
	
	
	public void genererRelevesMensuels () {
		Alert batchAlert = new Alert(AlertType.INFORMATION);
		batchAlert.setHeaderText("Relevés mensuels en dev");
		batchAlert.showAndWait();
	}
}
