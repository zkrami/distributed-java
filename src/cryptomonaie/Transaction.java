package cryptomonaie;

import java.io.Serializable;

/**
 *
 * @author Rami
 */
public class Transaction implements Serializable{
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
        return somme  + receveur * 31 + payeur * 31 * 31 ; 
    }
    
    
    
    
}
