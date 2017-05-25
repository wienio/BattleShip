package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;


/**
 * Created by Wienio on 2017-04-10.
 */
public class ServerConnection extends Thread {
    private static final int PORT = 4208;

    private ServerSocket serverSocket;
    private Socket serv;
    private DataInputStream din;
    private DataOutputStream dout;

    public ServerConnection(TextArea chatArea, TextArea textArea, Button gameStartButton, TextField chatText, List<Ship> shipList, Button randomizeButton, List<Ship> opponentShipList, Button resetButton) throws IOException {
        serverSocket = new ServerSocket(PORT);

        Thread t = new Thread() {
            public void run() {
                try {
                    serv = serverSocket.accept();
                    textArea.appendText("Klient podłączył się do serwera! \nWylosuj ustawienie swoich statków.\nPrzycisk Rozpocznij rozgrywkę zaczyna grę");
                    Controller.setYourTurn(true);
                    gameStartButton.setDisable(false);
                    chatText.setEditable(true);
                    din = new DataInputStream(serv.getInputStream());
                    dout = new DataOutputStream(serv.getOutputStream());
                    serv.setReuseAddress(true);

                    while (true) {
                        int event = din.readInt();

                        if (event == GameEvent.getSendMessage()) {   // receiving messages
                            String message = din.readUTF();
                            chatArea.appendText("Przecwnik> " + message + "\n");
                        } else if (event == GameEvent.getStartGame()) {       // for start game button
                            Controller.setIsGameStarted(true);
                            gameStartButton.setDisable(true);
                            randomizeButton.setDisable(true);
                            if (Controller.isYourTurn()) {
                                textArea.setText("Twoja tura!");
                            } else {
                                textArea.setText("Tura przeciwnika!");
                            }


                            for (int i = 0; i < 144; ++i) {
                                dout.writeInt(GameEvent.getShips());
                                dout.writeInt(i);
                                dout.writeInt(shipList.get(i).getStatus());
                            }

                        } else if (event == GameEvent.getShips()) {    // reading ship status
                            int index = din.readInt();
                            int status = din.readInt();
                            opponentShipList.get(index).setStatus(status);
                        } else if (event == GameEvent.getChangeTurn()) {  // turn change
                            Controller.setYourTurn(true);
                            textArea.setText("Twoja tura!");
                        } else if (event == GameEvent.getEndGame()) {   // if end game
                            Controller.setIsGameStarted(false);
                            textArea.setText("Niestety ale przeciwnik wygrał!\n\nWciśnij rozpocznij rozgrywkę, aby zagrać jeszcze raz!");
                            resetButton.setDisable(false);
                            resetButton.setVisible(true);
                        } else if (event == GameEvent.getResetGame()) {   // reset game
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
                    }
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Błąd sieci!");
                    alert.setContentText("Nieznany błąd przy próbie utworzenia servera!");
                    alert.showAndWait();
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    public static int getPort() {
        return PORT;
    }

    public DataOutputStream getDout() {
        return dout;
    }

}