package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;

public class ReasonBean implements Serializable {
    private String code;
    private String name;
    private String nameEn;
    private int num;
    private double price;

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ReasonBean{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", num=" + num +
                ", price=" + price +
                '}';
    }
}
