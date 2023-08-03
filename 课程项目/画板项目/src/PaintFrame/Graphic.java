package PaintFrame;

import java.util.ArrayList;

public class Graphic {
    private static final ArrayList<Shape> shapes = new ArrayList<>(); // 图形存储容器；
    public Graphic() {}
    public ArrayList<Shape> getShapes() { return shapes; }
    /**
     * 存储某个选中的图形
     * @param shapeObj 选中的图形
     */
    public void add(Shape shapeObj) { shapes.add(shapeObj); }
    /**
     * 绘制所有已选图形
     */
    public void draw(java.awt.Graphics2D g) {
        for (Shape shape:shapes)
            shape.draw(g);
    }
    /**
     * 清空
     */
    public void clear() { shapes.clear(); }
}
