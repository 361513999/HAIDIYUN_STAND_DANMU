package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;

public class ResonMenuBean implements Serializable {
	private String name;
	private String nameEn;
	private String code;
	private boolean mustSel;
	private boolean mustSelect;

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public boolean isMustSel() {
		return mustSel;
	}

	public void setMustSel(boolean mustSel) {
		this.mustSel = mustSel;
	}

	private boolean MultySelect;
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

	public boolean isMultySelect() {
		return MultySelect;
	}

	public void setMultySelect(boolean multySelect) {
		MultySelect = multySelect;
	}

	public boolean isMustSelect() {
		return mustSelect;
	}

	public void setMustSelect(boolean mustSelect) {
		this.mustSelect = mustSelect;
	}
}
