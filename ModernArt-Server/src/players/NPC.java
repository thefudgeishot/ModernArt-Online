package players;

import java.util.concurrent.ThreadLocalRandom;
import paintings.*;

public class NPC {

    private String type;

    public NPC(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
    public int playCard(int minIndex, int maxIndex, Player player) {
        return ThreadLocalRandom.current().nextInt(minIndex, maxIndex+1);
    }

    public int bid(int currentBid, Player player, Painting painting) {
        int choice = ThreadLocalRandom.current().nextInt(0,1 +1);

        if (currentBid < player.getMoney()) { // can't afford to bid
            return 0;
        }

        // if player can afford to bid
        if (choice == 0) { // raise
            return ThreadLocalRandom.current().nextInt(currentBid, player.getMoney()+1);
        } else { // pass
            return 0;
        }
    }
}
