package cardgame.messages;

import cardgame.Player;

public class PlayerMsg {

    public String name;
    public boolean online;
    public int roundTokens;
    public int gameTokens;
    public int totalTokens;

    public PlayerMsg() {
        this("", false, 0, 0);
    }

    public PlayerMsg(Player player) {
        this(player.getName(), player.isOnline(), player.getRoundTokens(), player.getGameTokens(), player.getTotalTokens());
    }

    public PlayerMsg(String name, boolean online, int gameTokens, int totalTokens) {
        this(name, online, 0, gameTokens, totalTokens);
    }

    public PlayerMsg(String name, boolean online, int roundTokens, int gameTokens, int totalTokens) {
        this.name = name;
        this.online = online;
        this.roundTokens = roundTokens;
        this.gameTokens = gameTokens;
        this.totalTokens = totalTokens;
    }

}
