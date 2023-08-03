package PaintFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class DrawPanel extends JPanel implements Runnable{
    Socket socket;
    String type;
    Color BackgroundColor=Color.WHITE;
    Color lineColor=Color.darkGray;
    Color fillColor=null;
    Graphic graphic=new Graphic();
    int lineWidth=3;
    int x,y,x1,y1;
    //选择图形时鼠标点击的坐标
    RoundRect rr;
    int xChoose,yChoose;
    /*
    分别为鼠标拖动图形时按下去的初始x，y坐标
    图形的初始两个坐标，四个值
    选择的图形的在实时绘制列表realList中的下标
     */
    int xPre,yPre,xShapePre,yShapePre,x1ShapePre,y1ShapePre,selectIndex=-1;
    //画笔或橡皮擦的前一个时刻的坐标
    int xPenPre,yPenPre;
    // 绘制图形集；（储存实时绘制的图形，以便显示）
    public Shape[] realList = new Shape[50000];
    int index=1;
    //使用Vector时对某一位置元素的更改不方便
    //private Vector<Shape> vector=new Vector<>();

    // Input and output streams from/to server
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private ObjectInputStream objectFromServer;
    private ObjectOutputStream objectToServer;
    // Host name or ip
    private String host = "localhost";

    public DrawPanel() throws IOException, ClassNotFoundException {
        setBackground(BackgroundColor);
        addMouseListener(new addShape());
        addMouseMotionListener(new realDraw());
        type="pen";
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                System.out.println("DrawPanel Repaint");
                repaint();
            }
        });
    }
    @Override
    public void run() {}
    //向vector和realList中添加图形
    class addShape extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {

            xChoose=e.getX();
            yChoose=e.getY();
            System.out.println("选择的坐标");
            if(type.equals("choose")){
                selectIndex=chooseShape(xChoose,yChoose);
                if(selectIndex!=-1){
                    xShapePre=realList[selectIndex].x;
                    yShapePre=realList[selectIndex].y;
                    x1ShapePre=realList[selectIndex].x1;
                    y1ShapePre=realList[selectIndex].y1;
                }
            }
            repaint();
        }
        @Override
        public void mousePressed(MouseEvent e) {
            //按下
            //获得图形的原点
            x = e.getX();
            y = e.getY();
            if(type.equals("choose")){
                xPre=x;
                yPre=y;
            }
            else if(type.equals("pen")||type.equals("eraser")){
                xPenPre=x;
                yPenPre=y;
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            //释放
            //获得图形的终点
            x1 = e.getX();
            y1 = e.getY();
            //获得绘图区的作图器
            Graphics2D g = (Graphics2D) getGraphics();
            if(type==null){
                System.out.println("未选择工具");
            }
            //移动图形结束后更新选择图形的初始坐标
            else if(type.equals("choose")){
                xShapePre=xShapePre+x1-xPre;
                yShapePre=yShapePre+y1-yPre;
                x1ShapePre=x1ShapePre+x1-xPre;
                y1ShapePre=y1ShapePre+y1-yPre;
            }
            else if (type.equals("line")) {
                //绘制直线
                Line line=new Line(x, y, x1, y1,lineColor,fillColor,lineWidth);
                line.draw(g);
                realList[index]=line;
                try {
                    sendShape(index,line);
                    //System.out.println("send finished");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                graphic.add(line);
                index++;
            }
            else if (type.equals("rect")) {
                //绘制矩形
                Rect rect=new Rect(x, y, x1, y1,lineColor,fillColor,lineWidth);
                rect.draw(g);
                realList[index]=rect;
                try {
                    sendShape(index,rect);
                    //System.out.println("send finished");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                graphic.add(rect);
                index++;
            }
            else if (type.equals("round")) {
                //绘制圆形
                Cube cube=new Cube(x, y, x1, y1,lineColor,fillColor,lineWidth);
                cube.draw(g);
                realList[index]=cube;
                try {
                    sendShape(index,cube);
                    //System.out.println("send finished");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                graphic.add(cube);
                index++;
            }
            else if (type.equals("tri")) {
                //绘制三角形
                Tri tri=new Tri(x, y, x1, y1,lineColor,fillColor,lineWidth);
                tri.draw(g);
                realList[index]=tri;
                try {
                    sendShape(index,tri);
                    //System.out.println("send finished");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                graphic.add(tri);
                index++;
            }
        }
    }
    //实时绘制
    class realDraw extends MouseMotionAdapter{
        @Override
        public void mouseDragged(MouseEvent e){
            super.mouseDragged(e);
            x1 = e.getX();
            y1 = e.getY();
            //获得绘图区的作图器
            Graphics2D g = (Graphics2D) getGraphics();
            if(type==null){
                System.out.println("未选择工具");
            }
            else if(type.equals("choose")&&selectIndex>=0){
                System.out.println("choose repaint");
                realList[selectIndex].moveShape(xShapePre+x1-xPre,yShapePre+y1-yPre
                ,x1ShapePre+x1-xPre,y1ShapePre+y1-yPre);
                rr.moveShape(xShapePre+x1-xPre-10,yShapePre+y1-yPre-10
                        ,x1ShapePre+x1-xPre+10,y1ShapePre+y1-yPre+10);
                try {
                    sendShape(selectIndex,realList[selectIndex]);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else if (type.equals("pen")) {
                //绘制直线
                Pen pen=new Pen(xPenPre, yPenPre, x1, y1,lineColor,fillColor,lineWidth);
                pen.draw(g);
                realList[index]=pen;
                try {
                    sendShape(index,pen);
                    System.out.println("send finished");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                index++;
                xPenPre=x1;
                yPenPre=y1;
            }
            else if (type.equals("eraser")) {
                //绘制直线
                Eraser eraser=new Eraser(xPenPre, yPenPre, x1, y1,Color.WHITE,Color.WHITE,lineWidth);
                eraser.draw(g);
                realList[index]=eraser;
                try {
                    sendShape(index,eraser);
                    System.out.println("send finished");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                index++;
                xPenPre=x1;
                yPenPre=y1;
            }
            else if (type.equals("line")) {
                //绘制直线
                Line line=new Line(x, y, x1, y1,lineColor,fillColor,lineWidth);
                line.draw(g);
                realList[index]=line;
                try {
                    sendShape(index,line);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else if (type.equals("rect")) {
                //绘制矩形
                Rect rect=new Rect(x, y, x1, y1,lineColor,fillColor,lineWidth);
                rect.draw(g);
                realList[index]=rect;
                try {
                    sendShape(index,rect);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else if (type.equals("round")) {
                //绘制圆形
                Cube cube=new Cube(x, y, x1, y1,lineColor,fillColor,lineWidth);
                cube.draw(g);
                realList[index]=cube;
                try {
                    sendShape(index,cube);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else if (type.equals("tri")) {
                //绘制三角形
                Tri tri=new Tri(x, y, x1, y1,lineColor,fillColor,lineWidth);
                tri.draw(g);
                realList[index]=tri;
                try {
                    sendShape(index,tri);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            repaint();
        }
    }
    //判断点击的坐标是否在传入的图形内
    public boolean judgeIn(int x1,int x2,int x){
        if((x>=x1&&x<=x2)||(x<=x1&&x>=x2)){
            return true;
        }
        else{
            return false;
        }
    }
    //放大图形
    public void enlargeShape(int selectIndex){
        if(selectIndex<0){
            System.out.println("未选择图形");
        }
        else{
            //使图形能够等比例放大
            realList[selectIndex].x1= (int) (realList[selectIndex].x1+
                    (realList[selectIndex].x1-realList[selectIndex].x)*0.1);
            realList[selectIndex].y1= (int) (realList[selectIndex].y1+
                    (realList[selectIndex].y1-realList[selectIndex].y)*0.1);
            //更新选择框的大小
            rr.x=(xShapePre=realList[selectIndex].x)-10;
            rr.y=(yShapePre=realList[selectIndex].y)-10;
            rr.x1=(x1ShapePre=realList[selectIndex].x1)+10;
            rr.y1=(y1ShapePre=realList[selectIndex].y1)+10;
            repaint();
        }
    }
    //缩小图形
    public void shrinkShape(int selectIndex){
        if(selectIndex<0){
            System.out.println("未选择图形");
        }
        else{
            //使图形能够等比例缩小
            realList[selectIndex].x1= (int) (realList[selectIndex].x1-
                    (realList[selectIndex].x1-realList[selectIndex].x)*0.1);
            realList[selectIndex].y1= (int) (realList[selectIndex].y1-
                    (realList[selectIndex].y1-realList[selectIndex].y)*0.1);
            //更新选择框的大小
            rr.x=(xShapePre=realList[selectIndex].x)-10;
            rr.y=(yShapePre=realList[selectIndex].y)-10;
            rr.x1=(x1ShapePre=realList[selectIndex].x1)+10;
            rr.y1=(y1ShapePre=realList[selectIndex].y1)+10;
            repaint();
        }
    }
    //选择图形
    private int chooseShape(int x,int y) {
        Graphics2D g = (Graphics2D) getGraphics();
        for(int i=0;i<realList.length;i++){
            Shape s= realList[i];
            if(s!=null&&judgeIn(s.x,s.x1,x)&&judgeIn(s.y,s.y1,y)){
                System.out.println("choose:"+i);
                rr=new RoundRect(s.x,s.y,s.x1,s.y1,null,null);
                rr.draw(g);
                return i;
            }
        }
        rr=null;
        return -1;
    }
    //设置背景颜色
    public void setBackgroundColor(Color color){
        BackgroundColor=color;
        setBackground(BackgroundColor);
        repaint();
    }
    //撤回操作
    public void recallOperate(){
//        //本地画板撤回操作
//        int flag=0;
//        index--;
//        while(index>=0&& Objects.equals(realList[index].shapeName, "Pen")){
//            realList[index]=null;
//            flag=1;
//            index--;
//        }
//        while(index>=0&& Objects.equals(realList[index].shapeName, "Eraser")){
//            realList[index]=null;
//            flag=1;
//            index--;
//        }
//        if(index>=0&&flag==0){
//            realList[index]=null;
//        }
//        if(index<0){
//            index=0;
//        }
//        repaint();
        //网络画板撤回操作
        int i=index-1;
        int flag=0;
        while((Objects.equals(realList[i].shapeName, "Non")||
                Objects.equals(realList[i].shapeName, "Eraser")||
                Objects.equals(realList[i].shapeName, "Pen"))&&i>=0){
            if(Objects.equals(realList[i].shapeName, "Eraser")||
                    Objects.equals(realList[i].shapeName, "Pen")){
                flag=1;
            }
            realList[i]=new Non();
            try {
                sendShape(i,new Non());
            } catch (IOException e) {
                e.printStackTrace();
            }
            i--;
        }
        //i=index-1;
        if(flag==0){
            realList[i]=new Non();
            try {
                sendShape(i,new Non());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        repaint();
    }
    //同步清空网络画板
    public void cleanPanel() throws IOException {
        int cleanPanelSyn=JOptionPane
                .showConfirmDialog(null, "清空操作是否同步到其它画板", "提示", JOptionPane.YES_NO_OPTION);
        if(cleanPanelSyn==JOptionPane.YES_OPTION){
            System.out.println("清空操作同步到其它画板");    //点击“是”后执行这个代码块
            for(int i=0;i<realList.length;i++){
                realList[i]=null;
            }
            graphic.getShapes().clear();
            index=0;
            repaint();
            sendShape(-10,new Non());
        }else{
            System.out.println("清空操作不同步到其它画板");    //点击“否”后执行这个代码块
            for(int i=0;i<realList.length;i++){
                realList[i]=null;
            }
            graphic.getShapes().clear();
            repaint();
        }
    }
    //清空当前画板
    public void cleanThisPanel() throws IOException {
        for(int i=0;i<realList.length;i++){
            realList[i]=null;
        }
        graphic.getShapes().clear();
        index=0;
        repaint();
    }
    //paint中实现重绘功能
    public void paint(Graphics gr){
        super.paint(gr);
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//        // TODO 自动生成的 catch 块
//            e.printStackTrace();
//        }
        Graphics2D g = (Graphics2D) gr;
        for (Shape s : realList) {
            if (s != null) {
                s.draw(g);
                //System.out.println("draw");
            }
        }
        if(rr!=null){
            rr.draw(g);
        }
    }
    //连接到服务器
    public void connectToServer() throws IOException, ClassNotFoundException {
        // Create a socket to connect to the server
        socket= new Socket(host, 5600);
        System.out.println("connect");
        // Create an input stream to receive data from the server
        fromServer = new DataInputStream(socket.getInputStream());
        System.out.println("1");
        System.out.println("run");
        //sendShape(3,new Line(1,1,1,1,null,null,3));
        receiveShape();
    }
    //发送图形
    public void sendShape(int index,Shape shape) throws IOException {
        if(socket==null){
            return;
        }
        toServer = new DataOutputStream(socket.getOutputStream());
        System.out.println("toServer");
        toServer.writeInt(index);
        System.out.println("index:"+index);
        objectToServer=new ObjectOutputStream(socket.getOutputStream());
        System.out.println("objectToServer");
        objectToServer.writeObject(shape);
        System.out.println("writeObject");
    }
    //接收图形
    public void receiveShape() throws IOException, ClassNotFoundException {
        while (true){
            fromServer = new DataInputStream(socket.getInputStream());
            System.out.println("fromServer");
            int temp=fromServer.readInt();
            System.out.println(temp);
            if(temp==-10){
                cleanThisPanel();
                objectFromServer=new ObjectInputStream(socket.getInputStream());
                System.out.println("objectFromServer");
                Shape shape= (Shape) objectFromServer.readObject();
                System.out.println("addShape");
                System.out.println(shape.shapeName);
            }
            else if(temp>=index){
                index=temp;
                objectFromServer=new ObjectInputStream(socket.getInputStream());
                System.out.println("objectFromServer");
                Shape shape= (Shape) objectFromServer.readObject();
                System.out.println("addShape");
                realList[temp]=shape;
                System.out.println(shape.shapeName);
                index++;
            }
            else{
                objectFromServer=new ObjectInputStream(socket.getInputStream());
                System.out.println("objectFromServer");
                Shape shape= (Shape) objectFromServer.readObject();
                System.out.println("addShape");
                realList[temp]=shape;
                System.out.println(shape.shapeName);
            }
            repaint();
        }
    }
    //保存已绘制的图形
    public void saveShape() throws IOException {
        //在当前路径下创建文件选择器
        JFileChooser chooser=new JFileChooser(".");
        //该Label用于显示预览
        JLabel accessory=new JLabel();
        //定义文件过滤器
        ExtensionFileFilter filter=new ExtensionFileFilter();
        filter.addExtension("txt");
        filter.addExtension("paint");
        filter.setDescription("文件(*.txt, *.paint)");
        chooser.addChoosableFileFilter(filter);
        //隐藏下拉列表中的“所有文件”选项
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setApproveButtonText("保存");
        chooser.setDialogTitle("保存文件");
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH.mm.ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        System.out.println("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间(24小时制)
        String saveName=sdf.format(date)+".paint";
        chooser.setSelectedFile(new File(saveName));
        //chooser.setApproveButtonToolTipText("保存文件");
        int result=chooser.showOpenDialog(this);
        if(result==JFileChooser.APPROVE_OPTION) {
            System.out.println(chooser.getCurrentDirectory());
            String name=chooser.getSelectedFile().getName();
            System.out.println(name);
            saveName=chooser.getCurrentDirectory().getPath()+"\\"+saveName;
            System.out.println(saveName);
            File saveFile=new File(saveName);
            try {
                saveFile.createNewFile();
                FileOutputStream out=new FileOutputStream(saveFile);
                ObjectOutputStream shapeOut=new ObjectOutputStream(out);
                for(int i=0;i<realList.length;i++){
                    if(realList[i]!=null&&!Objects.equals(realList[i].shapeName,"Non")){
                        shapeOut.writeObject(realList[i]);
                    }
                }
                shapeOut.flush();
                shapeOut.close();
                System.out.println("saved");
            }catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    //打开文件并绘制其中图形
    public void openFile() throws IOException, ClassNotFoundException {
        //在当前路径下创建文件选择器
        JFileChooser chooser=new JFileChooser(".");
        //该Label用于显示预览
        JLabel accessory=new JLabel();
        //定义文件过滤器
        ExtensionFileFilter filter=new ExtensionFileFilter();
        filter.addExtension("txt");
        filter.addExtension("paint");
        filter.setDescription("文件(*.txt, *.paint)");
        chooser.addChoosableFileFilter(filter);
        //隐藏下拉列表中的“所有文件”选项
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle("打开文件");
        int result=chooser.showOpenDialog(this);
        try {
            if(result==JFileChooser.APPROVE_OPTION){
                int res=JOptionPane
                        .showConfirmDialog(null, "是否覆盖已绘图形", "提示", JOptionPane.YES_NO_OPTION);
                if(res==JOptionPane.YES_OPTION){
                    System.out.println("覆盖已绘图形");    //点击“是”后执行这个代码块
                    cleanPanel();
                }else{
                    System.out.println("续接已绘图形");    //点击“否”后执行这个代码块
                }
                String name=chooser.getSelectedFile().getPath();
                System.out.println("name:"+name);
                File file=new File(name);
                FileInputStream in=new FileInputStream(file);
                ObjectInputStream shapeIn=new ObjectInputStream(in);
                Shape temp = null;
                int preIndex=index;
                while(true){
                    try{
                        temp= (Shape) shapeIn.readObject();
                    } catch (IOException e) {
                        System.out.println("read finished");
                        break;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    realList[index]=temp;
                    index++;
                }
                repaint();
                System.out.println("opened");
                int synchronization=JOptionPane
                        .showConfirmDialog(null, "打开文件是否同步到其它画板", "提示", JOptionPane.YES_NO_OPTION);
                if(synchronization==JOptionPane.YES_OPTION){
                    System.out.println("打开文件已同步到其它画板");    //点击“是”后执行这个代码块
                    for(int i=preIndex;i<index;i++){
                        sendShape(i,realList[i]);
                    }
                }else{
                    System.out.println("打开文件未同步到其它画板");    //点击“否”后执行这个代码块
                    sendShape(index-1,new Non());
                }
            }
            else{
                System.out.println("未选择文件");
            }
        } catch (Exception e) {
            System.out.println("未选择文件");
        }
    }
    //创建FileFilter的子类，用于实现文件过滤功能
    class ExtensionFileFilter extends FileFilter {
        private String description;
        private ArrayList<String> extensions=new ArrayList<>();
        //自定义方法，用于添加文件后缀名
        public void addExtension(String extension) {
            if(!extension.startsWith("."))
                extension="."+extension;
            extensions.add(extension.toLowerCase());
        }
        //用于设置该文件过滤器的描述文本
        public void setDescription(String description) {
            this.description=description;
        }
        public String getDescription() {
            return description;
        }
        public boolean accept(File file) {
            if(file.isDirectory()) return true;
            String name=file.getName().toLowerCase();
            for(String extension:extensions) {
                if(name.endsWith(extension)) return true;
            }
            return false;
        }
    }
}