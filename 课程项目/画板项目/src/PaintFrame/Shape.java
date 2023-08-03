package PaintFrame;

import java.awt.*;
import java.io.Serializable;

//Shape抽象类
public abstract class Shape implements Serializable {
    public String shapeName;
    public int x,y,x1,y1;
    public Color lineColor;
    public Color fillColor;
    public int lineWidth;
    public abstract void draw(Graphics2D g);    //抽象draw函数
    public void moveShape(int xChange,int yChange,int x1Change,int y1Change){
        x=xChange;
        y=yChange;
        x1=x1Change;
        y1=y1Change;
    }
}
