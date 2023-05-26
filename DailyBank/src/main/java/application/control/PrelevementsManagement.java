package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.CategorieOperation;
import application.tools.EditionMode;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import application.view.PrelevementsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.Prelevement;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.Access_BD_Prelevements;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class PrelevementsManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private PrelevementsManagementController omcViewController;
	private CompteCourant compteConcerne;
	private Prelevement prelevementConcerne;

	public PrelevementsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant cpt) {

		this.compteConcerne = cpt;
		this.dailyBankState = _dbstate;
		
		try {
			FXMLLoader loader = new FXMLLoader(
			OperationsManagementController.class.getResource("prelevementsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des prélèvements");
			this.primaryStage.setResizable(false);

			this.omcViewController = loader.getController();
			this.omcViewController.initContext(this.primaryStage, this, _dbstate, client, compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doPrelevementsManagementDialog() {
		this.omcViewController.displayDialog();
	}
	
	/**
	 * Nouveau prelevement sur le compte courant
	 * @return prelevement enregistré ou null si annulation
	 * @author Bernat
	 */
	public Prelevement enregistrerPrelevement() {
		
		PrelevementEditorPane oep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
		Prelevement prelev = oep.doPrelevementEditorDialog(null, EditionMode.CREATION);
		
		if (prelev != null) {
			try {
				Access_BD_Prelevements ao = new Access_BD_Prelevements();
				int idPrelev = ao.insertPrelevement(prelev.montant, prelev.dateRecurrente, prelev.beneficiaire, this.compteConcerne.idNumCompte);
				prelev.idPrelev = idPrelev;
				
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				prelev = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				prelev = null;
			}
		}
		
		return prelev;
	}

	/**
	 * Modification d'un prelevement
	 * @param prvmt prelevement à modifier
	 * @return prelevement modifié ou null si annulation
	 * @author Jimmy
	 */
	public Prelevement modifierPrelevement(Prelevement prvmt) {
		
		PrelevementEditorPane oep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
		Prelevement prelev = oep.doPrelevementEditorDialog( prvmt, EditionMode.MODIFICATION);
		
		if (prelev != null) {
			try {
				Access_BD_Prelevements ao = new Access_BD_Prelevements();
				ao.updatePrelevement(prelev.montant, prelev.dateRecurrente, prelev.beneficiaire, this.compteConcerne.idNumCompte, prelev.idPrelev);
				
				
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				prelev = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				prelev = null;
			}
		}
		
		return prelev;
	}

	/**
	 * Suppression d'un prelevement
	 * @param prvmt prelevement à supprimer
	 * @return prelevement supprimé ou null si annulation
	 * @author Bernat
	 */
	public Prelevement supprimerPrelevement(Prelevement prvmt) {
		PrelevementEditorPane oep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
		Prelevement prelev = oep.doPrelevementEditorDialog(prvmt, EditionMode.SUPPRESSION);
		
		if (prelev != null) {
			try {
				Access_BD_Prelevements ao = new Access_BD_Prelevements();
				ao.deletePrelevement(prelev.idPrelev);
								
				
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				prelev = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				prelev = null;
			}
		}
		
		return prelev;
	}
	
	/**
	 * Recupere la liste des prélevements et le solde du compte courant 
	 * @return paire de valeurs avec les operations et le solde du compte
	 */
	public PairsOfValue<CompteCourant, ArrayList<Prelevement>> operationsEtSoldeDunCompte() {
		ArrayList<Prelevement> listeOP = new ArrayList<>();

		try {
			// Relecture BD du solde du compte
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

			// lecture BD de la liste des prélevements du compte de l'utilisateur
			Access_BD_Prelevements ao = new Access_BD_Prelevements();
			listeOP = ao.getPrelevements(this.compteConcerne.idNumCompte);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeOP = new ArrayList<>();
			
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeOP = new ArrayList<>();
			
		}
		
		return new PairsOfValue<>(this.compteConcerne, listeOP);
	}
}
