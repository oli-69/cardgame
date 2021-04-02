package cardgame.messages;

import cardgame.Player;

public class PlayerMsg {

    public String name;
    public boolean online;
    public int gameTokens;
    public int totalTokens;

    public PlayerMsg() {
        this("", false, 0, 0);
    }

    public PlayerMsg(Player player) {
        this(player.getName(), player.isOnline(), player.getGameTokens(), player.getTotalTokens());
    }

    public PlayerMsg(String name, boolean online, int gameTokens, int totalTokens) {
        this.name = name;
        this.online = online;
        this.gameTokens = gameTokens;
        this.totalTokens = totalTokens;
    }

}
