package cardgame.messages;

import cardgame.Player;
import java.util.List;

public class AttendeeList {

    public final String action = "attendeeList";

    public PlayerMsg[] attendees;
    public int[] allAttendees;
    public String mover;

    public AttendeeList(List<Player> anttendeeList, Player mover) {
        this(anttendeeList, null, mover);
    }

    public AttendeeList(List<Player> anttendeeList, int[] allAttendees, Player mover) {
        attendees = new PlayerMsg[anttendeeList.size()];
        for (int i = 0; i < attendees.length; i++) {
            attendees[i] = new PlayerMsg(anttendeeList.get(i));
        }
        this.allAttendees = allAttendees;
        if (mover != null) {
            this.mover = mover.getName();
        }
    }

}
