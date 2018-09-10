package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;

public class Skin implements Serializable{
	private String name;
	private String apk;
	private String pkg;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getApk() {
		return apk;
	}
	public void setApk(String apk) {
		this.apk = apk;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	
}
