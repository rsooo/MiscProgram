package hakomusu.app;

import android.graphics.Point;

/**
 * ボード上に乗っかっているチップ
 * @author akira
 *
 */
public class Chip {
	final public int width;
	final public int height;
	final public int id;
	Point location;
	
	public Chip(final int w, final int h, final int id, Point pt){
		width = w; height = h; this.id = id; location = pt;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	
}
