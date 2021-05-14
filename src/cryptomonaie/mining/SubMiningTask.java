package cryptomonaie.mining;

import cryptomonaie.Blockchaine;
import cryptomonaie.Jonction;
import java.util.concurrent.Callable;

/**
 *
 * sub mining: Le mineur lance plusieurs sub mining tache pour diviser le
 * travail de trouver un sel
 */
public class SubMiningTask implements Callable<Integer> {

    Mineur mineur;
    Jonction jonction;
    MiningTask master;
    boolean valideted = false;

    int l;
    int r;

    public SubMiningTask(MiningTask master, Mineur mineur, Jonction jonction, int l, int r) {
        this.master = master;
        this.mineur = mineur;
        this.jonction = jonction;
        this.l = l;
        this.r = r;

    }

    @Override
    public Integer call() {
        boolean valid = false;

        // @MINING 
        int sel = -1;
        for (int i = l; i < r; i++) {
            if (mineur.interrupt || master.found) {
                break;
            }
            jonction.setSel(i);
            if (Blockchaine.inserable(jonction, mineur.getDifficulte())) {
                valid = true;
                sel = i;
                break;
            }
        }

        if (!valid) {
            return -1;
        }
        return sel;

    }

}
