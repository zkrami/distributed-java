package cryptomonaie;

import java.io.Serializable;

/**
 *
 * @author Rami
 */
public class TransactionResponse implements Serializable {

    Transaction transaction;
    int sel;
    int diffculte;
    boolean accepted;

    public TransactionResponse(boolean accepted) {
        this.accepted = accepted;
    }

    public TransactionResponse(Transaction transaction, int sel, int diffculte, boolean accepted) {
        this.transaction = transaction;
        this.sel = sel;
        this.diffculte = diffculte;
        this.accepted = accepted;
    }

}
