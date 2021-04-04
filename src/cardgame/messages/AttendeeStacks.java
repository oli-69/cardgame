package cardgame.messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendeeStacks {

    public final String action = "attendeeStacks";

    public Map<Integer, CardStack> stackMap;

    public AttendeeStacks(Map<Integer, List<cardgame.Card>> cards) {
        stackMap = new HashMap<>();
        cards.keySet().forEach(key -> addStack(key, cards.get(key)));
    }

    public void addStack(int attendeeID, List<cardgame.Card> cards) {
        stackMap.put(attendeeID, new CardStack(cards));
    }

}
