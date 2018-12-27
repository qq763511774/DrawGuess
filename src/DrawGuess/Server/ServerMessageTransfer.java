package DrawGuess.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMessageTransfer {
    public static void main(String[] args) throws IOException {
        ServerMessageTransfer transfer = new ServerMessageTransfer();
        int port = 8765;
        transfer.setServerPort( port );
    }

    private void setServerPort( int port ) throws IOException{
        ServerSocket server = new ServerSocket(port);

        // print server port
        System.out.println("服务器成功建立于端口" + port);

        // 多线程处理每个用户的连接
        int uid = 0;
        while( true ){
            Socket socket = server.accept();
            // debug
            System.out.println("New client connection: " + socket.getRemoteSocketAddress().toString());

            ServerThread serverThread = new ServerThread(socket, uid);
            uid++;
            serverThread.start();
        }
    }
}
