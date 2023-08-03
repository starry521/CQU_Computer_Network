import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Client extends JFrame{


    public static void main(String[] args) {
        new Client();
    }

    public Client(){
        setTitle("ChatWAY");
        BufferedImage img;
        try {
            img = ImageIO.read(Server.class.getResource("/ChatWAYIcon.png"));
            setIconImage(img);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        setLayout(null);
        setSize(320,470);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        setResizable(false);


        //用户头像面板
        JPanel jpUserHead=new JPanel();
        Image image = Toolkit.getDefaultToolkit().getImage("media/touxiang.png");
        ImageIcon imageIcon=new ImageIcon("media/touxiang.png");
        ImagePanel imagePanel=new ImagePanel();
        imagePanel.paintImage(image);
        imagePanel.setBounds(110,5,100,100);
        add(imagePanel);

        //界面标题
        JLabel headTitle = new JLabel("登录",SwingConstants.CENTER);
        headTitle.setFont(new Font("微软雅黑", Font.BOLD, 30));  // 设置文本的字体类型、样式 和 大小
        headTitle.setBounds(110,100,100,50);
        add(headTitle);

        //用户名标题
        JLabel username_label = new JLabel("用户名");
        username_label.setBounds(55,140,100,50);
        username_label.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        add(username_label);

        //输入用户名的文本区域
        JTextField username_field = new JTextField();
        username_field.setBounds(52,175,216,35);
        username_field.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        add(username_field);

        //密码标题
        JLabel password_label = new JLabel("密码");
        password_label.setBounds(55,200,100,50);
        password_label.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        add(password_label);

        //输入密码的文本区域
        JPasswordField password_field = new JPasswordField();
        password_field.setBounds(52,235,216,35);
        password_field.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        add(password_field);

        //登录按钮
        JButton login = new JButton("登录");
        login.setBounds(105,290,110,40);
        login.setFont(new Font("微软雅黑",Font.PLAIN,15));
        login.setBackground(new Color(0,122,255));
        add(login);
        //注册按钮
        JButton register = new JButton("注册");
        register.setBounds(105,340,110,40);
        register.setFont(new Font("微软雅黑",Font.PLAIN,15));
        add(register);

        //APP图标面板
        JPanel appIcon=new JPanel();
        Image appImage = Toolkit.getDefaultToolkit().getImage("media/chatway_icon.png");
        ImageIcon appImageIcon=new ImageIcon("media/chatway_icon.png");
        ImagePanel appImagePanel=new ImagePanel();
        appImagePanel.paintImage(appImage);
        appImagePanel.setBounds(135,405,50,25);
        add(appImagePanel);

        setVisible(true);   //设置是否可见

        //登录按钮点击事件
        login.addActionListener(e -> {
            String username = username_field.getText();
            String password = String.valueOf(password_field.getPassword());
            if(username.length()!=0 && password.length()!=0){   //用户名和密码均不为空
                User user = new User();     //登录成功，创建用户实例
                user.setUsername(username);
                user.setPassword(password);
                UserDao dao = new UserDao();
                User u = dao.login(user);
                if(u!=null){
                    setVisible(false);
                    new Chat2(username);
                }else{
                    ImageIcon icon = new ImageIcon("media/cuowu.png");//图片的大小需要调整到合适程度
                    JOptionPane.showMessageDialog(null, "登录失败\n账号或密码错误","提示",JOptionPane.ERROR_MESSAGE,icon);

                }
            }else {
                ImageIcon icon = new ImageIcon("media/cuowu.png");//图片的大小需要调整到合适程度
                JOptionPane.showMessageDialog(null, "登录失败\n账号或密码不能为空","提示",JOptionPane.ERROR_MESSAGE,icon);

            }
        });

        //注册按钮点击事件
        register.addActionListener(e -> {
            setVisible(false);
            new Register();
        });
    }

}

class ImagePanel extends JPanel {
    private Image image = null;

    public void paintImage(Image image) {
        this.image = image;
        this.repaint();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}

