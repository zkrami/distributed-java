package cryptomonaie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * Le serveur lance cette tache pour valider une transaction envoyé par le
 * mineur.
 */
public class TransactionTask implements Runnable {

    Serveur serveur;
    ServeurClient mineur;
    TransactionRequest transactionRequest;

    public TransactionTask(Serveur serveur, ServeurClient mineur) {
        this.serveur = serveur;
        this.mineur = mineur;
    }

    @Override
    public void run() {
        Blockchaine chaine = serveur.blockchaine;
        boolean valid = false;
        try {
            // recuperer la reqeute du mineur 

            transactionRequest = mineur.readTransactionRequest();

            chaine.add(transactionRequest.transaction, transactionRequest.sel);
            // si aucune exceptione n'est générée alors la transaction est valide 
            serveur.nextDifficulte();
            valid = true;

        } catch (NonInserableException ex) {
            Util.debug(this, ex, "Un mineur a essayé d'inséré une transaction non valide ");
        } catch (IOException ex) {
            Util.debug(this, ex);
        } catch (ClassNotFoundException ex) {
            Util.debug(this, ex);
        } catch (Exception ex) {
            Util.debug(this, ex);
        }

        if (valid) {
            // fait un multicast
            TransactionResponse response = new TransactionResponse(transactionRequest.transaction, transactionRequest.sel, serveur.difficulte, true);
            serveur.multicast(response);

        }

        // renvois la reponse au mineur 
        if (!mineur.isClosed()) {

            if (valid) {
                mineur.tryValid();

            } else {
                mineur.tryNotValid();
            }
            mineur.close();

        }

    }

}
