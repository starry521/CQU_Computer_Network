import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Chat2 extends JFrame{
    Socket socket;

    JLabel chatTitle;
    JPanel thechatPanel;

    String currentChatObject;

    Map messageMap=new HashMap();
    JTextArea message_area = new JTextArea();
    JTextArea show_user = new JTextArea(10, 10);
    DataOutputStream outputStream;
    DataInputStream inputStream;
    String username;
    ArrayList<String> username_list = new ArrayList<>();
    List<String> userList = new ArrayList<>();
    boolean is_stop = false;

    JLabel onlineTitle;
    JList<Object> list = new JList<>();
    JPopupMenu popupMenu;
    ClientFileThread fileThread;
    public static void main(String[] args) {
        new Chat2("hr");
    }

    public Chat2(final String username) {

        //文件传输线程
        fileThread = new ClientFileThread(username);
        fileThread.start();

        this.username = username;
        currentChatObject="groupChat";
        JTextArea temp=new JTextArea();
        messageMap.put(currentChatObject, temp);
        //信息发送区域
        sendMessagePanel();
        //聊天区域
        chatPanelFiled();
        //在线用户列表
        showOnlineUser();

        setLayout(null);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        setResizable(false);

        //获取数据
        JSONObject data = new JSONObject();
        data.put("username", username);
        data.put("msg", null);

        //设置窗口标题
        setTitle("ChatWAY   " + username);
        //设置应用图标
        BufferedImage img;
        try {
            img = ImageIO.read(Server.class.getResource("/ChatWAYIcon.png"));
            this.setIconImage(img);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        //建立socket连接
        try {
            socket = new Socket("127.0.0.1", 11111);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(data.toString());
            new Thread(new Read()).start();
            System.out.println("建立连接成功");

        } catch (IOException e) {
            message_area.append("服务器无响应");
            JOptionPane.showMessageDialog(null,"服务器无响应","提示",
                    JOptionPane.WARNING_MESSAGE);
        }



    }

    private void chatPanelFiled() {


        thechatPanel = new JPanel();
        thechatPanel.setLayout(new BorderLayout());
//        thechatPanel.setBorder(new TitledBorder("写消息区，若私聊，在内容后添加（-用户名）"));
//        thechatPanel.setBorder(BorderFactory.createLineBorder(new Color(0,122,255), 3));


        JScrollPane chatScrollPanel = new JScrollPane(message_area,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        thechatPanel.add(chatScrollPanel,BorderLayout.CENTER);
        message_area.setEditable(true);
        chatScrollPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 6));


        chatTitle=new JLabel("群聊",SwingConstants.CENTER);
        chatTitle.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        thechatPanel.add(chatTitle,BorderLayout.NORTH);

        thechatPanel.setBackground(new Color(125, 186, 255));
        thechatPanel.setBounds(150,5,645,350);
        add(thechatPanel);

    }

    private void sendMessagePanel() {
        final JPanel panel_south = new JPanel();
        panel_south.setLayout(new BorderLayout());
//        panel_south.setBorder(new TitledBorder("写消息区，若私聊，在内容后添加（-用户名）"));
//        panel_south.setBorder(BorderFactory.createLineBorder(new Color(125, 186, 255), 3));
        panel_south.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 6));

        //消息编辑区域
        JTextArea send_area = new JTextArea();
        send_area.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 3));
        send_area.setBackground(new Color(240,240,240));
        panel_south.add(send_area, BorderLayout.CENTER);

        //发送按钮
        JButton send_btn = new JButton("发送");
        send_btn.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        panel_south.add(send_btn, BorderLayout.EAST);

        //功能按钮区域
        JPanel functionButtonPanel=new JPanel(new GridLayout(3,1,5,5));
        //查看群聊
        JButton groupChatButton=new JButton("群聊");
        groupChatButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        //发送图片
        JButton pictureButton=new JButton("图片");
        pictureButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        //发送文件
        JButton fileButton=new JButton("文件");
        fileButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        functionButtonPanel.add(groupChatButton);
        functionButtonPanel.add(pictureButton);
        functionButtonPanel.add(fileButton);
        functionButtonPanel.setBackground(new Color(255,255,255));

        panel_south.add(functionButtonPanel,BorderLayout.WEST);


//        panel_south.setBackground(new Color(125, 186, 255));
        panel_south.setBackground(new Color(255, 255, 255));
        panel_south.setBounds(150,360,645,155);
        add(panel_south);

        //开启群聊
        groupChatButton.addActionListener(e -> {
            System.out.println("startGroupChat");
            currentChatObject="groupChat";
            chatTitle.setText("群聊");
            thechatPanel.setBackground(new Color(125, 186, 255));
            JTextArea temp1= (JTextArea) messageMap.get(currentChatObject);
            message_area.setText(temp1.getText());
        });

        //发送图片
        pictureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPictureOpenDialog();
            }
        });

        //发送文件
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFileOpenDialog();
            }
        });

        send_btn.addActionListener(e -> {
            try {
                if (is_stop) {
                    message_area.append("你已被踢出群聊，不能发送消息\n");
                    JOptionPane.showMessageDialog(null,"你已被踢出，不能发送消息，进程已经关闭","提示",
                            JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
                    String time = sdf.format(new Date());

                    String msg = send_area.getText().trim();

                    if (!msg.equals("")) {
//                        String[] msg1 = msg.split("-");
                        JSONObject data = new JSONObject();
                        data.put("username", username);
                        data.put("msg", msg);
                        data.put("time", time);

                        try {
                            data.put("private", currentChatObject);

                        } catch (ArrayIndexOutOfBoundsException e1) {
                            data.put("private", "");
                        }
                        outputStream.writeUTF(data.toString());
                    }
                }
                send_area.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    private void showPictureOpenDialog() {
        // 创建一个默认的文件选择器
        JFileChooser fileChooser = new JFileChooser();
        // 设置默认显示的文件夹
        fileChooser.setCurrentDirectory(new File("C:/Users/JOHN/Desktop"));
        // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名）
//        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("(txt)", "txt"));
        // 设置默认使用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
        fileChooser.setFileFilter(new FileNameExtensionFilter("图像文件", "jpg", "jpeg", "png", "gif"));
        // 打开文件选择框（线程将被堵塞，知道选择框被关闭）
        int result = fileChooser.showOpenDialog(new JPanel());  // 对话框将会尽量显示在靠近 parent 的中心
        // 点击确定
        if(result == JFileChooser.APPROVE_OPTION) {
            // 获取路径
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            System.out.println(path);
            fileThread.outFileToServer(path, currentChatObject);
        }
    }

    // “打开文件”调用函数
    private void showFileOpenDialog() {
        // 创建一个默认的文件选择器
        JFileChooser fileChooser = new JFileChooser();
        // 设置默认显示的文件夹
        fileChooser.setCurrentDirectory(new File("C:/Users/JOHN/Desktop"));
        // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名）
//        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("(txt)", "txt"));
        // 设置默认使用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
        fileChooser.setFileFilter(new FileNameExtensionFilter("文本文件", "txt", "doc", "docs"));
        // 打开文件选择框（线程将被堵塞，知道选择框被关闭）
        int result = fileChooser.showOpenDialog(new JPanel());  // 对话框将会尽量显示在靠近 parent 的中心
        // 点击确定
        if(result == JFileChooser.APPROVE_OPTION) {
            // 获取路径
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            System.out.println(path);
            fileThread.outFileToServer(path, currentChatObject);
        }
    }

    private void showOnlineUser() {
        //获取在线用户
        getOnlineUser();
        //给list添加鼠标点击事件
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(list.getSelectedIndex()!=-1){
                    if(e.getClickCount()==1){
                        //单击
                        System.out.println("单击了"+userList.get(list.getSelectedIndex()));
                    }
                    else if(e.getClickCount()==2){
                        //双击
                        System.out.println("双击了"+userList.get(list.getSelectedIndex()));
                        if (userList.get(list.getSelectedIndex()).equals(username)){
                            System.out.println("无法给自己发消息");
                        }
                        else{
                            StartPrivateChat(userList.get(list.getSelectedIndex()));
                        }

                    }
                    if(e.getButton()==3){
                        //右键单机弹出菜单
                        if(list.getSelectedValuesList().size()==1){
                            popupMenu.show(list,e.getX(),e.getY());
                        }
                        else {
                            list.getSelectedValuesList();
                        }
                    }
                }
            }
        });

        popupMenu =new JPopupMenu();
        JMenuItem[] menuItems=new JMenuItem[3];
        menuItems[0]=new JMenuItem("私聊");
        menuItems[1]=new JMenuItem("加好友");
        menuItems[2]=new JMenuItem("查看更多");

        for(int i = 0; i< menuItems.length; i++){
            //设置右键菜单字体
            menuItems[i].setFont(new Font("微软雅黑",Font.BOLD,13));
            menuItems[i].addActionListener(e -> {
            });
            popupMenu.add(menuItems[i]);
        }
        list.add(popupMenu);
        JScrollPane shownScroll = new JScrollPane(list);
        list.setFont(new Font("微软雅黑",Font.BOLD,13));
        JPanel listPanel=new JPanel(new BorderLayout());
        listPanel.add(list,BorderLayout.CENTER);

        onlineTitle=new JLabel("用户列表",SwingConstants.CENTER);
        onlineTitle.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        listPanel.add(onlineTitle,BorderLayout.NORTH);

//        listPanel.setBorder(BorderFactory.createLineBorder(new Color(0,122,255), 3));
        listPanel.setBounds(5,5,140,510);
        listPanel.setBackground(new Color(255, 160, 125));
        add(listPanel);

    }

    private void StartPrivateChat(String chatPeople) {
        System.out.println("StartPrivateChat");
        currentChatObject=chatPeople;
        System.out.println(currentChatObject);
        chatTitle.setText(chatPeople);
        thechatPanel.setBackground(new Color(125, 255, 197));
        JTextArea temp;
        if (messageMap.get(currentChatObject)==null){
            temp=new JTextArea();
            messageMap.put(currentChatObject,temp);
        }else{
            temp= (JTextArea) messageMap.get(currentChatObject);
        }
        message_area.setText(temp.getText());
    }

    private void getOnlineUser() {
        for(int i=0;i<userList.size();i++) {
            String name = userList.get(i);
        }
        DefaultListModel DModel = new DefaultListModel();        //创建model
        for (int i = 0; i < userList.size(); i++) {
            DModel.addElement(userList.get(i));		//存磁盘名字
        }
//        Icon[] icons = GetIcon.getIcons("Disk");
        list.setModel(DModel);
//        list.setCellRenderer(new CellRenderer(null));//重写，显示详细的cell
    }

    public class Read implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String json = inputStream.readUTF();
                    JSONObject data = JSONObject.fromObject(json);
                    String msg = data.getString("msg");
                    if (msg.contains("踢出") && msg.contains(username)) {
                        System.out.println("已被强制下线");
                        is_stop = true;
                        message_area.append(username + ",你已经被强制下线\n");
                        JOptionPane.showMessageDialog(null,"你已经被强制下线","提示  "+username,
                                JOptionPane.WARNING_MESSAGE);
                        System.exit(0);
                    } else {
                        String pri = data.getString("private");
                        currentChatObject = pri;
                        System.out.println(pri);
                        StartPrivateChat(pri);
                        JTextArea temp= (JTextArea) messageMap.get(currentChatObject);
                        temp.append(msg + "\n");
                        System.out.println("内容："+msg+"添加到"+currentChatObject);
                        message_area.setText(temp.getText());
                        messageMap.replace(currentChatObject, temp);

                        //获取所有用户
                        username_list.clear();
                        JSONArray jsonArray = data.getJSONArray("user_list");
                        for (Object o : jsonArray) {
                            username_list.add(o.toString());
                        }

                        //在线总人数
                        show_user.setText("人数有 " + jsonArray.size() + " 人\n");
                        onlineTitle.setText("用户列表("+jsonArray.size()+")");
                        for (String s : username_list) {
                            show_user.append(s + "\n");
                        }

                        System.out.println("刷新在线人数");
                        userList.clear();
                        for (int i=username_list.size()-1;i>=0;i--) {
                            userList.add(username_list.get(i));
                        }
                        getOnlineUser();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


