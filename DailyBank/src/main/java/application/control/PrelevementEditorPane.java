package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.OperationEditorPaneController;
import application.view.PrelevementEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Prelevement;

public class PrelevementEditorPane {

	private Stage primaryStage;
	private PrelevementEditorPaneController oepcViewController;

	public PrelevementEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(
					OperationEditorPaneController.class.getResource("prelevementeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Enregistrement d'un prélèvement");
			this.primaryStage.setResizable(false);

			this.oepcViewController = loader.getController();
			this.oepcViewController.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Ouvre la fenêtre de dialogue de gestion d'un prélèvement
	 * @param pvmt prélèvement à gérer
	 * @param mode mode d'édition
	 * @return le prélèvement géré
	 * @author Bernat
	 */
	public Prelevement doPrelevementEditorDialog(Prelevement pvmt, EditionMode mode) {
		return this.oepcViewController.displayDialog( pvmt, mode);
	}
}
