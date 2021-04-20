package cryptomonaie;

import java.io.Serializable;

/**
 *
 * @author Rami
 */
public class Bloc implements Serializable{

    Etat etat; // l'état final 
    Transaction transaction;

    Bloc(Etat etatFinal, Transaction transaction) {
        this.etat = etatFinal;
        this.transaction = transaction;
    }

    @Override
    public int hashCode() {
        return this.etat.hashCode() * 31 + this.transaction.hashCode() * 31 * 31;
    }
    
    void applyTransaction(){
        int r = transaction.receveur; 
        int s = transaction.somme;
        int p = transaction.payeur; 
        etat.monaie.set(p, etat.monaie.get(p) - s); 
        etat.monaie.set(r, etat.monaie.get(r) + s); 
    }

}
