package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;

public class FoodsBean implements Serializable{ 
	/**
	 * 
	 */
	private int cyMenuIndex;
	private String cyPcode;
	private static final long serialVersionUID = 1L;
	//id,classcode,code,name,help,unit,price,price_modify,weigh,discount,temp,suit,locked
	private int id;
	private String classCode;
	private String code;
	private String name;
	private String nameEn;
	private String help;
	private String unit;
	private String path;
	//根据情况进行数量的显示控制
	private boolean more;
	//是否进行做法选择
	private boolean require_cook;
	private double price;
	private double price1;
	private double price2;
	//时价
	private boolean priceMofidy;
	//过磅
	private boolean weigh;
	private boolean discount;
	//临时菜
	private boolean temp;
	//套餐
	private boolean suit;
	//已点数量
	private double count;
	private String description;
	private String type;
	private boolean isImage;
	private double orderMinLimit;
	private String chargeMode;

	public int getCyMenuIndex() {
		return cyMenuIndex;
	}

	public void setCyMenuIndex(int cyMenuIndex) {
		this.cyMenuIndex = cyMenuIndex;
	}

	public String getCyPcode() {
		return cyPcode;
	}

	public void setCyPcode(String cyPcode) {
		this.cyPcode = cyPcode;
	}

	public double getPrice1() {
		return price1;
	}

	public void setPrice1(double price1) {
		this.price1 = price1;
	}

	public double getPrice2() {
		return price2;
	}

	public void setPrice2(double price2) {
		this.price2 = price2;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getChargeMode() {
		return chargeMode;
	}

	public void setChargeMode(String chargeMode) {
		this.chargeMode = chargeMode;
	}

	public double getOrderMinLimit() {
		return orderMinLimit;
	}

	public void setOrderMinLimit(double orderMinLimit) {
		this.orderMinLimit = orderMinLimit;
	}

	public boolean isImage() {
		return isImage;
	}
	public void setImage(boolean isImage) {
		this.isImage = isImage;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isRequire_cook() {
		return require_cook;
	}
	public void setRequire_cook(boolean require_cook) {
		this.require_cook = require_cook;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isMore() {
		return more;
	}
	public void setMore(boolean more) {
		this.more = more;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClassCode() {
		return classCode;
	}
	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHelp() {
		return help;
	}
	public void setHelp(String help) {
		this.help = help;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public boolean isPriceMofidy() {
		return priceMofidy;
	}
	public void setPriceMofidy(boolean priceMofidy) {
		this.priceMofidy = priceMofidy;
	}
	public boolean isWeigh() {
		return weigh;
	}
	public void setWeigh(boolean weigh) {
		this.weigh = weigh;
	}
	public boolean isDiscount() {
		return discount;
	}
	public void setDiscount(boolean discount) {
		this.discount = discount;
	}
	public boolean isTemp() {
		return temp;
	}
	public void setTemp(boolean temp) {
		this.temp = temp;
	}
	public boolean isSuit() {
		return suit;
	}
	public void setSuit(boolean suit) {
		this.suit = suit;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "FoodsBean{" +
				"id=" + id +
				", classCode='" + classCode + '\'' +
				", code='" + code + '\'' +
				", name='" + name + '\'' +
				", nameEn='" + nameEn + '\'' +
				", help='" + help + '\'' +
				", unit='" + unit + '\'' +
				", path='" + path + '\'' +
				", more=" + more +
				", require_cook=" + require_cook +
				", price=" + price +
				", price1=" + price1 +
				", price2=" + price2 +
				", priceMofidy=" + priceMofidy +
				", weigh=" + weigh +
				", discount=" + discount +
				", temp=" + temp +
				", suit=" + suit +
				", count=" + count +
				", description='" + description + '\'' +
				", type='" + type + '\'' +
				", isImage=" + isImage +
				", orderMinLimit=" + orderMinLimit +
				", chargeMode='" + chargeMode + '\'' +
				'}';
	}
}
