package paintings;

import players.Player;
import players.clientMethods;

import java.io.IOException;

public class oneOfferAuction extends Painting {

    public oneOfferAuction(int artistID) {
        super(artistID);
    }

    @Override
    public void auction(Player[] players) throws InterruptedException, IOException {

        clientMethods requests = new clientMethods();

        currentBidder = owner;
        // bidding round
        int highestBid = 0;
        for (int i = 0; i != players.length; i++) {
            if (players[i] == owner) { // skip auctioneer
                continue;
            }

            int bid;
            if (!(players[i].getSocket() == null)) {
                bid = players[i].bid(players[i].getSocket(), highestBid);
            } else if (!(players[i].getNPC() == null)) { // if it's a bot
                bid = players[i].bid(players[i].getNPC(), highestBid, this);
            } else {
                bid = players[i].bid(highestBid);
            }
            if (highestBid < bid) {
                highestBid = bid;
                currentBidder = players[i];
                requests.broadcastMessageToSockets(players,(currentBidder.getName() + " has bid $" + highestBid + "."));
            } else {
                requests.broadcastMessageToSockets(players,(players[i].getName() + " passed."));
            }
        }

        // auctioneers turn

        int bid;
        if (!(owner.getSocket() == null)) {
            bid = owner.bid(owner.getSocket(), highestBid);
        } else if (!(owner.getNPC() == null)) { // if it's a bot
            bid = owner.bid(owner.getNPC(), highestBid, this);
        } else {
            bid = owner.bid(highestBid);
        }
        if (highestBid < bid) {
            currentBidder = owner;
            highestBid = bid;
            requests.broadcastMessageToSockets(players,(currentBidder.getName() + " has bid $" + highestBid + "."));
        }

        requests.broadcastMessageToSockets(players,(currentBidder.getName() + " has won the auction for " + this.getArtistColour() + this));
        currentBid = highestBid;
        sold(players);
    }

    @Override
    public String getType() {
        return "One Offer";
    }
}
