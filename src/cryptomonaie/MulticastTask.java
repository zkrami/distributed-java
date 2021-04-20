package cryptomonaie;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author Rami
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
            try (ObjectOutputStream os = new ObjectOutputStream(mineur.getOutputStream())) {
                os.writeObject(response);
                os.flush();
            } catch (IOException ex) {
                Util.debug(this, ex);
            }
        });
    }

}
