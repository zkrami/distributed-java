package cryptomonaie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author Rami
 */
public class TransactionTask implements Runnable {

    Serveur serveur;
    Socket mineur;
    TransactionRequest transactionRequest;

    public TransactionTask(Serveur serveur, Socket mineur) {
        this.serveur = serveur;
        this.mineur = mineur;
    }
    
    @Override
    public void run() {
        Blockchaine chaine = serveur.blockchaine;
        boolean valid = false;
        try {
            // recuperer la reqeute du mineur 

            // timeout 1 seconde
            // si le mineur ne renvois rien dans une seconde ignore lui pour ne pas bloquer les auters requetes
            mineur.setSoTimeout(1000);
            ObjectInputStream is = new ObjectInputStream(mineur.getInputStream());
            transactionRequest = (TransactionRequest) is.readObject();

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

        // response 
        if (!mineur.isClosed()) {
            try {

                OutputStreamWriter os = new OutputStreamWriter(mineur.getOutputStream());
                if (valid) {
                    // fait un multicast
                    TransactionResponse response = new TransactionResponse(transactionRequest.transaction, transactionRequest.sel, serveur.difficulte, true);
                    serveur.multicast(response);

                    // renvois la reponse au mineur 
                    os.write("VALID\n");
                    os.flush();
                    os.close();

                } else {
                    
                    os.write("NOT_VALID\n");
                    os.flush();
                    os.close();
                }
            } catch (IOException ex) {
                Util.debug(this, ex);

            }

            try {
                mineur.close();
            } catch (IOException ex) {

            }
        }

    }

}
