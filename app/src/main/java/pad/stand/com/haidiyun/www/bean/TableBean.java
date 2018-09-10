package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;

public class TableBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String code;
	private String typeName;
	private String typeCode;
	private int max;
	private boolean isLocked;
	private String state;
	private String priceType;
	private boolean isTemp;

	public boolean isTemp() {
		return isTemp;
	}

	public void setTemp(boolean temp) {
		isTemp = temp;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
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
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public boolean isLocked() {
		return isLocked;
	}
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	@Override
	public String toString() {
		return "TableBean{" +
				"name='" + name + '\'' +
				", code='" + code + '\'' +
				", typeName='" + typeName + '\'' +
				", typeCode='" + typeCode + '\'' +
				", max=" + max +
				", isLocked=" + isLocked +
				", state='" + state + '\'' +
				", priceType='" + priceType + '\'' +
				'}';
	}
}
