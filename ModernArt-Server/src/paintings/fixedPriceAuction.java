package paintings;

import players.Player;
import players.clientMethods;

import java.io.IOException;

public class fixedPriceAuction extends Painting {

    public fixedPriceAuction(int artistID) {
        super(artistID);
    }

    @Override
    public void auction(Player[] players) throws InterruptedException, IOException {

        clientMethods requests = new clientMethods();

        int basePrice;
        if (!(owner.getSocket() == null)) {
            basePrice = owner.bid(owner.getSocket(), 0);
        } else if (!(owner.getNPC() == null)) { // if it's a bot
            basePrice = owner.bid(owner.getNPC(), 0, this);
        } else {
            basePrice = owner.bid(0);
        }
        currentBidder = owner; // default the sale to auctioneer
        requests.broadcastMessageToSockets(players,(this + " price has been set at $" + basePrice + "."));

        // bidding round
        for (int i = 0; i != players.length; i++) {
            if (players[i] == owner) { // skip auctioneer
                continue;
            }

            int bid;
            if (!(players[i].getSocket() == null)) {
                bid = players[i].bid(players[i].getSocket(), basePrice);
            } else if (!(players[i].getNPC() == null)) { // if it's a bot
                bid = players[i].bid(players[i].getNPC(), basePrice, this);
            } else {
                bid = players[i].bid(basePrice);
            }
            if (basePrice <= bid) {
                currentBidder = players[i];
                break;
            } else {
                requests.broadcastMessageToSockets(players,(players[i].getName() + " has passed."));
            }
        }

        currentBid = basePrice;
        sold(players);
    }

    @Override
    public String getType() {
        return "Fixed Price";
    }
}

