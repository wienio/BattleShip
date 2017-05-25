package sample;

import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Created by Wienio on 2017-04-18.
 */
public class Client {
    private Socket client;
    private DataInputStream din;
    private DataOutputStream dout;

    public Client(TextField textField, TextArea chatArea, TextArea textArea, Button gameStartButton, TextField chatText, List<Ship> shipList, Button randomizeButton, List<Ship> opponentShipList, Button resetButton, Button connectionButton, CheckBox clientCheckBox, CheckBox serverCheckBox) {
        try {
            client = new Socket(textField.getText(), ServerConnection.getPort());
            din = new DataInputStream(client.getInputStream());
            dout = new DataOutputStream(client.getOutputStream());
            textArea.setText("Udało się połączyć z serverem: " + textField.getText() + "\nWylosuj ustawienie swoich statków,\nPrzycisk Rozpocznij rozgrywkę zaczyna grę");
            Controller.setYourTurn(false);
            gameStartButton.setDisable(false);
            chatText.setEditable(true);
            textField.setEditable(false);
            connectionButton.setDisable(true);
            serverCheckBox.setDisable(true);
            clientCheckBox.setDisable(true);


            Thread t = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            int event = din.readInt();
                            if (event == GameEvent.getSendMessage()) {
                                String message = din.readUTF();
                                chatArea.appendText("Przecwnik> " + message + "\n");
                            } else if (event == GameEvent.getStartGame()) {
                                Controller.setIsGameStarted(true);
                                gameStartButton.setDisable(true);
                                randomizeButton.setDisable(true);
                                textArea.setText("Tura przeciwnika!");

                                for (int i = 0; i < 144; ++i) {
                                    dout.writeInt(GameEvent.getShips());
                                    dout.writeInt(i);
                                    dout.writeInt(shipList.get(i).getStatus());
                                }

                            } else if (event == GameEvent.getShips()) {
                                int index = din.readInt();
                                int status = din.readInt();
                                opponentShipList.get(index).setStatus(status);
                            } else if (event == GameEvent.getChangeTurn()) {
                                Controller.setYourTurn(true);
                                textArea.setText("Twoja tura!");
                            } else if (event == GameEvent.getEndGame()) {
                                Controller.setIsGameStarted(false);
                                textArea.setText("Niestety ale przeciwnik wygrał!\n\nWciśnij rozpocznij rozgrywkę, aby zagrać jeszcze raz!");
                                resetButton.setDisable(false);
                                resetButton.setVisible(true);
                            } else if (event == GameEvent.getResetGame()) {
                                Ship.reset(shipList);
                                Ship.randominize(shipList);
                                for (Ship ship : opponentShipList) {
                                    ship.setFill(Color.DIMGRAY);
                                }
                                textArea.setText("Tura przeciwnika!");
                                Controller.setYourTurn(false);
                            } else if (event == GameEvent.getUpdateShipStatus()) {
                                int shipStatus = din.readInt();
                                if (shipStatus == 0) {
                                    shipList.get(din.readInt()).setFill(Color.RED);
                                } else {
                                    shipList.get(din.readInt()).setFill(Color.GREEN);
                                }
                            }
                        } catch (IOException e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Błąd!");
                            alert.setContentText("Wystąpił błąd podczas odczytywania danych!");
                            alert.showAndWait();
                        }
                    }
                }
            };

            t.setDaemon(true);
            t.start();

        } catch (IOException e) {
            textArea.setText("Nie udało się połączyć z serverem!");
            gameStartButton.setDisable(true);
            connectionButton.setDisable(false);
            clientCheckBox.setDisable(false);
            textField.setEditable(true);
        }
    }

    public DataOutputStream getDout() {
        return dout;
    }
}