package DrawGuess.Client;

import DrawGuess.Bag;
import DrawGuess.UserInfo;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientController extends Thread{
    private Socket socket;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    private ClientUI ui;
    private String userName = "annotation";
    private String DGControl = "draw";
    private String IPAddress;
    private UserInfo userInfo;

    public ClientController(ClientUI clientUI ){
        ui = clientUI;
        userInfo = new UserInfo();
    }
    // Test part
    public boolean connect(String IPAddress, String username){ // 新建socket，固定主机IP，初始化用户名。
        if(username != "") userName = username;
        this.IPAddress = IPAddress;
        userInfo.setName(userName);
        try {
            socket = new Socket(IPAddress, 8765);
			objectOutputStream = new ObjectOutputStream( socket.getOutputStream());
            objectInputStream = new ObjectInputStream( socket.getInputStream());
            Bag bag = new Bag(username, "Hello");
            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();
        }catch (IOException e) {
            return false;
        }
        finally {
            return socket.isConnected();
        }

    }


    public void run(){

            Object object;
            while(!isInterrupted()){
                try{
                    object = objectInputStream.readObject();
                    if(object != null){
                        Bag bag = (Bag)object;
                        DealBag(bag);
                    }
                }
                catch (IOException e){

                }
                catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
            Close();
    }

    private void Close(){
        try{
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void DealBag(Bag bag){
        System.out.println("bag received!");
        System.out.println("message:"+bag.message+" status:"+bag.status);
        if(bag.status == 1){
            String message = bag.message;
            if(message.equals("draw") || message.equals("guess")){
                DGControl = message;
                System.out.println(message);
                return;
            }
            ui.jTextArea.append(bag.userName+":"+message+"\r"+"\n");
        }
        if(bag.status == 3){
            //formateread
            String message = bag.message;
            if(message.equals("draw") || message.equals("guess")){
                DGControl = message;
                System.out.println(message);
                return;
            }
        }
        if(bag.status == 2){
            int x1 = bag.x1,x2 = bag.x2,y1 = bag.y1, y2 = bag.y2, width = bag.width;
            Color color = new Color(bag.color);
            ui.g.setColor(color);
//            System.out.println("x1:"+x1);
            ui.g.drawLine(x1,y1,x2,y2);
        }
    }
    
    private void SendDraw(int x1,int x2,int y1,int y2,int color,int width){
        Bag bag = new Bag(userName,x1,x2,y1,y2,color,width);
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
            System.out.println("message sended!");
            return "";
        }
        catch (IOException e){
            e.printStackTrace();
            return str;
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

    public void DrawAndSend(int x1,int x2,int y1,int y2,int color,int width){
        if(DGControl.equals("draw")){
            ui.g.drawLine(x1,y1,x2,y2);
//            System.out.println("x1:"+x1);
            SendDraw(x1,x2,y1,y2,color,width);
        }
    }
}
