package cryptomonaie;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

            chaine.add(transactionRequest.transaction, transactionRequest.sel, serveur.difficulte);
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
                ObjectOutputStream os = new ObjectOutputStream(mineur.getOutputStream());
                if (valid) {
                    TransactionResponse response = new TransactionResponse(transactionRequest.transaction, transactionRequest.sel, serveur.difficulte, true);
                    // fait un multicast
                    serveur.multicast(response);

                    // renvois la reponse au mineur 
                    os.writeObject(response);
                    os.close();

                } else {
                    TransactionResponse response = new TransactionResponse(false);
                    os.writeObject(response);
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
