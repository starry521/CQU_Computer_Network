import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
public class Chat extends JFrame{

    JTextArea show_area = new JTextArea();
    JTextArea show_user = new JTextArea(10, 10);
    DataOutputStream outputStream;
    DataInputStream inputStream;
    String username;
    ArrayList<String> username_list = new ArrayList<>();
    boolean is_stop = false;

    public Chat(final String username) {
        this.username = username;

        final JPanel panel_south = new JPanel();
        panel_south.setLayout(new BorderLayout());
        panel_south.setBorder(new TitledBorder("写消息区，若私聊，在内容后添加（-用户名）"));
        JTextField send_area = new JTextField(40);
        panel_south.add(send_area, BorderLayout.CENTER);
        JButton send_btn = new JButton("发送");
        panel_south.add(send_btn,BorderLayout.EAST);
        add(panel_south, BorderLayout.SOUTH);

        send_btn.addActionListener(e -> {
            try {

                if (is_stop) {
                    show_area.append("你已被踢出，不能发送消息\n");
                    JOptionPane.showMessageDialog(null,"你已被踢出，不能发送消息，进程已经关闭","提示",
                            JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = sdf.format(new Date());

                    String msg = send_area.getText().trim();

                    if (!msg.equals("")) {
                        String[] msg1 = msg.split("-");
                        JSONObject data = new JSONObject();
                        data.put("username", username);
                        data.put("msg", msg1[0]);
                        data.put("time", time);

                        try {
                            data.put("private", msg1[1]);

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

        JScrollPane panel = new JScrollPane(show_area,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.setBorder(new TitledBorder("信息显示区"));
        add(panel,BorderLayout.CENTER);
        show_area.setEditable(false);

        final JPanel panel_east = new JPanel();
        panel_east.setLayout(new BorderLayout());
        panel_east.setBorder(new TitledBorder("在线用户"));
        panel_east.add(new JScrollPane(show_user), BorderLayout.CENTER);
        show_user.setEditable(false);
        add(panel_east, BorderLayout.EAST);

        setTitle("用户  " + username);
        BufferedImage img;
        try {
            img = ImageIO.read(Server.class.getResource("/ChatWAYIcon.png"));
            this.setIconImage(img);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        JSONObject data = new JSONObject();
        data.put("username", username);
        data.put("msg", null);

        try {
            Socket socket = new Socket("127.0.0.1", 11111);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(data.toString());
            new Thread(new Read()).start();
        } catch (IOException e) {
            show_area.append("服务器无响应");
            JOptionPane.showMessageDialog(null,"服务器无响应","提示",
                    JOptionPane.WARNING_MESSAGE);
        }
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
                        is_stop = true;
                        show_area.append(username + ",你已经被踢出群聊\n");
                        JOptionPane.showMessageDialog(null,"你已经被踢出群聊","提示",
                                JOptionPane.WARNING_MESSAGE);
                        System.exit(0);
                    } else {
                        show_area.append(msg + "\n");
                        show_area.selectAll();
                        username_list.clear();
                        JSONArray jsonArray = data.getJSONArray("user_list");
                        for (Object o : jsonArray) {
                            username_list.add(o.toString());
                        }
                        show_user.setText("人数有 " + jsonArray.size() + " 人\n");
                        for (String s : username_list) {
                            show_user.append(s + "\n");
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}


