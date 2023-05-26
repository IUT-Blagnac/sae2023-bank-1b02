package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
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
	public ArrayList<Prelevement> getPrelevements(int idNumCompte)
			throws DataAccessException, DatabaseConnexionException {

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
	 * @return Toutes les prélèvements automatiques, liste vide si pas de
	 *         prélèvements
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @author aidan
	 */
	public ArrayList<Prelevement> getPrelevementsParJour(int numJour)
			throws DataAccessException, DatabaseConnexionException {

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
	 * @param montant      montant du prélèvement
	 * @param date         date du prélèvement (date récurrente)
	 * @param beneficiaire bénéficiaire du prélèvement (exemple : EDF)
	 * @param idNumCompte  id du compte sur lequel le prélèvement est effectué
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @return id du prélèvement inséré
	 * @authors Bernat & Aidan
	 */
	public int insertPrelevement(double montant, int date, String beneficiaire, int idNumCompte)
			throws DatabaseConnexionException, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "INSERT INTO PrelevementAutomatique VALUES (seq_id_prelevAuto.NEXTVAL" + ", " + "?" + ", "
					+ "?" + ", " + '?' + ", " + "?" + ")";
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
			int newPrelev = rs2.getInt(1);

			pst.close();
			rs2.close();
			con.commit();

			return newPrelev;

		} catch (SQLException e) {
			throw new DataAccessException(Table.Operation, Order.INSERT, "Erreur accès", e);
		}
	}

	public void updatePrelevement(double montant, int date, String beneficiaire, int idNumCompte, int idprelev)
			throws DatabaseConnexionException, DataAccessException {
		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "UPDATE PrelevementAutomatique SET montant = ?, dateRecurrente = ?, beneficiaire = ? WHERE idprelev = ?";
			PreparedStatement pst = con.prepareStatement(query);

			pst.setDouble(1, montant);
			pst.setInt(2, date);
			pst.setString(3, beneficiaire);
			pst.setInt(4, idprelev);

			System.err.println(query);
			pst.executeUpdate();
			con.commit();

		} catch (SQLException e) {
			throw new DataAccessException(Table.Client, Order.UPDATE, "Erreur accès", e);
		}
	}

	/**
	 * Suppression d'un prélèvement dans la base de données.
	 * 
	 * @param idPrelev id du prélèvement à supprimer
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @author Bernat
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
}
