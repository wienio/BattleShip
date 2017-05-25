package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Random;

/**
 * Created by Wienio on 2017-04-10.
 */
public class Ship extends Rectangle {
    // -1 to czyste pole (bez statku)
    // 0 to statek w danym polu
    // 1 to strzal oddany ale niecelny
    // 2 to pole trafione

    private int status = -1;
    private int ID;
    public static int shipCounter = 27;

    public Ship(double width, double height, int status, int ID) {
        super(width, height);
        this.status = status;
        this.ID = ID;
    }

    public static void randominize(List<Ship> shipList) {
        int value;
        boolean horizontalDirection;
        Random generator = new Random();
        shipCounter=27;

        for (int i = 7; i > 1; --i) {
            value = Math.abs(generator.nextInt() % 144);
            horizontalDirection = generator.nextBoolean();
            if (horizontalDirection) {                                   // poziomy kierunek układu statku
                if (value % 12 + i > 12) {
                    for (int j = value; j > value - i; --j) {            // rysowanie w lewo
                        if (shipList.get(j).getStatus() != 0) {
                            shipList.get(j).setFill(Color.DODGERBLUE);
                            shipList.get(j).setStatus(0);
                        } else {
                            for (int k = j + 1; k <= value; ++k) {
                                shipList.get(k).setFill(Color.TRANSPARENT);
                                shipList.get(k).setStatus(-1);
                            }
                            ++i;
                            break;
                        }
                    }
                } else {
                    for (int j = value; j < value + i; ++j) {               // rysowanie w prawo
                        if (shipList.get(j).getStatus() != 0) {
                            shipList.get(j).setFill(Color.DODGERBLUE);
                            shipList.get(j).setStatus(0);
                        } else {
                            for (int k = j - 1; k >= value; --k) {
                                shipList.get(k).setFill(Color.TRANSPARENT);
                                shipList.get(k).setStatus(-1);
                            }
                            ++i;
                            break;
                        }
                    }
                }
            } else {  // pionowy kierunek rysowania
                if (value + 12 * i > (value % 12) + 144) {                  // rysowanie w górę
                    for (int j = value; j > value - 12 * i; j -= 12) {
                        if (shipList.get(j).getStatus() != 0) {
                            shipList.get(j).setFill(Color.DODGERBLUE);
                            shipList.get(j).setStatus(0);
                        } else {
                            for (int k = j + 12; k <= value; k += 12) {
                                shipList.get(k).setFill(Color.TRANSPARENT);
                                shipList.get(k).setStatus(-1);
                            }
                            ++i;
                            break;
                        }
                    }
                } else {                                                    // rysowanie w dół
                    for (int j = value; j < value + 12 * i; j += 12) {
                        if (shipList.get(j).getStatus() != 0) {
                            shipList.get(j).setFill(Color.DODGERBLUE);
                            shipList.get(j).setStatus(0);
                        } else {
                            for (int k = j - 12; k >= value; k -= 12) {
                                shipList.get(k).setFill(Color.TRANSPARENT);
                                shipList.get(k).setStatus(-1);
                            }
                            ++i;
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void reset(List<Ship> shipList) {
        for (int i = 0; i < shipList.size(); ++i) {
            shipList.get(i).setStatus(-1);
            shipList.get(i).setFill(Color.TRANSPARENT);
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
