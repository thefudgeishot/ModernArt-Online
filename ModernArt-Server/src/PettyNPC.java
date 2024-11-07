import java.util.concurrent.ThreadLocalRandom;

public class PettyNPC extends NPC{

    public PettyNPC(String type, main main) {
        super("PettyNPC", main);
    }

    @Override
    public int bid(int currentBid, Player player, Painting painting) {
        int choice = ThreadLocalRandom.current().nextInt(0,10 +1);

        if (choice <= 4) { // 40% chance to pass
            return 0;
        } else { // otherwise be petty and up the bid by 1
            return currentBid+1;
        }
    }



}
