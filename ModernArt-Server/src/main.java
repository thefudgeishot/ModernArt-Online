import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class main {

    public static final int[][] PRE_DEAL = {null, null, null,  //game can't be played for 0, 1, 2 players
            {10,6,6,0}, {9,4,4,0}, {8,3,3,0}};

    public static final int ROUND = 4;

    public static final int INITIAL_MONEY = 100;

    public static final int[] INITIAL_COUNT = {12,15,15,15,20};

    private static final int SCORES[] = {30, 20, 10};

    private static final int MAX_PAINTINGS = 5;


    private Player[] players;
    private static int minPlayers = 3;
    private static int maxPlayers = 5;
    private int totalPlayers;
    private int botCount;

    private List<Painting> deck = new ArrayList<>();

    private int[][] scoreboard = new int[ROUND][Painting.ARTIST_NAMES.length];

    private Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        new main().server();
    }

    public void prepareDeck() {
        int[][] suitTable = new int[5][]; // 5 suits, 10 cards each
        for (int i = 0; i != suitTable.length; i++) {
            suitTable[i] = new int[INITIAL_COUNT[i]];
            for (int j = 0; j != suitTable[i].length; j++) {
                deck.add(new Painting(i));
            }
        }

        shuffle(deck);
    }

    public void dealPainting(int round) {
        int cardsToDeal = PRE_DEAL[this.totalPlayers][round];
        for (int player = 0; player != this.totalPlayers; player++) {
            for (int i = 0; i != cardsToDeal; i++) {
                this.players[player].dealPaintings(deck.getFirst());
                deck.removeFirst();
            }
        }
    }

    public int[] updateScoreboard(int round, int[] paintingCount) {

        int[] podium = new int[3];

        for (int i = 0; i < podium.length; i++) {

            // Find max
            int max = -1; // Use -1 to ensure we find the first maximum
            int maxIndex = -1; // Store the index of the maximum

            for (int j = 0; j < paintingCount.length; j++) {
                boolean skip = false;
                // Skip over items already on the podium
                for (int k = 0; k < i; k++) {
                    if (podium[k] == j) {
                        skip = true;
                        break;
                    }
                }
                if (!skip && paintingCount[j] > max) {
                    max = paintingCount[j];
                    maxIndex = j; // Store the index of the new maximum
                }
            }

            // Save the index of the maximum count to the podium
            if (maxIndex != -1) {
                podium[i] = maxIndex;
            }
        }

        // Update scoreboard
        for (int i = 0 ; i != scoreboard[round].length; i++){
            for (int j = 0; j != podium.length; j++) {
                if (i == podium[j]) { // if painter is on the podium
                    scoreboard[round][i] = SCORES[j]; // set the round score
                }
            }
        }

        // Handle output
        int[] output = new int[paintingCount.length];
        for (int i = 0; i < podium.length; i++) {
            int score = 0;
            for (int j = 0; j != round+1; j++) { // check for previous wins
                score += scoreboard[j][podium[i]];
            }


            output[podium[i]] = score;
        }

        return  output;
    }

    public void shuffle(List<Painting> deck) {
        for (int i = 0; i < deck.size(); i++) {
            int index = ThreadLocalRandom.current().nextInt(deck.size());
            Painting temp = deck.get(i);
            deck.set(i, deck.get(index));
            deck.set(index, temp);
        }
    }

    public void server() throws IOException, InterruptedException {

        clientMethods requests = new clientMethods();

        boolean valid = false;
        while (!valid) { // validate there is a correct number of players
            System.out.print("How many real players total (Including yourself): ");
            totalPlayers = Integer.parseInt(in.nextLine());

            System.out.print("How many bots total (0 for none): ");
            botCount = Integer.parseInt(in.nextLine());

            if ( (minPlayers <= (totalPlayers+botCount)) && ((totalPlayers+botCount) <= maxPlayers)) {
                valid = true;
            }
        }

        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("-----------=+=-----------");
        System.out.println("Modern Art Multiplayer (Host)\n" + "IP Address: " + (serverSocket.getInetAddress().getLocalHost()).toString().split("/")[1]);

        int playerCnt = 0;
        players = new Player[totalPlayers+botCount];
        if (!(totalPlayers == 0)) { // no host player (bot sim game)
            players[playerCnt] = new Player(INITIAL_MONEY);
            System.out.println("You are " + players[playerCnt++].getName() + ".");
        }
        System.out.println("Waiting for players...");

        boolean searching = true; // Search for players
        while (searching) {
            if (playerCnt == totalPlayers) { // while loop escape condition
                searching = false;
                continue;
            }

            Player player = new Player(INITIAL_MONEY); // make new player
            player.setSocket(serverSocket.accept()); // await a player connection
            players[playerCnt++] = player; // add player to array
            requests.broadcastMessageToSockets(players, player.getName() + " connected!"); // send message to all connected clients and host

            requests.broadcastMessageToSocket(player.getSocket(), "Connected to server as " + player.getName() + "."); // send message to recently connected client


        }

        // Add bots
        for (int i = 0; i != botCount; i++) {
            Player bot = new Player(INITIAL_MONEY);

            NPC npc;
            int choice = ThreadLocalRandom.current().nextInt(0,2+1); // Default upper bound (2+1) increase '2' by one for each additional bot
            switch (choice) {
                case 0: // default case
                    npc = new NPC("Default", this);
                    break;
                case 1: // Aggressive NPC
                    npc = new AgressiveNPC("anything doesn't really matter", this);
                    break;
                case 2: // Petty NPC
                    npc = new PettyNPC("Seriously it doesn't matter", this);
                    break;
                // Add additional switch case here for your bot(s)
                        /*
                        case 3:
                            npc = new YourBot(type, main);
                         */
                default:
                    npc = new NPC("Default", this);
            }
            bot.setNPC(npc);
            players[playerCnt++] = bot;
            requests.broadcastMessageToSockets(players, bot.getName() + "(" + bot.getNPC().getType() + ")" + " connected!"); // Send messages to all clients
        }
        totalPlayers = totalPlayers+botCount;

        // Main game loop, players loaded in.
        requests.broadcastMessageToSockets(players,"Game starting...");
        prepareDeck();



        for (int round = 0; round < ROUND; round++) {
            int currentPlayer = 0;

            //deal the paintings
            dealPainting(round);
            //start auction
            int[] paintingCount = new int[Painting.ARTIST_NAMES.length];
            while (true) {
                Player player = players[currentPlayer];
                requests.broadcastMessageToSockets(players, players[currentPlayer].getName() + " is playing...");

                Painting p;
                if (!(player.getSocket() == null)) { // if it's a client
                    p = player.playPainting(player.getSocket());
                } else if (!(player.getNPC() == null)) { // if it's a bot
                    p = player.playPainting(player.getNPC());
                } else {
                    p = player.playPainting();
                }

                if (++paintingCount[p.getArtistId()] == MAX_PAINTINGS) {
                    break; //this round end immediately and the painting is not putting up for auction
                }

                if (p != null) {
                    requests.broadcastMessageToSockets(players, p.toString() + " is being sold.");
                    p.auction(players);
                }

                currentPlayer = (currentPlayer + 1) % (totalPlayers);
                requests.broadcastMessageToSockets(players, "-----------=+=-----------");
                requests.broadcastMessageToSockets(players, "The number of painting sold: ");


                for (int i = 0; i < Painting.ARTIST_NAMES.length; i++) {
                    String[] paintings = new String[MAX_PAINTINGS];
                    for (int j = 0; j != MAX_PAINTINGS; j++) { // populate array with empty circles
                        int codepoint = Integer.parseUnsignedInt("0x25EF".substring(2), 16);
                        paintings[j] = String.valueOf(Character.toChars(codepoint));
                    }

                    for (int j = 0; j != paintingCount[i]; j++) { // add the solid circles
                        int codepoint = Integer.parseUnsignedInt("0x2B24".substring(2), 16);
                        paintings[j] = String.valueOf(Character.toChars(codepoint));
                    }
                    String output = " ";
                    for (int j = 0; j != paintings.length; j++) {
                        output += paintings[j] + " ";
                    }
                    requests.broadcastMessageToSockets(players, (Painting.ARTIST_COLOURS[i+1] + Painting.ARTIST_NAMES[i] + " " + output + Painting.ARTIST_COLOURS[0]));
                }
            }
            requests.broadcastMessageToSockets(players, "-----------=+=-----------");
            requests.broadcastMessageToSockets(players, "Complete auction - sell paintings");
            //update score board
            int[] scoreForThisRound = updateScoreboard(round, paintingCount);
            requests.broadcastMessageToSockets(players, "Print the score board after auction");
            requests.broadcastMessageToSockets(players, "-----------=+=-----------");

            String output = "";
            output += "\t\t";
            for (int j = 0; j < Painting.ARTIST_NAMES.length; j++) {
                output += j + "\t";
            }
            requests.broadcastMessageToSockets(players, output);
            output = "";
            for (int i = 0; i < round + 1; i++) {
                output += ("Round " + i + ":  ");

                for (int j = 0; j < Painting.ARTIST_NAMES.length; j++) {
                    output += scoreboard[i][j] + "\t";
                }
            }
            requests.broadcastMessageToSockets(players, output);

            requests.broadcastNewLineToSockets(players);
            requests.broadcastNewLineToSockets(players);

            requests.broadcastMessageToSockets(players, "Print the price for each artist's painting");
            for (int i = 0; i < Painting.ARTIST_NAMES.length; i++) {
                requests.broadcastMessageToSockets(players, (Painting.ARTIST_NAMES[i] + " " + scoreForThisRound[i]));
            }

            //Sell the paintings
            for (Player p : players) {
                p.sellPainting(scoreForThisRound);
            }
        }

        serverSocket.close();

    }

}