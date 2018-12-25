package DrawGuess.Server;

import DrawGuess.Bag;
import DrawGuess.UserInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Iterator;

public class ServerSender {
    // 客户端线程列表
    private static ArrayList<ServerThread> serverThreadArrayList = new ArrayList<ServerThread>();

    private ServerSender(){}

    // 加入新的客户端到客户端列表
    public static void addClient(ServerThread serverThread) throws IOException{
        serverThreadArrayList.add(serverThread);
    }

    // 从列表中移除一个客户端线程
    public static void deleteClient(ServerThread serverThread) throws IOException{
        serverThreadArrayList.remove(serverThread);
    }

    public static void sendMessage(Bag msg) throws IOException{
        Iterator<ServerThread> iterator = serverThreadArrayList.iterator();
        while( iterator.hasNext()){
            iterator.next().sendToClient(msg);
        }
    }

}
