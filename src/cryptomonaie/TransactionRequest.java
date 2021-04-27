package cryptomonaie;

import java.io.Serializable;

/**
 * Une requete d'une transaction envoyé par le mineur au serveur. 
 * Il contient la transaction et le sel trouvé. 
 * 
*/
public class TransactionRequest implements Serializable {

    Transaction transaction;
    int sel;

    public TransactionRequest(Transaction transaction, int sel) {
        this.transaction = transaction;
        this.sel = sel;
    }

   

}
