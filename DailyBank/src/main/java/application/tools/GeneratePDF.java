package application.tools;

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

import application.control.OperationsManagement;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.data.AgenceBancaire;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_AgenceBancaire;
import model.orm.Access_BD_Operation;
import model.orm.exception.ApplicationException;

public class GeneratePDF {
	
	public static void genererRelevePdf(Client client, CompteCourant compte, int selMonth, int selYear){
		FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Save File");
		fileChooser.setInitialFileName(compte.idNumCli+ "-" +compte.idNumCompte+ " Relevé mensuel " +selMonth+ "-" +selYear+ ".pdf");

    	// Créer un filtre d'extension pour les fichiers PDF
    	ExtensionFilter pdfFilter = new ExtensionFilter("PDF Files (*.pdf)", "*.pdf");
    	fileChooser.getExtensionFilters().add(pdfFilter);

    	// Afficher le dialogue et récupérer le fichier choisi
		File selectedFile = fileChooser.showSaveDialog(null);
		genererRelevePdfParMois(selectedFile.getAbsolutePath(), client, compte, selMonth, selYear);
    }
	
	/**
	 * Creation de PDFs
	 */
	public static void genererRelevePdfParMois(String path, Client client, CompteCourant compte, int selMonth, int selYear){
		Document document = new Document();
        
		try {
        	PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            
            Font largeFont = new Font(Font.FontFamily.HELVETICA, 35); 
			Font midFont = new Font(Font.FontFamily.HELVETICA, 20); // Définir une taille de police plus grande
			
            try {
				Access_BD_AgenceBancaire ag = new Access_BD_AgenceBancaire();
				AgenceBancaire cAg = ag.getAgenceBancaire(client.idAg);
				
	            document.add(new Paragraph("Banque DailyBank", largeFont));
	            document.add(new Paragraph(cAg.nomAg+"\n"
	            						  +cAg.adressePostaleAg+"\n"
	            						  +"06449643489\n"+
	            						  "contact@dailybank.com\n\n"));
			} catch (ApplicationException ae) {
				Alert batchAlert = new Alert(AlertType.ERROR);
				batchAlert.setTitle("Erreur");
				batchAlert.setHeaderText("Erreur lors de la génération du PDF");
				batchAlert.setContentText("Une erreur est survenue lors de la génération du PDF. Veuillez réessayer.");
				batchAlert.showAndWait();
			}
            
            document.add(new Paragraph("---------------------------------------------\n"
            						   +"Numéro de compte : "+ compte.idNumCompte +"\n"
            						   +"Titulaire du compte : "+ client.nom +" "+ client.prenom +"\n"
            						   +"Adresse postal: "+ client.adressePostale +"\n"
            						   +"Telephone: "+ client.telephone +"\n"
            						   +"Email: "+ client.email+"\n\n"));
            
			if (compte.estCloture.equals("O") ) {
				document.add(new Paragraph("Compte Cloturé", largeFont));
			}
			
			document.add(new Paragraph("---------------------------------------------"));
            document.add(new Paragraph("Liste operations", midFont));
			document.add(new Paragraph("---------------------------------------------\n\n"));
			
			

			PdfPTable table = new PdfPTable(5); // Création du tableau à 4 colonnes
			table.setWidthPercentage(100);
            table.addCell("Date"); table.addCell("Opération"); table.addCell("Débit"); table.addCell("Crédit"); table.addCell("Solde");
            
			double solde=0;
			double totalcredits=0;
			double totaldebits=0;
			
			try {
				Access_BD_Operation aOp = new Access_BD_Operation();
				ArrayList<Operation> listeOP = aOp.getOperationsInMonth(compte.idNumCompte, selMonth, selYear);
			
				for (Operation op : listeOP) {
					solde =+ op.montant;
					
					if (op.montant < 0) {
						OperationsManagement.addOperation(table, op.dateOp.toGMTString(), ""+op.idTypeOp, ""+op.montant, "", ""+solde);
						totalcredits =+ op.montant;
					} else {
						OperationsManagement.addOperation(table, op.dateOp.toGMTString(), ""+op.idTypeOp, "", ""+op.montant, ""+solde);
						totaldebits =+ op.montant;
					}
				}
				
				document.add(table);

				// Récapitulatif
	            document.add(new Paragraph("\n---------------------------------------------"));
	            document.add(new Paragraph("Récapitulatif", midFont));
	            document.add(new Paragraph("---------------------------------------------\n"
	            						   +"Nombre total d'opérations : "+listeOP.size()+"\n"
	            						   +"Total des debits: "+totaldebits+"€\n"
	            						   +"Total des credits: "+totalcredits+"€\n"
	            						   +"Solde total : " + compte.solde + "€"));
			} catch (ApplicationException ae) {
				Alert batchAlert = new Alert(AlertType.ERROR);
				batchAlert.setTitle("Erreur");
				batchAlert.setHeaderText("Erreur lors de la génération du PDF");
				batchAlert.setContentText("Une erreur est survenue lors de la génération du PDF. Veuillez réessayer.");
				batchAlert.showAndWait();
			}
			
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
