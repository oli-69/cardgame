package cardgame.ui;

import cardgame.CardGame;
import javax.swing.JPanel;

/**
 * GameFrame's content panel must inherit from this class.
 */
public abstract class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Getter for property game.
     *
     * @return return the game which is controlled by the panel ui.
     */
    public abstract CardGame getGame();
}
