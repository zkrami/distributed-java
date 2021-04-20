package cryptomonaie;

import java.io.Serializable;

/**
 *
 * @author Rami
 */
public class TransactionRequest implements Serializable {

    Transaction transaction;
    int sel;

    public TransactionRequest(Transaction transaction, int sel) {
        this.transaction = transaction;
        this.sel = sel;
    }

   

}
