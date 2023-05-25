package model.orm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import model.data.Client;
import model.data.Operation;
import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/**
 * Classe d'accès aux Operation en BD Oracle.
 */
public class Access_BD_Prelevements {

	public Access_BD_Prelevements() {
	}

	/**
	 * Recherche de toutes les prélèvements d'un compte.
	 *
	 * @param idNumCompte id du compte dont on cherche toutes les prélèvements
	 * @return Toutes les prélèvements du compte, liste vide si pas de prélèvements
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 */
	public ArrayList<Prelevement> getPrelevements(int idNumCompte) throws DataAccessException, DatabaseConnexionException {

		ArrayList<Prelevement> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM PrelevementAutomatique WHERE idNumCompte = ?";
			query += " ORDER BY idPrelev";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idPrelev = rs.getInt("idPrelev");
				double montant = rs.getDouble("montant");
				int dateRecurrente = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idNumCompteTrouve = rs.getInt("idNumCompte");

				alResult.add(new Prelevement(idPrelev, montant, dateRecurrente, beneficiaire, idNumCompteTrouve));
			}

			rs.close();
			pst.close();
			return alResult;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
		}
	}
	
	/**
	 * Recherche de touts les prélèvements d'un jour.
	 *
	 * @param numJour jour dont on cherche toutes les prélèvements automatiques
	 * @return Toutes les prélèvements automatiques, liste vide si pas de prélèvements
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 */
	public ArrayList<Prelevement> getPrelevementsParJour(int numJour) throws DataAccessException, DatabaseConnexionException {

		ArrayList<Prelevement> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM PrelevementAutomatique WHERE dateRecurrente = ?";
			query += " ORDER BY idNumCompte";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, numJour);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idPrelev = rs.getInt("idPrelev");
				double montant = rs.getDouble("montant");
				int dateRecurrente = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idNumCompteTrouve = rs.getInt("idNumCompte");

				alResult.add(new Prelevement(idPrelev, montant, dateRecurrente, beneficiaire, idNumCompteTrouve));
			}

			rs.close();
			pst.close();
			return alResult;
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.SELECT, "Erreur accès", e);
		}
	}


	/**
	 * Insertion d'un prélèvement dans la base de données.
	 * 
	 * @param montant montant du prélèvement
	 * @param date date du prélèvement (date récurrente)
	 * @param beneficiaire bénéficiaire du prélèvement (exemple : EDF)
	 * @param idNumCompte id du compte sur lequel le prélèvement est effectué
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @throws DataAccessException Erreur d'accès aux données (requête mal formée ou autre)
	*/
	public int insertPrelevement(double montant, int date, String beneficiaire, int idNumCompte) throws DatabaseConnexionException, DataAccessException {
		try {			
			Connection con = LogToDatabase.getConnexion();
			String query = "INSERT INTO PrelevementAutomatique VALUES (seq_id_prelevAuto.NEXTVAL" + ", " + "?" + ", " + "?" + ", " + '?' + ", " + "?" + ")";
			PreparedStatement pst = con.prepareStatement(query);
			
			pst.setDouble(1, montant);
			pst.setInt(2, date);
			pst.setString(3, beneficiaire);
			pst.setInt(4, idNumCompte);

			System.err.println(query);
			pst.executeUpdate();
			
			PreparedStatement pst2 = con.prepareStatement("SELECT seq_id_prelevAuto.CURRVAL from DUAL");
			ResultSet rs2 = pst2.executeQuery();
			rs2.next();
			int newPrelev  = rs2.getInt(1);
			
			pst.close();
			rs2.close();
			con.commit();
			
			return newPrelev;
			
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.INSERT, "Erreur accès", e);
		}
	}

	/**
	 * Suppression d'un prélèvement dans la base de données.
	 * 
	 * @param idPrelev id du prélèvement à supprimer
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @throws DataAccessException Erreur d'accès aux données (requête mal formée ou autre)
	 * 
	 */
	public void deletePrelevement(int idPrelev) throws DatabaseConnexionException, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE FROM prelevementautomatique WHERE idPrelev = ?";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idPrelev);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			
			
		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.DELETE, "Erreur accès", e);
		}
	}


	/*
	 * Fonction utilitaire qui retourne un ordre sql "to_date" pour mettre une date
	 * dans une requête sql
	 *
	 * @param d Date (java.sql) à transformer
	 *
	 * @return Une chaine : TO_DATE ('j/m/a', 'DD/MM/YYYY') 'j/m/a' : jour mois an
	 * de d ex : TO_DATE ('25/01/2019', 'DD/MM/YYYY')
	 
	private String dateToString(Date d) {
		String sd;
		Calendar cal;
		cal = Calendar.getInstance();
		cal.setTime(d);
		sd = "" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
		sd = "TO_DATE( '" + sd + "' , 'DD/MM/YYYY')";
		r
		return sd;
	}*/


}
