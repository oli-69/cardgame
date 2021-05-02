package cardgame.messages;

import cardgame.Player;

public class ChatMessage {

    public final String action = "chatMessage";
    public String text;
    public String sender;
    public boolean beep;

    public ChatMessage() {
        this("");
    }

    public ChatMessage(String text) {
        this(text, null);
    }

    public ChatMessage(String text, boolean beep) {
        this(text, null, beep);
    }

    public ChatMessage(String text, Player sender) {
        this(text, sender, false);
    }

    public ChatMessage(String text, Player sender, boolean beep) {
        this.text = text;
        if (sender != null) {
            this.sender = sender.getName();
        }
        this.beep = beep;
    }

}
