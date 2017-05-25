package sample;

/**
 * Created by Wienio on 2017-05-14.
 */
public class GameEvent {
    private static final int START_GAME = 1201;
    private static final int SEND_MESSAGE = 1202;
    private static final int GET_SHIPS = 1203;
    private static final int CHANGE_TURN = 1204;
    private static final int END_GAME = 1205;
    private static final int RESET_GAME = 1206;
    private static final int UPDATE_SHIP_STATUS = 1207;

    public static int getUpdateShipStatus() {
        return UPDATE_SHIP_STATUS;
    }

    public static int getResetGame() {
        return RESET_GAME;
    }

    public static int getEndGame() {
        return END_GAME;
    }

    public static int getChangeTurn() {
        return CHANGE_TURN;
    }

    public static int getShips() {
        return GET_SHIPS;
    }

    public static int getStartGame() {
        return START_GAME;
    }

    public static int getSendMessage() {
        return SEND_MESSAGE;
    }

}
