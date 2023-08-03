import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import net.sf.json.JSONObject;


//继承JFrame实现可视化
public class Server extends JFrame{

    //用户列表，用于存放连接上的用户信息
    ArrayList<User> user_list = new ArrayList<>();
    //用户名列表，用于显示已连接上的用户
    ArrayList<String> username_list = new ArrayList<>();

    //消息显示区域
    JTextArea show_area = new JTextArea();
    //用户名显示区域
    JTextArea show_user = new JTextArea(10, 10);

    //socket的数据输出流
    DataOutputStream outputStream = null;
    //socket的数据输入流
    DataInputStream inputStream = null;

    //从主函数里面开启服务端
    public static void main(String[] args) {
        new Server();
    }

    //构造函数
    public Server() {
        //文件服务
        ServerFileThread serverFileThread = new ServerFileThread();
        serverFileThread.start();

        //设置流式布局
        setLayout(new BorderLayout());
        //VERTICAL_SCROLLBAR_AS_NEEDED设置垂直滚动条需要时出现
        //HORIZONTAL_SCROLLBAR_NEVER设置水平滚动条不出现
        //创建信息显示区的画布并添加到show_area
        JScrollPane panel = new JScrollPane(show_area,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //设置信息显示区标题
        panel.setBorder(new TitledBorder("信息"));
        //布局到中央
        add(panel,BorderLayout.CENTER);
        //设置信息显示区为不可编辑
        show_area.setEditable(false);


        //创建用于显示用户的画布
        final JPanel panel_east = new JPanel();
        //添加流式布局
        panel_east.setLayout(new BorderLayout());
        //设置标题
        panel_east.setBorder(new TitledBorder("用户列表"));
        //在用户显示区添加show_user
        panel_east.add(new JScrollPane(show_user), BorderLayout.CENTER);
        //设置用户显示区域为不可编辑
        show_user.setEditable(false);
        //将显示用户的画布添加到整体布局的右侧
        add(panel_east, BorderLayout.WEST);

        //创建关于踢下线用户的画布
        final JPanel panel_south = new JPanel();
        //创建标签
        JLabel label = new JLabel("输入用户ID");
        //创建输入框
        JTextField out_area = new JTextField(40);
        //创建踢下线按钮
        JButton out_btn = new JButton("强制下线");
        //依次添加进画布
        panel_south.add(label);
        panel_south.add(out_area);
        panel_south.add(out_btn);
        //将踢下线用户的画布添加到整体布局的下侧
        add(panel_south,BorderLayout.SOUTH);

        //设置踢下线按钮的监听
        out_btn.addActionListener(e -> {
            try {
                //用于存储踢下线用户的名字
                String out_username;
                //从输入框中获取踢下线用户名
                out_username = out_area.getText().trim();
                //用于判断盖用户是否被踢下线
                boolean is_out=false;
                //遍历用户列表依次判断
                for (int i = 0; i < user_list.size(); i++){
                    //比较用户名，相同则踢下线
                    if(user_list.get(i).getUsername().equals(out_username)){
                        //获取被踢下线用户对象
                        User out_user = user_list.get(i);
                        //使用json封装将要传递的数据
                        JSONObject data = new JSONObject();
                        //封装全体用户名，广播至所有用户
                        data.put("user_list", username_list);
                        //广播的信息内容
                        data.put("msg", out_user.getUsername() + "被管理员踢出\n");
                        //服务端消息显示区显示相应信息
                        show_area.append(out_user.getUsername() + "被你踢出\n");
                        //依次遍历用户列表
                        for (User value : user_list) {
                            try {
                                //获取每个用户列表的socket连接
                                outputStream = new DataOutputStream(value.getSocket().getOutputStream());
                                //传递信息
                                outputStream.writeUTF(data.toString());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        //将被踢用户移出用户列表
                        user_list.remove(i);
                        //将被踢用户移出用户名列表
                        username_list.remove(out_user.getUsername());
                        //刷新在线人数
                        show_user.setText("人数有 " + username_list.size() + " 人\n");
                        //刷新在线用户
                        for (String s : username_list) {
                            show_user.append(s + "\n");
                        }
                        //判断踢出成功
                        is_out=true;
                        break;
                    }

                }
                //根据是否踢出成功弹出相应提示
                if(is_out){
                    JOptionPane.showMessageDialog(null,"强制下线成功","提示",
                            JOptionPane.WARNING_MESSAGE);
                }
                if(!is_out){
                    JOptionPane.showMessageDialog(null,"不存在用户","提示",
                            JOptionPane.WARNING_MESSAGE);
                }
                //重置输入框
                out_area.setText("");
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        //设置该窗口名
        setTitle("服务器 ");
        //引入图片
        BufferedImage img;
        try {
            //根据图片名引入图片
            img = ImageIO.read(Server.class.getResource("/ChatWAYIcon.png"));
            //设置其为该窗体logo
            setIconImage(img);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        //设置窗体大小
        setSize(700, 500);
        //设置窗体位置可移动
        setLocationRelativeTo(null);
        //设置窗体关闭方式
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗体可见
        setVisible(true);

        //socket连接相关代码
        try {
            //开启socket服务器，绑定端口11111
            ServerSocket serverSocket = new ServerSocket(11111);
            //信息显示区打印服务器启动时间
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY年MM月dd日 HH:mm:ss");
            String time = sdf.format(new Date());
            show_area.append("服务启动时间 " + time + "\n");
            //持续接收连接
            while (true) {
                //接收连接
                Socket socket = serverSocket.accept();
                //创建用户对象
                User user = new User();
                //判断是否连接上
                if (socket != null) {
                    //获取输入流
                    inputStream = new DataInputStream(socket.getInputStream());
                    //读取输入流
                    String json = inputStream.readUTF();
                    //创建信息对象
                    JSONObject data = JSONObject.fromObject(json);
                    //信息显示区打印用户上线
                    show_area.append("用户 " + data.getString("username") + " 在" + new Date() + "登陆系统"+"\n");
                    //创建新用户
                    user = new User();
                    //存储socket对象
                    user.setSocket(socket);
                    //获取输入流用户名
                    user.setUsername(data.getString("username"));
                    //添加进用户列表
                    user_list.add(user);
                    //添加进用户名列表
                    username_list.add(data.getString("username"));

                    //刷新在线人数
                    show_user.setText("人数有 " + username_list.size() + " 人\n");
                    //刷新在线用户
                    for (String s : username_list) {
                        show_user.append(s + "\n");
                    }

                }

                //封装信息对象
                JSONObject online = new JSONObject();
                //设置接收信息对象
                online.put("user_list", username_list);
                //设置信息内容
                online.put("msg", user.getUsername() + "  上线了");
                online.put("private","groupChat");
                //依次遍历，将信息广播给所有在线用户
                for (User value : user_list) {
                    //获取输出流
                    outputStream = new DataOutputStream(value.getSocket().getOutputStream());
                    //给所有用户输出上线信息
                    outputStream.writeUTF(online.toString());
                }

                //开启新线程，持续接收该socket信息
                new Thread(new ServerThread(socket)).start();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //线程代码
    class ServerThread implements Runnable {
        //存放全局变量socket
        private final Socket socket;

        //构造函数，初始化socket
        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                //获取输入流
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                //持续接收信息
                while (true) {
                    //获取传递进来的信息
                    String json = inputStream.readUTF();
                    //封装成json格式
                    JSONObject data = JSONObject.fromObject(json);

                    //通过json里面的private判断是否私发
                    boolean is_private = false;
                    //私发处理
                    for (int i = 0; i < user_list.size(); i++) {
                        //找到私发对象
                        if (user_list.get(i).getUsername().equals(data.getString("private"))) {
                            //构建私发信息内容
                            String msg = data.getString("username")+"  "
                                    + data.getString("time") + "\n"  + data.getString("msg");
                            String pri = data.getString("username");
                            //用该方法指定对象发送信息
                            send_msg(i, msg, pri);

                            //将发送成功反馈给原用户
                            for (int j = 0; j < user_list.size(); j++) {
                                //找到发信息用户
                                if(user_list.get(j).getUsername().equals(data.getString("username"))){
                                    //构建反馈信息内容
                                    String msg2 = data.getString("username")+"  "+
                                            data.getString("time")+"\n"+data.getString("msg");
                                    String pri1 = user_list.get(i).getUsername();
                                    //用该方法指定对象发送信息
                                    send_msg(j,msg2, pri1);
                                }
                            }
                            //将该操作打印到服务器监视窗
                            show_area.append(data.getString("username") +data.getString("time")+ "私发给"
                                    + data.getString("private") + ":\n" + data.getString("msg") + "\n");
                            //判断是私发
                            is_private = true;
                            break;
                        }
                    }
                    //非私发的情况
                    if (!is_private) {
                        //构建信息内容
                        String msg = data.getString("username") + "  " + data.getString("time") + "\n"
                                + data.getString("msg");
                        //添加到服务器显示
                        show_area.append(msg + "\n");
                        //依次发给所有在线用户
                        for (int i = 0; i < user_list.size(); ) {
                            String pri2 = "groupChat";
                            send_msg(i, msg, pri2);
                            i++;
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //发送信息给指定用户的方法
        public void send_msg(int i, String msg, String pri) {
            //构建对象
            JSONObject data = new JSONObject();
            //封装信息
            data.put("user_list", username_list);
            data.put("msg", msg);
            data.put("private",pri);
            //获取目标对象
            User user = user_list.get(i);
            try {
                //获取输出流
                outputStream = new DataOutputStream(user.getSocket().getOutputStream());
                //写信息
                outputStream.writeUTF(data.toString());
            } catch (IOException e) {
                //如果没有找到，则说明该用户已经下线
                User out_user = user_list.get(i);
                //重复删除操作
                user_list.remove(i);
                username_list.remove(out_user.getUsername());
                //重新构建信息
                JSONObject out = new JSONObject();
                out.put("user_list", username_list);
                out.put("msg", out_user.getUsername() + "  下线了\n");
                //将其下线通知广播给所有用户
                for (User value : user_list) {
                    try {
                        outputStream = new DataOutputStream(value.getSocket().getOutputStream());
                        outputStream.writeUTF(out.toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }
    }


}

