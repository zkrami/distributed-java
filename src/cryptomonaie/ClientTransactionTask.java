package cryptomonaie;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* La tache pour envoyer une transaction demandée par le client au mineur 
 */
public class ClientTransactionTask implements Runnable {

    Transaction transaction;

    public ClientTransactionTask(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void run() {
        ClientServeur serveur = new ClientServeur(new Socket());
        try {
            serveur.sendTransaction(transaction);
        } catch (IOException ex) {
            Util.debug(this, ex, transaction.toString() + " n'a pas pu etre envoyée ");
            serveur.close();
            return;
        }
        String response;
        try {
            response = serveur.readResponse();
        } catch (IOException ex) {
            Util.debug(this, ex, transaction.toString() + " pas de reponse ");
            return;
        } finally {
            serveur.close();
        }

        switch (response) {
            case "VALID":
                System.out.println(transaction.toString() + " a été validé.");
                break;
            case "TRANSACTION_NOT_VALID":
                System.out.println(transaction.toString() + " votre transaction n'est pas valide.");
                break;
            default:
                System.out.println(transaction.toString() + " message unconnue: " + response);
                break;
        }

    }

}
