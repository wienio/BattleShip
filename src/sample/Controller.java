package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private static ServerConnection serverConnection;
    private static Client client;
    private static List<Ship> shipList = new ArrayList<>(144);
    private static List<Ship> opponentShipList = new ArrayList<>(144);
    private static boolean yourTurn;
    private static boolean isGameStarted = false;  // for both, if true game will start

    @FXML
    private CheckBox serverCheckBox;

    @FXML
    private CheckBox clientCheckBox;

    @FXML
    private Button startButton;

    @FXML
    private Button connectionButton;

    @FXML
    private Button gameStartButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button randomizeButton;

    @FXML
    private TextArea textArea;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField textField;

    @FXML
    private TextField chatText;

    @FXML
    private AnchorPane panel;


    public void setTextArea(String s) {
        textArea.setText(s);
    }

    @FXML
    private void initialize() {
        chatText.setEditable(false);
        serverCheckBox.setSelected(true);
        textField.setDisable(true);
        connectionButton.setDisable(true);
        gameStartButton.setDisable(true);
        resetButton.setDisable(true);
        resetButton.setVisible(false);
        createTable();

        for (Ship ship : opponentShipList) {
            ship.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (isGameStarted && yourTurn) {

                        if (ship.getStatus() == -1) {
                            ship.setStatus(1);
                            ship.setFill(Color.GREEN);
                            yourTurn = false;
                            textArea.setText("Niestety nie udało Ci się trafić!\nTura przeciwnika!");
                            try {
                                changeTurn(opponentShipList.indexOf(ship), -1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (ship.getStatus() == 0) {
                            Ship.shipCounter--;
                            ship.setStatus(2);
                            ship.setFill(Color.RED);
                            yourTurn = false;
                            textArea.setText("Udało Ci się trafić statek przeciwnika!\nTura przeciwnika!");
                            try {
                                changeTurn(opponentShipList.indexOf(ship), 0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            endGameCheck();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @FXML
    private void clientCheckBoxClicked() {
        clientCheckBox.setSelected(true);
        serverCheckBox.setSelected(false);
        textField.setDisable(false);
        connectionButton.setDisable(false);
        startButton.setDisable(true);
    }

    @FXML
    private void serverCheckBoxClicked() {
        serverCheckBox.setSelected(true);
        clientCheckBox.setSelected(false);
        textField.setDisable(true);
        connectionButton.setDisable(true);
        startButton.setDisable(false);
    }

    @FXML
    private void sendMessage() throws IOException, InterruptedException {
        if (chatText.getLength() != 0) {
            if (serverConnection != null) {
                serverConnection.getDout().writeInt(GameEvent.getSendMessage());
                serverConnection.getDout().writeUTF(chatText.getText());
            }
            if (client != null) {
                client.getDout().writeInt(GameEvent.getSendMessage());
                client.getDout().writeUTF(chatText.getText());
            }
            chatArea.appendText("Ty> " + chatText.getText() + "\n");
            chatText.clear();
        }
    }

    @FXML
    private void handleStartButton() throws IOException {
        serverConnection = new ServerConnection(chatArea, textArea, gameStartButton, chatText, shipList, randomizeButton, opponentShipList, resetButton);
        textArea.setText("Server znajduje się na porcie: " + ServerConnection.getPort() + "\n");
        textArea.appendText("Oczekuje na połączenie...\n");
        clientCheckBox.setDisable(true);
        startButton.setDisable(true);
        startButton.setText("Server włączony...");
        serverCheckBox.setDisable(true);
    }

    @FXML
    private void handleConnectionButton() {
        client = new Client(textField, chatArea, textArea, gameStartButton, chatText, shipList, randomizeButton, opponentShipList, resetButton, connectionButton, clientCheckBox, serverCheckBox);
    }

    @FXML
    private void handleResetButton() throws IOException {
        Ship.reset(shipList);
        Ship.randominize(shipList);
        for (Ship ship : opponentShipList) {
            ship.setFill(Color.DIMGRAY);
        }
        if (serverConnection != null) {
            serverConnection.getDout().writeInt(GameEvent.getResetGame());
        } else {
            client.getDout().writeInt(GameEvent.getResetGame());
        }
        resetButton.setDisable(true);
        resetButton.setVisible(false);
        isGameStarted = true;
        yourTurn = true;
        textArea.setText("Twoja tura!");
        if (serverConnection != null) {
            serverConnection.getDout().writeInt(GameEvent.getStartGame());
            for (int i = 0; i < 144; ++i) {
                serverConnection.getDout().writeInt(GameEvent.getShips());
                serverConnection.getDout().writeInt(i);
                serverConnection.getDout().writeInt(shipList.get(i).getStatus());
            }
        } else {
            client.getDout().writeInt(GameEvent.getStartGame());
            for (int i = 0; i < 144; ++i) {
                client.getDout().writeInt(GameEvent.getShips());
                client.getDout().writeInt(i);
                client.getDout().writeInt(shipList.get(i).getStatus());
            }
        }
    }

    @FXML
    private void handleRandomizeButton() {
        Ship.reset(shipList);
        Ship.randominize(shipList);
    }

    @FXML
    private void handleStartGameButton() throws IOException {
        if (serverConnection != null) {
            textArea.setText("Twoja tura");
            serverConnection.getDout().writeInt(GameEvent.getStartGame());
            for (int i = 0; i < 144; ++i) {
                serverConnection.getDout().writeInt(GameEvent.getShips());
                serverConnection.getDout().writeInt(i);
                serverConnection.getDout().writeInt(shipList.get(i).getStatus());
            }
        } else {
            textArea.setText("Tura przeciwnika!");
            client.getDout().writeInt(GameEvent.getStartGame());
            for (int i = 0; i < 144; ++i) {
                client.getDout().writeInt(GameEvent.getShips());
                client.getDout().writeInt(i);
                client.getDout().writeInt(shipList.get(i).getStatus());
            }
        }
        isGameStarted = true;
        gameStartButton.setDisable(true);
        randomizeButton.setDisable(true);
    }

    public void createTable() {
        int counter = 0;
        for (int i = 0; i < 12; ++i) {   // tworzy elementy na planszy
            for (int j = 0; j < 12; ++j) {
                Rectangle rectangle = new Rectangle(30, 30, Color.DIMGRAY);
                Ship ship = new Ship(30, 30, -1, counter++);
                rectangle.setLayoutX(41 + 32 * j);
                rectangle.setLayoutY(57 + 32 * i);
                ship.setLayoutX(rectangle.getLayoutX());
                ship.setLayoutY(rectangle.getLayoutY());
                ship.setFill(Color.TRANSPARENT);
                shipList.add(ship);
                panel.getChildren().add(rectangle);
            }
        }

        counter = 0;
        for (int i = 0; i < 12; ++i) {
            for (int j = 0; j < 12; ++j) {
                Rectangle rectangle = new Rectangle(30, 30, Color.DIMGRAY);
                Ship ship = new Ship(30, 30, -1, counter++);
                rectangle.setLayoutX(530 + 32 * j);
                rectangle.setLayoutY(57 + 32 * i);
                ship.setLayoutX(rectangle.getLayoutX());
                ship.setLayoutY(rectangle.getLayoutY());
                ship.setFill(Color.TRANSPARENT);
                opponentShipList.add(ship);
                panel.getChildren().add(rectangle);
            }
        }
        Ship.randominize(shipList);
        panel.getChildren().addAll(shipList);
        panel.getChildren().addAll(opponentShipList);
    }

    public void changeTurn(int index, int shipStatus) throws IOException {
        if (serverConnection != null) {
            serverConnection.getDout().writeInt(GameEvent.getChangeTurn());
            serverConnection.getDout().writeInt(GameEvent.getUpdateShipStatus());
            serverConnection.getDout().writeInt(shipStatus);
            serverConnection.getDout().writeInt(index);
        } else {
            client.getDout().writeInt(GameEvent.getChangeTurn());
            client.getDout().writeInt(GameEvent.getUpdateShipStatus());
            client.getDout().writeInt(shipStatus);
            client.getDout().writeInt(index);
        }
    }

    public static void setYourTurn(boolean yourTurn) {
        Controller.yourTurn = yourTurn;
    }

    public static void setIsGameStarted(boolean isGameStarted) {
        Controller.isGameStarted = isGameStarted;
    }

    public void endGameCheck() throws IOException {
        if (Ship.shipCounter == 0) {
            if (serverConnection != null) {
                serverConnection.getDout().writeInt(GameEvent.getEndGame());
            } else {
                client.getDout().writeInt(GameEvent.getEndGame());
            }
            textArea.setText("GRATULACJEE, WYGRAŁEŚ!\n\nMożesz zagrać jeszcze raz wciskając przycisk reset!");
            resetButton.setDisable(false);
            resetButton.setVisible(true);
        }
    }

    public static boolean isYourTurn() {
        return yourTurn;
    }
}