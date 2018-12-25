package DrawGuess;

import java.io.Serializable;

public class Bag implements Serializable {
    public String name;
    public String message;

    public Bag(String name, String message){
        this.message = message;
        this.name = name;
    }
}
