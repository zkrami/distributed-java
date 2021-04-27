package cryptomonaie;

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
        Mineur mineur = this.task.mineur;
        if (mineur._last != null) { // faut arreter le calcule car la dernière jonction n'est pas valid 
            mineur.interrupt = true;
            mineur.reset();
        }
        // refaire la tache 
        mineur.submitMiningTask(task);
    }

    // send a message to the client 
    void valid() {
        task.client.tryValid();
    }

    boolean validate() { // verifie si la jonction est toujours valid selon le mineur 

        Jonction last = this.task.mineur.getLast();
        if (Blockchaine.validate(task.transaction, last) == false) {
            return false;
        }
        Jonction jon = Blockchaine.newJonction(last, task.transaction);
        if (Blockchaine.inserable(jon, task.sel) == false) {
            return false;
        }

        return true;

    }

    @Override
    public void run() {

        Mineur mineur = task.mineur;
        try {
            // essais d'abord si la jonction est toujours valid sinon n'embete pas le servuer 
            if (validate()) {
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

            } else {
                notValid();
            }
        } catch (IOException ex) {
            // probleme de connexion refaire la tache 
            notValid();

        }
    }

}
