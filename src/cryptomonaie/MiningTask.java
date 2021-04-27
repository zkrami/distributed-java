package cryptomonaie;

import java.io.IOException;
import java.net.SocketException;

/**
 *
 * La tache de mining: Le mineur lance cette tache pour trouver une bonne sel pour une transaction. 
 */
public class MiningTask implements Runnable {

    Mineur mineur;
    MineurClient client;
    Transaction transaction = null;
    boolean valideted = false;
    int sel; 

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
            
            Jonction jon = null;
            Jonction last = this.mineur.getLast(); 
            if(Blockchaine.validate(transaction, last) == false ){
                this.client.tryNotValidTransactionResponse();
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
            
        } catch (ClassNotFoundException | IOException ex) {
            // un probleme d'une connexion ou le client n'a rien envoyé 
            this.client.tryErrorResponse();
            Util.debug(this, ex, "Le client n'a pas envoyé la transaction demandée ");
            
        } catch (SelNotFoundException ex) {
         
           

            // SelNotFoundException
            // peut arriver dans le cas ou le derinier jonction (lastJonction)
            // a été modifié au milieu de la boucle
             this.mineur.submitMiningTask(this); // refaire la tache 
        }

    }

}
