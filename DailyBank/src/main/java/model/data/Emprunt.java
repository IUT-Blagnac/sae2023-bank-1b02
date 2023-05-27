package model.data;

/**
 * @@author hugob
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 */

public class Emprunt {

	public int idEmprunt;
	public double tauxEmprunt;
	public int capitalEmprunt;
	public int dureeEmprunt;
	private java.util.Date  dte = new java.util.Date();
	public  java.sql.Date dateDebutEmprunt = new java.sql.Date(dte.getTime());
	public int idClient;


	public Emprunt(int idEmprunt, double tauxE, int capitalE, int dureeE, int idC) {
		super();
		this.idEmprunt = idEmprunt;
		this.tauxEmprunt = tauxE;
		this.capitalEmprunt = capitalE;
		this.dureeEmprunt = dureeE;
		this.idClient = idC;
	}


	public Emprunt(double tauxE, int capitalE, int dureeE, int idC) {
		super();
		this.tauxEmprunt = tauxE;
		this.capitalEmprunt = capitalE;
		this.dureeEmprunt = dureeE;
		this.idClient = idC;
	}

	public Emprunt(Emprunt em) {
		this(em.tauxEmprunt, em.capitalEmprunt, em.dureeEmprunt, em.idClient);
	}

	public String toString() {
		String s = "Date de l'emprunt : " + this.dateDebutEmprunt + " | Montant de l'emprunt : " + this.capitalEmprunt + " | Taux de l'emprunt : " + this.tauxEmprunt + " | Durée de l'emprunt : " + this.dureeEmprunt + " mois";
		return s;
	}
}