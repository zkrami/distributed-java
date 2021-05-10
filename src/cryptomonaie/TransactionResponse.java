package cryptomonaie;

import java.io.Serializable;

/**
 * Une reponse à la requete de la transaction envoyé par le serveur aux mineurs.
 * Il contient la transaction,le sel trouvé, et la nouvelle difficulté.
 *
 */
public class TransactionResponse implements Serializable {

    public Transaction transaction;
    public int sel;
    public int diffculte;
    public boolean accepted;

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
