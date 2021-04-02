package cardgame.messages;

import cardgame.Player;
import java.util.List;

/**
 *
 */
public class PlayerList {

    public final String action = "playerList";
    
    public PlayerMsg[] players;
    
    public PlayerList() {
        players = new PlayerMsg[0];
    }

    public PlayerList(List<Player> playerList) {
        players = new PlayerMsg[playerList.size()];
        for( int i=0; i<players.length; i++) {
            players[i] = new PlayerMsg(playerList.get(i));
        }
    }

}
