package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;

public class PicPoint implements Serializable{
	//需要展示动画的宽度和高度
	private int width;
	private int height;
	//关于坐标点
	private int[] point;
	private String pic;
	 
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int[] getPoint() {
		return point;
	}
	public void setPoint(int[] point) {
		this.point = point;
	}
	
}
