package cryptomonaie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author Rami
 */
public class MiningTask implements Runnable {

    Mineur mineur;
    Socket client;
    Transaction transaction = null;
    boolean valideted = false;
    int sel; 

    public MiningTask(Mineur mineur, Socket client) {
        this.mineur = mineur;
        this.client = client;
    }

   

    void initTransaction() throws SocketException, IOException {
        if (transaction == null) {
            return;
        }
        // si le client ne renvois rien dans une seconde ignore lui pour ne pas bloquer les auters requetes
        client.setSoTimeout(1000);
        ObjectInputStream is = new ObjectInputStream(client.getInputStream());

    }

    /**
     *
     * Try to send a message to the client
     * @param message
     */
     public void trySend(String message) {

        try {
            OutputStreamWriter os = new OutputStreamWriter(client.getOutputStream());
            os.write(message + "\n");
            os.flush();
            os.close();
        } catch (Exception ex) {
            try {
                client.close();
            } catch (IOException ex1) {
            }
        }
    }
     
    /**
     * *
     * Try to send an error response to the client
     */
    public void tryErrorResponse() {
           trySend("ERROR"); 
    }

    public void tryNotValidTransactionResponse(){
        trySend("TRANSACTION_NOT_VALID"); 
    }
    

    @Override
    public void run() {
        boolean valid = false;
        try {
            this.initTransaction();
            mineur.interrupt = false; 
            
            Jonction jon = null;
            Jonction last = this.mineur.getLast(); 
            if(Blockchaine.validate(transaction, last) == false ){
                this.tryNotValidTransactionResponse();
                return ; 
            }
            jon = Blockchaine.newJonction(last, transaction);
            
            // @MINING 
            // @TODO The limit should be verified
            // @TODO The algorithm should be verified 
            for(int i = 0 ; i < 1e8 ; i ++){ 
                if(mineur.interrupt) break; 
                jon.setSel(i);

                if(Blockchaine.inserable(jon, mineur.getDifficulte())){
                    valid = true ; 
                    this.sel = i;
                    mineur.setLast(jon);
                    break;
                }
            }
            

            if (!valid) {
                throw new SelNotFoundException();
            }else{
                mineur.submitValidationTask(this);
            }
            
        } catch (IOException ex) {
            // un probleme d'une connexion ou le client n'a rien envoyé 
            this.tryErrorResponse();
        } catch (SelNotFoundException ex) {
         
           

            // SelNotFoundException
            // peut arriver dans le cas ou le derinier jonction (lastJonction)
            // a été modifié au milieu de la boucle
             this.mineur.submitMiningTask(this); // refaire la tache 
        }

    }

}
