package cardgame;

import cardgame.messages.WebradioUrl;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

/**
 * Abstract Superclass for GameServer implementations.
 */
public class GameServer {

    static {
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.DEBUG);
    }
    private static final Logger LOGGER = LogManager.getLogger(GameServer.class);

    protected static void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            LOGGER.error(ex);
        }
    }

    protected static List<WebradioUrl> getWebradioList(Properties settings) {
        List<WebradioUrl> radioList = new ArrayList<>();
        settings.stringPropertyNames().stream().filter((String key) -> key.startsWith("radio-url.")).sorted().forEach((String key) -> {
            int i = Integer.parseInt(key.substring(key.indexOf(".") + 1));
            radioList.add(new WebradioUrl(settings.getProperty("radio." + i), settings.getProperty(key)));
        });
        return radioList;
    }

    protected static List<String> getAdminNames(Properties settings) {
        String[] names = settings.getProperty("adminNames", "").split(",");
        List<String> adminNames = new ArrayList<>(names.length);
        for (String name : names) {
            if (!name.isEmpty()) {
                adminNames.add(name.trim());
            }
        }
        return adminNames;
    }

    protected static final class PingWatchdog {

        private final CardGame game;
        private final long interval = 1000 * 30;
        private final Timer timer;

        public PingWatchdog(CardGame game) {
            this.game = game;
            timer = new Timer("ServerWatchdog");
        }

        public void start() {
            timer.schedule(getTask(), interval);
        }

        private TimerTask getTask() {
            return new TimerTask() {
                @Override
                public void run() {
                    game.sendPing();
                    game.checkGameTimeout();
                    timer.schedule(getTask(), interval);
                }
            };
        }
    }

}
