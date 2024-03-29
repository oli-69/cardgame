package cardgame;

import cardgame.messages.LoginError;
import cardgame.messages.LoginSuccess;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

/**
 * This class implements the player's web socket.
 */
public abstract class PlayerSocket {

    public static final String PROP_CONNECTED = "connected";
    public static final String PROP_LOGOFF = "logoff";
    public static final String PROP_MESSAGE = "message";

    private static final Logger LOGGER = LogManager.getLogger(PlayerSocket.class);

    protected final Gson gson;

    private final PropertyChangeSupport propChangeSupport;
    private final String configPath;
    private final JsonParser jsonParser;
    private final CardGame game;

    private Session session;
    private boolean connected = false;

    public PlayerSocket(CardGame game, String configPath) {
        this.game = game;
        this.configPath = configPath;
        jsonParser = new JsonParser();
        gson = new Gson();
        propChangeSupport = new PropertyChangeSupport(this);
    }

    PlayerSocket(CardGame game) {
        this(game, System.getProperty("user.dir"));
    }

    public void addPropertyChangeListener(PropertyChangeListener pl) {
        propChangeSupport.addPropertyChangeListener(pl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pl) {
        propChangeSupport.removePropertyChangeListener(pl);
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) throws IOException {
        try {
            LOGGER.debug("Message received:" + message);
            if (session.isOpen()) {
                JsonObject jsonObj = jsonParser.parse(message).getAsJsonObject();
                String action = jsonObj.get("action").getAsString();
                switch (action) {
                    case "login":
                        onLogin(jsonObj);
                        break;
                    case "logoff":
                        onLogoff(jsonObj);
                        break;
                    default:
                        processMessage(new SocketMessage(action, message, jsonObj));
                }
            }
        } catch (Exception e) {
            LOGGER.error("onText() failed: ", e);
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        this.session = session;
        connected = true;
        propChangeSupport.firePropertyChange(PROP_CONNECTED, Boolean.FALSE, Boolean.TRUE);

        //Check whether client is behind any proxy
        String ipAddress = session.getUpgradeRequest().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {    //Means client was not behind any proxy
            ipAddress = session.getRemoteAddress().getHostString();  // Then we can use getRemoteAddress to get the client ip address
        }
        LOGGER.info(ipAddress + " connected!");
//        LOGGER.info(session.getRemoteAddress().getHostString() + " connected!");
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        connected = false;
        propChangeSupport.firePropertyChange(PROP_CONNECTED, Boolean.TRUE, Boolean.FALSE);

        //Check whether client is behind any proxy
        String ipAddress = session.getUpgradeRequest().getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {    //Means client was not behind any proxy
            ipAddress = session.getRemoteAddress().getHostString();  // Then we can use getRemoteAddress to get the client ip address
        }
        LOGGER.info(ipAddress + " closed!");
//        LOGGER.info(session.getRemoteAddress().getHostString() + " closed!");
    }

    public void close() {
        try {
            closeSession(session);
        } catch (Exception e) {
            onClose(session, 0, ""); // force the event to be thrown.
        }
    }

    public void sendString(String buff) {
        try {
            if (session.isOpen()) {
                session.getRemote().sendString(buff);
            }
        } catch (IOException ex) {
            LOGGER.error("sendString", ex);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public Session getSession() {
        return session;
    }
    
    protected abstract Player createPlayer(String name);

    private boolean validateLoginData(String name, byte[] pwd) {
        String errorMsg = null;
        Properties usersProps = new Properties();
        try {
            String path = configPath + File.separator + "users.properties";
            usersProps.load(new FileInputStream(path));
            String userPwd = usersProps.getProperty(name);
            if (userPwd != null) {
                if (!userPwd.equals(new String(pwd))) {
                    errorMsg = "badPwd";
                }
            } else {
                errorMsg = "badUser";
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to read user properties", ex);
            errorMsg = "internalServerError";
        }
        if (errorMsg != null) {
            sendString(gson.toJson(new LoginError(errorMsg)));
            LOGGER.info("LOGIN FAILED: " + errorMsg);
            return false;
        }
        return true;
    }

    private void closeSession(Session session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void onLogoff(JsonObject jsonObj) {
        propChangeSupport.firePropertyChange(PROP_LOGOFF, null, Boolean.TRUE);
    }

    private void onLogin(JsonObject jsonObj) {
        String name = jsonObj.get("name").getAsString();
        byte[] pwd = jsonObj.get("pwd").getAsString().getBytes();
        if (validateLoginData(name, pwd)) {
            Player player = game.getPlayer(name);
            String successMessage = gson.toJson(new LoginSuccess(game.getVideoRoomName(), game.getRadioList()));
            if (player == null) {
                player = createPlayer(name);
                game.addPlayerToRoom(player);
                sendString(game.getGameState(player));
                sendString(successMessage);
            } else {
                closeSession(player.getSocket().getSession());
                player.setSocket(this);
                sendString(successMessage);
            }
        LOGGER.info("LOGIN " + name);
        } 
    }

    private synchronized void processMessage(SocketMessage socketMessage) {
        propChangeSupport.firePropertyChange(PROP_MESSAGE, null, socketMessage);
    }
}
