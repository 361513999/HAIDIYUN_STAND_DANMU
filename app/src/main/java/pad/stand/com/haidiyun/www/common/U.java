package pad.stand.com.haidiyun.www.common;

public class U {
    private static final String BASE_URL = "/DataService.svc/";
    public static final String URL = "/DataService.svc/";
    public static final String URL_INDEX = BASE_URL + "index";
    public static final String URL_GUQING = BASE_URL+"GetSaleOutMenus";
    public static final String URL_TIP = "http://watch.haidiyun.top/ajax/Ajax_LoginAction?method=GetTip";
    public static final String URL_DOWNLOAD_DATA = BASE_URL + "DownloadData";
    public static final String URL_ORDER_FINISH_STATUS = BASE_URL + "GetOrderPayResult";
    public static final String URL_DOWNLOAD_IMAGES = BASE_URL + "DownloadImgZip";
    public static final String URL_ORDER_SEND = BASE_URL + "SendOrderDtl";
    public static final String URL_ORDER_PRE_SEND = BASE_URL + "SendPrePayOrderDtl";
    public static final String URL_TABLE_OPEN = BASE_URL + "AddOrder";
    public static final String URL_REFREF_TABLE_STATUS = BASE_URL + "GetRestTable";
    public static final String URL_GET_ORDER = BASE_URL + "GetOrderDtl";
    public static final String URL_GET_USERINFO = BASE_URL + "GetMemberInfo";
    public static final String URL_PAY_USERINFO = BASE_URL + "MemberCheckOut";
    public static final String URL_PHONE_PAY = BASE_URL + "GetPayUrl";
    public static final String URL_PHONE_PAY_NEW = BASE_URL + "QrCodePay";
    public static final String URL_PAY_STATUS = BASE_URL + "GetBillStatus";
    public static final String URL_PRINT_BILL = BASE_URL + "PrintBill";
    public static final String URL_USER_VAL = BASE_URL + "ValidateUser";
    public static final String URL_ORDER_TUI = BASE_URL + "Tuicai";
    public static final String URL_ORDER_ZENG = BASE_URL + "Zengsong";
    public static final String URL_POST_PJ = BASE_URL + "SubmitBillComment";
    public static final String URL_GET_DISCOUNT = BASE_URL + "GetHdyDiscDetail";

    public static String VISTER(String IP, String url) {
        return "http://" + IP + url;
    }
}
