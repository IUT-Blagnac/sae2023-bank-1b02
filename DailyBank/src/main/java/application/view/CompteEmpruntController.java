package application.view;

import java.util.ArrayList;
import java.util.Locale;

import application.DailyBankState;
import application.control.CompteEmpruntPane;
import application.control.ComptesManagement;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Emprunt;

public class CompteEmpruntController {
		// Etat courant de l'application
		private DailyBankState dailyBankState;

		// Contrôleur de Dialogue associé à ComptesEmpruntController
		private CompteEmpruntPane cmDialogController;

		// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
		private Stage primaryStage;
		
		//Données de la fenêtre
		private Client clientDesComptes;
		
		private ObservableList<CompteCourant> oListCompteCourant;
		
		private double tauxAnnuel;
		private double tauxEmprunt;
		
		// Attributs de la page FXML
				@FXML
				private TextField montantEmprunt;
				@FXML
				private TextField dureeEmprunt;
				@FXML 
				private TextField tauxApplicable;
				@FXML
				private Button btnAnnuler;
				@FXML
				private Button btnValider;
		
		// Manipulation de la fenêtre
				public void initContext(Stage _containingStage, CompteEmpruntPane _cm, DailyBankState _dbstate, Client client) {
					this.cmDialogController = _cm;
					this.primaryStage = _containingStage;
					this.dailyBankState = _dbstate;
					this.clientDesComptes = client;
					this.configure();
					}
		
		//Configuration de la fenêtre 
		public void configure() {
			this.tauxAnnuel = 0.10;
			this.tauxEmprunt = this.tauxAnnuel/12;
			this.tauxApplicable.setText(String.format(Locale.ENGLISH, "%1.2f",this.tauxEmprunt));
			this.tauxApplicable.setEditable(false);
		}
		
		@FXML
		private void doAnnuler() {
			this.primaryStage.close();
		}
		
		@FXML
		private void doValider() {
			if(this.montantEmprunt.getText().trim().isEmpty()) {
				Alert alt = new Alert(AlertType.WARNING);
				alt.setContentText("Vous devez choisir un montant à emprunter");
				alt.showAndWait();
			}else if(this.dureeEmprunt.getText().trim().isEmpty()) {
				Alert alt = new Alert(AlertType.WARNING);
				alt.setContentText("Vous devez choisir une durée en mois sur l'emprunt");
				alt.showAndWait();
			}else {
				Emprunt emprunt = new Emprunt(this.tauxEmprunt,Integer.parseInt(this.montantEmprunt.getText().trim()), Integer.parseInt(this.dureeEmprunt.getText().trim()), this.clientDesComptes.idNumCli);
				this.cmDialogController.simulerEmprunt(emprunt);
				this.primaryStage.close();
			}
		}
		
		public void displayDialog() {
			this.primaryStage.showAndWait();
		}
		
}