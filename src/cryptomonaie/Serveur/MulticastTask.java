package cryptomonaie.serveur;

import cryptomonaie.TransactionResponse;


/**
 *
 * Le serveur lance cette tache pour diffuser un message aux tous les mineurs connectés au canal de multicast. 
 */
public class MulticastTask implements Runnable {

    Serveur serveur;
    TransactionResponse response;

    public MulticastTask(Serveur serveur, TransactionResponse response) {
        this.serveur = serveur;
        this.response = response;
    }

    @Override
    public void run() {

        serveur.mineurs.forEach((mineur) -> {
           mineur.trySendResponse(response); 
        });
        // si la connexion a été fermée alors retire de la canal 
        serveur.mineurs.removeIf((t) -> {
            return t.isClosed();
        });

    }

}
