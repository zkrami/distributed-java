package cryptomonaie;

import java.io.Serializable;

/**
 * La transaction se consiste d'un somme, un payeur, et un receveur.
 */
public class Transaction implements Serializable {

    int somme;
    int payeur;
    int receveur;

    public Transaction(int somme, int payeur, int receveur) {
        this.somme = somme;
        this.payeur = payeur;
        this.receveur = receveur;
    }

    @Override
    public int hashCode() {
        return somme + receveur * 31 + payeur * 31 * 31;
    }

    @Override
    public String toString() {
        return "Transaction: Somme(" + somme + ") , " + "Payeur(" + payeur + ") , " + " Receveur(" + receveur + ") ";
    }

}
