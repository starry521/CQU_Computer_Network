package PaintFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;


public class PaintFrame3 extends JFrame{
    private final DrawPanel drawPanel;
    private JToolBar jToolBar;              //工具栏
    private JMenuBar jMenuBar;              //菜单栏


    JMenu jMenuFile;                        //文件
    JMenuItem save;                         //保存
    JMenuItem open;                         //打开
    JMenu jMenuPanel;                       //画板
    JMenuItem newDrawBoard;                 //清空画板
    JMenuItem drawPanelBackGroundColor;     //选择画板颜色
    JMenu jMenuEdit;                        //编辑
    JMenuItem Recall;                       //撤回

    JButton clean;                          //清屏
    JButton eraser;                         //橡皮擦
    JButton pen;                            //画笔
    JButton line;                           //直线
    JButton rect;                           //矩形
    JButton round;                          //圆形
    JButton tri;                            //三角形
    JButton choose;                         //选择图形
    JButton lineColor;                      //绘制时的线条颜色
    JButton fillColor;                      //绘制时的填充颜色
    JButton shapeLineColor;                 //改变图形线条颜色
    JButton shapeFillColor;                 //改变图形填充颜色
    JButton deleteShape;                    //删除图形
    JButton enlarge;                        //放大图形
    JButton shrink;                         //缩小图形


    //初始化并布局菜单栏
    private void layoutJMenuBar() {
        Font f=new Font("微软雅黑",Font.BOLD,13);
        jMenuBar = new JMenuBar();
        jMenuFile=new JMenu("文件");
        jMenuFile.setFont(f);
        save=new JMenuItem("\uD83D\uDCBE 保存文件");     //💾
        save.setFont(new Font("System",Font.BOLD,13));
        open=new JMenuItem("\uD83D\uDDBF 打开...");     //🗁🖿
        open.setFont(new Font("System",Font.BOLD,13));

        jMenuPanel = new JMenu("画板");
        jMenuPanel.setFont(f);
        newDrawBoard = new JMenuItem("\uD83D\uDDCB  清空画板");
        newDrawBoard.setFont(new Font("System",Font.BOLD,13));
        drawPanelBackGroundColor = new JMenuItem("\uD83C\uDFA8 画板颜色");
        drawPanelBackGroundColor.setFont(new Font("System",Font.BOLD,13));
        jMenuEdit = new JMenu("编辑");
        jMenuEdit.setFont(f);
        Recall = new JMenuItem("⮌ 撤回");
        Recall.setFont(new Font("System",Font.BOLD,13));
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    drawPanel.saveShape();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        jMenuFile.add(save);
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    drawPanel.openFile();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            }
        });
        jMenuFile.add(open);
        //给"清空画板"按钮添加点击事件
        newDrawBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    drawPanel.cleanPanel();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        jMenuPanel.add(newDrawBoard);
        //给"选择画板颜色"按钮添加点击事件
        drawPanelBackGroundColor.addActionListener(this::drawPanelBackGroundColorActionPerformed);
        jMenuPanel.add(drawPanelBackGroundColor);
        //给"撤回"按钮添加点击事件
        Recall.addActionListener(this::recallActionPerformed);
        jMenuEdit.add(Recall);
        //将菜单添加到菜单栏中
        jMenuBar.add(jMenuFile);
        jMenuBar.add(jMenuPanel);
        jMenuBar.add(jMenuEdit);
    }

    //撤回操作
    private void recallActionPerformed(ActionEvent event) {
        System.out.println("撤回");
        drawPanel.recallOperate();
    }
    //选择画板颜色
    private void drawPanelBackGroundColorActionPerformed(ActionEvent event) {
        System.out.println("选择画板颜色");
        Color selectColor = JColorChooser.showDialog(null, "请选择颜色", null);        //默认颜色设置成选择的颜色
        //讲选择的颜色返回到按钮"选择颜色"的背景上
        //menuItem_BackGroundColor.setBackground(selectColor);
        drawPanel.setBackgroundColor(selectColor);
    }
    //初始化并布局工具栏
    private void layoutJToolBar(){
        jToolBar=new JToolBar("工具栏",JToolBar.HORIZONTAL);
        jToolBar.setBackground(new Color(181,237,255)); //227,227,227
        jToolBar.setLayout(new FlowLayout());

        Font f=new Font("System",Font.BOLD,20);
        clean = new JButton(" \uD83D\uDDCB ");   //🗑❌✖🗋
        clean.setFont(new Font("System",Font.BOLD,20));
        clean.setFocusPainted(false);
        clean.setToolTipText("清空画板");
        clean.setBackground(Color.WHITE);
        eraser = new JButton("橡皮");
        eraser.setFont(new Font("微软雅黑",Font.PLAIN,20));
        eraser.setFocusPainted(false);
        eraser.setToolTipText("橡皮擦");
        eraser.setBackground(Color.WHITE);
        pen = new JButton(" \uD83D\uDD8C ");    //🖋🖊
        pen.setFont(f);
        pen.setFocusPainted(false);
        pen.setToolTipText("画笔");
        pen.setBackground(Color.WHITE);
        line = new JButton(" \uD83D\uDCCF ");     //⎯
        line.setFont(f);
        line.setFocusPainted(false);
        line.setToolTipText("直尺");
        line.setBackground(Color.WHITE);
        rect = new JButton(" □ ");
        rect.setFont(f);
        rect.setFocusPainted(false);
        rect.setToolTipText("矩形");
        rect.setBackground(Color.WHITE);
        round = new JButton(" ○ ");
        round.setFont(f);
        round.setFocusPainted(false);
        round.setToolTipText("圆与椭圆");
        round.setBackground(Color.WHITE);
        tri = new JButton(" △ ");          //△
        tri.setFont(f);
        tri.setFocusPainted(false);
        tri.setToolTipText("三角形");
        tri.setBackground(Color.WHITE);
        choose = new JButton("选择");
        choose.setFont(new Font("微软雅黑",Font.PLAIN,20));
        choose.setFocusPainted(false);
        choose.setToolTipText("选择图形");
        choose.setBackground(Color.WHITE);
        lineColor = new JButton("\uD83D\uDD8C \uD83C\uDFA8");    //🎨
        lineColor.setFont(f);
        lineColor.setFocusPainted(false);
        lineColor.setToolTipText("绘制时画笔颜色");
        lineColor.setBackground(Color.WHITE);
        fillColor = new JButton("▨ \uD83C\uDFA8");    //填充颜色🪣
        fillColor.setFont(new Font("System",Font.BOLD,20));
        fillColor.setFocusPainted(false);
        fillColor.setToolTipText("绘制时填充颜色");
        fillColor.setBackground(Color.WHITE);
        shapeLineColor = new JButton("线条颜色");    //🎨
        shapeLineColor.setFont(new Font("微软雅黑",Font.PLAIN,20));
        shapeLineColor.setFocusPainted(false);
        shapeLineColor.setToolTipText("改变选择图形线条颜色");
        shapeLineColor.setBackground(Color.WHITE);
        shapeFillColor = new JButton("填充颜色");    //
        shapeFillColor.setFont(new Font("微软雅黑",Font.PLAIN,20));
        shapeFillColor.setFocusPainted(false);
        shapeFillColor.setToolTipText("改变选择图形填充颜色");
        shapeFillColor.setBackground(Color.WHITE);
        deleteShape = new JButton(" ✖ ");   //删除图形
        deleteShape.setFont(new Font("System",Font.BOLD,20));
        deleteShape.setFocusPainted(false);
        deleteShape.setToolTipText("删除选择的图形");
        deleteShape.setBackground(Color.WHITE);
        enlarge = new JButton(" 🔍 ");
        enlarge.setFont(f);
        enlarge.setFocusPainted(false);
        enlarge.setToolTipText("放大选择的图形");
        enlarge.setBackground(Color.WHITE);
        shrink = new JButton(" \uD83D\uDCF7 "); //🔬
        shrink.setFont(f);
        shrink.setFocusPainted(false);
        shrink.setToolTipText("缩小选择的图形");
        shrink.setBackground(Color.WHITE);

        //给所有的按钮添加点击鼠标点击事件
        clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    drawPanel.cleanPanel();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        eraser.addActionListener(this::eraserActionListener);
        pen.addActionListener(this::penActionListener);
        line.addActionListener(this::lineActionListener);
        rect.addActionListener(this::rectActionListener);
        round.addActionListener(this::roundActionListener);
        tri.addActionListener(this::triActionListener);
        choose.addActionListener(this::chooseActionListener);
        lineColor.addActionListener(this::lineColorActionListener);
        fillColor.addActionListener(this::fillColorActionListener);
        shapeLineColor.addActionListener(this::shapeLineColorActionListener);
        shapeFillColor.addActionListener(this::shapeFillColorActionListener);
        deleteShape.addActionListener(this::deleteShapeActionListener);
        enlarge.addActionListener(this::enlargeActionListener);
        shrink.addActionListener(this::shrinkActionListener);
        //将所有按钮添加到工具栏中
        jToolBar.setLayout(new FlowLayout());
        jToolBar.add(clean);
        jToolBar.add(eraser);
        jToolBar.add(lineColor);
        jToolBar.add(fillColor);
        jToolBar.add(pen);
        jToolBar.add(line);
        jToolBar.add(rect);
        jToolBar.add(round);
        jToolBar.add(tri);
        jToolBar.add(choose);
        jToolBar.add(shapeLineColor);
        jToolBar.add(shapeFillColor);
        jToolBar.add(deleteShape);
        jToolBar.add(enlarge);
        jToolBar.add(shrink);
        //添加分隔
        jToolBar.addSeparator();
        //添加笔画粗细滑动选择条
        final JSlider strokeJSlide = new JSlider(SwingConstants.HORIZONTAL, 0, 15, 5);
        strokeJSlide.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                "笔画粗细", TitledBorder.CENTER, TitledBorder.BELOW_BOTTOM,
                new Font("微软雅黑",Font.BOLD,12), Color.BLACK)); // 标题；
        strokeJSlide.setMajorTickSpacing(5);          //主刻度间隔
        strokeJSlide.setMinorTickSpacing(1);          //次刻度间隔
        strokeJSlide.setPaintTicks(true);             //绘制刻度
        strokeJSlide.setPaintLabels(true);            //绘制标签
        strokeJSlide.setBackground(Color.WHITE);
        // 设置刻度对应的标签；
        Hashtable<Integer, JComponent> hashtable = new Hashtable<>();
        hashtable.put(0,  new JLabel("0px"));     //0刻度处显示"0px"
        hashtable.put(5,  new JLabel("5px"));     //5刻度处显示"5px"
        hashtable.put(10, new JLabel("10px"));    //10刻度处显示"10px"
        hashtable.put(15, new JLabel("15px"));    //20刻度处显示"15px"
        strokeJSlide.setLabelTable(hashtable);
        strokeJSlide.addChangeListener(e -> drawPanel.lineWidth=strokeJSlide.getValue()); // 添加响应事件；
        jToolBar.add(strokeJSlide);
    }
    //改变图形填充颜色
    private void shapeFillColorActionListener(ActionEvent event) {
        Color selectColor;
        selectColor = JColorChooser.showDialog(null, "请选择颜色", null);        //默认颜色设置成选择的颜色
        //讲选择的颜色返回到按钮"选择颜色"的背景上
        shapeFillColor.setBackground(selectColor);
        if(drawPanel.selectIndex!=-1){
            drawPanel.realList[drawPanel.selectIndex].fillColor=selectColor;
            try {
                drawPanel.sendShape(drawPanel.selectIndex,drawPanel.realList[drawPanel.selectIndex]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawPanel.repaint();
        }
    }
    //改变图形线条颜色
    private void shapeLineColorActionListener(ActionEvent event) {
        Color selectColor;
        selectColor = JColorChooser.showDialog(null, "请选择颜色", null);        //默认颜色设置成选择的颜色
        System.out.println(selectColor);
        //讲选择的颜色返回到按钮"选择颜色"的背景上
        shapeLineColor.setBackground(selectColor);
        if(drawPanel.selectIndex!=-1){
            drawPanel.realList[drawPanel.selectIndex].lineColor=selectColor;
            try {
                drawPanel.sendShape(drawPanel.selectIndex,drawPanel.realList[drawPanel.selectIndex]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawPanel.repaint();
        }
    }
    //删除图形
    private void deleteShapeActionListener(ActionEvent event) {
        if(drawPanel.selectIndex!=-1){
            drawPanel.realList[drawPanel.selectIndex]=new Non();
            try {
                drawPanel.sendShape(drawPanel.selectIndex,drawPanel.realList[drawPanel.selectIndex]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawPanel.selectIndex=-1;
            drawPanel.rr=null;
            drawPanel.repaint();
        }
    }
    //放大图形
    private void shrinkActionListener(ActionEvent event) {
        drawPanel.type="choose";
        drawPanel.shrinkShape(drawPanel.selectIndex);
    }
    //缩小图形
    private void enlargeActionListener(ActionEvent event) {
        drawPanel.type="choose";
        drawPanel.enlargeShape(drawPanel.selectIndex);
    }
    //选择橡皮擦
    private void eraserActionListener(ActionEvent event) {
        drawPanel.type="eraser";
    }
    //选择画笔
    private void penActionListener(ActionEvent event) {
        drawPanel.type="pen";
    }
    //绘制时填充的颜色
    private void fillColorActionListener(ActionEvent event) {
        Color selectColor;
        selectColor = JColorChooser.showDialog(null, "请选择颜色", null);        //默认颜色设置成选择的颜色
        //讲选择的颜色返回到按钮"选择颜色"的背景上
        fillColor.setBackground(selectColor);
        drawPanel.fillColor=selectColor;
    }
    //绘制时线条的颜色
    private void lineColorActionListener(ActionEvent event) {
        Color selectColor;
        selectColor = JColorChooser.showDialog(null, "请选择颜色", null);        //默认颜色设置成选择的颜色
        System.out.println(selectColor);
        //讲选择的颜色返回到按钮"选择颜色"的背景上
        lineColor.setBackground(selectColor);
        drawPanel.lineColor=selectColor;
    }
    //选择图形
    private void chooseActionListener(ActionEvent event) {
        drawPanel.type= "choose";
    }
    //选择三角形
    private void triActionListener(ActionEvent event) {
        drawPanel.type= "tri";
    }
    //选择圆形
    private void roundActionListener(ActionEvent event) {
        drawPanel.type= "round";
    }
    //选择矩形
    private void rectActionListener(ActionEvent event) {
        drawPanel.type= "rect";
    }
    //选择直线
    private void lineActionListener(ActionEvent event) {
        drawPanel.type= "line";
    }
    //初始化并布局主界面
    public PaintFrame3() throws IOException, ClassNotFoundException {
        Toolkit tool = getToolkit();
        tool.getScreenSize();
        //setBounds(200, 200, dim.width - 400, dim.height - 300); // 设置窗体大小
        setSize(1150, 750);                           // 设定大小为 1150 x 750
        setLocationRelativeTo(null);                              // 设定位于屏幕中间
        setTitle("画板");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        repaint();
        layoutJToolBar();
        layoutJMenuBar();
        setJMenuBar(jMenuBar);
        //add(Func,BorderLayout.NORTH);
        drawPanel=new DrawPanel();
        //drawPanel.setBackground(Color.WHITE);
        add(drawPanel,BorderLayout.CENTER);
        add(jToolBar,BorderLayout.SOUTH);
        setVisible(true);
        try {
            drawPanel.connectToServer();
        } finally {
            System.out.println("服务器未连接");
        }
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        PaintFrame3 frame=new PaintFrame3();
        frame.setVisible(true);
    }
}
