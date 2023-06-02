package application.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class OperationsManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private OperationsManagementController omcViewController;
	private Client clientDuCompte;
	private CompteCourant compteConcerne;

	public OperationsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {

		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("operationsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des opérations");
			this.primaryStage.setResizable(false);

			this.omcViewController = loader.getController();
			this.omcViewController.initContext(this.primaryStage, this, _dbstate, client, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doOperationsManagementDialog() {
		this.omcViewController.displayDialog();
	}

	/**
	 * execute la requête de débit sur le compte courant
	 * @return opération enregistrée ou null si annulation
	 */
	public Operation enregistrerDebit() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.DEBIT);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();

				ao.insertDebit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}
	/**
	 * execute la requête de débit sur le compte courant
	 * @return opération enregistrée ou null si annulation
	 * @author Bernat
	 */
	public Operation enregistrerDebitExeptionnel() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.DEBIT);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();

				ao.insertDebitExeptionnel(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}
	/**
	 * execute la requête de crédit sur le compte courant
	 * @return opération enregistrée ou null si annulation
	 * @author SOLDEVILA Bernat
	 */
	public Operation enregistrerCredit() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.CREDIT);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();
				
				ao.insertCredit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}
	
	
	
	/**
	 * fonction qui creer le pdf des operations du compte courant en utilisant itext
	 */
	public void genererRelevePdf(){
		Document document = new Document();
        try {
        	FileChooser fileChooser = new FileChooser();
        	fileChooser.setTitle("Save");

        	// Créer un filtre d'extension pour les fichiers PDF
        	ExtensionFilter pdfFilter = new ExtensionFilter("PDF Files (*.pdf)", "*.pdf");
        	fileChooser.getExtensionFilters().add(pdfFilter);

        	// Afficher le dialogue et récupérer le fichier choisi
        	File selectedFile = fileChooser.showSaveDialog(this.primaryStage);

        	if (selectedFile != null) {
        	    String selectedFilePath = selectedFile.getAbsolutePath();
        	    // Utilisez le chemin du fichier choisi
        	    PdfWriter.getInstance(document, new FileOutputStream(selectedFilePath));
        	}
            
            document.open();
            
            Font largeFont = new Font(Font.FontFamily.HELVETICA, 35); 
			Font midFont = new Font(Font.FontFamily.HELVETICA, 20); // Définir une taille de police plus grande
            document.add(new Paragraph("Banque ZID-ANE", largeFont));
            document.add(new Paragraph(""+this.dailyBankState.getAgenceActuelle().nomAg+"\n"+this.dailyBankState.getAgenceActuelle().adressePostaleAg+"\n06449643489\nemailpro.@banque.com\n\n"));
			
            document.add(new Paragraph("---------------------------------------------\nNuméro de compte : "+ this.compteConcerne.idNumCompte+"\nTitulaire du compte : "+this.clientDuCompte.nom+" "+this.clientDuCompte.prenom+"\nAdresse postal: "+ this.clientDuCompte.adressePostale +"\ntelephone: "+ this.clientDuCompte.telephone +"\nEmail: "+ this.clientDuCompte.email+"\n\n"));
			if (this.compteConcerne.estCloture.equals("O") ) {
				document.add(new Paragraph("Compte Cloturé", largeFont));
			}
			document.add(new Paragraph("---------------------------------------------"));
            document.add(new Paragraph("Liste operations", midFont));
			document.add(new Paragraph("---------------------------------------------\n\n"));

			PdfPTable table = new PdfPTable(5); // Création du tableau à 4 colonnes
			table.setWidthPercentage(100);
            table.addCell("Date");
            table.addCell("Opération");
            table.addCell("Débit");
            table.addCell("Crédit");
            table.addCell("Solde");
			PairsOfValue<CompteCourant, ArrayList<Operation>> opesEtCompte = this.operationsEtSoldeDunCompte();
            ArrayList<Operation> listeOP;
			this.compteConcerne = opesEtCompte.getLeft();
			listeOP = opesEtCompte.getRight();
			double solde=0;
			double totalcredits=0;
			double totaldebits=0;
			for (Operation op : listeOP) {
				solde = solde + op.montant;
				if (op.montant < 0) {
					addOperation(table, op.dateOp.toGMTString(), ""+op.idTypeOp, ""+op.montant, "", ""+solde);
					totalcredits = totalcredits + op.montant;
				} else {
					addOperation(table, op.dateOp.toGMTString(), ""+op.idTypeOp, "", ""+op.montant, ""+solde);
					totaldebits = totaldebits + op.montant;
				}
			}
            document.add(table);

			// Récapitulatif
            document.add(new Paragraph("\n---------------------------------------------"));
            document.add(new Paragraph("Récapitulatif", midFont));
            document.add(new Paragraph("---------------------------------------------\nNombre total d'opérations : "+listeOP.size()+"\nTotal des debits: "+totaldebits+"€\nTotal des credits: "+totalcredits+"€\nSolde total : " + this.compteConcerne.solde + " €"));

            document.close();
            System.out.println("Le relevé des opérations a été créé avec succès !");
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
	
	
	
	public static void addOperation(PdfPTable table, String date, String operation, String debit, String credit, String solde) {
        table.addCell(date);
        table.addCell(operation);
        table.addCell(debit);
        table.addCell(credit);
        table.addCell(solde);
    }

	/**
	 * Recupere la liste des opérations et le solde du compte courant 
	 * @return paire de valeurs avec les operations et le solde du compte
	 */
	public PairsOfValue<CompteCourant, ArrayList<Operation>> operationsEtSoldeDunCompte() {
		ArrayList<Operation> listeOP = new ArrayList<>();

		try {
			// Relecture BD du solde du compte
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

			// lecture BD de la liste des opérations du compte de l'utilisateur
			Access_BD_Operation ao = new Access_BD_Operation();
			listeOP = ao.getOperations(this.compteConcerne.idNumCompte);

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
		System.out.println(this.compteConcerne.solde);
		return new PairsOfValue<>(this.compteConcerne, listeOP);
	}
}
