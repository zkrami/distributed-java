package cryptomonaie;

import java.util.ArrayList;

/**
 *
 * @author Rami 
 */
public class Bloc {

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

    
    

}
