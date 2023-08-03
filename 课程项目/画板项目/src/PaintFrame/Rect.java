package PaintFrame;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Rect extends Shape implements Serializable {
    public Rect(int xNew, int yNew, int x1New, int y1New, Color lineColor,Color fillColor,int lineWidth) {
        //使鼠标无论如何拖动都可绘制恰当的图形
        if(xNew<x1New&&yNew<y1New){
            x=xNew;
            y=yNew;
            x1=x1New;
            y1=y1New;
        }
        else if(xNew>x1New&&yNew>y1New){
            x1=xNew;
            y1=yNew;
            x=x1New;
            y=y1New;
        }
        else if(xNew>x1New&&yNew<y1New){
            x1=xNew;
            y=yNew;
            x=x1New;
            y1=y1New;

        }
        else{
            x=xNew;
            y=y1New;
            x1=x1New;
            y1=yNew;
        }
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        this.lineWidth=lineWidth;
        this.shapeName="Rect";
    }

    public Rect(int x, int y, int x1, int y1, JButton lineColor, JButton fillColor) {
        super();
    }

    public void draw(Graphics2D g){

        g.setColor(lineColor);
        g.setStroke(new BasicStroke(lineWidth));
        g.drawRect(x, y, x1 - x, y1 - y);
        //System.out.println("draw a Rect");
        if(fillColor!=null){
            g.setColor(fillColor);
            g.fillRect(x, y, x1 - x, y1 - y);
        }
    }
}
