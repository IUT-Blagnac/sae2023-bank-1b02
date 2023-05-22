package model.data;

import java.sql.Date;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 */

public class Prelevement {

	public int idPrelev;
	public double montant;
	public int dateRecurrente;
	public String beneficiaire;
	public int idNumCompte;

	public Prelevement(int idPrevelement, double montantPreleve, int jourPrelev, String beneficiairePrelev, int idNumCompte) {
		super();
		this.idPrelev = idPrevelement;
		this.montant = montantPreleve;
		this.dateRecurrente = jourPrelev;
		this.beneficiaire = beneficiairePrelev;
		this.idNumCompte = idNumCompte;
	}

	public Prelevement(Prelevement o) {
		this(o.idPrelev, o.montant, o.dateRecurrente, o.beneficiaire, o.idNumCompte);
	}

	public Prelevement() {
		this(-1000, 0, 0, "Null", -1000);
	}

	@Override
	public String toString() {
		return "["+ this.idPrelev +"] "+ this.beneficiaire + " (jour "+ this.dateRecurrente +") : "+ this.montant +"€";

//		return "Operation [idOperation=" + idOperation + ", montant=" + montant + ", dateOp=" + dateOp + ", dateValeur="
//				+ dateValeur + ", idNumCompte=" + idNumCompte + ", idTypeOp=" + idTypeOp + "]";
	}
}
