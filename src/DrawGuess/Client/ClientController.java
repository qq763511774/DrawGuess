package DrawGuess.Client;

import DrawGuess.Bag;
import DrawGuess.UserInfo;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
//import java.util.Queue;

public class ClientController extends Thread{
    private Socket socket;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    private ClientUI ui;
    private boolean isDraw;
    private String IPAddress;
    private UserInfo userInfo;
//    private Queue<Bag> initQueue;
    private boolean isReady = false;

    public ClientController(ClientUI clientUI ){
        ui = clientUI;
        userInfo = new UserInfo();
//        initQueue = new LinkedList<>();
    }
    // Test part
    public boolean connect(String IPAddress, String username){ // 新建socket，固定主机IP，初始化用户名。
        if(username != "") userInfo.setName(username);
        else{ userInfo.setName("annotation");}
        this.IPAddress = IPAddress;
        try {
            socket = new Socket(IPAddress, 8765);
			objectOutputStream = new ObjectOutputStream( socket.getOutputStream());
            objectInputStream = new ObjectInputStream( socket.getInputStream());
            SendMsg("Hello");
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
//        System.out.println(initQueue.isEmpty());
//        if(ui.jTextArea != null){
//            while(!initQueue.isEmpty()){
//                Bag tempBag = initQueue.poll();
//                ui.jTextArea.append(tempBag.userName+":"+tempBag.message+"\r"+"\n");
//            }
//        }
        System.out.println("userName:"+bag.userName+" message:"+bag.message+" status:"+bag.status);
        if(bag.status == 1){
            String message = bag.message;
            ui.jTextArea.append(bag.userName+":"+message+"\r"+"\n");
        }
        if(bag.status == 3){
            //formateread
            String message = bag.message;
//            if(message.equals("draw") || message.equals("guess")){
//                DGControl = message;
//                System.out.println(message);
//                return;
//            }
            if(bag.userName.equals("HINT")){
                if(!isDraw){
                    ui.content.setText(bag.message);
                }
            }
            if(message.equals("STOPGAME")){
                isReady = true;
                ChangeReady();
                ui.GameToWait();

            }
//            if(message.equals("STARTGAME")){
//                if(DGControl.equals("draw"))ui.WaitToDrawGame();
//                if(DGControl.equals("guess"))ui.WaitToGuessGame();
//            }
            if(Character.isDigit(message.charAt(0))){
                userInfo.setUid(Integer.valueOf(message));
            }
            if(bag.userName.equals("UID")){
                if(bag.x1 == userInfo.getUid()){
                    isDraw = true;
                    ui.WaitToDrawGame(message);
                }
                else{
                    isDraw = false;
                    ui.WaitToGuessGame();
                }
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
        Bag bag = new Bag(userInfo.getName(),x1,x2,y1,y2,color,width);
        try{
            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public String SendMsg(String str){
        Bag bag = new Bag(userInfo.getName(),str);
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

    public void SendInfo(String str){
        Bag bag = new Bag(userInfo.getName(),str,3);
        try{
            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void DrawAndSend(int x1,int x2,int y1,int y2,int color,int width){
        if(isDraw){
            ui.Draw(x1,x2,y1,y2);
//            System.out.println("x1:"+x1);
            SendDraw(x1,x2,y1,y2,color,width);
        }
    }

    public void ChangeReady(){
        Bag bag = new Bag(userInfo.getName(),"setReady",3);
        if(isReady == false){
            bag.message = "setReady";
            isReady = true;
        }
        else{
            bag.message = "cancelReady";
            isReady = false;
        }
        try{
            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
