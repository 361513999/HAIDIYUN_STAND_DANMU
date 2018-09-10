package pad.com.haidiyun.www.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 菜篮子
 *
 * @author Administrator
 */
public class DishTableBean implements Serializable {
    /**
     *
     */
    private String detailNames;
    private static final long serialVersionUID = 1L;
    private int i;
    private String code;
    private String name;
    private String unit;
    private double price;
    private boolean more;
    private boolean price_modify;
    private boolean weigh;
    private boolean discount;
    private boolean temp;
    private boolean suit;
    private boolean isCanSendMustFlag;
    private String SuitMenuDetail;
    private int count;
    private ArrayList<ReasonBean> reasonBeans;
    private HashMap<String, HashMap<String, ReasonBean>> reasonTcBeans;
    private String cook_codes;
    private String cook_prices;
    private String remark;
    private String flag;
    private String listStr;
    private int extra;
    private String cook_names;
    private String mustSelectName;
    private String tc_cook_codes;
    private String tc_cook_names;
    private String tc_cook_prices;
    private String tcListStr;
    private String tcExtra;
    private int tcMustNum;
    private int tcActualNum;

    public int getTcActualNum() {
        return tcActualNum;
    }

    public void setTcActualNum(int tcActualNum) {
        this.tcActualNum = tcActualNum;
    }

    public String getTcListStr() {
        return tcListStr;
    }

    public void setTcListStr(String tcListStr) {
        this.tcListStr = tcListStr;
    }

    public String getTcExtra() {
        return tcExtra;
    }

    public void setTcExtra(String tcExtra) {
        this.tcExtra = tcExtra;
    }

    public int getTcMustNum() {
        return tcMustNum;
    }

    public void setTcMustNum(int tcMustNum) {
        this.tcMustNum = tcMustNum;
    }


    public HashMap<String, HashMap<String, ReasonBean>> getReasonTcBeans() {
        return reasonTcBeans;
    }

    public void setReasonTcBeans(HashMap<String, HashMap<String, ReasonBean>> reasonTcBeans) {
        this.reasonTcBeans = reasonTcBeans;
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

    public String getMustSelectName() {
        return mustSelectName;
    }

    public void setMustSelectName(String mustSelectName) {
        this.mustSelectName = mustSelectName;
    }

    public String getListStr() {
        return listStr;
    }

    public void setListStr(String listStr) {
        this.listStr = listStr;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    public boolean isCanSendMustFlag() {
        return isCanSendMustFlag;
    }

    public void setCanSendMustFlag(boolean canSendMustFlag) {
        isCanSendMustFlag = canSendMustFlag;
    }

    private ArrayList<String> remarkList;

    public String getDetailNames() {
        return detailNames;
    }

    public void setDetailNames(String detailNames) {
        this.detailNames = detailNames;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
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

    @Override
    public String toString() {
        return "DishTableBean{" +
                "detailNames='" + detailNames + '\'' +
                ", i=" + i +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", more=" + more +
                ", price_modify=" + price_modify +
                ", weigh=" + weigh +
                ", discount=" + discount +
                ", temp=" + temp +
                ", suit=" + suit +
                ", isCanSendMustFlag=" + isCanSendMustFlag +
                ", SuitMenuDetail='" + SuitMenuDetail + '\'' +
                ", count=" + count +
                ", reasonBeans=" + reasonBeans +
                ", reasonTcBeans=" + reasonTcBeans +
                ", cook_codes='" + cook_codes + '\'' +
                ", cook_prices='" + cook_prices + '\'' +
                ", remark='" + remark + '\'' +
                ", flag='" + flag + '\'' +
                ", listStr='" + listStr + '\'' +
                ", extra=" + extra +
                ", cook_names='" + cook_names + '\'' +
                ", mustSelectName='" + mustSelectName + '\'' +
                ", tc_cook_codes='" + tc_cook_codes + '\'' +
                ", tc_cook_names='" + tc_cook_names + '\'' +
                ", tc_cook_prices='" + tc_cook_prices + '\'' +
                ", tcListStr='" + tcListStr + '\'' +
                ", tcExtra='" + tcExtra + '\'' +
                ", tcMustNum=" + tcMustNum +
                ", remarkList=" + remarkList +
                '}';
    }
}
