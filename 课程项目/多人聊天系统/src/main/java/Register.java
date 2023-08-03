import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Register extends JFrame {
    public static void main(String[] args) {
        new Register();
    }
    public Register(){
        setTitle("ChatWAY");
        BufferedImage img;
        try {
            img = ImageIO.read(Server.class.getResource("/ChatWAYIcon.png"));
            this.setIconImage(img);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        setLayout(null);
        setSize(320,480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        setResizable(false);


        //背景
//        JPanel jpBackground=new JPanel();
//        Image backgroundImage = Toolkit.getDefaultToolkit().getImage("media/register_bg.png");
//        ImageIcon backgroundImageIcon=new ImageIcon("media/register_bg.png");
//        ImagePanel backgroundImagePanel=new ImagePanel();
//        backgroundImagePanel.paintImage(backgroundImage);
//        backgroundImagePanel.setBounds(0,0,320,470);
//        backgroundImagePanel.setOpaque(false);
//        add(backgroundImagePanel);

        //用户头像面板
        JPanel jpUserHead=new JPanel();
        Image image = Toolkit.getDefaultToolkit().getImage("media/register_icon.png");
        ImageIcon imageIcon=new ImageIcon("media/register_icon.png");
        ImagePanel imagePanel=new ImagePanel();
        imagePanel.paintImage(image);
        imagePanel.setBounds(110,5,100,100);
        add(imagePanel);

        JLabel username_label = new JLabel("用户名");
        username_label.setBounds(55,90,100,50);
        username_label.setFont(new Font(null, Font.PLAIN, 13));
        add(username_label);

        JLabel password_label = new JLabel("密码");
        password_label.setBounds(55,150,100,50);
        password_label.setFont(new Font(null, Font.PLAIN, 13));
        add(password_label);

        JLabel password_label2 = new JLabel("请再次输入密码");
        password_label2.setBounds(55,210,100,50);
        password_label2.setFont(new Font(null, Font.PLAIN, 13));
        add(password_label2);

        JTextField username_field = new JTextField();
        username_field.setBounds(52,125,216,35);
        add(username_field);

        JPasswordField password_field = new JPasswordField();
        password_field.setBounds(52,185,216,35);
        add(password_field);

        JPasswordField password_field2 = new JPasswordField();
        password_field2.setBounds(52,245,216,35);
        add(password_field2);

        //按钮
        JButton register_success = new JButton("注册");
        register_success.setBounds(105,300,110,40);
        add(register_success);

        JButton back = new JButton("返回");
        back.setBounds(105,350,110,40);
        add(back);

        //APP图标面板
        JPanel appIcon=new JPanel();
        Image appImage = Toolkit.getDefaultToolkit().getImage("media/chatway_icon.png");
        ImageIcon appImageIcon=new ImageIcon("media/chatway_icon.png");
        ImagePanel appImagePanel=new ImagePanel();
        appImagePanel.paintImage(appImage);
        appImagePanel.setBounds(135,415,50,25);
        add(appImagePanel);

        setVisible(true);


        register_success.addActionListener(e -> {
            String username = username_field.getText();
            String password = String.valueOf(password_field.getPassword());
            String password2 = String.valueOf(password_field2.getPassword());
            System.out.println(password);
            System.out.println(password2);
            if(username.length()==0 || password.length()==0){
//                JOptionPane.showMessageDialog(null,"注册失败\n账号或密码不能为空","提示",
//                        JOptionPane.WARNING_MESSAGE);

                ImageIcon icon = new ImageIcon("media/tishi.png");//图片的大小需要调整到合适程度
                JOptionPane.showMessageDialog(null, "注册失败\n账号或密码不能为空","提示",JOptionPane.ERROR_MESSAGE,icon);

            }else if (!password.equals(password2)) {
//                JOptionPane.showMessageDialog(null,"注册失败\n两次输入密码不匹配","提示",
//                        JOptionPane.WARNING_MESSAGE);
                ImageIcon icon = new ImageIcon("media/tishi.png");//图片的大小需要调整到合适程度
                JOptionPane.showMessageDialog(null, "注册失败\n两次输入密码不匹配","提示",JOptionPane.ERROR_MESSAGE,icon);

            }else{
                System.out.println();
                System.out.println(password);
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                UserDao dao = new UserDao();
                int flag = dao.register(user);
                if(flag!=0){
//                    JOptionPane.showMessageDialog(null,"注册成功\n欢迎登录","提示",
//                            JOptionPane.WARNING_MESSAGE);
                    ImageIcon icon = new ImageIcon("media/chenggong.png");//图片的大小需要调整到合适程度
                    JOptionPane.showMessageDialog(null, "注册成功\n欢迎登录","提示",JOptionPane.ERROR_MESSAGE,icon);

                    setVisible(false);
                    new Client();

                }else{
                    //建表语句中设置了user为主键，重复则建表失败
//                    JOptionPane.showMessageDialog(null,"注册失败\n账号已经存在","提示",
//                            JOptionPane.WARNING_MESSAGE);
                    ImageIcon icon = new ImageIcon("media/tishi.png");//图片的大小需要调整到合适程度
                    JOptionPane.showMessageDialog(null, "注册失败\n账号已经存在","提示",JOptionPane.ERROR_MESSAGE,icon);

                }
            }
        });

        back.addActionListener(e ->{
            setVisible(false);
            new Client();
        });


    }

}


