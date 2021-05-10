package cryptomonaie.client;

import cryptomonaie.Transaction;
import cryptomonaie.Util;
import java.io.IOException;
import java.net.Socket;

/*
* La tache pour envoyer une transaction demandée par le client au mineur 
 */
public class ClientTransactionTask implements Runnable {

    Transaction transaction;
    int mineurPort; 
    public ClientTransactionTask(Transaction transaction , int mineurPort) {
        this.transaction = transaction;
        this.mineurPort = mineurPort; 
    }

    @Override
    public void run() {

        ClientServeur serveur = null;
        try {
            serveur = new ClientServeur(new Socket(Client.mineurHost, mineurPort));
            serveur.sendTransaction(transaction);
        } catch (IOException ex) {
            Util.debug(this, ex, transaction.toString() + " n'a pas pu etre envoyée ");
            if (serveur != null) {
                serveur.close();
            }
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
