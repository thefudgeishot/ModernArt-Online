import java.util.concurrent.ThreadLocalRandom;

public class NPC {

    private String type;
    private main main;

    public NPC(String type, main main) {
        this.type = type;
        this.main = main;
    }

    public String getType() {
        return this.type;
    }
    public int playCard(int minIndex, int maxIndex, Player player) {
        return ThreadLocalRandom.current().nextInt(minIndex, maxIndex);
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
