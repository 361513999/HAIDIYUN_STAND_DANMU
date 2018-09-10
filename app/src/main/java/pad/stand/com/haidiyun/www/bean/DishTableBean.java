package pad.stand.com.haidiyun.www.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 菜篮子
 *
 * @author Administrator
 */
public class DishTableBean implements Serializable {
    /**
     *
     */
    private boolean mst;

    public boolean isMst() {
        return mst;
    }

    public void setMst(boolean mst) {
        this.mst = mst;
    }

    private static final long serialVersionUID = 1L;
    private int i;
    private String code;
    private String name;
    private String nameEn;
    private String unit;
    private double price;
    private double price1;
    private double price2;
    private boolean more;
    private boolean price_modify;
    private boolean weigh;
    private boolean discount;
    private boolean temp;
    private boolean suit;
    private boolean isEditPrice;
    private String SuitMenuDetail;
    private String detailNames;
    private double count;
    private ArrayList<ReasonBean> reasonBeans;
    private String cook_codes;
    private String cook_names;
    private String cook_prices;
    private String remark;
    private String flag;
    private int lt;
    private ArrayList<String> remarkList;
    private boolean canTz;
    private String tc_cook_codes;
    private String tc_cook_names;
    private String tc_cook_prices;
    private double orderMinLimit;
    private boolean isjj;

    public boolean isIsjj() {
        return isjj;
    }

    public void setIsjj(boolean isjj) {
        this.isjj = isjj;
    }

    public boolean isEditPrice() {
        return isEditPrice;
    }

    public void setEditPrice(boolean editPrice) {
        isEditPrice = editPrice;
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

    public double getOrderMinLimit() {
        return orderMinLimit;
    }

    public void setOrderMinLimit(double orderMinLimit) {
        this.orderMinLimit = orderMinLimit;
    }

    public String getDetailNames() {
        return detailNames;
    }

    public void setDetailNames(String detailNames) {
        this.detailNames = detailNames;
    }

    public boolean isCanTz() {
        return canTz;
    }

    public void setCanTz(boolean canTz) {
        this.canTz = canTz;
    }

    public int getLt() {
        return lt;
    }

    public void setLt(int lt) {
        this.lt = lt;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
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

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
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

    public String getSuitMenuDetail() {
        return SuitMenuDetail;
    }

    public void setSuitMenuDetail(String suitMenuDetail) {
        SuitMenuDetail = suitMenuDetail;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public ArrayList<ReasonBean> getReasonBeans() {
        return reasonBeans;
    }

    public void setReasonBeans(ArrayList<ReasonBean> reasonBeans) {
        this.reasonBeans = reasonBeans;
    }

    public String getCook_codes() {
        return cook_codes;
    }

    public void setCook_codes(String cook_codes) {
        this.cook_codes = cook_codes;
    }

    public String getCook_names() {
        return cook_names;
    }

    public void setCook_names(String cook_names) {
        this.cook_names = cook_names;
    }

    public String getCook_prices() {
        return cook_prices;
    }

    public void setCook_prices(String cook_prices) {
        this.cook_prices = cook_prices;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ArrayList<String> getRemarkList() {
        return remarkList;
    }

    public void setRemarkList(ArrayList<String> remarkList) {
        this.remarkList = remarkList;
    }

    public String getTc_cook_codes() {
        return tc_cook_codes;
    }

    public void setTc_cook_codes(String tc_cook_codes) {
        this.tc_cook_codes = tc_cook_codes;
    }

    public String getTc_cook_names() {
        return tc_cook_names;
    }

    public void setTc_cook_names(String tc_cook_names) {
        this.tc_cook_names = tc_cook_names;
    }

    public String getTc_cook_prices() {
        return tc_cook_prices;
    }

    public void setTc_cook_prices(String tc_cook_prices) {
        this.tc_cook_prices = tc_cook_prices;
    }

    @Override
    public String toString() {
        return "DishTableBean{" +
                "mst=" + mst +
                ", i=" + i +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", price1=" + price1 +
                ", price2=" + price2 +
                ", more=" + more +
                ", price_modify=" + price_modify +
                ", weigh=" + weigh +
                ", discount=" + discount +
                ", temp=" + temp +
                ", suit=" + suit +
                ", isEditPrice=" + isEditPrice +
                ", SuitMenuDetail='" + SuitMenuDetail + '\'' +
                ", detailNames='" + detailNames + '\'' +
                ", count=" + count +
                ", reasonBeans=" + reasonBeans +
                ", cook_codes='" + cook_codes + '\'' +
                ", cook_names='" + cook_names + '\'' +
                ", cook_prices='" + cook_prices + '\'' +
                ", remark='" + remark + '\'' +
                ", flag='" + flag + '\'' +
                ", lt=" + lt +
                ", remarkList=" + remarkList +
                ", canTz=" + canTz +
                ", tc_cook_codes='" + tc_cook_codes + '\'' +
                ", tc_cook_names='" + tc_cook_names + '\'' +
                ", tc_cook_prices='" + tc_cook_prices + '\'' +
                ", orderMinLimit=" + orderMinLimit +
                '}';
    }
}
