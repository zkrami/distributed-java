package cryptomonaie.mining;

import cryptomonaie.Blockchaine;
import cryptomonaie.Jonction;
import cryptomonaie.SelNotFoundException;
import cryptomonaie.Transaction;
import cryptomonaie.Util;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * La tache de mining: Le mineur lance cette tache pour trouver une bonne sel
 * pour une transaction.
 */
public class MiningTask implements Runnable {

    Mineur mineur;
    MineurClient client;
    Transaction transaction = null;
    int sel;
    volatile  boolean found = false;

    public MiningTask(Mineur mineur, MineurClient client) {
        this.mineur = mineur;
        this.client = client;
    }

    void initTransaction() throws SocketException, IOException, ClassNotFoundException {
        if (transaction != null) {
            return;
        }

        this.transaction = client.readTransaction();

    }

    @Override
    public void run() {
        boolean valid = false;
        try {
            this.initTransaction();
            mineur.interrupt = false;

            Jonction last = this.mineur.getLast();
            if (Blockchaine.validate(transaction, last) == false) {
                this.client.tryNotValidTransactionResponse();
                return;
            }
            ;

            // @MINING diviser le travail sur les coeurs   
            int range = (int) 1e8;
            int n = mineur.cores;
            int p = range / n;
            int l = 0;
            int r = p;

            ExecutorService subMiningPool = Executors.newFixedThreadPool(8);
            CompletionService<Integer> taskCompletionService = new ExecutorCompletionService<>(subMiningPool);
            for (int i = 0; i < n; i++) {
                if (i == n - 1) { // mode 
                    r = range;
                }
                taskCompletionService.submit(new SubMiningTask(this, mineur,  Blockchaine.newJonction(last, transaction) , l, r));

                l = r;
                r += p;

            }
            for (int i = 0; i < n; i++) {
                int s = taskCompletionService.take().get();
                if (s != -1) {
                    this.found = true ; 
                    valid = true;
                    this.sel = s;
                    Jonction jon = Blockchaine.newJonction(last, transaction);
                    subMiningPool.shutdown();
                    jon.setSel(this.sel);
                    mineur.setLast(jon);
                }

            }

            if (mineur.interrupt || !valid) {
                throw new SelNotFoundException();
            } else {
                mineur.submitValidationTask(this);
            }

        } catch (ClassNotFoundException | IOException ex) {
            // un probleme d'une connexion ou le client n'a rien envoyé 
            this.client.tryErrorResponse();
            Util.debug(this, ex, "Le client n'a pas envoyé la transaction demandée ");

        } catch (ExecutionException | InterruptedException | SelNotFoundException ex) {

            // SelNotFoundException
            // peut arriver dans le cas ou le derinier jonction (lastJonction)
            // a été modifié au milieu de la boucle
            this.mineur.submitMiningTask(this); // refaire la tache 
        }

    }

}
