package players;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.net.*;
import java.util.concurrent.TimeUnit;
import paintings.*;
/**
 * This class represents a player in the ModernArt game
 * 
 * You are not allowed to add any new field to this class
 * You are not allowed to add any new public method to this class
 */
public class Player {
    /**
     * The name of the player
     * 
     * The first player should have the name "Player 0"
     * The second player should have the name "Player 1"
     * The third player should have the name "Player 2"
     * ...
     */
    private final String name;
    /**
     * The money the player has
     */
    private int money;
    /**
     * The total number of players in the game
     */
    private static int totalPlayers = 0;
    /**
     * The paintings the player has in hand
     */
    private List<Painting> handPaintings = new ArrayList<>();
    /**
     * The paintings the player has bought
     */
    private List<Painting> boughtPaintings = new ArrayList<>();

    // Separator
    private String seperator = "-----------------------------------------\n";

    // Addition of network Socket
    private Socket socket;

    // Addition of npc
    private NPC bot;
    /**
     * Constructor of the Player class
     */
    private Scanner in = new Scanner(System.in);

    public Player(int money) {
        this.name = "Player " + totalPlayers++;
        this.money = money;
    }

    // Addition of setSocket()
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    // Addition of getSocket()
    public Socket getSocket() {
        return this.socket;
    }

    // Addition of setNPC()
    public void setNPC(NPC npc) {
        this.bot = npc;
    }

    // Addition of getNPC()
    public NPC getNPC() {
        return this.bot;
    }

    // Addition of getHandPaintings()
    public List<Painting> getHandPaintings() {
        return  this.handPaintings;
    }

    /**
     * To deal a painting to the player
     */
    public void dealPaintings(Painting painting) {
        painting.setOwner(this);
        handPaintings.add(painting);
    }
    /**
     * Get the name of the player
     */
    public String getName() {
        return this.name;
    }

    // Addition of genSeparator()
    private String genSeperator(int size) {
        String output = "";
        for (int i = 0; i != size+1; i++) {
            output += "------";
        }
        output += "---\n";
        return  output;
    }

    /**
     * To let the player to put up a painting for auction
     * After this method, the painting should be removed from handPaintings
     * 
     * Validation of user's input should be done in this method,
     * such as checking if the index is valid. If it is invalid,
     * the player will need to enter the index again.
     */
    public Painting playPainting() { // host playPainting

        System.out.println(this + " has $" + this.getMoney());
        System.out.print(genSeperator(handPaintings.size()) + "| ");
        for (int i = 0; i != handPaintings.size(); i++) {
            Painting painting = handPaintings.get(i);
            System.out.printf("%d %s | ", i, painting.toChar());
        }
        System.out.println();
        System.out.print(genSeperator(handPaintings.size()));

        boolean valid = false;
        int index = 0;
        while (!valid) {
            System.out.print("Please enter the index of the Painting you want to play: ");
            index = in.nextInt();

            if (index < 0) {
                continue;
            }
            if (handPaintings.size() < index) {
                continue;
            }
            valid = true;
        }
        Painting output = handPaintings.get(index);
        handPaintings.remove(index);
        return output;
    }

    // Addition of overloaded method playPainting() for clients
    public Painting playPainting(Socket socket) throws IOException { // client playPainting

        clientMethods requests = new clientMethods();

        requests.broadcastMessageToSocket(socket, this + " has $" + this.getMoney());
        requests.broadcastMessageToSocket(socket, genSeperator(handPaintings.size()));

        String output = " |";
        for (int i = 0; i != handPaintings.size(); i++) {
            Painting painting = handPaintings.get(i);
            output += (i + " " + painting.toChar() + " |");
        }
        requests.broadcastMessageToSocket(socket, output);
        requests.broadcastMessageToSocket(socket, genSeperator(handPaintings.size()));

        boolean valid = false;
        int index = 0;
        while (!valid) {
            index = requests.getIntFromSocket(socket, ("Please enter the index of the Painting you want to play: "), handPaintings.size() );
            if (index < 0) {
                continue;
            }
            if (handPaintings.size() < index) {
                continue;
            }
            valid = true;
        }
        Painting outputs = handPaintings.get(index);
        handPaintings.remove(index);
        return outputs;
    }

    // Addition of overloaded method playPainting() for NPCs
    public Painting playPainting(NPC npc) throws InterruptedException {

        TimeUnit.SECONDS.sleep(1);
        int index = npc.playCard(0,handPaintings.size()-1, this);
        Painting output = handPaintings.get(index);
        handPaintings.remove(index);
        return output;
    }

    /**
     * Get the money the player has
     */
    public int getMoney() {
        return money;
    }
    /**
     * To let the player to bid. 
     * 
     * In some auctions, e.g. open auction, the player knows the current bid.
     * In this case the currentBid will be passed to the method.
     * 
     * In some auctions, e.g. blind auction, the player does not know the current bid.
     * In this case, the currentBid passed to the method will be 0.
     * 
     * A human player should be asked to input the bid amount.
     * The bid amount should be less than or equal to the money the player has.
     * If the bid amount is too high, the player should be asked to input again.
     * 
     * If the bid amount is too small (less than the current bid or less than 1),
     * the bid amount will also be returned, which may means to pass the bid.
     * 
     * You should not assume there is only open auction when writing this method
     */
    public int bid(int currentBid) {
        System.out.println(this + " has $" + getMoney());

        boolean valid = false;
        int bid = 0;
        while (!valid) {
            System.out.print("Enter your bid (enter 0 = forfeit): ");
            bid = in.nextInt();

            if (bid < 0) {
                continue;
            }
            if (bid < 1 || bid < currentBid) { // the bid is too small (pass)
                bid = 0;
                valid = true; // still a valid bid
            } else if (bid <= this.money) { // player has the money
                valid = true; // otherwise it's a valid bid
            }
        }
        return bid;
    }

    // Addition of overloaded method bid() for clients
    public int bid(Socket socket, int currentBid) throws IOException {

        clientMethods requests = new clientMethods();

        requests.broadcastMessageToSocket(socket, this + " has $" + getMoney());

        boolean valid = false;
        int bid = 0;
        while (!valid) {
            requests.broadcastMessageToSocket(socket, "Current bid is $" + currentBid + "."); // TODO: execute depends on card type
            bid = requests.getIntFromSocket(socket, "Enter your bid (enter 0 = forfeit): ", getMoney());

            if (bid < 0) {
                continue;
            }
            if (bid < 1 || bid < currentBid) { // the bid is too small (pass)
                bid = 0;
                valid = true; // still a valid bid
            } else if (bid <= this.money) { // player has the money
                valid = true; // otherwise it's a valid bid
            }
        }
        return bid;
    }

    // Addition of overloaded method bid() for bots
    public int bid(NPC npc, int currentBid, Painting painting) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        return npc.bid(currentBid, this, painting);
    }

    /**
     * To let the player to pay
     */
    public void pay(int amount) {
        this.money -= amount;
    }
    /**
     * To let the player to earn
     */
    public void earn(int amount) {
        this.money += amount;
    }
    /**
     * toString method that you need to override
     */
    @Override
    public String toString() {
        return this.getName();
    }
    /**
     * To finalize a bid and purchase a painting
     * 
     * This method has been finished for you
     */
    public void buyPainting(Painting Painting) {
        boughtPaintings.add(Painting);
    }
    /**
     * To sell all the paintings the player has bought to the bank 
     * after each round
     */    
    public void sellPainting(int[] scores) {
        for (int i = 0; i != boughtPaintings.size(); i++) { // bought paintings loop
            money += scores[boughtPaintings.get(i).getArtistId()];
        }
    }
}
