package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	 * @param idNumClient id du client (clé primaire)
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
	
}