package DrawGuess.Client;

import DrawGuess.Bag;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    public static void main(String args[]){
        Scanner input = new Scanner(System.in);
        String username = input.next();
        try {
            Socket socket = new Socket("192.168.1.201", 8765);

            Bag bag = new Bag(username, "Hello");

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();

            // ObjectInputStream objectInputStream = new ObjectInputStream( new BufferedInputStream(socket.getInputStream()));
            ObjectInputStream objectInputStream = new ObjectInputStream( socket.getInputStream());
            while( true ) {

                Object obj = objectInputStream.readObject();
                if (obj != null) {
                    Bag bag1 = (Bag) obj;
                    System.out.println(bag1.name + ": " + bag1.message);
                }

            }

        }catch (IOException e){

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}
