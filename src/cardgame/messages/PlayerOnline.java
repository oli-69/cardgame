package cardgame.messages;

import cardgame.Player;

public class PlayerOnline extends PlayerMsg {

    public final String action = "playerOnline";

    public String admin;

    public PlayerOnline(Player player, Player activeAdmin) {
        super(player);
        admin = activeAdmin != null ? activeAdmin.getName() : null;
    }
}
