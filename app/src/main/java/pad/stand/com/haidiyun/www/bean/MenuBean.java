package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;

public class MenuBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int i;
	private int id;
	private String parentCode;
	private String code;
	private String name;
	private String help;
	private String name_en;
	private String level;
	private String siteCode;
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
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
	public String getName_en() {
		return name_en;
	}
	public void setName_en(String name_en) {
		this.name_en = name_en;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	@Override
	public String toString() {
		return "MenuBean{" +
				"i=" + i +
				", id=" + id +
				", parentCode='" + parentCode + '\'' +
				", code='" + code + '\'' +
				", name='" + name + '\'' +
				", help='" + help + '\'' +
				", name_en='" + name_en + '\'' +
				", level='" + level + '\'' +
				", siteCode='" + siteCode + '\'' +
				'}';
	}
}
