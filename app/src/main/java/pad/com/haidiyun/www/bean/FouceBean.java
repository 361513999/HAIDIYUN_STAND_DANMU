package pad.com.haidiyun.www.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


public class FouceBean implements Serializable {
	private int []points;
	private String id;
	private String classcode;
	private String code;
	private String name;
	private String name_en;
	private String help;
	private String unit;
	private double price;
	private boolean price_modify;
	private boolean weigh;
	private boolean discount;
	private boolean temp;
	private boolean suit;
	private boolean require_cook;
	private boolean locked;
	private boolean del;
	private String image;
	private int page;
	private String type;
	public int[] getPoints() {
		return points;
	}
	public void setPoints(int[] points) {
		this.points = points;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClasscode() {
		return classcode;
	}
	public void setClasscode(String classcode) {
		this.classcode = classcode;
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
	public String getName_en() {
		return name_en;
	}
	public void setName_en(String name_en) {
		this.name_en = name_en;
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
	public boolean isPrice_modify() {
		return price_modify;
	}
	public void setPrice_modify(boolean price_modify) {
		this.price_modify = price_modify;
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
	public boolean isRequire_cook() {
		return require_cook;
	}
	public void setRequire_cook(boolean require_cook) {
		this.require_cook = require_cook;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean isDel() {
		return del;
	}
	public void setDel(boolean del) {
		this.del = del;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public List<String> listIndex;

	public List<String> getListIndex() {
		return listIndex;
	}

	public void setListIndex(List<String> listIndex) {
		this.listIndex = listIndex;
	}

	@Override
	public String toString() {
		return "FouceBean{" +
				"points=" + Arrays.toString(points) +
				", id='" + id + '\'' +
				", classcode='" + classcode + '\'' +
				", code='" + code + '\'' +
				", name='" + name + '\'' +
				", name_en='" + name_en + '\'' +
				", help='" + help + '\'' +
				", unit='" + unit + '\'' +
				", price=" + price +
				", price_modify=" + price_modify +
				", weigh=" + weigh +
				", discount=" + discount +
				", temp=" + temp +
				", suit=" + suit +
				", require_cook=" + require_cook +
				", locked=" + locked +
				", del=" + del +
				", image='" + image + '\'' +
				", page=" + page +
				", type='" + type + '\'' +
				'}';
	}
}
