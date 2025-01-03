package paintings;

import java.io.IOException;
import players.*;

/**
 * The Painting class represents a painting in the game.
 * A painting has an artist, an owner, a current bidder and a current bid.
 * 
 * Each painting has a type of auction. In this assignment, all paintings
 * have the same type of auction, which is "Open Auction".
 * 
 * You are not allowed to add any new field to this class
 * You are not allowed to add any new public method to this class
 */
public abstract class Painting {
    /**
     * The artist ID of the painting, should be between 0 and 4.
     */
    private final int artist_id;
    /**
     * The type of auction of the painting
     */
    private final String TYPE = "Open Auction";
    /**
     * The owner of the painting.
     * 
     * When the painting is dealt to a player, the owner is set to that player.
     * When the painting is sold in the auction, the owner is set to the player 
     * that won the auction.
     * After the painting is sold to the bank after each round, the owner 
     * information is irrelevant and can be set as any value.
     * 
     */
    protected Player owner;
    /**
     * The current bidder of the painting.
     */
    protected Player currentBidder;
    /**
     * The current bid of the painting.
     */
    protected int currentBid;
    /**
     * The names of the artists
     */

    public static final String[] ARTIST_COLOURS = {"\u001B[0m","\u001B[34m","\u001B[31m","\u001B[32m","\u001B[33m","\u001B[35m"}; // 0 index is reset colour, apply offsets!!!
    public static final String[] ARTIST_CARDS = {"0x1F0AC","0x1F0BC","0x1F0CC","0x1F0DC","0x1F0CF"};
    public static final String[] ARTIST_NAMES = {"0. Manuel Carvalho", "1. Sigrid Thaler", "2. Daniel Melim", "3. Ramon Martins", "4. Rafael Silveira"};        

    /**
     * Constructor of the Painting class
     */
    public Painting(int artist_id) {
        this.artist_id = artist_id;
    }
    /**
     * Get the artist ID of the painting
     */
    public int getArtistId() {
        return this.artist_id;
    }
    /**
     * Setter of owner
     */
    public void setOwner(Player p) {
        this.owner = p;
    }
    /**
     * Getter of owner
     */
    public Player getOwner() {
        return this.owner;
    }
    /**
     * Get the name of the artist
     */
    public String getType() {
        return  TYPE;
    }
    // Addition of getArtistColour()
    public String getArtistColour() {
        return ARTIST_COLOURS[getArtistId()+1];
    }

    // Addition of getArtistCard()
    public String getArtistCard() {
        return ARTIST_CARDS[getArtistId()];
    }

    public String getArtistName() {
        return ARTIST_NAMES[getArtistId()];
    }
    /**
     * Sold the painting to the current bidder
     * This method has been completed for you.
     * You should not modify this method.
     */
    public void sold(Player[] players) throws IOException {
        clientMethods request = new clientMethods();
        request.broadcastMessageToSockets(players, (this.toString() + "Is sold to " + currentBidder + " for " + currentBid));

        if (currentBidder == null || owner == currentBidder) {
            //owner get the painting automatically
            owner.buyPainting(this);
            owner.pay(currentBid); //owner pay to the bank
        } else {
            //currentBidder get the painting
            currentBidder.buyPainting(this);
            currentBidder.pay(currentBid);
            //owner get the money
            owner.earn(currentBid);
            owner = currentBidder;
        }
        
    }

    // Addition of toChar()
    public String toChar() {
        int codepoint = Integer.parseUnsignedInt(getArtistCard().substring(2), 16);
        return this.getArtistColour() + String.valueOf(Character.toChars(codepoint)) + ARTIST_COLOURS[0];
    }
    /**
     * toString method to be modified
     */
    @Override
    public String toString() {
        return this.getArtistColour() + this.getArtistName() + ARTIST_COLOURS[0] + " [" + this.getType() + "] Owner: " + this.getOwner() + ". ";
    }
    /**
     * The auction method - open auction
     * 
     * In open auction, each player has a chance to bid for the painting.
     * If a player bids higher than the current bid, he becomes the current bidder.
     * We always start the auction with the first player in the players array.
     * When all other players have passed (either bidding 0 or bidding lower 
     * than the current bid), the painting is sold to the current bidder.
     * 
     * p.s. we model the auction in round-robin fashion although open auction 
     * allows player to bid at any time.
     * 
     */
    public abstract void auction(Player[] players) throws IOException, InterruptedException;

}
