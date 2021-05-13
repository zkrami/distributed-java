package cryptomonaie.mining;

import cryptomonaie.TransactionRequest;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * Le mineur lance cette tache pour valider le sel trouver avec le serveur
 */
public class ValidationTask implements Runnable {

    MiningTask task;

    public ValidationTask(MiningTask task) {
        this.task = task;
    }

    void notValid() {
        System.err.println("La transaction a été refusé par le serveur  "); // @LOG
        Mineur mineur = this.task.mineur;
        
        // refaire la tache 
        mineur.submitMiningTask(task);
        
        if (mineur._last != null) { 
            // faut arreter le calcule car la dernière jonction n'est pas valid 
            
            mineur.interrupt = true;
            mineur.reset();
            // en consequence toutes les taches dans le queue de validation ne seront plus valides pour cela on vide le queue de validation             
            mineur.resubmitValidation();
            
        }
        
    }

    // send a message to the client 
    void valid() {
        task.client.tryValid();
    }

  
    @Override
    public void run() {

        Mineur mineur = task.mineur;
        try {

            // server validation 
            MineurServeur serveur = new MineurServeur(new Socket(mineur.serverHost, mineur.transactionPort));
            serveur.sendTransactionRequest(new TransactionRequest(task.transaction, task.sel));

            // wait for the response 
            String response = serveur.readResponse();
            if (response.equals("VALID")) {
                valid();
            } else {
                notValid();
            }

        } catch (IOException ex) {
            // probleme de connexion refaire la tache 
            notValid();

        }
    }

}
