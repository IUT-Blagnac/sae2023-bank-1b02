package application.view;

import java.util.Locale;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;

public class PrelevementEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private EditionMode editionMode;
	private Client clientDuCompte;
	private Prelevement prelevementEdite;
	private Prelevement prelevementResultat;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}
	/**
	 * Méthode de configuration de la fenêtre
	 * 
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.txtBeneficiaire.focusedProperty().addListener((t, o, n) -> this.focusBeneficiaire(t, o, n));
		this.txtMontant.focusedProperty().addListener((t, o, n) -> this.focusMontant(t, o, n));
		this.txtDateRecurrente.focusedProperty().addListener((t, o, n) -> this.focusDate(t, o, n));
	}
	/**
	 * Méthode de gestion de la fermeture de la fenêtre
	 * @param client le client concerné
	 * @param cpte le compte concerné
	 * @param mode le mode d'édition
	 * @return
	 */
	public Prelevement displayDialog( Prelevement pvmt, EditionMode mode) {
		this.editionMode = mode;
		if (pvmt == null) {
			this.prelevementEdite = new Prelevement(0, 0, 1, "", 0);
		} else {
			this.prelevementEdite = new Prelevement(pvmt);
			this.txtDateRecurrente.setText(""+this.prelevementEdite.dateRecurrente);
			this.txtBeneficiaire.setText(""+this.prelevementEdite.beneficiaire);
			this.txtMontant.setText(""+this.prelevementEdite.montant);
		}
		this.prelevementResultat = null;
		
		switch (mode) {
		case CREATION:
			this.lblTitre.setText("Informations sur le nouveau prélèvement");
			this.btnOk.setText("Ajouter");
			this.btnCancel.setText("Annuler");
			break;
		case MODIFICATION:
			AlertUtilities.showAlert(this.primaryStage, "Non implémenté", "Modif de prelevement n'est pas implémenté", null,
					AlertType.ERROR);
			return null;
		// break;
		case SUPPRESSION:
			this.lblTitre.setText("Informations sur le prélèvement à supprimer");
			this.btnOk.setText("Supprimer");
			this.btnCancel.setText("Annuler");
			
			this.txtMontant.setDisable(true);
			this.txtMontant.setEditable(false);
			this.txtMontant.setStyle("-fx-opacity: 1; ");
			
			this.txtBeneficiaire.setDisable(true);
			this.txtBeneficiaire.setEditable(false);
			this.txtBeneficiaire.setStyle("-fx-opacity: 1; ");
			
			this.txtDateRecurrente.setDisable(true);
			this.txtDateRecurrente.setEditable(false);
			this.txtDateRecurrente.setStyle("-fx-opacity: 1; ");
			
			
			break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		// initialisation du contenu des champs
		//----POUR MODIF PRELEVEMENT AJOUTER LES VALEURS PAR DEFAULT ICI
		
		//this.prelevementResultat = null;
		

		this.primaryStage.showAndWait();
		return this.prelevementResultat;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}
	
	// Attributs de la scene + actions
	@FXML
	private Label lblTitre;
	@FXML
	private TextField txtMontant;
	@FXML
	private TextField txtDateRecurrente;
	@FXML
	private TextField txtBeneficiaire;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	/**
	 * Modification du montant du prélèvement sur interface
	 * @param txtField le champ de texte
	 * @param oldPropertyValue l'ancienne valeur
	 * @param newPropertyValue la nouvelle valeur
	 * @return null
	 */
	private Object focusMontant(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				double val;
				this.txtMontant.setStyle("-fx-text-fill: black; ");
				val = Double.parseDouble(this.txtMontant.getText().trim());
				if (val < 0) {
					throw new NumberFormatException();
				}
				this.prelevementEdite.montant = val;
			} catch (NumberFormatException nfe) {
				this.txtMontant.setText(String.format(Locale.ENGLISH, "%3.2f", this.prelevementEdite.montant));
				this.txtMontant.setStyle("-fx-text-fill: red; ");
			}
		}
		this.txtMontant.setText(String.format(Locale.ENGLISH, "%3.2f", this.prelevementEdite.montant));
		return null;
	}
	
	/**
	 * Modification du montant du prélèvement sur interface
	 * @param txtField le champ de texte
	 * @param oldPropertyValue l'ancienne valeur
	 * @param newPropertyValue la nouvelle valeur
	 * @return null
	 */
	private Object focusDate(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				this.txtDateRecurrente.setStyle("-fx-text-fill: black; ");
				double val;
				val = Double.parseDouble(this.txtDateRecurrente.getText().trim());
				if (val < 0.1 || val >31) {
					throw new NumberFormatException();
				}
				this.prelevementEdite.dateRecurrente = ( int ) val;
			} catch (NumberFormatException nfe) {
				this.txtDateRecurrente.setText(String.format(Locale.ENGLISH, "%01d", this.prelevementEdite.dateRecurrente));
				this.txtDateRecurrente.setStyle("-fx-text-fill: red; ");
			}
		}
		this.txtDateRecurrente.setText(String.format(Locale.ENGLISH, "%01d", this.prelevementEdite.dateRecurrente));
		return null;
	}
	/**
	 * Modification du beneficiaire du prélèvement sur interface
	 * @param txtField le champ de texte
	 * @param oldPropertyValue l'ancienne valeur
	 * @param newPropertyValue la nouvelle valeur
	 * @return null
	 */
	private Object focusBeneficiaire(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				this.txtBeneficiaire.setStyle("");
				String val;
				val = this.txtBeneficiaire.getText().trim();
				
				this.prelevementEdite.beneficiaire = val;
			} catch (NumberFormatException nfe) {
				this.txtBeneficiaire.setText(String.format(Locale.ENGLISH, "%s", this.prelevementEdite.beneficiaire));
			}
		}
		this.txtBeneficiaire.setText(String.format(Locale.ENGLISH, "%s", this.prelevementEdite.beneficiaire));
		return null;
	}
	
	/**
	 * Annulation de la saisie, fermeture de la fenêtre
	 */
	@FXML
	private void doCancel() {
		this.prelevementResultat = null;
		this.primaryStage.close();
	}

	/**
	 * Validation de la saisie, fermeture de la fenêtre
	 */
	@FXML
	private void doAjouter() {
		switch (this.editionMode) {
		case CREATION:
			if (this.isSaisieValide()) {
				
				this.prelevementResultat = this.prelevementEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.prelevementResultat = this.prelevementEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.prelevementResultat = this.prelevementEdite;
			this.primaryStage.close();
			break;
		}

	}

	/**
	 * Vérification de la validité de la saisie
	 * @return true si la saisie est valide
	 */
	private boolean isSaisieValide() {
		if ( this.prelevementEdite.montant < 0) {
			System.out.println("Montant invalide");
			return false;
		} else if (this.prelevementEdite.beneficiaire == "") {
			this.txtBeneficiaire.setStyle("-fx-control-inner-background: #ffcbca; ");
			System.out.println("Aucun bénéficiaire fourni");
			return false;
		}else {
			return true;
		}
	}
}
