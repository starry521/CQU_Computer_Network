package PaintFrame;

import java.awt.*;
import java.io.Serializable;

public class RoundRect extends Shape implements Serializable {
    public RoundRect(int xNew, int yNew, int x1New, int y1New, Color lineColor, Color fillColor){
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
        x-=10;
        y-=10;
        x1+=10;
        y1+=10;
        this.lineColor=Color.GRAY;
        this.fillColor=null;
        this.lineWidth=3;
    }
    public void draw(Graphics2D g){
        //System.out.println("draw a RoundRect");
        g.setColor(lineColor);
        g.setStroke(new BasicStroke(lineWidth));
        g.drawRoundRect(Math.min(x1, x), Math.min(y1, y), Math.abs(x - x1), Math.abs(y - y1),
                20, 20);
    }
}
