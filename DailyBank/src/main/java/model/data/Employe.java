package model.data;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 */

public class Employe {

	public int idEmploye;
	public String nom, prenom, droitsAccess;
	public String login, motPasse;

	public int idAg;

	/**
	 * Employe : Constructeur complet
	 * 
	 * @param idEmploye IN : identifiant de l'employé
	 * @param nom IN : nom de l'employé
	 * @param prenom IN : prénom de l'employé
	 * @param droitsAccess IN : droits d'accès de l'employé
	 * @param login IN : login de l'employé
	 * @param motPasse IN : mot de passe de l'employé
	 * @param idAg IN : identifiant de l'agence de l'employé
	 */
	public Employe(int idEmploye, String nom, String prenom, String droitsAccess, String login, String motPasse,
			int idAg) {
		super();
		this.idEmploye = idEmploye;
		this.nom = nom;
		this.prenom = prenom;
		this.droitsAccess = droitsAccess;
		this.login = login;
		this.motPasse = motPasse;
		this.idAg = idAg;
	}

	/**
	 * Employe : Constructeur de copie
	 * 
	 * @param e
	 */
	public Employe(Employe e) {
		this(e.idEmploye, e.nom, e.prenom, e.droitsAccess, e.login, e.motPasse, e.idAg);
	}

	/**
	 * Employe : Constructeur par défaut
	 */
	public Employe() {
		this(-1000, null, null, null, null, null, -1000);
	}
	
	/**
	 * toString : Affichage des informations de l'employé
	 */
	@Override
	public String toString() {
		return "[" + this.idEmploye + "] " + this.nom.toUpperCase() + " " + this.prenom + " (" + this.login + ")  {"
				+ this.droitsAccess + "}";
	}
	
	/**
	@Override
	public String toString() {
		return "Employe [idEmploye=" + this.idEmploye + ", nom=" + this.nom + ", prenom=" + this.prenom
				+ ", droitsAccess=" + this.droitsAccess + ", login=" + this.login + ", motPasse=" + this.motPasse
				+ ", idAg=" + this.idAg + "]";
	}
	*/
}
