package paintings;

import players.Player;
import players.clientMethods;

import java.io.IOException;

public class hiddenAuction extends Painting {
    public hiddenAuction(int artistID) {
        super(artistID);
    }

    @Override
    public void auction(Player[] players) throws IOException, InterruptedException {
        clientMethods requests = new clientMethods();

        int[] bids = new int[players.length];
        // bidding round
        int highestBid = 0;
        for (int i = 0; i != players.length; i++) {
            int bid;
            if (!(players[i].getSocket() == null)) {
                bid = players[i].bid(players[i].getSocket(), currentBid);
            } else if (!(players[i].getNPC() == null)) { // if it's a bot
                bid = players[i].bid(players[i].getNPC(), currentBid, this);
            } else {
                bid = players[i].bid(currentBid);
            }
            bids[i] = bid;
            if (highestBid == bid) { // tie's in bids are defaulted by structure to closets player to index 0 in array
                if (players[i] == owner && currentBidder != owner) { // if the auctioneer is involved in a tie, they win by default
                    currentBidder = owner;
                }
            } else if (highestBid < bid) {
                highestBid = bid;
                currentBidder = players[i];
            }
        }

        // condition checks
        if (highestBid == 0) { // no bids
            currentBidder = owner; // auctioneer wins
        }

        // print results
        for (int i = 0; i != bids.length; i++) {
            requests.broadcastMessageToSockets(players,(players[i].getName() + " bid $" + bids[i] + "."));
        }

        requests.broadcastMessageToSockets(players,(currentBidder.getName() + " has won the auction for " + this.getArtistColour() + this));
        sold(players);
    }

    public String getType() {
        return "Hidden";
    }
}
