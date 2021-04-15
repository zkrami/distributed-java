package cryptomonaie;

/**
 *
 * @author Rami
 */
public class Transaction {
    int somme;    
    int receveur; 
    int payeur;

    @Override
    public int hashCode() {
        return somme  + receveur * 31 + payeur * 31 * 31 ; 
    }
    
    
    
    
}
