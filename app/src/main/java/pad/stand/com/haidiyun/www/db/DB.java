package pad.stand.com.haidiyun.www.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.bean.Dish;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.bean.FlipBean;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.FouceBean;
import pad.stand.com.haidiyun.www.bean.ImageInfo;
import pad.stand.com.haidiyun.www.bean.MenuBean;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.bean.ResonMenuBean;
import pad.stand.com.haidiyun.www.bean.TBean;
import pad.stand.com.haidiyun.www.bean.TableBean;
import pad.stand.com.haidiyun.www.bean.TablesBean;
import pad.stand.com.haidiyun.www.bean.TcBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.common.TimeUtil;

public class DB {
    private static final String TAG = "DB";
    public static DB dao;
    private static DBHelper dbHelper;
    private static SQLiteDatabase db;
    private static SharedUtils sharedUtils;

    private void DB() {
    }

    /**
     * 单列数据库操作对象
     *
     * @return
     */

    public static synchronized DB getInstance() {
        sharedUtils = new SharedUtils(Common.CONFIG);
        if (dao == null) {
            synchronized (DB.class) {
                if (dao == null) {
                    dao = new DB();
                    dbHelper = new DBHelper(BaseApplication.application);
                    db = dbHelper.getWritableDatabase();
                }
            }
        }
        return dao;
    }

    /**
     * 根据字段名字获得数据
     *
     * @param cursor
     * @param indexName
     * @return
     */
    // --------------------------
    // ---------------获取数据处理
    private String getString(Cursor cursor, String indexName) {
        return cursor.getString(cursor.getColumnIndex(indexName));
    }

    private int getInt(Cursor cursor, String indexName) {
        return cursor.getInt(cursor.getColumnIndex(indexName));
    }

    private double getDouble(Cursor cursor, String indexName) {
        return cursor.getDouble(cursor.getColumnIndex(indexName));
    }

    private long getLong(Cursor cursor, String indexName) {
        return cursor.getLong(cursor.getColumnIndex(indexName));
    }

    private boolean getBoolean(Cursor cursor, String indexName) {
        return cursor.getInt(cursor.getColumnIndex(indexName)) == 1 ? true
                : false;
    }

    // ------------------------------
    // -------------------json处理
    private String getJsonString(JSONObject json, String element)
            throws JSONException {
        return json.getString(element).trim();
    }

    private int getJsonInt(JSONObject json, String element)
            throws JSONException {
        return json.getInt(element);
    }

    private double getJsonDouble(JSONObject json, String element)
            throws JSONException {
        return json.getDouble(element);
    }

    private long getJsonLong(JSONObject json, String element)
            throws JSONException {
        return json.getLong(element);
    }

    private boolean getJsonBoolean(JSONObject json, String element)
            throws JSONException {
        return json.getBoolean(element);
    }

    // 添加新数据

    /**
     * 添加菜品
     *
     * @param array
     * @throws JSONException
     */
    public void addDish(JSONArray array, Map<String, String> mes) {
        try {
            int len = array != null ? array.length() : 0;
            db.beginTransaction();
            for (int i = 0; i < len; i++) {
                // 解析数据并添加到数据库
                JSONObject json = array.getJSONObject(i);

                db.execSQL(
                        "insert into dish(id,pcode,classcode,code,name,name_en,help,unit,price,price1,price2,price_modify,weigh,discount,temp,suit,require_cook,description,locked,uniqueid,timestamp,del,show,Sort,type,srvf,srvt,orderMinLimit,chargeMode) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{getJsonInt(json, "Id"),
                                mes.get(getJsonString(json, "ClassCode")),
                                getJsonString(json, "ClassCode"),
                                getJsonString(json, "Code"),
                                getJsonString(json, "Name"),
                                getJsonString(json, "EName"),
                                getJsonString(json, "HelpCode"),
                                getJsonString(json, "Unit"),
                                getJsonDouble(json, "Price"),
                                getJsonDouble(json, "Price1"),
                                getJsonDouble(json, "Price2"),
                                getJsonBoolean(json, "PriceModify"),
                                getJsonBoolean(json, "NeedWeigh"),
                                getJsonBoolean(json, "AllowDiscount"),
                                getJsonBoolean(json, "IsTmp"),
                                getJsonBoolean(json, "IsSuit"),
                                getJsonBoolean(json, "RequireCook"),
                                getJsonString(json, "Description"),
                                getJsonBoolean(json, "Locked"),
                                getJsonString(json, "UniqueId"),
                                json.has("timestamp") ? json.getLong("timestamp") : 0,
//                            getJsonLong(json, "timestamp"),
                                getJsonBoolean(json, "DelStatus"),
                                getJsonBoolean(json, "ShowOnPad"),
                                getJsonInt(json, "Sort"),
                                getJsonString(json, "Type"),
                                getJsonString(json, "SrvFrom"),
                                getJsonString(json, "SrvTo"),
                                getJsonDouble(json, "OrderMinLimit"),
                                getJsonString(json, "SuitMenuChargeMode")
//                            "A"

                        });
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (final Exception e) {
            e.printStackTrace();
            new Thread() {
                public void run() {
                    try {
                        FileUtils.writeLog(
                                e.toString(), "加入菜品表");
                    } catch (Exception e) {
                        e.printStackTrace();
                        FileUtils.writeLog("写入异常", "加入菜品表");
                    }
                }
            }.start();
        }
    }

    /**
     * 添加分类
     *
     * @param array
     * @throws JSONException
     */
    public Map<String, String> addCate(JSONArray array) throws JSONException {
        Map<String, String> maps = new HashMap<>();
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into menu(id,parent_code,code,name,help,name_en,level,sitecode,sort,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    new Object[]{
                            getJsonInt(json, "Id"),
                            getJsonString(json, "ParentCode"),
                            getJsonString(json, "Code"),
                            getJsonString(json, "Name"),
                            getJsonString(json, "HelpCode"),
                            getJsonString(json, "EName"),
                            getJsonString(json, "Level"),
                            getJsonString(json, "SiteCode"),
                            getJsonInt(json, "Sort"),
                            getJsonString(json, "Description"),
                            getJsonString(json, "UniqueId"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
                            json.getBoolean("DelStatus")});
            maps.put(getJsonString(json, "Code"), getJsonString(json, "ParentCode"));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return maps;
    }

    /**
     * 添加用户组
     *
     * @param array
     * @throws JSONException
     */
    public void addUser(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into user(code,pwd,locked,uniqueid,timestamp,del,description) values(?,?,?,?,?,?,?)",
                    new Object[]{getJsonString(json, "Code"),
                            getJsonString(json, "Pwd"),
                            getJsonBoolean(json, "Locked"),
                            getJsonString(json, "UniqueId"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
//                            json.getLong("timestamp"),
                            json.getBoolean("DelStatus"),
                            json.has("Name") ? json.getString("Name") : ""});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    //添加新用户
    public void addUser(JSONObject json) {
        try {
            if (isExits(getJsonString(json, "code"))) {
                db.execSQL("update user set pwd = ?", new Object[]{getJsonString(json, "pwd")});
            } else {
                db.execSQL(
                        "insert into user(code,pwd,locked,uniqueid,timestamp,del) values(?,?,?,?,?,?)",
                        new Object[]{getJsonString(json, "code"),
                                getJsonString(json, "pwd"),
                                getJsonBoolean(json, "locked"),
                                getJsonString(json, "uniqueid"),
                                json.getLong("timestamp"),
                                json.getBoolean("del")});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 餐台区域
     *
     * @param array
     * @throws JSONException
     */
    public void addArea(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL("insert into area(id,code,name,help,site,sort,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?,?)", new Object[]{getJsonInt(json, "Id"), getJsonString(json, "Code"), getJsonString(json, "Name"), getJsonString(json, "HelpCode"), getJsonString(json, "Site"), getJsonInt(json, "Sort"), getJsonString(json, "Description"), getJsonString(json, "UniqueId"), json.has("timestamp") ? json.getLong("timestamp") : 0, json.getBoolean("DelStatus")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 桌台
     *
     * @param array
     * @throws JSONException
     */
    public void addBoard(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL("insert into board(id,code,name,help,table_class,area,max,description,locked,uniqueid,timestamp,del) " +
                            "values(?,?,?,?,?,?,?,?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"), getJsonString(json, "Code"), getJsonString(json, "Name"),
                            getJsonString(json, "HelpCode"), getJsonString(json, "TableCls"), getJsonString(json, "Area"),
                            getJsonInt(json, "MaxGstNum"), getJsonString(json, "Description"), getJsonBoolean(json, "Locked"),
                            getJsonString(json, "UniqueId"), json.has("timestamp") ? json.getLong("timestamp") : 0, json.getBoolean("DelStatus")});

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

//    db.execSQL("create table order_table(i integer primary key autoincrement,id int ,billNo varchar,state varchar,tableNo varchar,orderSymbol varchar)");

    /**
     * 桌台拼桌
     *
     * @param array
     * @throws JSONException
     */
    public void addOrder_table(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL("insert into Order_table(id,billNo,state,tableNo,orderSymbol) " +
                            "values(?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"), getJsonString(json, "BillNo"), getJsonString(json, "State"),
                            getJsonString(json, "TableNo"), getJsonString(json, "OrderSymbol")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 营业点
     *
     * @param array
     * @throws JSONException
     */
    public void addSite(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into site(id,code,name,help,sort,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "Code"),
                            getJsonString(json, "Name"),
                            getJsonString(json, "HelpCode"),
                            getJsonInt(json, "Sort"),
                            getJsonString(json, "Description"),
                            getJsonString(json, "UniqueId"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
//                            json.getLong("timestamp"),
                            json.getBoolean("DelStatus")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 做法类别
     *
     * @param array
     * @throws JSONException
     */
    public void addCookCls(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into cook_class(id,code,name,sort,description,uniqueid,timestamp,del,MultySelect) values(?,?,?,?,?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "Code"),
                            getJsonString(json, "Name"),
                            getJsonInt(json, "Sort"),
                            getJsonString(json, "Description"),
                            getJsonString(json, "UniqueId"),
//                            json.getLong("timestamp"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
                            json.getBoolean("DelStatus"), json.getBoolean("MultySelect")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 做法
     *
     * @param array
     * @throws JSONException
     */
    public void addCook(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into cook(id,cookclass,code,name,help,public,price,sort,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "CookCls"),
                            getJsonString(json, "Code"),
                            getJsonString(json, "Name"),
                            getJsonString(json, "HelpCode"),
                            getJsonBoolean(json, "Public"),
                            getJsonDouble(json, "Price"),
                            getJsonInt(json, "Sort"),
                            getJsonString(json, "Description"),
                            getJsonString(json, "UniqueId"),
//                            json.getLong("timestamp"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
                            json.getBoolean("DelStatus")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 做法关系
     *
     * @param array
     * @throws JSONException
     */
    public void addMenuCook(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into menu_cook(id,menucode,cookcode,require,price,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?)",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "MenuCode"),
                            getJsonString(json, "CookCode"),
                            getJsonBoolean(json, "Require"),
                            getJsonDouble(json, "Price"),
                            getJsonString(json, "UniqueId"),
//                            json.getLong("timestamp"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
                            json.getBoolean("DelStatus")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 图片组
     *
     * @param array
     * @throws JSONException
     */
    public void addImages(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into images(id,code,path,sort,uniqueid,timestamp,del) values(?,?,?,?,?,?,?);",
                    new Object[]{
                            getJsonInt(json, "Id"),
                            getJsonString(json, "MenuCode"),
                            getJsonString(json, "ImagePath").replace("\\", "/"),
                            getJsonInt(json, "Sort"),
                            getJsonString(json, "UniqueId"),
//                            json.getLong("timestamp"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
                            json.getBoolean("DelStatus")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 套餐
     *
     * @param array
     * @throws JSONException
     */
    public void addDetail(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into detail(id,suitcode,code,number,price,require,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "SuitMenuCode"),
                            getJsonString(json, "MenuCode"),
                            getJsonInt(json, "Number"),
                            getJsonDouble(json, "Price"),
                            getJsonBoolean(json, "Require"),
                            getJsonString(json, "UniqueId"),
//                            json.getLong("timestamp"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
                            json.getBoolean("DelStatus")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 限制
     *
     * @param array
     * @throws JSONException
     */
    public void addLimit(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into lt(code,lt,ltype,ltnum,ltmax) values(?,?,?,?,?);",
                    new Object[]{
                            getJsonString(json, "MenuCode"),
                            getJsonBoolean(json, "Limit"), getJsonString(json, "limitType"),
                            getJsonInt(json, "LimitNumber"), getJsonInt(json, "MaxNumber")
                    });
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    /**
     * 口味
     *
     * @param array
     * @throws JSONException
     */
    public void addRemark(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into remark(id,code,name,help,sort,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "Code"),
                            getJsonString(json, "Name"),
                            getJsonString(json, "HelpCode"),
                            getJsonInt(json, "Sort"),
                            getJsonString(json, "UniqueId"),
//                            json.getLong("timestamp"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
                            json.getBoolean("DelStatus")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 组合方案
     *
     * @param array
     * @throws JSONException
     */
    public void addMgroup(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into mgroup(id,mcode,gcode,gname,max_num,min_num,sort,timestamp,del,description) values(?,?,?,?,?,?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "MenuCode"),
                            getJsonString(json, "GroupCode"),
                            getJsonString(json, "GroupName"),
                            getJsonInt(json, "MaxSelectNumber"),
                            getJsonInt(json, "MinSelectNumber"),
                            getJsonInt(json, "Sort"),
//                            json.getLong("timestamp"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
                            json.getBoolean("DelStatus"),
                            getJsonString(json, "Description")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 组合明细
     *
     * @param array
     * @throws JSONException
     */
    public void addMgDetail(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into mgdetail(id,mcode,gcode,scode,num,price,require,sort,timestamp,del) values(?,?,?,?,?,?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "MainMenuCode"),
                            getJsonString(json, "GroupCode"),
                            getJsonString(json, "SubMenuCode"),
                            getJsonInt(json, "Number"),
                            getJsonDouble(json, "Price"),
                            getJsonBoolean(json, "Require"),
                            getJsonInt(json, "Sort"),
//                            json.getLong("timestamp"),
                            json.has("timestamp") ? json.getLong("timestamp") : 0,
                            json.getBoolean("DelStatus")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 退菜理由
     *
     * @param array
     * @throws JSONException
     */
    public void addRc(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            if (getJsonString(json, "Catagory").equals("Reason")) {
                db.execSQL(
                        "insert into tcReason(id,code,name,sort,timestamp,del) values(?,?,?,?,?,?);",
                        new Object[]{getJsonInt(json, "Id"),
                                getJsonString(json, "Code"),
                                getJsonString(json, "Name"),
                                getJsonInt(json, "Sort"),
//                                json.getLong("timestamp"),
                                json.has("timestamp") ? json.getLong("timestamp") : 0,
                                json.getBoolean("DelStatus")});
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 桌台类型
     *
     * @param array
     * @throws JSONException
     */
    public void addTableType(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            if (getJsonString(json, "Catagory").equals("TableType")) {
                db.execSQL(
                        "insert into tableType(id,code,name,sort,timestamp,del,extra) values(?,?,?,?,?,?,?);",
                        new Object[]{getJsonInt(json, "Id"),
                                getJsonString(json, "Code"),
                                getJsonString(json, "Name"),
                                getJsonInt(json, "Sort"),
//                                json.getLong("timestamp"),
                                json.has("timestamp") ? json.getLong("timestamp") : 0,
                                json.getBoolean("DelStatus"),
                                json.getString("Extra")
                        });
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void addCookLit(JSONArray array) throws JSONException {
        P.c("数据" + array.toString());
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);

            db.execSQL(
                    "insert into cook_lit(menucode,cookcls,require,uniqueid,del) values(?,?,?,?,?);",
                    new Object[]{getJsonString(json, "MenuCode"),
                            getJsonString(json, "CookCls"),
                            getJsonBoolean(json, "Require"),
                            getJsonString(json, "UniqueId"),
                            json.getBoolean("DelStatus")});

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 菜品分类
     *
     * @return
     */
    public ArrayList<MenuBean> getMenus(int level, String sitecode) {
        ArrayList<MenuBean> menuBeans = new ArrayList<MenuBean>();
//	String sql = "select i,id,parent_code,code,name,help,name_en,level,sitecode,(select count(*) from dish where classcode in(select code from menu where parent_code=m.code) and del=0) as count from menu as m where level=? and sitecode=? and del=0;";
        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        String sql = "select i,id,parent_code,code,name,help,name_en,level,sitecode from menu where level=? and sitecode=? and del=0  order by sort asc ,code asc;";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(level),
                    sitecode});
            while (cursor.moveToNext()) {
                MenuBean bean = new MenuBean();
                bean.setCode(getString(cursor, "code"));
                bean.setHelp(getString(cursor, "help"));
                bean.setI(getInt(cursor, "i"));
                bean.setId(getInt(cursor, "id"));
                String item = getString(cursor, "name");
                //如果是包房模式
                if (sharedUtils.getBooleanValue("is_room") || sharedUtils.getBooleanValue("is_hall")) {
                    if (sharedUtils.getBooleanValue("is_room")) {
                        //包房菜
                        if (sharedUtils.getStringValue("table_name").startsWith("V")) {
                            bean.setName(getString(cursor, "name"));
                        } else {
                            if (item.startsWith("V")) {
                                continue;
                            } else {
                                bean.setName(getString(cursor, "name"));
                            }
                        }
                    }
                    if (sharedUtils.getBooleanValue("is_hall")) {
                        //大厅菜
                        if (sharedUtils.getStringValue("table_name").endsWith("I")) {
                            bean.setName(getString(cursor, "name"));
                        } else {
                            if (item.endsWith("I")) {
                                continue;
                            } else {
                                bean.setName(getString(cursor, "name"));
                            }
                        }
                    }
                } else {
                    if (item.startsWith("V") || item.endsWith("I")) {
                        continue;
                    } else {
                        bean.setName(getString(cursor, "name"));
                    }
                }
                bean.setName_en(getString(cursor, "name_en"));
                bean.setParentCode(getString(cursor, "parent_code"));

                bean.setSiteCode(getString(cursor, "sitecode"));
                String ss = bean.toString();
                menuBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return menuBeans;
    }

    /**
     * 获得小类
     *
     * @param id
     * @return
     */
    public ArrayList<MenuBean> getChildMenuBeans(String parent_code) {
        ArrayList<MenuBean> menuBeans = new ArrayList<MenuBean>();
        String sql = "select i,id,parent_code,code,name,help,name_en,level,sitecode from menu where parent_code=? and del=0";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{parent_code});
            while (cursor.moveToNext()) {
                MenuBean bean = new MenuBean();
                bean.setCode(getString(cursor, "code"));
                bean.setHelp(getString(cursor, "help"));
                bean.setI(getInt(cursor, "i"));
                bean.setId(getInt(cursor, "id"));
                bean.setName(getString(cursor, "name"));
                bean.setName_en(getString(cursor, "name_en"));
                bean.setParentCode(getString(cursor, "parent_code"));
                bean.setSiteCode(getString(cursor, "sitecode"));
//                String sss = bean.toString();
                menuBeans.add(bean);
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return menuBeans;
    }

    //
    public ArrayList<FoodsBean> getFoods(Map<String, Integer> maps) {
        Set<String> sets = maps.keySet();
        Iterator it = sets.iterator();
        ArrayList<String> temps = new ArrayList<>();
        while (it.hasNext()) {
            temps.add("'" + it.next().toString() + "'");
        }
        String str = temps.toString().replace("[", "").replace("]", "").replace(" ", "");


        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        String sql = "";
        if (sharedUtils.getBooleanValue("is_time")) {
            //开启供应时间
            sql = "select d.pcode,d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code left join menu as m on m.code=d.pcode where d.pcode  in(" + str + ")  and d.show=1   and d.del=0 " + goTime() + " group by d.id order by m.sort,d.pcode,d.sort asc";

        } else {
            sql = "select d.pcode,d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code left join menu as m on m.code=d.pcode where d.pcode  in(" + str + ")  and d.show=1   and d.del=0 group by d.id order by m.sort,d.pcode,d.sort asc";
        }
        P.c("查询语句" + sql);
        ArrayList<FoodsBean> foodsBeans = new ArrayList<FoodsBean>();
        Cursor cursor = null;
        try {
            P.c("查询大类" + str + "查询前"
                    + TimeUtil.getTimeAll(System.currentTimeMillis()));
            cursor = db.rawQuery(
                    sql,
                    null);
            while (cursor.moveToNext()) {
                FoodsBean bean = new FoodsBean();
                bean.setCyPcode(getString(cursor, "pcode"));
                bean.setCyMenuIndex(maps.get(bean.getCyPcode()));
                bean.setClassCode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getInt(cursor, "id"));
                bean.setOrderMinLimit(getDouble(cursor, "orderMinLimit"));
                bean.setName(getString(cursor, "name"));
                bean.setNameEn(getString(cursor, "name_en"));
                bean.setDescription(getString(cursor, "description"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPrice1(getDouble(cursor, "price1"));
                bean.setPrice2(getDouble(cursor, "price2"));
                bean.setPriceMofidy(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setCount(getDouble(cursor, "count"));
                bean.setPath(getString(cursor, "path"));
                bean.setImage(getBoolean(cursor, "w"));
                bean.setMore(getBoolean(cursor, "more"));
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                bean.setType(getString(cursor, "type"));
                String ss = getString(cursor, "chargeMode");
                bean.setChargeMode(getString(cursor, "chargeMode"));
                foodsBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        P.c("查询后" + TimeUtil.getTimeAll(System.currentTimeMillis()));
        return foodsBeans;
    }


    /**
     * 根据类别查询菜品,新加入
     *
     * @param menuCode
     * @param NUM
     * @param PAGE
     * @return
     */
    public ArrayList<FoodsBean> getFoods(String menuCode, int NUM, int PAGE) {
        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        String sql = "";
        if (sharedUtils.getBooleanValue("is_time")) {
            //开启供应时间
            sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.classcode  in(select code from menu where parent_code=?)  and d.show=1   and d.del=0 " + goTime() + " group by d.id order by d.sort asc limit ?,?;";

        } else {
            sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.classcode  in(select code from menu where parent_code=?)  and d.show=1   and d.del=0 group by d.id order by d.sort asc limit ?,?;";

        }
        ArrayList<FoodsBean> foodsBeans = new ArrayList<FoodsBean>();
        Cursor cursor = null;
        try {
            P.c("查询大类" + menuCode + "查询前"
                    + TimeUtil.getTimeAll(System.currentTimeMillis()));
            cursor = db.rawQuery(
                    sql,
                    new String[]{menuCode, String.valueOf(PAGE),
                            String.valueOf(NUM)});
            while (cursor.moveToNext()) {
                FoodsBean bean = new FoodsBean();
                bean.setClassCode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getInt(cursor, "id"));
                bean.setOrderMinLimit(getDouble(cursor, "orderMinLimit"));
                bean.setName(getString(cursor, "name"));
                bean.setNameEn(getString(cursor, "name_en"));
                bean.setDescription(getString(cursor, "description"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPrice1(getDouble(cursor, "price1"));
                bean.setPrice2(getDouble(cursor, "price2"));
                bean.setPriceMofidy(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setCount(getDouble(cursor, "count"));
                bean.setPath(getString(cursor, "path"));
                bean.setImage(getBoolean(cursor, "w"));
                bean.setMore(getBoolean(cursor, "more"));
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                bean.setType(getString(cursor, "type"));
                String ss = getString(cursor, "chargeMode");
                bean.setChargeMode(getString(cursor, "chargeMode"));
                foodsBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        P.c("查询后" + TimeUtil.getTimeAll(System.currentTimeMillis()));
        return foodsBeans;
    }

    /**
     * 追加，加载更多  菜类大类
     *
     * @param menuCode
     * @param NUM
     * @param PAGE
     * @return
     */
    public void getFoods(ArrayList<FoodsBean> foodsBeans, String menuCode,
                         int NUM, int PAGE) {
        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        String sql = "";
        if (sharedUtils.getBooleanValue("is_time")) {
            //开启供应时间
            sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.classcode  in(select code from menu where parent_code=?)  and d.show=1 and d.del=0 " + goTime() + " group by d.id order by d.sort asc limit ?,?;";

        } else {
            sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit," +
                    "d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked," +
                    "case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when " +
                    "dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 " +
                    "left join dish_table as dt on dt.code=d.code where d.classcode  in(select code from menu where parent_code=?)  " +
                    "and d.show=1 and d.del=0 group by d.id order by d.sort asc limit ?,?;";

        }

        Cursor cursor = null;
        try {
            P.c("查询大类--" + menuCode + "查询前"
                    + TimeUtil.getTimeAll(System.currentTimeMillis()));
            cursor = db.rawQuery(
                    sql,
                    new String[]{menuCode, String.valueOf(PAGE),
                            String.valueOf(NUM)});
            //011 10 0
            //011 10 10
            //011 10 20
            while (cursor.moveToNext()) {
                FoodsBean bean = new FoodsBean();
                bean.setClassCode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getInt(cursor, "id"));
                bean.setOrderMinLimit(getDouble(cursor, "orderMinLimit"));
                bean.setName(getString(cursor, "name"));
                bean.setNameEn(getString(cursor, "name_en"));
                bean.setDescription(getString(cursor, "description"));
//                String type = sharedUtils.getStringValue("tableType");
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPrice1(getDouble(cursor, "price1"));
                bean.setPrice2(getDouble(cursor, "price2"));
                bean.setPriceMofidy(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setCount(getDouble(cursor, "count"));
                bean.setPath(getString(cursor, "path"));
                bean.setImage(getBoolean(cursor, "w"));
                bean.setMore(getBoolean(cursor, "more"));
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                bean.setType(getString(cursor, "type"));
                bean.setChargeMode(getString(cursor, "chargeMode"));
                foodsBeans.add(bean);
                bean = null;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        P.c("查询后" + TimeUtil.getTimeAll(System.currentTimeMillis()));
        if (foodsBeans.size()>10){
            P.c("菜类大类第十一菜品" + foodsBeans.get(10).toString());
        }
    }

    /**
     * 查询子类下所有菜品   菜类小类
     *
     * @param id
     * @return
     */
    private String goTime() {
        return " and '" + TimeUtil.getN() + "' Between d.srvf And d.srvt ";
    }

    public ArrayList<FoodsBean> getChildFoods(String menuCode, int NUM, int PAGE) {

        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        String sql = "";
        if (sharedUtils.getBooleanValue("is_time")) {
            sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code  where d.classcode = ?  and d.show=1 and d.del=0 " + goTime() + "  group by d.id order by d.sort asc limit ?,?;";

        } else {
            sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit," +
                    "d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked," +
                    "case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when" +
                    " dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0" +
                    " left join dish_table as dt on dt.code=d.code " +
                    " where d.classcode = ?  and d.show=1 and d.del=0  group by d.id order by d.sort asc limit ?,?;";

        }

        ArrayList<FoodsBean> foodsBeans = new ArrayList<FoodsBean>();
        Cursor cursor = null;
        try {
            P.c("查询子类" + menuCode + "查询前"
                    + TimeUtil.getTimeAll(System.currentTimeMillis()));
            cursor = db.rawQuery(
                    sql,
                    new String[]{menuCode, String.valueOf(PAGE),
                            String.valueOf(NUM)});
            P.c(cursor.getCount() + "--------------");
            while (cursor.moveToNext()) {
                FoodsBean bean = new FoodsBean();
                bean.setClassCode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getInt(cursor, "id"));
                bean.setOrderMinLimit(getDouble(cursor, "orderMinLimit"));
                bean.setName(getString(cursor, "name"));
                bean.setNameEn(getString(cursor, "name_en"));
                bean.setDescription(getString(cursor, "description"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPrice1(getDouble(cursor, "price1"));
                bean.setPrice2(getDouble(cursor, "price2"));
                bean.setPriceMofidy(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setCount(getDouble(cursor, "count"));
                bean.setPath(getString(cursor, "path"));
                bean.setImage(getBoolean(cursor, "w"));
                bean.setMore(getBoolean(cursor, "more"));
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                bean.setType(getString(cursor, "type"));
                String s = getString(cursor, "chargeMode");
//                bean.setChargeMode(getString(cursor, "chargeMode"));
                foodsBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        P.c("查询后" + TimeUtil.getTimeAll(System.currentTimeMillis()));
        if (foodsBeans.size()>10){
            P.c("菜类小类第十一菜品" + foodsBeans.get(10).toString());
        }
        return foodsBeans;

    }

    /**
     * 查询小类，追加
     *
     * @param menuCode
     * @param NUM
     * @param PAGE
     * @return
     */
    public void getChildFoods(ArrayList<FoodsBean> foodsBeans, String menuCode, int NUM, int PAGE) {
        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        String sql = "";
        if (sharedUtils.getBooleanValue("is_time")) {
            sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.description,d.help,d.unit,d.price,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code  where d.classcode = ? and d.show=1  and d.del=0 " + goTime() + " group by d.id order by d.sort asc limit ?,?;";

        } else {
            sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.description,d.help,d.unit,d.price,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code  where d.classcode = ? and d.show=1  and d.del=0  group by d.id order by d.sort asc limit ?,?;";

        }
        Cursor cursor = null;
        try {
            P.c("查询子类" + menuCode + "查询前"
                    + TimeUtil.getTimeAll(System.currentTimeMillis()));
            cursor = db.rawQuery(
                    sql,
                    new String[]{menuCode, String.valueOf(PAGE),
                            String.valueOf(NUM)});
            P.c(cursor.getCount() + "--------------");
            while (cursor.moveToNext()) {
                FoodsBean bean = new FoodsBean();
                bean.setClassCode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getInt(cursor, "id"));
                bean.setOrderMinLimit(getDouble(cursor, "orderMinLimit"));
                bean.setName(getString(cursor, "name"));
                bean.setDescription(getString(cursor, "description"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPriceMofidy(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setCount(getDouble(cursor, "count"));
                bean.setPath(getString(cursor, "path"));
                bean.setImage(getBoolean(cursor, "w"));
                bean.setMore(getBoolean(cursor, "more"));
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                bean.setType(getString(cursor, "type"));
                bean.setChargeMode(getString(cursor, "chargeMode"));
                foodsBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        P.c("查询后" + TimeUtil.getTimeAll(System.currentTimeMillis()));
    }

    /**
     * 获得大类总量
     *
     * @param id
     * @return
     */
    public int getChildCounts(String menuCode) {
        String sql = "SELECT COUNT(*) FROM DISH WHERE CLASSCODE=? and del=0";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[]{menuCode});
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor = null;
            }
        }
        return count;
    }

    /**
     * 获得大类总量
     *
     * @param id
     * @return
     */
    public int getCounts(String menuCode) {
        String sql = "select count(*) from dish where classcode in(select code from menu where parent_code=?) and del=0;";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[]{menuCode});
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor = null;
            }
        }
        return count;
    }

    public String getStand(String pwd) {
        String sql = "select case when (select count(*) from user where pwd=?)==0  then (select code from user)  else (select code from user where pwd=?) end eptName from user limit 0,1;";
        String eptName = "";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{pwd, pwd});
            int count = cursor.getCount();
            if (count != 0) {
                if (cursor.moveToNext()) {
                    eptName = getString(cursor, "eptName");
                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        P.c("操作员" + eptName);
        return eptName;
    }

    /**
     * 校验账户密码
     */
    public boolean isVal(String code, String pwd) {
        // select pwd from user where code='Admin1'
        boolean flag = false;
        String sql = "select pwd from user where code=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{code});
            int count = cursor.getCount();
            if (count != 0) {
                if (cursor.moveToNext()) {
                    if (getString(cursor, "pwd").equals(pwd)) {
                        // 账户存在，密码一样
                        flag = true;
                    } else {
                        flag = false;
                    }

                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return flag;

    }

    public boolean isExits(String code) {
        // select pwd from user where code='Admin1'
        boolean flag = false;
        String sql = "select * from user where code=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{code});
            int count = cursor.getCount();
            if (count != 0) {
                flag = true;
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return flag;

    }


    /**
     * 搜索
     *
     * @param search
     * @return
     */
    /**
     * 获得总量
     *
     * @param id
     * @return
     */
    public int getSearchCounts(String search, boolean word) {
        String sql;
        if (word) {
            sql = "select count(*) from dish  where name like ? and del=0;";
        } else {
            sql = "select count(*) from dish  where help like ? and del=0;";
        }
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[]{"%" + search + "%"});
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return count;
    }

    public ArrayList<FoodsBean> getSearchFoods(String search, boolean word) {

        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        String sql = "";
        if (sharedUtils.getBooleanValue("is_time")) {
            if (word) {

                sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.name like ? and d.show=1   and d.del=0 " + goTime() + " group by d.id order by d.pcode,d.sort asc;";
            } else {
                sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.help like ? and d.show=1   and d.del=0 " + goTime() + " group by d.id order by d.pcode,d.sort asc;";
            }

        } else {
            if (word) {
                sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.name like ? and d.show=1   and d.del=0 group by d.id order by d.pcode,d.sort asc;";
            } else {
                sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.help like ? and d.show=1   and d.del=0 group by d.id order by d.pcode,d.sort asc;";
            }
        }

        ArrayList<FoodsBean> foodsBeans = new ArrayList<FoodsBean>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    sql,
                    new String[]{"%" + search + "%"});
            P.c(cursor.getCount() + "---->");
            while (cursor.moveToNext()) {
                FoodsBean bean = new FoodsBean();
                bean.setClassCode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getInt(cursor, "id"));
                bean.setOrderMinLimit(getDouble(cursor, "orderMinLimit"));
                bean.setName(getString(cursor, "name"));
                bean.setNameEn(getString(cursor, "name_en"));
                bean.setDescription(getString(cursor, "description"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPrice1(getDouble(cursor, "price1"));
                bean.setPrice2(getDouble(cursor, "price2"));
                bean.setPriceMofidy(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setCount(getDouble(cursor, "count"));
                bean.setPath(getString(cursor, "path"));
                bean.setImage(getBoolean(cursor, "w"));
                bean.setMore(getBoolean(cursor, "more"));
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                bean.setType(getString(cursor, "type"));
                bean.setChargeMode(getString(cursor, "chargeMode"));
                foodsBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        P.c("查询后" + TimeUtil.getTimeAll(System.currentTimeMillis()));
        return foodsBeans;
    }

    /**
     * 首次加载搜索
     *
     * @param search
     * @param NUM
     * @param PAGE
     * @return
     */
    public ArrayList<FoodsBean> getSearchFoods(String search, int NUM, int PAGE, boolean word) {

        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        String sql = "";
        if (sharedUtils.getBooleanValue("is_time")) {
            if (word) {

                sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.name like ? and d.show=1   and d.del=0 " + goTime() + " group by d.id order by d.sort asc limit ?,?;";
            } else {
                sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.help like ? and d.show=1   and d.del=0 " + goTime() + " group by d.id order by d.sort asc limit ?,?;";
            }

        } else {
            if (word) {
                sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.name like ? and d.show=1   and d.del=0 group by d.id order by d.sort asc limit ?,?;";
            } else {
                sql = "select d.chargeMode,d.orderMinLimit,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price1,d.price2,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more,d.type   from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.help like ? and d.show=1   and d.del=0 group by d.id order by d.sort asc limit ?,?;";
            }

        }

        ArrayList<FoodsBean> foodsBeans = new ArrayList<FoodsBean>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    sql,
                    new String[]{"%" + search + "%", String.valueOf(PAGE),
                            String.valueOf(NUM)});
            P.c(cursor.getCount() + "---->");
            while (cursor.moveToNext()) {
                FoodsBean bean = new FoodsBean();
                bean.setClassCode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getInt(cursor, "id"));
                bean.setOrderMinLimit(getDouble(cursor, "orderMinLimit"));
                bean.setName(getString(cursor, "name"));
                bean.setNameEn(getString(cursor, "name_en"));
                bean.setDescription(getString(cursor, "description"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPrice1(getDouble(cursor, "price1"));
                bean.setPrice2(getDouble(cursor, "price2"));
                bean.setPriceMofidy(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setCount(getDouble(cursor, "count"));
                bean.setPath(getString(cursor, "path"));
                bean.setImage(getBoolean(cursor, "w"));
                bean.setMore(getBoolean(cursor, "more"));
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                bean.setType(getString(cursor, "type"));
                bean.setChargeMode(getString(cursor, "chargeMode"));
                foodsBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        P.c("查询后" + TimeUtil.getTimeAll(System.currentTimeMillis()));
        return foodsBeans;
    }

    /**
     * 加载更多
     *
     * @param foodsBeans
     * @param search
     * @param NUM
     * @param PAGE
     */
    public void getSearchFoods(ArrayList<FoodsBean> foodsBeans, String search,
                               int NUM, int PAGE) {
        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        String sql = "";
        if (sharedUtils.getBooleanValue("is_time")) {
            sql = "select d.chargeMode,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more ,d.type  from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.help like ? and d.show=1   and d.del=0 " + goTime() + " group by d.id order by d.sort asc limit ?,?;";

        } else {
            sql = "select d.chargeMode,d.id,d.classcode,d.code,d.name,d.name_en,d.description,d.help,d.unit,d.price,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,case when dt.count>0 then dt.count else 0 end count, min(i.path) as path,i.w is not null as w,case when dt.more==1 then 1 else 0 end more ,d.type  from dish as d left join images as i on d.code =i.code and i.del=0 left join dish_table as dt on dt.code=d.code where d.help like ? and d.show=1   and d.del=0 group by d.id order by d.sort asc limit ?,?;";

        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    sql,
                    new String[]{search, String.valueOf(PAGE),
                            String.valueOf(NUM)});
            while (cursor.moveToNext()) {
                FoodsBean bean = new FoodsBean();
                bean.setClassCode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getInt(cursor, "id"));
                bean.setName(getString(cursor, "name"));
                bean.setNameEn(getString(cursor, "name_en"));
                bean.setDescription(getString(cursor, "description"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPriceMofidy(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setCount(getDouble(cursor, "count"));
                bean.setPath(getString(cursor, "path"));
                bean.setImage(getBoolean(cursor, "w"));
                bean.setMore(getBoolean(cursor, "more"));
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                bean.setType(getString(cursor, "type"));
                bean.setChargeMode(getString(cursor, "chargeMode"));
                foodsBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        P.c("查询后" + TimeUtil.getTimeAll(System.currentTimeMillis()));
    }

    public Map<String, String> getMustSel(String menuCode) {
        String sql = "select menucode,cookcls,require from cook_lit where del=0 and menucode=?";
        Cursor cursor = null;
        int count = 0;
        Map<String, String> map = new HashMap<String, String>();
        try {
            cursor = db.rawQuery(sql, new String[]{menuCode});
            while (cursor.moveToNext()) {
                if (getBoolean(cursor, "require")) {
                    map.put(getString(cursor, "cookcls"), "");
                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return map;

    }

    /**
     * 做法菜单
     *
     * @param id
     * @return
     */
    public ArrayList<ResonMenuBean> getResonMenuBeans(ArrayList<ResonMenuBean> resonMenuBeans, String code, Map<String, String> mustMap) {
        String is = "select count(*) from cook where code=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(is, new String[]{});
            while (cursor.moveToNext()) {
                //
                if (!cursor.isNull(0)) {
                    // 基本菜品
                    ResonMenuBean bean = new ResonMenuBean();
                    bean.setCode("-1");
                    bean.setNameEn("special for this one");
                    bean.setName("本菜特有");
//                    bean.setName("本菜特有");
                    bean.setMultySelect(true);
                    bean.setMustSelect(false);
                    resonMenuBeans.add(bean);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        //-----------------
//-------------------------------
        Cursor cursor2 = null;
        String codeName = "";
        try {
            String sql2 = "";
            StringBuffer s = new StringBuffer("");
            boolean require = false;
            sql2 = "select * from cook_lit where menucode =?";
            cursor2 = db.rawQuery(sql2, new String[]{code});
            while (cursor2.moveToNext()) {
                //基本菜品
                ResonMenuBean bean = new ResonMenuBean();
                codeName = getString(cursor2, "cookcls");
                bean.setCode(codeName);
                require = getBoolean(cursor2, "require");
                if (require) {//setMustSelect
                    bean.setMultySelect(true);
                    bean.setMustSelect(true);
                } else {
                    bean.setMultySelect(false);
                    bean.setMustSelect(false);
                }
                String sql = "select name,description,MultySelect from cook_class where code =?";
                Cursor cursor1 = null;
                try {
                    cursor1 = db.rawQuery(sql, new String[]{codeName});
                    while (cursor1.moveToNext()) {
                        String name = getString(cursor1, "name");
                        String nameEn = getString(cursor1, "description");
                        bean.setName(name);
                        bean.setNameEn(nameEn);
                        bean.setMultySelect(getBoolean(cursor1, "MultySelect"));
                        if (mustMap.containsKey(bean.getCode())) {
                            bean.setMustSel(true);
                        }
                        resonMenuBeans.add(bean);
                        String is1 = bean.toString();
                        cursor1.close();
                    }
                } catch (Exception e) {
                    P.c(e.getLocalizedMessage());
                } finally {
                    if (cursor1 != null) {
                        cursor1.close();
                    }
                }
            }
            cursor2.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor2 != null) {
                cursor2.close();
            }
        }
        return resonMenuBeans;
    }

    /**
     * 做法菜单
     *
     * @param id
     * @return
     */
    /*public ArrayList<ResonMenuBean> getResonMenuBeans(	ArrayList<ResonMenuBean> resonMenuBeans,String code,Map<String,String> mustMap){
        String is = "select count(*) from cook where code=?";
        Cursor cursor = null;
        try{
            cursor = db.rawQuery(is, new String[]{});
            while(cursor.moveToNext()){
                //
                if (!cursor.isNull(0)) {
                    // 基本菜品
                    ResonMenuBean bean = new ResonMenuBean();
                    bean.setCode("-1");
                    bean.setName("本菜特有");
                    bean.setMultySelect(true);
                    resonMenuBeans.add(bean);
                }
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        //-----------------

        String sql = "select code,name,MultySelect from cook_class order by sort";
        Cursor cursor1 = null;
        try{
            cursor1 = db.rawQuery(sql, new String[]{});
            while(cursor1.moveToNext()){
                //
                if(!cursor1.isNull(0)){
                    //基本菜品
                    ResonMenuBean bean = new ResonMenuBean();
                    bean.setCode(getString(cursor1, "code"));
                    bean.setName(getString(cursor1, "name"));
                    bean.setMultySelect(getBoolean(cursor1,"MultySelect"));
                    if(mustMap.containsKey(bean.getCode())){
                        bean.setMustSel(true);
                    }
                    resonMenuBeans.add(bean);
                }
            }
            cursor1.close();
        }catch(Exception e){

        }finally{
            if(cursor1!=null){
                cursor1.close();
            }
        }
        return resonMenuBeans;
    }*/

    /**
     * 获取桌台区域菜单
     *
     * @return
     */
    public ArrayList<TablesBean> getTablesAreas(
            ArrayList<TablesBean> tablesAreas, String site) {
        // select code,name from area order by sort
        String sql = "select code,name from area where del=0 and site=? order by sort ";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{site});
            if (tablesAreas != null) {
                tablesAreas.clear();
            }
            while (cursor.moveToNext()) {
                //
                if (!cursor.isNull(0)) {
                    // 基本菜品
                    TablesBean bean = new TablesBean();
                    bean.setCode(getString(cursor, "code"));
                    bean.setName(getString(cursor, "name"));
                    tablesAreas.add(bean);
                }
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return tablesAreas;
    }

    /**
     * 获取营业点
     *
     * @return
     */
    public ArrayList<TablesBean> getTablesSites(
            ArrayList<TablesBean> tablesAreas) {
        // select code,name from area order by sort
        String sql = "select code,name from site where del=0 order by sort ";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                //
                if (!cursor.isNull(0)) {
                    // 基本菜品
                    TablesBean bean = new TablesBean();
                    bean.setCode(getString(cursor, "code"));
                    bean.setName(getString(cursor, "name"));
                    tablesAreas.add(bean);
                }
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return tablesAreas;
    }

    /**
     * 根据区域获得桌台号
     *
     * @return
     */

//    http://192.168.1.118/DataService.svc/GetRestTable
    public ArrayList<TableBean> getTableCodeBeans(String code) {
        ArrayList<TableBean> tableBeans = new ArrayList<TableBean>();
        String sql = "select b.code,b.name,b.max,b.locked,t.name as typeName,t.code as typeCode,t.extra,tp.tablecode FROM tableType as t inner JOIN board as b ON t.code = b.table_class   left join temp_table as tp on tp.tablecode=b.code where b.area=? and b.del=0 group by b.code";
//        select ID, '' as State, Code as TableNo, '', Name from[dbo].[NTORestTable]as a union all
//        (select ID, State, TableNo, OrderSymbol, '' from[dbo].[NTORestOrder]as b where b.State = 'I'
//        and b.OrderSymbol<> '')


//        String sql = "select m.mcode,d.name,m.gcode,m.scode,m.num,m.price,m.require,min(i.path) as path from mgdetail as m   " +
//                "left join dish as d on d.code=m.scode and d.del=0 LEFT  JOIN images i on m.scode = i.code where m.gcode=? group by m.scode;";

//        String sql = "select b.code,b.name,b.max,b.locked from board as b where area=? and del=0 left join order_table as o on b.code= o.tableNo";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{code});
            while (cursor.moveToNext()) {
                TableBean bean = new TableBean();
                bean.setCode(getString(cursor, "code"));
                bean.setName(getString(cursor, "name"));
                bean.setMax(getInt(cursor, "max"));
                bean.setLocked(getBoolean(cursor, "locked"));
                bean.setTypeCode(getString(cursor, "typeCode"));
                bean.setTypeName(getString(cursor, "typeName"));
                bean.setPriceType(getString(cursor, "extra"));
                P.c(cursor.isNull(7)+"~~~~"+bean.getCode());
                if(sharedUtils.getBooleanValue("is_gua")){
                    if(cursor.isNull(7)){
                        bean.setTemp(false);
                    }else {

                        bean.setTemp(true);
                    }
                }else{
                    bean.setTemp(false);
                }

                tableBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return tableBeans;
    }

    public TableBean getTableCodeInfo(String code) {
        ArrayList<TableBean> tableBeans = new ArrayList<TableBean>();
        String sql = "select b.code,b.name,b.max,b.locked,t.name as typeName,t.code as typeCode,t.extra FROM tableType as t inner JOIN board as b ON t.code = b.table_class where b.code =?";
        Cursor cursor = null;
        TableBean bean = new TableBean();
        try {
            cursor = db.rawQuery(sql, new String[]{code});
            while (cursor.moveToNext()) {
                bean.setCode(getString(cursor, "code"));
                bean.setName(getString(cursor, "name"));
                bean.setMax(getInt(cursor, "max"));
                bean.setLocked(getBoolean(cursor, "locked"));
                bean.setTypeCode(getString(cursor, "typeCode"));
                bean.setTypeName(getString(cursor, "typeName"));
                bean.setPriceType(getString(cursor, "extra"));
                tableBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return bean;

    }

    /**
     * 挂单
     * @param tableCode
     */
    public void copyTotemp(String tableCode){

        String sql = "insert into temp_table  select *,? from dish_table ";
        db.execSQL(sql,new Object[]{tableCode});
        clear();

    }

    /**
     * 还原表
     *
     */
    public void resetToDish(String tableCode){
        //还原表

            String sql = "insert into dish_table  select i,code,name,name_en,unit,price,price1,price2,more,price_modify,weigh,discount,temp,suit,count,cook_codes,cook_names,cook_prices,remark,details,detailNames,flag,lt,mst,must_value,listStr,extra,tc_cook_codes,tc_cook_names,tc_cook_prices,tcListStr,tcMustNum,tcActualNum,orderMinLimit,isEditPrice,isjj from temp_table where tablecode=?";
        db.execSQL(sql,new Object[]{tableCode});
        //清除
        String delte = "delete from temp_table where tablecode=?";
           db.execSQL(delte,new Object[]{tableCode});

    }



    /**
     * 添加菜品到购物篮【没有附加条件的添加】 cook_codes cook_names cook_prices
     *
     * @param bean
     */
    public boolean addDishToPad(FoodsBean bean, ArrayList<ReasonBean> resons,
                                String remark, String details, String detailNames, String num, Handler handler) {
        if (Common.guSttxKeys.containsKey(bean.getCode())) {
            String strNum = Common.guSttxKeys.get(bean.getCode());
            if (strNum.equals("0.0")) {
                handler.sendEmptyMessage(112);
                return false;
            }
        }


        StringBuilder builder_code = new StringBuilder();
        StringBuilder builder_name = new StringBuilder();
        StringBuilder builder_price = new StringBuilder();
        if (resons != null) {
            // 对按code进行排序
            Collections.sort(resons, new Comparator<ReasonBean>() {
                @Override
                public int compare(ReasonBean rb0, ReasonBean rb1) {
                    String code0 = rb0.getCode();
                    String code1 = rb1.getCode();
                    // 可以按reasoncode对象的其他属性排序，只要属性支持compareTo方法
                    return code0.compareTo(code1);
                }
            });
            int len = resons.size();
            for (int i = 0; i < resons.size(); i++) {
                ReasonBean rb = resons.get(i);
                if (i == len - 1) {
                    builder_code.append(rb.getCode());
                    builder_name.append(rb.getName());
                    builder_price.append(rb.getPrice());

                } else {
                    builder_code.append(rb.getCode() + ",");
                    builder_name.append(rb.getName() + ",");
                    builder_price.append(rb.getPrice() + ",");
                }
            }
        }
        //--------------------------------------------套餐加入builder
        String s1 = "";
        String s2 = "";
        String s3 = "";
        if (resons == null) {
            StringBuilder builder1 = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();
            StringBuilder builder3 = new StringBuilder();
            builder1.append("|");
            builder2.append("|");
            builder3.append("|");
            s1 = builder1.toString();
            s2 = builder2.toString();
            s3 = builder3.toString();
        }
        //-----------------------判断套餐
        boolean isCanSelect = true;
        int value = 0;
        if (!"".equals(details)) {
//            StringBuffer s = new StringBuffer(details + "," + bean.getCode());
            //先不算主菜的code必选
            StringBuffer s = new StringBuffer(details);
            final String tps[] = s.toString().split(",");
            int plus = 0;
            int le = tps.length;
            for (int j = 0; j < le; j++) {
                String codeDetail = tps[j];
                Cursor cursor2 = null;
                try {
                    String sql2 = "";
                    sql2 = "select * from cook_lit where menucode =?";
                    cursor2 = db.rawQuery(sql2, new String[]{codeDetail});
                    while (cursor2.moveToNext()) {
                        boolean require = getBoolean(cursor2, "require");
                        if (require) {
                            plus++;
                        }
                    }
                    cursor2.close();
                } catch (Exception e) {
                    P.c("测试");
                } finally {
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                }
            }
            value = plus;
            if (plus > 0) {
                isCanSelect = false;
            } else {
                isCanSelect = true;
            }
        } else {//非套餐
            //---------------------------------
            Cursor cursor2 = null;
            try {
                int plus = 0;
                StringBuffer s = new StringBuffer("");
                String sql2 = "";
                sql2 = "select * from cook_lit where menucode =?";
                cursor2 = db.rawQuery(sql2, new String[]{bean.getCode()});
                while (cursor2.moveToNext()) {
                    boolean require = getBoolean(cursor2, "require");
                    if (require) {
                        plus++;
                    }
                }
                if (plus > 0) {
                    isCanSelect = false;
                } else {
                    isCanSelect = true;
                }
                cursor2.close();
            } catch (Exception e) {
                P.c("测试");
            } finally {
                if (cursor2 != null) {
                    cursor2.close();
                }
            }
        }
        if(remark.length()>KouLeng){
            handler.sendEmptyMessage(-66);
            return false;
        }
        //-----------------------
        if (Un_Save(bean.getCode(), String.valueOf(bean.getPrice()), builder_code.toString(), remark, details) && canAddByC(bean.getCode(), "+") && canAddByD(bean.getCode(), "+")) {
            try {
                boolean isReq = false;
                if (resons == null) {
                    isReq = isReq(bean.getCode());
                } else {
                    if (resons.size() == 0) {
                        isReq = isReq(bean.getCode());
                    }
                }
//                db.execSQL("insert into dish_table(code,name,name_en,unit,price,more,price_modify,weigh,discount,temp,suit,count,cook_codes,cook_names,cook_prices,remark,details,detailNames,flag,mst) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{bean.getCode(),bean.getName(),"",bean.getUnit(),bean.getPrice(),bean.isRequire_cook(),bean.isPriceMofidy(),bean.isWeigh(),bean.isDiscount(),bean.isTemp(),bean.isSuit(),num,builder_code.toString(),builder_name.toString(),builder_price.toString(),remark,details,detailNames,System.currentTimeMillis(),isReq});

                db.execSQL("insert into dish_table(code,name,name_en,unit,price,price1,price2,more,price_modify,weigh,discount,temp,suit,count,cook_codes,cook_names,cook_prices,remark,details,detailNames,flag,mst,must_value,cook_codes,cook_names,cook_prices,tcMustNum,tcActualNum,orderMinLimit,isEditPrice,isjj) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{bean.getCode(), bean.getName(), bean.getNameEn(), bean.getUnit(), bean.getPrice(), bean.getPrice1(), bean.getPrice2(), bean.isRequire_cook(), bean.isPriceMofidy(), bean.isWeigh(), bean.isDiscount(), bean.isTemp(), bean.isSuit(), num, builder_code.toString(), builder_name.toString(), builder_price.toString(), remark, details, detailNames, System.currentTimeMillis(), isReq, isCanSelect, s1, s2, s3, value, value, bean.getOrderMinLimit(), false,false});

            } catch (Exception e) {
                P.c(e.getMessage());
                return false;
            }
            return true;
        } else {
            if (handler != null) {
                handler.sendEmptyMessage(-6);
            }
        }
        return false;
    }

    public boolean isReq(String menuCode) {
        String sql = "select sum(require) require from cook_lit  where del=0 and menucode=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{menuCode});
            int count = cursor.getCount();
            if (count != 0 && cursor.moveToFirst()) {
                int rt = getInt(cursor, "require");
                cursor.close();
                if (rt == 0) {
                    return false;
                } else {
                    return true;
                }


            } else {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            P.c("异常C");
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return false;
    }

    /**
     * 限制
     */
    public boolean canAddByC(String code, String type) {//单次点单上限
        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        if (sharedUtils.getBooleanValue("is_limit")) {
            String sql = "select case when ltnum==0 then 0  when (ltnum!=0 and (?)>ltnum) then 0 else 1  end result from lt where code=? and ltype='C' and lt=1";
            int sum = lnum(code);
            P.c("C数量" + sum);

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, new String[]{type.equals("+") ? String.valueOf(sum + 1) : String.valueOf(sum - 1), code});
                int count = cursor.getCount();
                P.c("C处" + count);
                if (count != 0 && cursor.moveToFirst()) {//不为0
                    boolean rt = getBoolean(cursor, "result");
                    cursor.close();
                    return rt;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                P.c("异常C");
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return true;
    }

    private int lnum(String code) {
        String sql = "select case when sum(count) is null then 0 else sum(count) end sum from dish_table where code=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{code});
            if (cursor.moveToFirst()) {
                int rt = getInt(cursor, "sum");
                cursor.close();
                return rt;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            P.c("异常lnum");
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return 0;
    }

    /**
     * 限制,本机计算
     */
    public boolean canAddByD(String code, String type) {
        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        if (sharedUtils.getBooleanValue("is_limit")) {
            String sql = "select case when ltmax==0 then 0  when (ltmax!=0 and (?)>ltmax) then 0 else 1  end result from lt where code=? and ltype='D' and lt=1";
            int sum = lnum(code);
            P.c("D数量" + sum);
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, new String[]{type.equals("+") ? String.valueOf(sum + 1) : String.valueOf(sum - 1), code});
                int count = cursor.getCount();
                if (count != 0 && cursor.moveToFirst()) {
                    boolean rt = getBoolean(cursor, "result");
                    P.c(rt + "的值");
                    cursor.close();
                    return rt;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                P.c("异常D");
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return true;
    }

    /**
     * 限制,下单计算时,返回-1证明通过否则就是超过范围
     */
    public int canAddByBill(double sum, String code) {
        SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
        if (sharedUtils.getBooleanValue("is_limit")) {
            String sql = "select case when ltmax==0 then 0  when (ltmax!=0 and (?)>ltmax) then 0 else 1  end result,ltmax from lt where code=? and ltype='D' and lt=1";
            P.c("D数量" + sum);
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, new String[]{String.valueOf(sum), code});
                int count = cursor.getCount();
                if (count != 0 && cursor.moveToFirst()) {
                    boolean rt = getBoolean(cursor, "result");
                    if (rt) {
                        cursor.close();
                        return -1;
                    } else {
                        int ret = getInt(cursor, "ltmax");
                        cursor.close();
                        return ret;
                    }
                } else {
                    return -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                P.c("异常D");
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        }
        return -1;
    }

    /**
     * 是否不存在这样id
     *
     * @param id
     * @return
     */

    public boolean Un_Save(String code, String price, String cook_codes, String remark, String details) {
        String s_ = "select * from dish_table where code=? and price=? and cook_codes=? and remark=? and details=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(s_, new String[]{code, price, cook_codes, remark, details});
            int count = cursor.getCount();
            P.c(code + "已点数量" + count);
            cursor.close();
            if (count != 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        // db.close();
        return true;
    }

    /**
     * 获得菜篮子
     *
     * @return
     */
    public ArrayList<DishTableBean> getTableBeans() {
        ArrayList<DishTableBean> dishTableBeans = new ArrayList<DishTableBean>();
        Cursor cursor = null;
        String sql = "select orderMinLimit,isEditPrice,i,code,name,name_en,unit,price,price1,price2,more,price_modify,weigh,discount,temp,suit,count,cook_codes,cook_names,cook_prices,remark,details,detailNames,flag,must_value,listStr,extra,tc_cook_codes,tc_cook_names,tc_cook_prices,case when lt is null then 0 else lt end lt,mst,isjj from dish_table";
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                DishTableBean tableBean = new DishTableBean();
                tableBean.setI(getInt(cursor, "i"));
                tableBean.setCode(getString(cursor, "code"));
                tableBean.setName(getString(cursor, "name"));
                tableBean.setNameEn(getString(cursor, "name_en"));
                tableBean.setUnit(getString(cursor, "unit"));
//                if (sharedUtils.getStringValue("tableType").equals("002")) {
//                    tableBean.setPrice(getDouble(cursor, "price1"));
//                } else if (sharedUtils.getStringValue("tableType").equals("003")) {
//                    tableBean.setPrice(getDouble(cursor, "price2"));
//                } else {
//                    tableBean.setPrice(getDouble(cursor, "price"));
//                }
                tableBean.setPrice(getDouble(cursor, "price"));
                tableBean.setPrice1(getDouble(cursor, "price1"));
                tableBean.setPrice2(getDouble(cursor, "price2"));
                tableBean.setMore(getBoolean(cursor, "more"));
                tableBean.setPrice_modify(getBoolean(cursor, "price_modify"));
                tableBean.setWeigh(getBoolean(cursor, "weigh"));
                tableBean.setDiscount(getBoolean(cursor, "discount"));
                tableBean.setTemp(getBoolean(cursor, "temp"));
                tableBean.setSuit(getBoolean(cursor, "suit"));
                tableBean.setCount(getDouble(cursor, "count"));
                String cook_codes = getString(cursor, "cook_codes");
                tableBean.setCook_codes(cook_codes);
                tableBean.setCook_names(getString(cursor, "cook_names"));
                tableBean.setCook_prices(getString(cursor, "cook_prices"));
                tableBean.setSuitMenuDetail(getString(cursor, "details"));
                tableBean.setDetailNames(getString(cursor, "detailNames"));
                tableBean.setFlag(getString(cursor, "flag"));
                tableBean.setLt(getInt(cursor, "lt"));
                String remark = getString(cursor, "remark");
                tableBean.setMst(getBoolean(cursor, "mst"));
                tableBean.setEditPrice(getBoolean(cursor, "isEditPrice"));
                tableBean.setOrderMinLimit(getDouble(cursor, "orderMinLimit"));
                tableBean.setIsjj(getBoolean(cursor,"isjj"));
                tableBean.setRemark(remark);
                ArrayList<String> remarkList = new ArrayList<String>();
                if (remark.length() != 0 && remark.contains(",")) {
                    String[] rks = remark.split(",");
                    for (int i = 0; i < rks.length; i++) {
                        remarkList.add(rks[i]);
                    }
                }
                tableBean.setRemarkList(remarkList);
                // 默认给菜篮子0数量的口味
                ArrayList<ReasonBean> reasons = new ArrayList<ReasonBean>();
                if (cook_codes.length() != 0) {
                    // 证明有做法选择
                    // P.c(cursor.getString(6));
                    if (cook_codes.contains(",")) {
                        // 价格不会出现“，”
                        String[] codes = cook_codes.split(",");
                        String[] names = getString(cursor, "cook_names").split(
                                ",");
                        String[] prices = getString(cursor, "cook_prices")
                                .split(",");
                        for (int i = 0; i < codes.length; i++) {
                            //
                            ReasonBean ba = new ReasonBean();
                            ba.setCode(codes[i]);
                            ba.setName(names[i]);
                            ba.setPrice(Double.parseDouble(prices[i]));
                            reasons.add(ba);
                        }
                    } else {
                        // 不包含,那么就只有一个
                        ReasonBean ba = new ReasonBean();
                        ba.setCode(cook_codes);
                        ba.setName(getString(cursor, "cook_names"));
                        ba.setPrice(Double.parseDouble(getString(cursor,
                                "cook_prices")));
                        reasons.add(ba);
                    }
                }
                tableBean.setReasonBeans(reasons);
                tableBean.setTc_cook_codes(getString(cursor, "tc_cook_codes"));
                tableBean.setTc_cook_names(getString(cursor, "tc_cook_names"));
                tableBean.setTc_cook_prices(getString(cursor, "tc_cook_prices"));
                // -------临时
                dishTableBeans.add(tableBean);
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
            P.c("异常问题" + e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return dishTableBeans;
    }

    private double formatOne(double total) {
        BigDecimal b = new BigDecimal(total);
        // 保留2位小数
        double total_v = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return total_v;
    }

    /* public void changePrice(DishTableBean bean) {
         String sql = "update dish_table set price = ? where i = ?";
         db.execSQL(sql, new Object[]{bean.getPrice(), bean.getI()});
     }
     public void changeName(DishTableBean bean) {
         String sql = "update dish_table set name = ? where i = ?";
         db.execSQL(sql, new Object[]{bean.getName(), bean.getI()});
     }*/
    public void changePrice(DishTableBean bean) {
        String sql = "update dish_table set price = ?,isEditPrice=? where code = ?";
        db.execSQL(sql, new Object[]{bean.getPrice(), bean.isEditPrice(), bean.getCode()});
    }

    public void changeName(DishTableBean bean) {
        String sql = "update dish_table set name = ? where code = ?";
        db.execSQL(sql, new Object[]{bean.getName(), bean.getCode()});
        P.c("改之后:" + bean.toString());
    }
    public void changeJJ(DishTableBean bean,boolean is) {
        String sql = "update dish_table set isjj = ? where i = ?";
        db.execSQL(sql, new Object[]{is, bean.getI()});

    }
    public void changeJJ(boolean is) {
        String sql = "update dish_table set isjj = ?";
        db.execSQL(sql, new Object[]{is});

    }
    public void changeNameEn(DishTableBean bean) {
        String sql = "update dish_table set name_en = ? where code = ?";
        db.execSQL(sql, new Object[]{bean.getNameEn(), bean.getCode()});
    }

    /**
     * 修改菜篮子数量
     *
     * @param num
     * @param id
     */
    public void changeNum(double count, int i, String code, String type, Handler handler) {
        boolean c = canAddByC(code, type);
        boolean d = canAddByD(code, type);
        P.c(c + "~~" + d);
        if (c && d) {
            String sql = "update dish_table set count = ? where i = ?";
            db.execSQL(sql, new Object[]{formatOne(count), i});
        } else {
            //false
            if (handler != null) {
                handler.sendEmptyMessage(-7);
            }
        }

    }

    public void getImageInfos(ArrayList<ImageInfo> imageInfos, String code) {
        String sql = "select * from images where code=? and del!=1";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{code});
            while (cursor.moveToNext()) {
                ImageInfo bean = new ImageInfo();
                bean.height = getInt(cursor, "h");
                bean.width = getInt(cursor, "w");
                bean.url = getString(cursor, "path");
                imageInfos.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }


    }


    public void changeImage(int whs[], String name) {
        String sql = "update images set w = ?,h=? where path = ?";
        db.execSQL(sql, new Object[]{whs[0], whs[1], name});
    }

    public void changelt(int lt, String code) {
        String sql = "update dish_table set lt = ? where code = ?";
        db.execSQL(sql, new Object[]{lt, code});
    }

    public void delete(int i) {
        String s_ = "delete from dish_table where i=?";
        db.execSQL(s_, new Object[]{i});
    }

    /**
     * 修改点餐界面数量
     *
     * @param num
     * @param id
     */
    public void changeNum(double num, String code, double price, String type, Handler handler) {
        boolean c = canAddByC(code, type);
        boolean d = canAddByD(code, type);
        P.c(c + "~~" + d);
        if (c && d) {
            String sql = "update dish_table set count = ? where code = ? and price = ?";
            db.execSQL(sql, new Object[]{formatOne(num), code, price});
        } else {
            if (handler != null) {
                handler.sendEmptyMessage(-7);
            }
        }
    }

    public void delete(String code, double price) {

        String s_ = "delete from dish_table where code=? and price=?";

        db.execSQL(s_, new Object[]{code, price});
    }

    /**
     * 获得此分类的和该菜品 相关的所有可选做法
     *
     * @param ba
     * @return pbc 1是公共   0是私有
     */
    public ArrayList<ReasonBean> getCpBeans(String castcode, String menucode) {
        P.c(castcode + "--" + menucode);
        ArrayList<ReasonBean> rs = new ArrayList<ReasonBean>();
        Cursor cursor = null;
        try {
            String sql = "";
            if ("-1".equals(castcode)) {

                sql = "select code,name,description,case when public==1 then price else (select price from menu_cook where menucode=? and cookcode=code) end price from cook where  code in (select cookcode from menu_cook where menucode=?)";
                cursor = db.rawQuery(sql, new String[]{menucode, menucode});

            } else {
                sql = "select code,name,description,case when public==1 then price else (select price from menu_cook where menucode=? and cookcode=code) end price from cook where public=1 and cookclass=?";
                cursor = db.rawQuery(sql, new String[]{menucode, castcode});
            }
            P.c(sql);
            while (cursor.moveToNext()) {
                //增加菜品做法
                ReasonBean bean = new ReasonBean();
                bean.setCode(getString(cursor, "code"));
                bean.setName(getString(cursor, "name"));
                bean.setNameEn(getString(cursor, "description"));
                bean.setPrice(getDouble(cursor, "price"));
                rs.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            P.c("测试");
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return rs;
    }

    //select code  from cook where public=1 and cookclass in ('001','0')
    public ArrayList<String> getUnSel(String buld) {
        P.c(".." + buld);
//    '0515','0508','0572'
        ArrayList<String> res = new ArrayList<String>();
        String sql = "select c.cookclass from cook as c where c.code in (" + buld + ")";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, null);
            P.c("长度" + cursor.getCount());
            while (cursor.moveToNext()) {
                res.add(getString(cursor, "cookclass"));
                P.c("点选的类" + getString(cursor, "cookclass"));

            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }

        }
        String ss = res.toString();
        int ss1 = res.size();
        return res;


    }
//select cl.name from cook_class as cl where cl.code = (select c.cookclass from cook as c where c.code='0001')


    /**
     * 更新数据的规格参数
     *
     * @param bean
     * @param id
     */
    public void updateDishTable(ArrayList<ReasonBean> bean, DishTableBean tb, String numView,Handler handler) {

        String sql = "update dish_table set cook_codes=?,cook_names=?,cook_prices=?,count=? ,mst=0 where i = ?";
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        StringBuilder builder3 = new StringBuilder();
        int size = bean.size();
        for (int i = 0; i < size; i++) {
            if (i != size - 1) {
                builder1.append(bean.get(i).getCode() + ",");
                builder2.append(bean.get(i).getName() + ",");
                builder3.append(bean.get(i).getPrice() + ",");
            } else {
                builder1.append(bean.get(i).getCode());
                builder2.append(bean.get(i).getName());
                builder3.append(bean.get(i).getPrice());
            }
        }
        if(handler!=null){
            if(builder1.toString().length()>KouLeng){
                handler.sendEmptyMessage(-66);
                return;
            }
        }
        db.execSQL(sql, new Object[]{builder1.toString(),
                builder2.toString(), builder3.toString(), numView, tb.getI()});
    }
    int KouLeng = 50;
    /**
     * 更新数据的规格参数  套餐列表更新  更新dish_table表,更新resmap数据
     *
     * @param bean
     * @param id
     */
    //这里有注释代码
    public void updateDishTable(ArrayList<ReasonBean> bean, DishTableBean tb, HashMap<String, ArrayList<ReasonBean>> tcBeans, HashMap<String, String> tcListStr, int position, int tcMustNum) {
        StringBuilder builder1 = new StringBuilder("");
        StringBuilder builder2 = new StringBuilder("");
        StringBuilder builder3 = new StringBuilder("");
        StringBuilder builder4 = new StringBuilder("");
        String sql = "update dish_table set tc_cook_codes=?,tc_cook_names=?,tc_cook_prices=? ,tcListStr = ?,must_value=? ,tcActualNum = ?,mst = ? where i = ?";
        //第几个index 第一个就
        Set<String> keysTc = tcBeans.keySet();
        Set<String> keysTcList = tcListStr.keySet();
        ArrayList<String> keys = new ArrayList<String>();
        for (int i = 0; i < tb.getSuitMenuDetail().split(",").length; i++) {
            P.c(i + "bean" + tb.getSuitMenuDetail().split(",")[i]);
            keys.add(tb.getSuitMenuDetail().split(",")[i]);
        }
        int coun = 0;
        int count1 = 0;
        if (keysTc.size() != 0) {

            do {
                if (tcBeans.containsKey(keys.get(coun))) {
                    P.c(keys.get(coun) + "cunzai" + tcBeans.containsKey(keys.get(coun)));

//                        P.c("点选"+ tcBeans.get(keys.get(coun)).toString());


                    ArrayList value = tcBeans.get(keys.get(coun));

                    P.c("子项:" + keys.get(coun) + "=====" + (value == null ? "空" : value.hashCode()));
                    //----------------------------
                    if (!builder1.toString().contains(keys.get(coun))) {
                        builder1.append(keys.get(coun) + "|");
                        builder2.append(keys.get(coun) + "|");
                        builder3.append(keys.get(coun) + "|");
                    }
                    ///////////////////////////////套餐的操作
                    ArrayList<ReasonBean> listRb = tcBeans.get(keys.get(coun));
                    if (listRb == null && count1 != tcBeans.size()) {
                        builder1.append(";");
                        builder2.append(";");
                        builder3.append(";");
                    } else {
                        if (listRb != null && listRb.size() != 0) {
                            for (int j = 0; j < listRb.size(); j++) {//变成4了
                                if (j == listRb.size() - 1 && count1 != tcBeans.size()) {
                                    builder1.append(listRb.get(j).getCode() + ";");
                                    builder2.append(listRb.get(j).getName() + ";");
                                    builder3.append(listRb.get(j).getPrice() + ";");
                                } else {
                                    builder1.append(listRb.get(j).getCode() + ",");
                                    builder2.append(listRb.get(j).getName() + ",");
                                    builder3.append(listRb.get(j).getPrice() + ",");
                                }
                            }
                        }
                    }
                    //----------------------------
                } else {


                }
                coun++;
            } while (keysTc.size() != coun);

        }
        //////////////////////////////////liststr
        int coun2 = 0;
        int count3 = 0;
        if (keysTcList.size() != 0) {

            do {
                if (tcListStr.containsKey(keys.get(coun2))) {
                    P.c(keys.get(coun2) + "cunzai" + tcListStr.containsKey(keys.get(coun2)));

                    String value = tcListStr.get(keys.get(coun2));

//                    P.c("子项:" + keys.get(coun2) + "=====" + (value == null ? "空" : value.hashCode()));
                    //----------------------------
                    if (!builder4.toString().contains(keys.get(coun2))) {
                        builder4.append(keys.get(coun2) + "|");
                    }
                    //liststr必选的操作
                    String tclistStr = tcListStr.get(keys.get(coun2));
                    if (tclistStr == null && count3 != tcListStr.size()) {
                        builder4.append(";");
                    } else {
//                        02502|3,2,;02617|;00306|;
                        builder4.append(tclistStr + ";");
                    }
                } else {


                }
                coun2++;
            } while (keysTcList.size() != coun2);

        }
        String s2 = builder2.toString();
        String s4 = builder4.toString();
        //计算已选必选做法的数量
        int sTc = 0;
        int mst = 0;
        boolean isCan = true;
        if (tcListStr.size() != 0) {
            Set<String> keysTcList1 = tcListStr.keySet();
            Iterator<String> itTcList = keysTcList1.iterator();
            while (itTcList.hasNext()) {
                String key = itTcList.next();
                String str = tcListStr.get(key);
                int len = 0;
                if (str == null || "".equals(str)) {
                    len = 0;
                } else {
                    len = str.split(",").length;

                }
                sTc = sTc + len;
            }
        }
        //两个都是0
        int ssss = sTc;
        int ssssss = tcMustNum;
        if (sTc >= tcMustNum) {
            isCan = true;
            mst = 0;
        } else {
            isCan = false;
            mst = 1;
        }
        db.execSQL(sql, new Object[]{builder1.toString(),
                builder2.toString(), builder3.toString(), builder4.toString(), isCan, tcMustNum, mst, tb.getI()});
        String s24 = builder2.toString();
        String s43 = builder4.toString();
    }


    public void updateDishInit(DishTableBean tb) {
        String sql = "update dish_table set cook_codes='',cook_names='',cook_prices='',mst=0 where i=?";
        db.execSQL(sql, new Object[]{tb.getI()});
    }

    public void clearAll() {
        db.beginTransaction();
        db.execSQL("delete from dish");
        db.execSQL("delete from images");
        db.execSQL("delete from remark");
        db.execSQL("delete from site");
        db.execSQL("delete from area");
        db.execSQL("delete from board");
        db.execSQL("delete from menu");
        db.execSQL("delete from cook");
        db.execSQL("delete from cook_class");
        db.execSQL("delete from cook_lit");
        db.execSQL("delete from menu_cook");
        db.execSQL("delete from user");
        db.execSQL("delete from detail");
        db.execSQL("delete from dish_table");
        db.execSQL("delete from lt");
        db.execSQL("delete from mgroup");
        db.execSQL("delete from mgdetail");
        db.execSQL("delete from tcReason");
        db.execSQL("delete from tableType");

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void clear() {
        String sql = "delete from dish_table";
        db.execSQL(sql);
    }

    public void clear(String tableName[]) {
        db.beginTransaction();
        for (int i = 0; i < tableName.length; i++) {
            db.execSQL("delete from " + tableName[i]);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    /********************************套餐**************************************************/

    /**
     * 查找套餐
     * 必选
     * select d.code,d.number,d.price,c.name from detail d INNER JOIN dish c on d.suitcode = 51001 and c.del = 0 and  d.require = 1 and d.code = c.code
     * 可选
     * select d.code,d.number,d.price,c.name from detail d INNER JOIN dish c on d.suitcode = 51001 and c.del = 0 and  d.require = 0 and d.code = c.code
     */
    public ArrayList<TcBean> getTc(String foodCode, boolean isNeed) {
        ArrayList<TcBean> tcBeans = new ArrayList<TcBean>();
        String sql;
        if (isNeed) {
            sql = "select d.code,d.number,d.price,c.name,min(m.path) as path from detail d " +
                    "INNER JOIN dish c on d.suitcode = ? and c.del = 0 and  d.require = 1 and d.code = c.code " +
                    "LEFT OUTER JOIN images m on d.code = m.code group by d.code;";
        } else {
            sql = "select d.code,d.number,d.price,c.name,min(m.path) as path from detail d " +
                    "INNER JOIN dish c on d.suitcode = ? and c.del = 0 and  d.require = 0 and d.code = c.code " +
                    "LEFT OUTER JOIN images m on d.code = m.code group by d.code;";
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{foodCode});
            while (cursor.moveToNext()) {
                TcBean tcBean = new TcBean();
                tcBean.setCode(getString(cursor, "code"));
                tcBean.setName(getString(cursor, "name"));
                tcBean.setNum(getInt(cursor, "number"));
                tcBean.setPrice(getDouble(cursor, "price"));
                tcBean.setPath(getString(cursor, "path") == null ? "" : getString(cursor, "path"));
                tcBeans.add(tcBean);
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return tcBeans;
    }

    public ArrayList<TcBean> getSuitItems(String foodCode) {
        ArrayList<TcBean> tcBeans = new ArrayList<TcBean>();
        String sql = "select gcode,gname ,max_num,min_num from mgroup where mcode=? order by sort";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{foodCode});
            while (cursor.moveToNext()) {
                TcBean tcBean = new TcBean();
                tcBean.setCode(getString(cursor, "gcode"));
                tcBean.setName(getString(cursor, "gname"));
                tcBean.setMax(getInt(cursor, "max_num"));
                tcBean.setMin(getInt(cursor, "min_num"));
                tcBeans.add(tcBean);
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return tcBeans;
    }

    /**
     * 组合套餐
     */

    public ArrayList<TcBean> getTc(String foodCode, String chargeMode) {
        ArrayList<TcBean> tcBeans = new ArrayList<TcBean>();
        String sql = "select m.mcode,d.name,m.gcode,m.scode,m.num,m.price,m.require,min(i.path) as path from mgdetail as m   left join dish as d on d.code=m.scode and d.del=0 LEFT  JOIN images i on m.scode = i.code where m.gcode=? group by m.scode;";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{foodCode});
            while (cursor.moveToNext()) {
                TcBean tcBean = new TcBean();
                tcBean.setCode(getString(cursor, "scode"));
                tcBean.setName(getString(cursor, "name"));
                tcBean.setNum(getInt(cursor, "num"));
                tcBean.setPrice(getDouble(cursor, "price"));
                tcBean.setPath(getString(cursor, "path") == null ? "" : getString(cursor, "path"));
                String s = tcBean.toString();
                tcBeans.add(tcBean);
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return tcBeans;
    }

    /**
     * 获得数据
     *
     * @return
     */
    public ArrayList<TBean> getTbeans() {
        ArrayList<TBean> tcBeans = new ArrayList<TBean>();
        String sql = "select code,name from tcreason order by sort";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                TBean tBean = new TBean();
                tBean.setCode(getString(cursor, "code"));
                tBean.setName(getString(cursor, "name"));
                tcBeans.add(tBean);
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return tcBeans;
    }


    // ---------------------------
    // ---------------------------基本版-----------------------------------------------------------------------------------------------------
    // ---------------------------

    /**
     * 获取数据版本
     *
     * @return
     */
    public String getVersion() {
        String sql = "SELECT DateVal FROM QuanJuCanShu WHERE Name = 'BaseDataVer'";
        Cursor cursor = null;
        String version = "";
        try {
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
            if (count == 1) {
                while (cursor.moveToNext()) {
                    version = cursor.getString(0);
                }
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return version;
    }

    /**
     * 菜品数量
     *
     * @return
     */
    public int getCount() {
        int i = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from dish_table", null);
            i = cursor.getCount();
            cursor.close();
        } catch (Exception e) {
            Log.v(TAG, "查询不到已点菜单数量");
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return i;
    }

    public ArrayList<FoodsBean> getMnDishs(String id) {
        ArrayList<FoodsBean> foodsBeans = new ArrayList<FoodsBean>();
        String sql = "SELECT  b.pid,b.caipingid,b.caipingname,b.jiage,b.morendanwei FROM  MN_CaiAndSubCai as a inner  JOIN   caiping as b  ON a.Subcai = b.PID    where a.maincai=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{id});
            while (cursor.moveToNext()) {
                //
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return foodsBeans;
    }

    /**
     * 获得多规格code
     *
     * @param id
     * @return
     */
    public Map<String, ReasonBean> getSelectedRessons(int index) {
        String sql = "select cook_codes,cook_names,cook_prices from dish_table where i=?";
        Cursor cursor = null;
        Map<String, ReasonBean> resMap = new HashMap<String, ReasonBean>();
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(index)});
            while (cursor.moveToNext()) {
                if (cursor.isNull(0)) {

                } else {
                    // 不等于空就进行解析处理
                    String[] codes = cursor.getString(0).split(",");
                    String[] names = cursor.getString(1).split(",");
                    String[] prices = cursor.getString(2).split(",");
                    for (int i = 0; i < codes.length; i++) {
                        ReasonBean bean = new ReasonBean();
                        bean.setName(names[i]);
                        bean.setCode(codes[i]);
                        bean.setPrice(Double.parseDouble(prices[i]));
                        resMap.put(bean.getCode(), bean);
                    }

                }

            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return resMap;
    }

    // ----------------------------------------------------------------------
    // ---------------------------

	/*
     * public void insert(FouceBean buyObj) { String sql =
	 * "insert into dish_table values(?,?,?,?,?,?,?,?,?)"; if
	 * (Un_Save(buyObj.getId())) { db.execSQL(sql, new Object[] {
	 * buyObj.getId(), buyObj.getName(), 1, buyObj.getPrice(), buyObj.getCode()
	 * ,buyObj.getReasons().size()==0?false:true,"","",""}); } else { // } }
	 */

    /**
     * 获得已点菜品列表
     *
     * @return
     */
    public ArrayList<FouceBean> getDishList() {
        Cursor cursor = null;
        ArrayList<FouceBean> fouceBeans = new ArrayList<FouceBean>();
        try {
            cursor = db.rawQuery("select * from dish_table", null);

            while (cursor.moveToNext()) {
                FouceBean bean = new FouceBean();
                bean.setId(cursor.getString(0));
                bean.setName(cursor.getString(1));
                bean.setNum(cursor.getInt(2));
                bean.setPrice(cursor.getDouble(3));
                bean.setCode(cursor.getString(4));
                bean.setRes(cursor.getInt(5) == 1 ? true : false);
                ArrayList<ReasonBean> reasons = new ArrayList<ReasonBean>();

                if (cursor.getString(6).length() != 0
                        && cursor.getString(8).length() != 0) {
                    // P.c(cursor.getString(6));
                    if (cursor.getString(8).contains(",")) {
                        // 价格不会出现“，”
                        String[] codes = cursor.getString(6).split(",");
                        String[] names = cursor.getString(7).split(",");
                        String[] prices = cursor.getString(8).split(",");
                        for (int i = 0; i < codes.length; i++) {
                            //
                            ReasonBean ba = new ReasonBean();
                            ba.setCode(codes[i]);
                            ba.setName(names[i]);
                            ba.setPrice(Double.parseDouble(prices[i]));
                            reasons.add(ba);
                        }
                    } else {
                        // 不包含,那么就只有一个
                        ReasonBean ba = new ReasonBean();
                        ba.setCode(cursor.getString(6));
                        ba.setName(cursor.getString(7));
                        ba.setPrice(Double.parseDouble(cursor.getString(8)));
                        reasons.add(ba);
                    }
                }
                bean.setReasons(reasons);
                fouceBeans.add(bean);
            }

            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return fouceBeans;
    }


    /**
     * 对菜品的操作
     */
    public boolean Un_SaveDish(String id, String coords) {
        Cursor cursor = null;
        String s_ = "select * from dish where id=? and coords=?";
        try {
            cursor = db.rawQuery(s_, new String[]{id});
            int count = cursor.getCount();
            cursor.close();
            if (count != 0) {
                return false;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        // db.close();
        return true;
    }

    /**
     * 是否存在当前无菜品页
     *
     * @param page
     * @return
     */
    public boolean Un_SaveDishNo(int page) {
        Cursor cursor = null;
        String s_ = "select * from dish where page=?";
        try {
            cursor = db.rawQuery(s_, new String[]{String.valueOf(page)});
            int count = cursor.getCount();
            cursor.close();
            if (count != 0) {
                return false;
            }
            // db.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return true;
    }

    /**
     * 添加菜品
     *
     * @param dish
     */
    public void insertDish(Dish dish) {
        String sql = "insert into dish values(?,?,?,?,?,?,?,?,?,?,?,?)";
        if (Un_SaveDish(dish.getId(), dish.getCoords())) {
            db.execSQL(
                    sql,
                    new Object[]{dish.getId(), dish.getName(),
                            dish.getCode(), dish.getType(), dish.getUnit(),
                            dish.getCoords(), dish.getPrice(), dish.getPage(),
                            dish.getShortCode(), dish.getReasonCode(),
                            dish.getReasonName(), dish.getReasonPrice()});
        }
    }

    /**
     * 获取菜品数量
     *
     * @return
     */
    public int getDishCount() {
        int count = 0;
        Cursor cursor = null;
        String sql = "select page from dish group by page";
        try {
            cursor = db.rawQuery(sql, null);
            count = cursor.getCount();
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return count;
    }

    /**
     * 保存空数据页码
     *
     * @param dish
     */
    public void insertDishNo(Dish dish) {
        String sql = "insert into dish(page) values(?)";
        if (Un_SaveDishNo(dish.getPage())) {
            db.execSQL(sql, new Object[]{dish.getPage()});
        }
    }

    /**
     * 清除菜品
     */
    public void clearDish() {
        String sql = "delete from dish";
        db.execSQL(sql);
    }

    public ArrayList<FlipBean> getDishsToFlip(String imageNames[]) {
        ArrayList<FlipBean> flipBeans = new ArrayList<FlipBean>();
        String sql = "select page from dish group by page";
        ArrayList<Integer> pages = new ArrayList<Integer>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                pages.add(cursor.getInt(0));
                // 获得页数
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        // 根据页数来获取每页的操作
        String sql_dish = "select * from dish where page=?";
        for (int i = 0; i < pages.size(); i++) {
            Cursor dishCursor = null;
            try {
                dishCursor = db.rawQuery(sql_dish,
                        new String[]{String.valueOf(pages.get(i))});
                FlipBean flipBean = new FlipBean();
                ArrayList<FouceBean> fouceBeans = new ArrayList<FouceBean>();
                while (dishCursor.moveToNext()) {
                    if (!dishCursor.isNull(0)) {
                        FouceBean fb = new FouceBean();
                        fb.setId(dishCursor.getString(0));
                        fb.setCode(dishCursor.getString(2));
                        fb.setName(dishCursor.getString(1));
                        fb.setPrice(dishCursor.getDouble(6));
                        fb.setUnit(dishCursor.getString(4));
                        String[] vars = dishCursor.getString(5).split(",");
                        int co[] = new int[vars.length];
                        for (int j = 0; j < vars.length; j++) {
                            co[j] = Integer.parseInt(vars[j]);
                        }
                        fb.setPoints(co);
                        ArrayList<ReasonBean> reasonBeans = new ArrayList<ReasonBean>();
                        if (dishCursor.getString(9).length() != 0) {
                            // 只判断code,因为都是必须对应的
                            String first = dishCursor.getString(9);

                            if (first.contains(",")) {
                                String[] codes = dishCursor.getString(9).split(
                                        ",");
                                String[] names = dishCursor.getString(10)
                                        .split(",");
                                String[] prices = dishCursor.getString(11)
                                        .split(",");
                                for (int k = 0; k < codes.length; k++) {
                                    ReasonBean reasonBean = new ReasonBean();
                                    reasonBean.setCode(codes[k]);
                                    reasonBean.setName(names[k]);
                                    reasonBean.setPrice(Double
                                            .parseDouble(prices[k]));
                                    reasonBeans.add(reasonBean);
                                }
                            } else {
                                // 如果不存在这样的"," 那么就是只有一个数据
                                ReasonBean reasonBean = new ReasonBean();
                                reasonBean.setCode(dishCursor.getString(9));
                                reasonBean.setName(dishCursor.getString(10));
                                P.c(dishCursor.getString(11));
                                reasonBean.setPrice(Double
                                        .parseDouble(dishCursor.getString(11)));
                                reasonBeans.add(reasonBean);
                            }
                        }
                        // 9 10 11 code name price
                        fb.setReasons(reasonBeans);
                        fouceBeans.add(fb);
                    }
                }
                flipBean.setFouceBeans(fouceBeans);
                flipBean.setPath(Common.SOURCE + imageNames[i]);
                flipBeans.add(flipBean);
                dishCursor.close();
            } catch (Exception e) {
                // TODO: handle exception

            } finally {
                if (dishCursor != null) {
                    dishCursor.close();
                    dishCursor = null;
                }
            }
        }
        return flipBeans;
    }

    /**
     * 根据菜品获取页数
     */
    public int getPage(String id) {
        int page = 0;
        String sql = "select page from dish where id=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{id});
            while (cursor.moveToNext()) {
                page = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return page;
    }

    /**
     * 菜品检索
     */
    public ArrayList<FouceBean> getSearchDish(String param) {
        ArrayList<FouceBean> fouceBeans = new ArrayList<FouceBean>();
        String sql = "select * from dish where shortCode like ? group by name;";
        Cursor cursor = null;
        try {
            // 解析数据
            cursor = db.rawQuery(sql, new String[]{param});
            while (cursor.moveToNext()) {
                FouceBean fb = new FouceBean();
                fb.setId(cursor.getString(0));
                fb.setCode(cursor.getString(2));
                fb.setName(cursor.getString(1));
                fb.setPrice(cursor.getDouble(6));
                fb.setUnit(cursor.getString(4));
                String[] vars = cursor.getString(5).split(",");
                int co[] = new int[vars.length];
                for (int j = 0; j < vars.length; j++) {
                    co[j] = Integer.parseInt(vars[j]);
                }
                fb.setPoints(co);
                ArrayList<ReasonBean> reasonBeans = new ArrayList<ReasonBean>();
                if (cursor.getString(9).length() != 0) {
                    // 只判断code,因为都是必须对应的
                    String first = cursor.getString(9);

                    if (first.contains(",")) {
                        String[] codes = cursor.getString(9).split(",");
                        String[] names = cursor.getString(10).split(",");
                        String[] prices = cursor.getString(11).split(",");
                        for (int k = 0; k < codes.length; k++) {
                            ReasonBean reasonBean = new ReasonBean();
                            reasonBean.setCode(codes[k]);
                            reasonBean.setName(names[k]);
                            reasonBean.setPrice(Double.parseDouble(prices[k]));
                            reasonBeans.add(reasonBean);
                        }
                    } else {
                        // 如果不存在这样的"," 那么就是只有一个数据
                        ReasonBean reasonBean = new ReasonBean();
                        reasonBean.setCode(cursor.getString(9));
                        reasonBean.setName(cursor.getString(10));
                        P.c(cursor.getString(11));
                        reasonBean.setPrice(Double.parseDouble(cursor
                                .getString(11)));
                        reasonBeans.add(reasonBean);
                    }
                }
                // 9 10 11 code name price
                fb.setReasons(reasonBeans);
                fouceBeans.add(fb);
            }
            cursor.close();
        } catch (Exception e) {
            P.c("模糊查询异常");
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return fouceBeans;
    }

    /**
     * 取得多口味
     */
    public ArrayList<ReasonBean> getReasons(FouceBean bean) {
        ArrayList<ReasonBean> reasonBeans = new ArrayList<ReasonBean>();
        String sql = "select reasoncode,reasonname,reasonprice from dish where id=? and code =?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql,
                    new String[]{bean.getId(), bean.getCode()});
            while (cursor.moveToNext()) {
                if (cursor.getString(0).length() != 0
                        && reasonBeans.size() == 0) {
                    // 保证N多种数据不被重复添加，和空内容不被计算
                    String cs = cursor.getString(0);
                    if (cs.contains(",")) {
                        String[] rc = cursor.getString(0).split(",");
                        String[] rn = cursor.getString(1).split(",");
                        String[] rp = cursor.getString(2).split(",");
                        for (int i = 0; i < rc.length; i++) {
                            ReasonBean rb = new ReasonBean();
                            rb.setCode(rc[i]);
                            rb.setName(rn[i]);
                            rb.setPrice(Double.parseDouble(rp[i]));
                            reasonBeans.add(rb);
                        }
                    } else {
                        ReasonBean rb = new ReasonBean();
                        rb.setCode(cursor.getString(0));
                        rb.setName(cursor.getString(1));
                        rb.setPrice(cursor.getShort(2));
                        reasonBeans.add(rb);
                    }
                }
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return reasonBeans;
    }

    //获取做法的数量
    public int getCookClassDataCount(String menucode) {
        Cursor cursor = null;
        int count = 0;
        try {
            String sql = "";
            sql = "select * from cook_lit where menucode =?";
            cursor = db.rawQuery(sql, new String[]{menucode});
            while (cursor.moveToNext()) {
                count = cursor.getCount();
            }
            cursor.close();
        } catch (Exception e) {
            P.c("测试");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    //获取tcMustNum总数量
    public int getNum(int index, String code) {
        String sql = "select tcMustNum from dish_table where i=?";
        Cursor cursor = null;
        int num = 0;
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(index)});
            while (cursor.moveToNext()) {
                if (cursor.isNull(0)) {
                } else {
                    num = cursor.getInt(0);
                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return num;
    }

    /**
     * 获得多规格code    套餐map  listMap
     *
     * @param id
     * @return
     */
    public HashMap<String, HashMap<String, ReasonBean>> getSelectedTcRessons(int index, String code) {
        String sql = "select tc_cook_codes,tc_cook_names,tc_cook_prices from dish_table where i=?";
        Cursor cursor = null;
        HashMap<String, HashMap<String, ReasonBean>> list = new HashMap<String, HashMap<String, ReasonBean>>();
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(index)});
            while (cursor.moveToNext()) {
                if (cursor.isNull(0)) {
                } else {
//                  02502|+8元配罗宋汤     ;      02617|烧肉汁;     00306|
                    String tcCode = cursor.getString(0);
                    String tcName = cursor.getString(1);
                    String tcPrice = cursor.getString(2);
                    String[] codes = tcCode.split(";");
                    String[] names = tcName.split(";");
                    String[] prices = tcPrice.split(";");
                    for (int i = 0; i < codes.length; i++) {
                        HashMap<String, ReasonBean> map = new HashMap<String, ReasonBean>();
                        String[] codeAll = codes[i].split("\\|");
                        String[] namesAll = names[i].split("\\|");
                        String[] pricesAll = prices[i].split("\\|");
                        if (codeAll.length > 1) {
                            if (codeAll[1].contains(",")) {
                                String[] codeFinal = codeAll[1].split(",");
                                String[] nameFinal = namesAll[1].split(",");
                                String[] pricesFinal = pricesAll[1].split(",");
                                int sizeCode = codeFinal.length;
                                for (int j = 0; j < codeFinal.length; j++) {
                                    ReasonBean bean = new ReasonBean();
                                    bean.setCode(codeFinal[j]);
                                    bean.setName(nameFinal[j]);
                                    bean.setPrice(Double.parseDouble(pricesFinal[j]));
                                    map.put(bean.getCode(), bean);
                                }
                            } else {
                                ReasonBean bean = new ReasonBean();
                                bean.setCode(codeAll[1]);
                                bean.setName(namesAll[1]);
                                bean.setPrice(Double.parseDouble(pricesAll[1]));
                                map.put(bean.getCode(), bean);
                            }
                            list.put(codeAll[0], map);
                        } else {
                            list.put(codeAll[0], null);
                        }
                    }
                }

            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 获得多规格code    套餐map   tcbeans
     *
     * @param id
     * @return
     */
    public HashMap<String, ArrayList<ReasonBean>> getSelectedTcRessonsTcBeans(int index, String code) {
        String sql = "select tc_cook_codes,tc_cook_names,tc_cook_prices from dish_table where i=?";
        Cursor cursor = null;
        HashMap<String, ArrayList<ReasonBean>> tcBeans = new HashMap<String, ArrayList<ReasonBean>>();
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(index)});
            while (cursor.moveToNext()) {
                if (cursor.isNull(0)) {
//                    02502|黑松露汁,饮料    ;   02617|   ;     00306|
                } else {
                    String tcCode = cursor.getString(0);
                    String tcName = cursor.getString(1);
                    String tcPrice = cursor.getString(2);
                    String[] codes = tcCode.split(";");
                    String[] names = tcName.split(";");
                    String[] prices = tcPrice.split(";");
                    for (int i = 0; i < codes.length; i++) {
                        ArrayList<ReasonBean> map = new ArrayList<ReasonBean>();
                        String[] codeAll = codes[i].split("\\|");
                        String[] namesAll = names[i].split("\\|");
                        String[] pricesAll = prices[i].split("\\|");
                        if (codeAll.length > 1) {
                            String[] codeFinal = codeAll[1].split(",");
                            String[] nameFinal = namesAll[1].split(",");
                            String[] pricesFinal = pricesAll[1].split(",");
                            for (int j = 0; j < codeFinal.length; j++) {
                                ReasonBean bean = new ReasonBean();
                                bean.setCode(codeFinal[j]);
                                bean.setName(nameFinal[j]);
                                bean.setPrice(Double.parseDouble(pricesFinal[j]));
                                map.add(bean);
                                tcBeans.put(codeAll[0], map);
                            }
                        } else {
                            tcBeans.put(namesAll[0], null);
                        }
                    }

                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tcBeans;
    }

    //获取tcliststr
    public HashMap<String, String> getSelectedTcRessonsTcBeansListStr(int index, String code) {
        String sql = "select tcListStr from dish_table where i=?";
        Cursor cursor = null;
        HashMap<String, String> tcListStr = new HashMap<String, String>();
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(index)});
            while (cursor.moveToNext()) {
                if (cursor.isNull(0)) {
//                    02502|    ;    02617|1,2,    ;    00306|;
//                    02502|黑松露汁,饮料    ;   02617|   ;     00306|
                } else {
//                    01407|0,;     01304|;     00603|;     00105|;     02613|0,;     02614|0,;
                    String tcList = cursor.getString(0);
                    String[] tcs = tcList.split(";");
                    for (int i = 0; i < tcs.length; i++) {
                        String[] tcsFinal = tcs[i].split("\\|");
                        if (tcsFinal.length > 1) {
                            tcListStr.put(tcsFinal[0], tcsFinal[1]);
                        } else {
                            tcListStr.put(tcsFinal[0], null);
                        }
                    }

                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tcListStr;
    }

    //获取liststr
    public HashMap<String, List<String>> getListStr(int index, String code) {
        String sql = "select listStr from dish_table where i=?";
        Cursor cursor = null;
        List<String> list = new ArrayList<String>();
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(index)});
            while (cursor.moveToNext()) {
                if (cursor.isNull(0)) {
//                    02502|    ;    02617|1,2,    ;    00306|;
//                    02502|黑松露汁,饮料    ;   02617|   ;     00306|
                } else {
                    String tcList = cursor.getString(0);
                    String[] tcs = tcList.split(",");
                    for (int i = 0; i < tcs.length; i++) {
                        list.add(tcs[i]);
                    }
                    map.put(tcList, list);
                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return map;
    }

    //获取用户名字
    public String getName(String code) {
        String sql = "select description from user where upper(code)=upper(?)";
        String name = "";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{code});
            int count = cursor.getCount();
            if (count != 0) {
                if (cursor.moveToNext()) {
                    name = getString(cursor, "description");
                }
            }
            cursor.close();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return name;

    }
}
