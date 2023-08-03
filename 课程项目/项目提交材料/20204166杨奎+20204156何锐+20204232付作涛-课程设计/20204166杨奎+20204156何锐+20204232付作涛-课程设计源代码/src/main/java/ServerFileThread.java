// ServerFileThread.java

import net.sf.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerFileThread extends Thread{
    ServerSocket server = null;
    Socket socket = null;
    static List<User> list = new ArrayList<User>();  // 存储客户端

    public void run() {
        try {
            server = new ServerSocket(8090);
            System.out.println("ServerFileThread启动成功");

            while(true) {
                System.out.println("添加socket中...");
                socket = server.accept();

                //获取输入流
                DataInputStream in = new DataInputStream(socket.getInputStream());
                //读取输入流
                String json = in.readUTF();
                //创建信息对象
                JSONObject data = JSONObject.fromObject(json);

                //创建新用户
                User user = new User();
                //存储socket对象
                user.setSocket(socket);
                //获取输入流用户名
                user.setUsername(data.getString("username"));
                //添加进用户列表
                list.add(user);
                System.out.println(list);

                System.out.println("与服务器端socket连接成功");
                // 开启文件传输线程
                FileReadAndWrite fileReadAndWrite = new FileReadAndWrite(socket);
                fileReadAndWrite.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class FileReadAndWrite extends Thread {
    private Socket nowSocket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public FileReadAndWrite(Socket socket) {
        this.nowSocket = socket;
    }
    public void run() {
        try {
            input = new DataInputStream(nowSocket.getInputStream());  // 输入流
            while (true) {
                // 获取文件名字和文件长度
                String textName = input.readUTF();
                String people = input.readUTF();    //私发对象
                System.out.println("私发对象" + people);
                long textLength = input.readLong();
                System.out.println("文件名"+textName+"     私发对象"+people+"    文件长度"+textLength);
                if (people.equals("groupChat")){
                    System.out.println("进行文件群发");
                    for(User u: ServerFileThread.list) {
                        if (u.getSocket()!=nowSocket){
                            output = new DataOutputStream(u.getSocket().getOutputStream());  // 输出流
                            output.writeUTF(textName);
                            output.flush();
                            output.writeLong(textLength);
                            output.flush();
                        }
                    }
                    System.out.println("文件群发完毕");
                }else{
                    System.out.println("进行文件私发");
                    // 发送文件名字和文件长度给所有客户端
                    for(User u: ServerFileThread.list) {
                        if (u.getUsername().equals(people)) {       //私发对象
                            output = new DataOutputStream(u.getSocket().getOutputStream());  // 输出流
                            output.writeUTF(textName);
                            output.flush();
                            output.writeLong(textLength);
                            output.flush();
                        }
                    }
                }

                byte[] buff = new byte[1024];
                int length = -1;
                long curLength = 0;
                if (people.equals("groupChat")){
                    System.out.println("进行文件群发");
                    while ((length = input.read(buff)) > 0){
                        curLength += length;
                        for(User u: ServerFileThread.list) {
                            if (u.getSocket()!=nowSocket){
                                output = new DataOutputStream(u.getSocket().getOutputStream());  // 输出流
                                output.write(buff, 0, length);
                                output.flush();
                            }
                        }
                        if (curLength >= textLength) {  // 强制退出
                            System.out.println("文件群发完毕");
                            break;
                        }
                    }
                }else{
                    System.out.println("进行文件私发");
                    for(User u: ServerFileThread.list) {
                        if (u.getUsername().equals(people)) {       //私发对象
                            output = new DataOutputStream(u.getSocket().getOutputStream());  // 输出流
                        }
                    }
                    while ((length = input.read(buff)) > 0){
                        curLength += length;
                        output.write(buff, 0, length);
                        output.flush();
                        if (curLength >= textLength) {  // 强制退出
                            System.out.println("文件发送完毕");
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            ServerFileThread.list.remove(nowSocket);  // 线程关闭，移除相应套接字
        }
    }

}

