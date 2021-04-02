package cardgame;

import java.util.Comparator;
import java.util.List;

public class PlayerIdComparator implements Comparator<Player> {
    
    final List<Player> playerList;

    public PlayerIdComparator(List<Player> playerList) {
        this.playerList = playerList;
    }

    @Override
    public int compare(Player p1, Player p2) {
        return playerList.indexOf(p1) < playerList.indexOf(p2) ? -1 : 1;
    }
    
}
