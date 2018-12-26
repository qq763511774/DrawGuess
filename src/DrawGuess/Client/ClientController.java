package DrawGuess.Client;

import DrawGuess.Bag;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientController {
    private Socket socket;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    private ClientUI ui;
    private String userName;
    private String DGControl;

    public ClientController(ClientUI clientUI ){
        ui = clientUI;
    }
    // Test part
    public boolean dealwith(String IPAddress, String username){
        try {
            socket = new Socket(IPAddress, 8765);
            userName = username;
            Bag bag = new Bag(username, "Hello");
            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream( socket.getInputStream());
            DataInputStream dataInputStream= new DataInputStream(socket.getInputStream());
            DGControl = dataInputStream.readUTF();
            dataInputStream.close();
            while( true ) {
                Object obj = objectInputStream.readObject();
                if (obj != null) {
                    Bag bag1 = (Bag) obj;
                    DealBag(bag1);
                }
            }
        }catch (IOException e){
            //return false;
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        finally {
            try{
                objectInputStream.close();
                objectOutputStream.close();
            }
            catch (Exception e){
                System.out.println("close error!");
            }
            return true;
        }

    }

    public void DealBag(Bag bag){
        if(bag.status == 1){
            String message = bag.message;
            ui.jTextArea.append(message);
        }
        if(bag.status == 2){
            //formateread
        }
        if(bag.status == 3){
            int x = bag.x1,x2 = bag.x2,y1 = bag.y1, y2 = bag.y2, width = bag.width;
            Color color = bag.color;
            //ui.Draw(x1,x2,y1,y2,width,color);
        }
    }

    private void SendDraw(int x1,int x2,int y1,int y2,Color color,int width){
        Bag bag = new Bag(userName,x1,x2,y1,y2,width,color);
        try{
            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public String SendMsg(String str){
        Bag bag = new Bag(userName,str);
        try{
            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();
            return str;
        }
        catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public void SendHint(String str){
        Bag bag = new Bag(userName,str);
        bag.status = 3;
        try{
            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void DrawAndSend(int x1,int x2,int y1,int y2,Color color,int width){
        if(DGControl == "draw"){
            ui.g.drawLine(x1,y1,x2,y2);
            SendDraw(x1,x2,y1,y2,color,width);
        }
    }
}
