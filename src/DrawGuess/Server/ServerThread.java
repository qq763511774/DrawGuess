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

    public ServerThread(Socket client, int uid) {
        userInfo = new UserInfo();
        this.client = client;
        userInfo.setUid(uid);
    }

    public UserInfo getUserInfo() {
        return this.userInfo;
    }

    public void run() {
        try {
            processSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToClient(Bag bag) throws IOException {
        objectOutputStream.writeObject(bag);
        objectOutputStream.flush();
    }


    private void processSocket() throws IOException {
        objectInputStream = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
        objectOutputStream = new ObjectOutputStream(client.getOutputStream());
        Object obj;
        Bag bag;


        try {
            obj = objectInputStream.readObject();
            bag = (Bag) obj;

            userInfo.setName(bag.userName);
            userInfo.isReady = false;

            System.out.println(bag.userName + ": " + bag.message);

            Bag returnmsg = new Bag("Server", "" + userInfo.getUid());
            sendToClient(returnmsg);

            ServerSender.addClient(this);
            ServerSender.sendMessage(new Bag("Server", "Welcome " + this.userInfo.getName()));

            /*
            从用户那里收到一条信息 采用序列化的类的实例
            while( ！实例中拥有退出房间的信息 ){
                把这个信息实例交给处理游戏信息的方法 --> 分工
                方法返回一个处理过的信息类  --> 分工
                把返回的信息发还给用户
            }
            */

            // long iiii = System.currentTimeMillis();
            // long jjjj = 0;
            while (true) {

                obj = objectInputStream.readObject();
                bag = (Bag) obj;
                System.out.println(bag.userName + ": " + bag.message);

                if (bag.status == 1) {
                    // 交给后台判断胜负
                    String tempString = GameThread.judge(bag.message);
                    String 结果 = tempString.substring(0, 3);
                    String 处理串 = tempString.substring(4);

                    // 把结果发送给所有人
                    if (结果.equals("YES")) { // 猜对了
                        ServerSender.sendMessage(new Bag("Server", userInfo.getName() + "猜对了答案"));
                    } else { // 猜错了
                        bag.message = 处理串;
                        ServerSender.sendMessage(bag);
                    }
                } else if (bag.status == 2) { // point info
                    // 直接进行转发
                    ServerSender.sendMessage(bag);
                } else if (bag.status == 3) { // 收到的状态信息
                    if (bag.message.equals("DISCONNECT")) break; // 客户主动断开连接

                    if (bag.message.equals("setReady")) {
                        userInfo.isReady = true;
                        if (ServerSender.allReady()) { // 所有人都准备好了
                            // 开始游戏
                            GameThread.startGame(ServerSender.getThreadNumber());
                        }
                    }

                    if (bag.message.equals("cancelReady")) {
                        userInfo.isReady = false;
                    }

                }

                /*
                if((System.currentTimeMillis() - iiii) / 1000 > jjjj ){
                    jjjj = (System.currentTimeMillis() - iiii)/1000;
                    sendToClient(new Bag("Server","Connection will be break in " + (120 - jjjj) + "seconds."));
                }
                if( (System.currentTimeMillis() - iiii) / 1000 >= 20 ) break;
                */


            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
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

    public void closeMe() throws IOException {
        ServerSender.deleteClient(this);
        client.close();
    }
}
