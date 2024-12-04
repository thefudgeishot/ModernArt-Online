package players;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import paintings.*;

public class AgressiveNPC extends NPC {

    public AgressiveNPC(String type) {
        super("AggressiveNPC");
    }

    @Override
    public int playCard(int minIndex, int maxIndex, Player player) {
        List<Painting> paintings = player.getHandPaintings();
        int[] popularity = new int[5];

        for (int i = 0; i != paintings.size(); i++) { // rank hand cards in frequency
            popularity[paintings.get(i).getArtistId()] += 1;
        }

        int mostPopID = paintings.getFirst().getArtistId(); // default to first card id
        int max = -1;
        for (int i = 0; i != popularity.length; i++) { // get most popular index
            if (max < popularity[i]) {
                max = popularity[i];
                mostPopID = i;
            }
        }

        for (int i = 0; i != paintings.size(); i++) { // play most popular id
            if (paintings.get(i).getArtistId() == mostPopID) {
                return i;
            }
        }
        return 0; // default to play the first card in list
    }

    @Override
    public int bid(int currentBid, Player player, Painting painting) {
        if (player.getMoney() < currentBid) { // can't afford to bid
            return 0; // pass
        }

        int choice = ThreadLocalRandom.current().nextInt(0, 10+1);
        if (choice <= 2) { // 20% chance to pass
            return 0;
        }

        int tries = 0;
        int bid = 0; // default to skip
        boolean valid = false;
        while (!valid) {
            if (3 < tries) { // if bot tries to bid more than they have 3 times in a row, pass
                return 0;
            }
            bid = ThreadLocalRandom.current().nextInt(currentBid+1, currentBid+20+1);
            tries++;
            if (bid <= player.getMoney()) {
                valid = true;
            }
        }
        return bid;
    }


}
