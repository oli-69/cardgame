package cardgame.messages;

import cardgame.Player;

public class PlayerOnline extends PlayerMsg {

    public final String action = "playerOnline";

    public PlayerOnline(Player player) {
        super(player);
    }

}
