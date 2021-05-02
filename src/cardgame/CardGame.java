package cardgame;

import cardgame.messages.ChatMessage;
import cardgame.messages.WebradioUrl;
import com.google.gson.Gson;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 */
public abstract class CardGame {

    private static final Logger LOGGER = LogManager.getLogger(CardGame.class);

    public static final int CARDS_52 = 52;
    public static final int CARDS_32 = 32;
    public static final int CARDS_24 = 24;
    public static final String PROP_WEBRADIO_PLAYING = "webradioPlaying";
    public static final String PROP_WEBRADIO_URL = "webradioUrl";
    public static final String PROP_ATTENDEESLIST = "attendeesList";
    public static final String PROP_PLAYERLIST = "playerList";
    public static final String PROP_PLAYER_ONLINE = "playerOnline";
    public static final String PROP_GAMEPHASE = "gameState";

    protected final String videoRoomName;
    protected final PlayerIdComparator playerIdComparator;
    protected final List<Player> players; // List of all players in the room
    protected final List<Player> attendees; // sub-list of players, which are actually in the game (alive).
    protected final Gson gson;

    protected Player mover = null; // this is like the cursor or pointer of the player which has to move.

    private final PropertyChangeSupport propChangeSupport;
    private final int cardsCount;
    private final List<Card> allCards; // Alle Karten
    private final List<Card> bigStack;
    private final List<WebradioUrl> webradioList;
    private final PropertyChangeListener playerListener;

    private boolean webradioPlaying = true;
    private WebradioUrl radioUrl = null;

    public CardGame(int cardsCount, String conferenceName, List<WebradioUrl> webradioList) {
        if (cardsCount != CARDS_24 && cardsCount != CARDS_32 && cardsCount != CARDS_52) {
            throw new IllegalArgumentException("Unkown count of cards.");
        }
        propChangeSupport = new PropertyChangeSupport(this);
        this.cardsCount = cardsCount;
        allCards = Collections.synchronizedList(new ArrayList<>(cardsCount));
        bigStack = Collections.synchronizedList(new ArrayList<>(cardsCount));
        initializeCards();
        players = Collections.synchronizedList(new ArrayList<>());
        playerIdComparator = new PlayerIdComparator(players);
        attendees = Collections.synchronizedList(new ArrayList<>());
        playerListener = this::playerPropertyChanged;
        gson = new Gson();
        this.webradioList = webradioList;
        if (!webradioList.isEmpty()) {
            radioUrl = webradioList.iterator().next();
        }
        videoRoomName = conferenceName;
    }

    public void addPropertyChangeListener(PropertyChangeListener pl) {
        propChangeSupport.addPropertyChangeListener(pl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pl) {
        propChangeSupport.removePropertyChangeListener(pl);
    }

    protected void firePropertyChange(String popertyName, Object oldValue, Object newValue) {
        propChangeSupport.firePropertyChange(popertyName, oldValue, newValue);
    }

    /**
     * Mischt den Kartenhaufen und setzt den Stapel zurueck Erstellungsdatum:
     * (15.06.2003 11:53:30)
     */
    protected void shuffleStack() {
        reshuffleStack(allCards);
    }

    protected void reshuffleStack(List<Card> sourceStack) {
        Random random = new Random(System.currentTimeMillis());
        List<Card> tempStack = new ArrayList<>(sourceStack.size());
        tempStack.addAll(sourceStack);
        bigStack.clear();
        while (tempStack.size() > 0) {
            bigStack.add(tempStack.remove(random.nextInt(tempStack.size())));
        }
    }

    /**
     * Liefert die naechste Karte vom Stapel Erstellungsdatum: (15.06.2003
     * 13:56:20)
     *
     * @return de.ofh.cardgame.Card
     */
    protected Card getFromStack() {
        return bigStack.isEmpty() ? null : bigStack.remove(bigStack.size() - 1);
    }

    protected int stackSize() {
        return bigStack.size();
    }

    /**
     * Initialisisert das Kartenspiel Erstellungsdatum: (15.06.2003 12:10:45)
     */
    private void initializeCards() {
        // Anzahl der Karten festlegen
        int start;
        switch (cardsCount) {
            case CARDS_24:
                start = Card.NEUN;
                break;
            case CARDS_32:
                start = Card.SIEBEN;
                break;
            default:
                start = Card.ZWEI;
                break;
        }

        // Kartenspiel erstellen
        for (int value = start; value <= Card.AS; value++) {
            for (int color = Card.CARO; color <= Card.KREUZ; color++) {
                allCards.add(new Card(color, value));
            }
        }
    }

    /**
     * Sends a ping to all clients. Required to prevent the websocket timeout in
     * case of no action.
     */
    public void sendPing() {
        sendToPlayers("{\"action\":\"ping\"}");
    }

    /**
     * Sends a message to all players.
     *
     * @param message the message in JSON format.
     */
    public void sendToPlayers(String message) {
        getPlayerList().forEach(p -> {
            p.getSocket().sendString(message);
        });
    }

    /**
     * Getter for property player list.
     *
     * @return the list of players in the room.
     */
    public List<Player> getPlayerList() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Setter for property WebRadioPlaying.
     *
     * @param play true to turn on the webradio, false to turn off.
     */
    public void setWebRadioPlaying(boolean play) {
        boolean oldValue = webradioPlaying;
        webradioPlaying = play;
        firePropertyChange(PROP_WEBRADIO_PLAYING, oldValue, play);
    }

    /**
     * Getter for property WebradioPlaying.
     *
     * @return true if the webradio is currently playing, false otherwise.
     */
    public boolean isWebradioPlaying() {
        return webradioPlaying;
    }

    /**
     * Getter for property webradio url.
     *
     * @return the currently selected webradio url.
     */
    public WebradioUrl getRadioUrl() {
        if (radioUrl == null) {
            // fallback url
            radioUrl = new WebradioUrl("Radio Seefunk", "https://onlineradiobox.com/json/de/radioseefunk/play?platform=web");
        }
        return radioUrl;
    }

    /**
     * Setter for property webradio url
     *
     * @param url new value for webradio url.
     */
    public void setRadioUrl(WebradioUrl url) {
        WebradioUrl oldValue = this.radioUrl;
        this.radioUrl = url;
        firePropertyChange(PROP_WEBRADIO_URL, oldValue, this.radioUrl);
    }

    /**
     * Getter for property radio list.
     *
     * @return a list with the known webradios.
     */
    public List<WebradioUrl> getRadioList() {
        return webradioList;
    }

    /**
     * Getter for property videoRoomName.
     *
     * @return the name for the room in Jitsi meet.
     */
    public String getVideoRoomName() {
        return videoRoomName;
    }

    /**
     * Lookup for a player by name.
     *
     * @param name the player's name.
     * @return the player specified by name, null if there isn't one.f
     */
    public Player getPlayer(String name) {
        return players.stream().filter((Player player) -> player.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    /**
     * The Login function. A player logged in and therefore "entered the room".
     *
     * @param player the player causing the event.
     */
    public void addPlayerToRoom(Player player) {
        if (mover == null) {
            mover = player;
        }
        player.addPropertyChangeListener(playerListener);
        players.add(player);
        firePropertyChange(PROP_PLAYERLIST, null, players);
        String msg = player.getName() + " ist dazugekommen";
        chat(msg);
        LOGGER.info(msg);
    }

    /**
     * The logout function. A player logged out and therefore "left the round".
     * Currently disabled in the clients.
     *
     * @param player the player causing the event.
     */
    public void removePlayerFromRoom(Player player) {
        if (isAttendee(player)) {
            removeAttendee(player);
        }
        player.removePropertyChangeListener(playerListener);
        players.remove(player);
        firePropertyChange(PROP_PLAYERLIST, null, players);
        String msg = "Spieler " + player.getName() + " ist gegangen";
        chat(msg);
        LOGGER.info(msg);
    }

    /**
     * Removes a player from the list of attendees.
     *
     * @param attendee the player to remove from the attendees.
     */
    public abstract void removeAttendee(Player attendee);

    /**
     * Adds a player to the list of attendees.
     *
     * @param attendee player to add to the attendees.
     */
    public abstract void addAttendee(Player attendee);

    /**
     * Shuffle the player positions. Subclasses should override and check the
     * game state first.
     */
    public void shufflePlayers() {
        List<Player> currentAttendees = new ArrayList<>();
        currentAttendees.addAll(attendees);
        attendees.clear();
        firePropertyChange(PROP_ATTENDEESLIST, null, attendees);
        Collections.shuffle(players);
        firePropertyChange(PROP_PLAYERLIST, null, players);
        players.stream().filter((player) -> (currentAttendees.contains(player))).forEach((player) -> {
            attendees.add(player);
        });
        firePropertyChange(PROP_ATTENDEESLIST, null, attendees);
    }

    /**
     * Getter for property game name.
     *
     * @return the name of the game.
     */
    public abstract String getName();

    /**
     * Getter for property game icon.
     *
     * @return the image icon for the game
     */
    public abstract Image getIcon();

    /**
     * Starts the game.
     */
    public abstract void startGame();

    /**
     * Stops a game. (Serverside only, not part of the game rules)
     */
    public abstract void stopGame();

    /**
     * Getter for property game state.
     *
     * @param player the player for which it is asked for. Will vary e.g. if the
     * player is allowed to knock etc.
     * @return the game state for this player in JSON format
     */
    public abstract String getGameState(Player player);

    protected void playerPropertyChanged(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Player.PROP_LOGOUT:
                Player player = (Player) evt.getSource();
                removePlayerFromRoom(player);
                break;
            case Player.PROP_ONLINE:
                firePropertyChange(PROP_PLAYER_ONLINE, null, evt.getSource());
                break;
            case Player.PROP_SOCKETMESSAGE:
                processMessage((Player) evt.getSource(), (SocketMessage) evt.getNewValue());
                break;
        }
    }

    protected abstract void processMessage(Player player, SocketMessage socketMessage);

    /**
     * Lookup for property isAttendee.
     *
     * @param player the player for which it is asked for.
     * @return true if the player is currently attendee of the game, false
     * otherwise.
     */
    public boolean isAttendee(Player player) {
        return attendees.contains(player);
    }

    /**
     * Sends a chat message to all clients.
     *
     * @param text the text to be send to the chat.
     */
    public void chat(String text) {
        chat(text, false);
    }

    /**
     * Sends a chat message to all clients.
     *
     * @param text the text to be send to the chat.
     * @param beep if the client must beep.
     */
    public void chat(String text, boolean beep) {
        chat(text, null, beep);
    }

    /**
     * Sends a chat message to all clients.
     *
     * @param text the text to be send to the chat.
     * @param sender the sending player.
     */
    public void chat(String text, Player sender) {
        chat(text, sender, false);
    }

    /**
     * Sends a chat message to all clients.
     *
     * @param text the text to be send to the chat.
     * @param sender the sending player.
     * @param beep if the client must beep.
     */
    public void chat(String text, Player sender, boolean beep) {
        if (text != null && !text.trim().isEmpty()) {
            ChatMessage chatMessage = new ChatMessage(text, sender, beep);
            sendToPlayers(gson.toJson(chatMessage));
        }
    }

    /**
     * Getter for property Attendees count.
     *
     * @return the number of attendees (still) in the game.
     */
    public int getAttendeesCount() {
        return attendees.size();
    }

    /**
     * Getter for property mover.
     *
     * @return the player which is allowed to select a move. (The game's
     * 'cursor')
     */
    public Player getMover() {
        return mover;
    }

    /**
     * Get the next attendee to a player.
     *
     * @param player the player to ask for.
     * @return the next attendee to the player.
     */
    public Player getNextTo(Player player) {
        if (attendees.isEmpty()) {
            return null;
        }
        int index = attendees.indexOf(player) + 1;
        return attendees.get(index < attendees.size() ? index : 0);
    }

    /**
     * Comparator to sort a card stack, in respect to a trump color.
     */
    public static class CardComparator implements Comparator<Card> {

        private int trump;

        public CardComparator() {
            this(0);
        }

        public CardComparator(int trump) {
            this.trump = trump;
        }

        public void setTrump(int trump) {
            this.trump = trump;
        }

        @Override
        public int compare(Card o1, Card o2) {
            int color1 = o1.getColor() == trump ? 5 : o1.getColor();
            int color2 = o2.getColor() == trump ? 5 : o2.getColor();
            if (color1 > color2) {
                return 1;
            } else if (color1 < color2) {
                return -1;
            } else if (o1.getValue() > o2.getValue()) {
                return 1;
            } else if (o1.getValue() < o2.getValue()) {
                return -1;
            }
            return 0; // corrupt card stack ;-)
        }
    }
}
