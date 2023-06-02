package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.Emprunt;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/**
 *
 * Classe d'accès aux Emprunts en BD Oracle.
 *
 */

public class Access_BD_Emprunt{

	public Access_BD_Emprunt() {
	}

	/**
	 * Ajout d'un Emprunt dans la base de données
	 * @param emp Emprunt (clé primaire)
	 * @throws DataAccessException               Erreur d'accès aux données (requête
	 *                                           mal formée ou autre)
	 * @throws DatabaseConnexionException        Erreur de connexion
	 * @author hugob
	 */
	public void nouveauEmprunt(Emprunt emp) throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException{
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO EMPRUNT VALUES ("+"seq_id_emprunt.NEXTVAL" + ", " + "?" + ", " + "?" + ", " + "?" + ", " + "?" + ", " + "?" + ")"; 

			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, emp.tauxEmprunt);
			pst.setInt(2, emp.capitalEmprunt);
			pst.setInt(3, emp.dureeEmprunt);
			pst.setDate(4, emp.dateDebutEmprunt);
			pst.setInt(5,emp.idClient);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Emprunt, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_emprunt.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			emp.idEmprunt = rs.getInt(1);

			con.commit();
			rs.close();
			pst2.close();

		}catch(SQLException e) {
			throw new DataAccessException(Table.Emprunt, Order.SELECT, "Erreur accès", e);
		}		
	}
	/**
	 * Recherche des Emprunts d'un client à partir de son id.
	 *
	 * @param idNumCli id du client dont on cherche les emprunts
	 * @return Tous les Emprunts de idNumCli (ou liste vide)
	 * @throws DataAccessException        Erreur d'accès aux données (requête mal
	 *                                    formée ou autre)
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @author hugob
	 */
	public ArrayList<Emprunt> getEmprunts(int idNumCli)
			throws DataAccessException, DatabaseConnexionException {

		ArrayList<Emprunt> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM Emprunt where idNumCli = ?";
			query += " ORDER BY idEmprunt";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCli);
			System.err.println(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idEmprunt = rs.getInt("idEmprunt");
				double tauxEmprunt = rs.getDouble("tauxEmp");
				int cap = rs.getInt("capitalEmp");
				int dureeEmp = rs.getInt("dureeEmp");
				int idNumCliTROUVE = rs.getInt("idNumCli");

				alResult.add(new Emprunt(idEmprunt, tauxEmprunt, cap, dureeEmp, idNumCliTROUVE));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.Emprunt, Order.SELECT, "Erreur accès", e);
		}

		return alResult;
	}

	/**
	 * Suppression d'un emprunt dans la base de données.
	 * 
	 * @param emp l'emprunt à supprimer
	 * @author hugob
	 * @throws DatabaseConnexionException Erreur de connexion
	 * @throws DataAccessException Erreur d'accès aux données (requête mal formée ou autre)
	 * 
	 */
	public void deleteEmprunt(Emprunt emp) 
			throws DataAccessException, DatabaseConnexionException, RowNotFoundOrTooManyRowsException   {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE FROM Emprunt" +  " WHERE idEmprunt = " + "? ";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, emp.idEmprunt);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.Emprunt, Order.DELETE,
						"Delete anormal (Supression de moins ou plus d'une ligne)", null, result);
			}

			con.commit();

		}catch (SQLException e) {
			throw new DataAccessException(Table.Emprunt, Order.DELETE, "Erreur accès", e);
		}
	}
}