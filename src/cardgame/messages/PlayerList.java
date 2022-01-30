package cardgame.messages;

import cardgame.Player;
import java.util.List;

/**
 *
 */
public class PlayerList {

    public final String action = "playerList";

    public PlayerMsg[] players;
    public String admin;

    public PlayerList() {
        players = new PlayerMsg[0];
    }

    public PlayerList(List<Player> playerList) {
        this(playerList, null);
    }

    public PlayerList(List<Player> playerList, Player activeAdmin) {
        players = new PlayerMsg[playerList.size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new PlayerMsg(playerList.get(i));
        }
        admin = activeAdmin != null ? activeAdmin.getName() : null;
    }

}
