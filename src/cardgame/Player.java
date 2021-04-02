package cardgame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public abstract class Player {

    public static final String PROP_LOGOUT = "logout";
    public static final String PROP_ONLINE = "online";
    public static final String PROP_SOCKETMESSAGE = "socketMessage";

    private final String name;
    private final PropertyChangeSupport propChangeSupport;
    private final List<Card> stack;

    protected final PropertyChangeListener socketListener;
    protected PlayerSocket socket;

    protected int roundTokens = 0;
    protected int gameTokens = 0;
    protected int totalTokens = 0;
    protected int skipCounter = 0;

    public Player(String name) {
        this.name = name;
        this.stack = new ArrayList<>();
        socketListener = this::socketPropertyChanged;
        propChangeSupport = new PropertyChangeSupport(this);
    }

    public Player(String name, PlayerSocket socket) {
        this(name);
        this.socket = socket;
        socket.addPropertyChangeListener(socketListener);
    }

    public void addPropertyChangeListener(PropertyChangeListener pl) {
        propChangeSupport.addPropertyChangeListener(pl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pl) {
        propChangeSupport.removePropertyChangeListener(pl);
    }

    public String getName() {
        return name;
    }

    public void clearStack() {
        stack.clear();
    }

    public List<Card> getStack() {
        return stack;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract void reset();

    protected void firePropertyChange(String popertyName, Object oldValue, Object newValue) {
        propChangeSupport.firePropertyChange(popertyName, oldValue, newValue);
    }

    /**
     * Getter for property socket.
     *
     * @return the player's webSocket.
     */
    public PlayerSocket getSocket() {
        return socket;
    }

    /**
     * Getter for property isOnline.
     *
     * @return true if the player's connection is ok, false otherwise.
     */
    public boolean isOnline() {
        return socket != null && socket.isConnected();
    }

    /**
     * Setter for property socket.
     *
     * @param socket the new webSocket.
     */
    public void setSocket(PlayerSocket socket) {
        if (socket != null) {
            socket.removePropertyChangeListener(socketListener);
        }
        this.socket = socket;
        if (socket != null) {
            socket.addPropertyChangeListener(socketListener);
            firePropertyChange(PROP_ONLINE, Boolean.FALSE, Boolean.TRUE);
        }
    }

    protected void socketPropertyChanged(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case PlayerSocket.PROP_CONNECTED:
                firePropertyChange(PROP_ONLINE, evt.getOldValue(), evt.getNewValue());
                break;
            case PlayerSocket.PROP_LOGOFF:
                firePropertyChange(PROP_LOGOUT, null, this);
                break;
            case PlayerSocket.PROP_MESSAGE:
                firePropertyChange(PROP_SOCKETMESSAGE, null, evt.getNewValue());
                break;
        }
    }

    public int getRoundTokens() {
        return roundTokens;
    }

    public void increaseRoundTokens() {
        this.roundTokens++;
    }

    public void resetRoundTokens() {
        this.roundTokens = 0;
    }

    /**
     * Adds an amount of tokens.
     *
     * @param tokens the amount of tokens to add.
     */
    public void addTotalTokens(int tokens) {
        totalTokens += tokens;
    }

    /**
     * Decrease the number of game tokens. (If the player is a/the payer of a
     * round).
     *
     * @return the new number of game tokens after decreasing.
     */
    public int decreaseGameToken() {
        --gameTokens;
        return gameTokens;
    }

    /**
     * Add the number of game tokens.
     *
     */
    public void addGameTokens(int tokens) {
        gameTokens += tokens;

    }

    /**
     * Get the amount of tokens in the current game.
     *
     * @return -1=death, 0=swimming, 1, 2 or 3 otherwise.
     */
    public int getGameTokens() {
        return gameTokens;
    }

    /**
     * Getter for property total tokens.
     *
     * @return the number of the tokens over all games.
     */
    public int getTotalTokens() {
        return totalTokens;
    }

    /**
     * Remove an amount of game tokens.
     *
     * @param tokens the amount of tokens to remove.
     */
    public void removeTotalTokens(int tokens) {
        totalTokens -= tokens;
    }

    /**
     * Get the number of skips. For games where a player can skip a round etc.
     *
     * @return the value of the skip counter.
     */
    public int getSkipCount() {
        return skipCounter;
    }

    /**
     * Increase the skip counter.
     */
    public void increaseSkipCount() {
        skipCounter++;
    }

    /**
     * Reset the skip counter (set to zero).
     */
    public void resetSkipCount() {
        skipCounter = 0;
    }

    /**
     * Zaehlt die Anzahl (nicht die Werte) der Karten einer Farbe zusammen.
     *
     * @return int
     * @param color int
     */
    public int countColor(int color) {
        int count = 0;
        for (Card card : stack) {
            if (card.getColor() == color) {
                count++;
            }
        }
        return count;
    }
}
