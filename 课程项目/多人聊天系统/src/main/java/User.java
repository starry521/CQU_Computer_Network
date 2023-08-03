import java.net.Socket;

//用户类
public class User {
    private String username;    //用户名
    private String password;    //密码
    private Socket socket;      //socket

    public String getUsername() {   //获取用户名
        return username;
    }

    public void setUsername(String username) {      //设置用户名
        this.username = username;
    }

    public String getPassword() {   //获取密码
        return password;
    }

    public void setPassword(String password) {      //设置密码
        this.password = password;
    }

    public Socket getSocket() {     //获取socket
        return socket;
    }

    public void setSocket(Socket socket) {      //设置socket
        this.socket = socket;
    }
}



