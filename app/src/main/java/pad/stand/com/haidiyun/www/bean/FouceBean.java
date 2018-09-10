package pad.stand.com.haidiyun.www.bean;

import java.util.ArrayList;

public class FouceBean {
	private int []points;
	private String id;
	private String name;
	private String code;
	private double price;
	private int num;
	private String unit;
	private boolean isRes;
	private ArrayList<ReasonBean>  reasons;
	
	public boolean isRes() {
		return isRes;
	}
	public void setRes(boolean isRes) {
		this.isRes = isRes;
	}
	public ArrayList<ReasonBean> getReasons() {
		return reasons;
	}
	public void setReasons(ArrayList<ReasonBean> reasons) {
		this.reasons = reasons;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int[] getPoints() {
		return points;
	}
	public void setPoints(int[] points) {
		this.points = points;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
