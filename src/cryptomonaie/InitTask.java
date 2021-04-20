package cryptomonaie;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Rami
 */
public class InitTask implements Runnable {

    Serveur serveur;
    Socket mineur;

    public InitTask(Serveur serveur, Socket mineur) {
        this.serveur = serveur;
        this.mineur = mineur;
    }

    @Override
    public void run() {

        try (ObjectOutputStream os = new ObjectOutputStream(mineur.getOutputStream())) {
            os.writeObject(serveur.blockchaine.chaine);
            os.close();
        } catch (IOException ex) {
            Util.debug(this, ex);
        } finally {
            try {
                mineur.close();
            } catch (IOException ex) {
            }
        }

    }
}
