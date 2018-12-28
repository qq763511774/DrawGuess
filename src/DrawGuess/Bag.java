package DrawGuess;

import java.io.Serializable;
import java.awt.Color;

public class Bag implements Serializable {
    public int x1,x2,y1,y2,width;
    public int color;
    public String message;
    public String userName;
    public int status; // 1: plain message 2: point information 3: hint message or none-client-related message
    public Bag(String userName,String message) {
        this.message = message;
        this.userName = userName;
        this.status = 1;
    }
    public Bag(String userName,String message, int newStatus) {
        this.message = message;
        this.userName = userName;
        this.status = newStatus;
    }

    public Bag(){}
    public Bag(String userName,int x1,int x2,int y1,int y2,int color,int width){
        this.userName = userName;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.width = width;
        this.color = color;
        this.message = "this is not a message";
        this.status = 2;
    }
}

