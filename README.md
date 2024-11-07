# ModernArt-Online

Simple Modern Art board game made in java with online multiplayer support and bots.  

## Running the game

> [!WARNING]
> The game uses unicode to show gameplay and strategic information, terminals like command prompt may have problems running this game

**Java Version Requirement: Java 21+**
```bash
> java -jar ModernArt-Server.jar
```
```bash
> java -jar ModernArt-Client.jar
```

## Adding your own bots

- Each bot(NPC) class should extend `NPC.java`

Bots have two interaction functions, `playCard()` and `bid()`.
```java
public int playCard(int minIndex, int maxIndex, Player player) { // minimum index, maximum index, player class
    return 0; // returns index of the card to play
}
```

```java
public int bid(int currentBid, Player player, Painting painting) { // current bid, player class, paiting class
    return 0; // returns amount to bid 
}
```
> [!CAUTION]
> For not very good reasons, there's no hard coded upper and lower bound checks for the bids coming from the bots, uhh... add balance checks through your code, see `AggressiveNPC.java`


- Add your bot to the spawning pool

In `ModernArt-Server/src/main.java` line 175 and 186
```java
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
```

#

