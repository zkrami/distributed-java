package cryptomonaie.serveur;

/**
 *
 * La tache de l'initialisation. (Dans le cas où le mineur s'est arreté il peut demander toute la chaine)
 */
public class InitTask implements Runnable {

    Serveur serveur;
    ServeurClient mineur;

    public InitTask(Serveur serveur, ServeurClient mineur) {
        this.serveur = serveur;
        this.mineur = mineur;
    }

    @Override
    public void run() {
        if(mineur.trySendChaine(serveur.blockchaine)){
            System.out.println("(InitTask): La chaine a été envoyé ");
        }else{
            System.err.println("(InitTask): La chaine n'a pas été envoyé ");
        }
        
        
        
    }
}
