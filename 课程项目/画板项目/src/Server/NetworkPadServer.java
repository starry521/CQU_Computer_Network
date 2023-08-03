package Server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;

public class NetworkPadServer extends JFrame{
    // 保存的图形集
    public static ShapeList shapeList =new ShapeList();
    // 当前最大下标，初始值赋值为-1
    public static MaxIndex maxIndex=new MaxIndex(-1);
    // 用Vector来存储加入服务端的客户端所申请的线程
    public static Vector<ServerThread> threadVector =new Vector<ServerThread>();

    public NetworkPadServer() throws IOException, ClassNotFoundException {
        JTextArea jtaLog = new JTextArea();
        // Create a scroll pane to hold text area
        JScrollPane scrollPane = new JScrollPane(jtaLog);
        // Add the scroll pane to the frame
        add(scrollPane, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setTitle("NetworkPad Server");
        setVisible(true);

        System.out.println("等待链接");
        ServerSocket serverSocket = new ServerSocket(5600);
        Socket paint;
        jtaLog.append(new Date()+": Server started at socket 5600\n");
        while(true){
            //等待客户端请求
            paint=serverSocket.accept();
            jtaLog.append(new Date()+": Wait for players to join session "+paint+'\n');
            //每次请求都启动一个线程来处理，同时传入端口，最大下标，图形列表，以及储存所有与服务端连接的客户端线程
            ServerThread s=new ServerThread(paint,maxIndex, shapeList, threadVector);
            new Thread(s).start();
        }
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        NetworkPadServer frame=new NetworkPadServer();
    }
}

//最大下标类
class MaxIndex{
    public int Index;
    public MaxIndex(int i){
        Index=i;
    }
}

//图形存储列表
class ShapeList{
    PaintFrame.Shape[] realList = new PaintFrame.Shape[30000];
    public ShapeList(){}
}