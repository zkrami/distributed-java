package cryptomonaie.mining;

import cryptomonaie.Transaction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * Minor client: Wrapper de la connexion entre le mineur et le client. Le mineur
 * communique avec le client un utilisant cette classe.
 */
public class MineurClient {

    private Socket socket;

    public MineurClient(Socket socket) {
        this.socket = socket;
    }

    /**
     *
     * Try to send a message to the client
     *
     * @param message
     */
    private void trySend(String message) {

        try {
            OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
            os.write(message + "\n");
            os.flush();
            os.close();
        } catch (Exception ex) {

        }
    }

    /**
     * *
     * Les messages renvoyées au client
     */
    public void tryErrorResponse() {
        trySend("ERROR");
        this.close();
    }

    public void tryNotValidTransactionResponse() {
        trySend("TRANSACTION_NOT_VALID");
        this.close();
    }

    public void tryValid() {
        trySend("VALID");
        this.close();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ex1) {
        }
    }

    public Transaction readTransaction() throws SocketException, IOException, ClassNotFoundException {
        // si le client ne renvois rien dans une seconde ignore lui pour ne pas bloquer les auters requetes
        socket.setSoTimeout(1000);
        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
        return (Transaction) is.readObject();

    }

}
