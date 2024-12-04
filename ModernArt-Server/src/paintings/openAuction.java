package paintings;

import players.Player;
import players.clientMethods;

import java.io.IOException;

public class openAuction extends Painting {
    public openAuction(int artistID) {
        super(artistID);
    }

    @Override
    public void auction(Player[] players) throws IOException, InterruptedException {

        clientMethods requests = new clientMethods();

        int[] stand = new int[players.length]; // 0 playing, 1 stand

        boolean auctioning = true;
        while (auctioning) {
            int standTotal = 0;
            for (int i = 0; i != stand.length; i++) {
                standTotal += stand[i];
                if (stand[i] == 0) { //
                    currentBidder = players[i];
                }
            }
            if (standTotal >= players.length-1) { // one player remains
                // end off the auction
                for (int i = 0; i != stand.length; i++) {
                    if (stand[i] == 0) { // set current bidder to last player remaining (just to make sure)
                        currentBidder = players[i];
                        break;
                    }
                }
                sold(players);
                auctioning = false;
            }

            for (int i = 0; i != players.length; i++) {
                if (stand[i] == 1) {
                    continue;
                }

                standTotal = 0;
                for (int j = 0; j != stand.length; j++) {
                    standTotal += stand[j];
                }
                if (standTotal >= players.length-1) {
                    break;
                }

                int bid = 0;
                currentBidder = players[i];
                requests.broadcastMessageToSockets(players, players[i].getName() + "'s turn...");
                if (!(players[i].getSocket() == null)) {
                    bid = players[i].bid(players[i].getSocket(), currentBid);
                } else if (!(players[i].getNPC() == null)) { // if it's a bot
                    bid = players[i].bid(players[i].getNPC(), currentBid, this);
                } else {
                    bid = players[i].bid(currentBid);
                }
                if (!(bid == 0)) {
                    currentBid = bid;
                    currentBidder = players[i];
                    requests.broadcastMessageToSockets(players, currentBidder + " has bid $" + currentBid + ".");
                } else { // stand
                    stand[i] = 1;
                    requests.broadcastMessageToSockets(players, currentBidder + " has passed.");
                }
            }
        }
    }

    public String getType() {
        return "Open";
    }
}
