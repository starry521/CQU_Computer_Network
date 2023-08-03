package Server;

import PaintFrame.Non;
import PaintFrame.Shape;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

//服务线程类
public class ServerThread extends Thread{
    private Socket paint;                        //客户端端口
    private DataInputStream fromPaint;           //数据输入流
    private DataOutputStream toPaint;            //数据输出流
    private ObjectInputStream objectFromPaint;   //类对象输入流
    private ObjectOutputStream objectToPaint;    //类对象输出流

    MaxIndex maxIndex=null;
    ShapeList realList=null;
    Vector<ServerThread> threadVector =null;

    public ServerThread(Socket socket, MaxIndex imaxIndex, ShapeList irealList, Vector<ServerThread> itvector) throws IOException, ClassNotFoundException {
        super();
        paint=socket;
        System.out.println("paint");
        maxIndex=imaxIndex;
        realList=irealList;
        //重置加进服务端的画板的最大下标
        if(maxIndex.Index>=0){
            send(maxIndex.Index+1,new Non());
        }

        threadVector =itvector;
        //将本线程加入到threadVector中
        threadVector.add(this);

        //是否同步其它客户端的已绘图形到当前画板
        int synchronization= JOptionPane
                .showConfirmDialog(null, "是否同步其它客户端的已绘图形到当前画板", "提示", JOptionPane.YES_NO_OPTION);
        if(synchronization==JOptionPane.YES_OPTION){
            System.out.println("同步到当前画板");     //点击“是”后执行这个代码块
            sendAll();
        }else{
            System.out.println("不同步到当前画板");    //点击“否”后执行这个代码块
        }
    }
    @Override
    public void run() {
        super.run();
        try{
            while(true){
                //接收下标
                System.out.println("receive");
                fromPaint = new DataInputStream(paint.getInputStream());
                int temp = fromPaint.readInt();
                System.out.println("temp: "+temp);
                if(temp>maxIndex.Index) {
                    maxIndex.Index = temp;    //更新最大下标
                }
                else if(temp==-10){           //接收到同步清空画板信号
                    maxIndex.Index=0;
                    cleanList();
                }
                //接收下标所对应的图形
                objectFromPaint=new ObjectInputStream(paint.getInputStream());
                PaintFrame.Shape tempShape= (PaintFrame.Shape) objectFromPaint.readObject();
                System.out.println("tempShape: "+tempShape.shapeName);
                //System.out.println("size:"+ threadVector.size());
                //将所接收到的下标以及下标所对应的图形发送到所有加入到该服务端的客户端
                for(int i = 0; i< threadVector.size(); i++){
                    if(threadVector.get(i)!=this){
                        threadVector.get(i).send(temp,tempShape);
                    }
                }
                //更新服务端存储的图形
                if(temp>=0){
                    realList.realList[temp]=tempShape;
                }
                System.out.println("next");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //发送下标和图形
    public void send(int index, Shape shape) throws IOException {
        System.out.println("send");
        toPaint = new DataOutputStream(paint.getOutputStream());
        System.out.println("toPaint");
        toPaint.writeInt(index);
        objectToPaint=new ObjectOutputStream(paint.getOutputStream());
        System.out.println("objectToPaint");
        objectToPaint.writeObject(shape);
        System.out.println("writeObject");
    }
    //发送所有服务端存储的图形
    public void sendAll() throws IOException {
        for(int i=0;i<realList.realList.length;i++){
            if(realList.realList[i]!=null){
                System.out.println("send");
                toPaint = new DataOutputStream(paint.getOutputStream());
                System.out.println("toPaint");
                toPaint.writeInt(i);
                objectToPaint=new ObjectOutputStream(paint.getOutputStream());
                System.out.println("objectToPaint");
                objectToPaint.writeObject(realList.realList[i]);
                System.out.println("writeObject");
            }
        }
    }
    //清空服务端存储的图形
    public void cleanList() throws IOException {
        for(int i=0;i<realList.realList.length;i++){
            realList.realList[i]=null;
        }
    }
}
