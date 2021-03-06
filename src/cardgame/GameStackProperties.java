package cardgame;

import cardgame.messages.GameStack;
import java.awt.Point;
import java.util.List;

public class GameStackProperties {

    public final List<Card> cards;
    public Point[] offset = new Point[]{new Point(), new Point(), new Point(),};
    public float[] rotation = new float[]{0f, 0f, 0f};
    public int[] cardFlips;

    private final int maxXOffset;
    private final int maxYOffset;
    private final float maxRot;

    public GameStackProperties(List<Card> cards, int size) {
        this(cards, size, 10, 5, 3.5f);
    }

    public GameStackProperties(List<Card> cards, int size, int maxX, int maxY, float rot) {
        this.cards = cards;
        this.maxXOffset = maxX;
        this.maxYOffset = maxY;
        this.maxRot = rot;
        initialize(size);
    }

    private void initialize(int size) {
        cards.clear();
        cardFlips = new int[size];
        rotation = new float[size];
        offset = new Point[size];
        for (int i = 0; i < size; i++) {
            offset[i] = new Point();
            cards.add(Card.GHOST);
        }
        shakeAll();
    }

    public void setSize(int size) {
        initialize(size);
    }

    public GameStack getGameStack() {
        return new GameStack(cards, offset, rotation, cardFlips);
    }

    public void shakeAll() {
        shakeAll(false);
    }

    public void shakeAll(boolean flips) {
        clearFlips();
        for (int i = 0; i < cardFlips.length; i++) {
            shake(i, (flips && shallFlip()) ? getRandomFlips() : 0);
        }
    }

    public void shake(int id) {
        clearFlips();
        shake(id, shallFlip() ? getRandomFlips() : 0);
    }

    private void shake(int id, int flips) {
        this.cardFlips[id] = flips;
        rotate(id);
        offset(id);
    }

    private int getRandomFlips() {
        int max = 5;
        int numFlips = (int) ((Math.random() * (2 * max)) - max);
        boolean superFlip = Math.random() > 0.9;
        return (superFlip ? 3 : 1) * numFlips;
    }

    private void rotate(int id) {
        rotation[id] = getRandomRotation(maxRot);
    }

    public void offset(int id) {
        offset[id].setLocation(getRandomOffset(maxXOffset), getRandomOffset(maxYOffset));
    }

    private float getRandomRotation(float max) {
        return (float) ((Math.random() * (2 * max)) - max);
    }

    private int getRandomOffset(int max) {
        return (int) ((Math.random() * (2 * max)) - max);
    }

    private boolean shallFlip() {
        return Math.random() > 0.9;
    }

    private void clearFlips() {
        for (int i = 0; i < cardFlips.length; i++) {
            cardFlips[i] = 0;
        }
    }
}
