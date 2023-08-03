package PaintFrame;

import java.awt.*;
import java.io.Serializable;

public class Tri extends Shape implements Serializable {
    public Tri(int xNew, int yNew, int x1New, int y1New, Color lineColor,Color fillColor,int lineWidth){
        x=xNew;
        y=yNew;
        x1=x1New;
        y1=y1New;
        this.lineColor=lineColor;
        this.fillColor=fillColor;
        this.lineWidth=lineWidth;
        this.shapeName="Tri";
    }
    public void draw(Graphics2D g){
        //System.out.println("draw a Tri");
        g.setColor(lineColor);
        g.setStroke(new BasicStroke(lineWidth));
//        g.drawLine(x,y1,x1,y1);
//        g.drawLine((x+x1)/2,y,x1,y1);
//        g.drawLine((x+x1)/2,y,x,y1);
        int px1[]={x,x1,(x+x1)/2};//首末点相重,才能画多边形
        int py1[]={y1,y1,y};
        g.drawPolygon(px1,py1,3);
        if(fillColor!=null){
            g.setColor(fillColor);
            g.fillPolygon(px1,py1,3);
        }
    }
}
