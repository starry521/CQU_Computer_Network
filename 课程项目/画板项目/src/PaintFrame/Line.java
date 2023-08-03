package PaintFrame;

import java.awt.*;
import java.io.Serializable;

public class Line extends Shape implements Serializable {
    public Line(int xNew, int yNew, int x1New, int y1New,Color lineColor,Color fillColor,int lineWidth){
        x=xNew;
        y=yNew;
        x1=x1New;
        y1=y1New;
        this.lineColor=lineColor;
        this.fillColor=fillColor;
        this.lineWidth=lineWidth;
        this.shapeName="Line";
    }
    public void draw(Graphics2D g){
        //System.out.println("draw a Line");
        g.setColor(lineColor);
        g.setStroke(new BasicStroke(lineWidth));
        g.drawLine(x,y,x1,y1);
    }
}
