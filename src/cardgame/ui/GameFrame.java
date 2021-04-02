package cardgame.ui;

import cardgame.CardGame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of the server side user interface. Created with NetBeans GUI
 * Editor.
 */
public class GameFrame extends javax.swing.JFrame {

    private static final Logger LOGGER = LogManager.getLogger(GameFrame.class);
    private static final long serialVersionUID = 1L;
    private final CardGame game;

    /**
     * Creates new form GameFrame
     */
    public GameFrame() {
        this(new GamePanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public CardGame getGame() {
                return null;
            }
        });
    }

    public GameFrame(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.game = gamePanel.getGame();
        initComponents();
        if (game != null) {
            super.setTitle(game.getName());
            super.setIconImage(game.getIcon());
        }
    }

    private JPanel getGamePanel() {
        return gamePanel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gamePanel = getGamePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().add(gamePanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (JOptionPane.YES_OPTION
                == JOptionPane.showConfirmDialog(this,
                        "Soll der Game Server beendet werden?", getTitle(), JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE)) {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel gamePanel;
    // End of variables declaration//GEN-END:variables
}