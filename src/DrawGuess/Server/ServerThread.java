package DrawGuess.Server;

import DrawGuess.Bag;
import DrawGuess.UserInfo;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private UserInfo userInfo;

    public ServerThread(Socket client){
        this.client = client;
    }

    public UserInfo getUserInfo() {
        return this.userInfo;
    }

    public void run() {
        try{
            processSocket();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendToClient(Bag bag) throws IOException {
        objectOutputStream.writeObject(bag);
        objectOutputStream.flush();
    }


    private void processSocket() throws IOException{
        // InputStream inputStream = client.getInputStream();
        // outputStream = client.getOutputStream();
        // BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        objectInputStream = new ObjectInputStream( new BufferedInputStream(client.getInputStream()));
        objectOutputStream = new ObjectOutputStream(client.getOutputStream());
        Object obj;
        Bag bag;

        // String userName = bufferedReader.readLine();
        userInfo = new UserInfo();
        // userInfo.setName(userName);

        try{
            obj = objectInputStream.readObject();
            bag = (Bag)obj;
            userInfo.setName(bag.userName);
            System.out.println(bag.userName+ ": " + bag.message);
            Bag returnmsg = new Bag("Server", "Sended Successful!");
            sendToClient(returnmsg);

            ServerSender.addClient(this);
            ServerSender.sendMessage(new Bag("Server","Welcome "+ this.userInfo.getName()));

            /*
            从用户那里收到一条信息 采用序列化的类的实例
            while( ！实例中拥有退出房间的信息 ){
                把这个信息实例交给处理游戏信息的方法 --> 分工
                方法返回一个处理过的信息类  --> 分工
                把返回的信息发还给用户
            }
            */
            
            // debug
            returnmsg = new Bag("metal","draw");
            returnmsg.status = 3;
            ServerSender.sendMessage(returnmsg);
            // debug
            
            long iiii = System.currentTimeMillis();
            long jjjj = 0;
            while( bag.message!="DISCONNECT" ){

                obj = objectInputStream.readObject();
                bag = (Bag)obj;
                System.out.println(bag.userName+ ": " + bag.message);
                ServerSender.sendMessage(bag);

                /*
                if((System.currentTimeMillis() - iiii) / 1000 > jjjj ){
                    jjjj = (System.currentTimeMillis() - iiii)/1000;
                    sendToClient(new Bag("Server","Connection will be break in " + (120 - jjjj) + "seconds."));
                }
                if( (System.currentTimeMillis() - iiii) / 1000 >= 20 ) break;
                */
            }
        }
        catch (IOException e){
        }
        catch (ClassNotFoundException e){
            System.out.println("ClassNotFound");
        }
        /* finally {
            try {
                objectInputStream.close();
            } catch(Exception ex) {}
            try {
                objectOutputStream.close();
            } catch(Exception ex) {}
        }
        */

        // 结束线程
        this.closeMe();

    }

    public void closeMe() throws IOException{
        ServerSender.deleteClient(this);
        client.close();
    }
}
