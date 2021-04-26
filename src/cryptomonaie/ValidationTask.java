package cryptomonaie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author Rami
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
        Socket socket = task.client;

        try {
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write("VALID\n");
            writer.flush();
        } catch (Exception ex) {

        } finally {
            try {
                socket.close();
            } catch (Exception ex) {
            }
        }
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
                Socket socket = new Socket(mineur.serverHost, mineur.transactionPort);
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                TransactionRequest transactionRequest = new TransactionRequest(task.transaction, task.sel);
                os.writeObject(transactionRequest); // send request 
                os.flush();
                // wait for the response 
                BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = is.readLine();
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
