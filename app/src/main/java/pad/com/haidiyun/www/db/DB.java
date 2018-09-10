package pad.com.haidiyun.www.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.Dis;
import pad.com.haidiyun.www.bean.Dish;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.FlipBean;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.MenuBean;
import pad.com.haidiyun.www.bean.Pbean;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.bean.ResonMenuBean;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.bean.TablesBean;
import pad.com.haidiyun.www.bean.TcBean;
import pad.com.haidiyun.www.common.P;

public class DB {
    public static DB dao;
    private static DBHelper dbHelper;
    private static SQLiteDatabase db;

    private void DB() {
    }

    /**
     * 单列数据库操作对象
     *
     * @return
     */
    public static synchronized DB getInstance() {
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
//------------------------------------------------------------------------------------------
    // 添加新数据

    /**
     * 添加菜品
     *
     * @param array
     * @throws JSONException
     */
    public void addDish(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            // 解析数据并添加到数据库
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into dish(id,classcode,code,name,name_en,help,unit,price,price_modify,weigh,discount,temp,suit,require_cook,description,locked,uniqueid,timestamp,del,type) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "ClassCode"),
                            getJsonString(json, "Code"),
                            getJsonString(json, "Name"),
                            getJsonString(json, "EName"),
                            getJsonString(json, "HelpCode"),
                            getJsonString(json, "Unit"),
                            json.getDouble("Price"),
                            getJsonBoolean(json, "PriceModify"),
                            getJsonBoolean(json, "NeedWeigh"),
                            getJsonBoolean(json, "AllowDiscount"),
                            getJsonBoolean(json, "IsTmp"),
                            getJsonBoolean(json, "IsSuit"),
                            getJsonBoolean(json, "RequireCook"),
                            getJsonString(json, "Description"),
                            getJsonBoolean(json, "Locked"),
                            getJsonString(json, "UniqueId"),
                            json.getLong("timestamp"),
                            json.getBoolean("DelStatus"),
                            json.getString("Type")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * 添加分类
     *
     * @param array
     * @throws JSONException
     */
    public void addCate(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into menu(id,parent_code,code,name,help,name_en,level,sitecode,description,uniqueid,timestamp,del,Sort) values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "ParentCode"),
                            getJsonString(json, "Code"),
                            getJsonString(json, "Name"),
                            getJsonString(json, "HelpCode"),
                            getJsonString(json, "EName"),
                            getJsonString(json, "Level"),
                            getJsonString(json, "SiteCode"),
                            getJsonString(json, "Description"),
                            getJsonString(json, "UniqueId"),
                            json.getLong("timestamp"),
                            json.getBoolean("DelStatus"),
                            json.getInt("Sort")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    private   long getData(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(date).getTime();
    }
    public void addDis(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            try {
                db.execSQL(
                        "insert into dis(code,name,vf,vt,locked,del) values(?,?,?,?,?,?)",
                        new Object[]{getJsonString(json,"Code"),getJsonString(json,"Name"),getData(getJsonString(json,"ValidFrom").replace("T"," ")),getData(getJsonString(json,"ValidTo").replace("T"," ")),getJsonBoolean(json,"Locked"),getJsonBoolean(json,"DelStatus")});
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
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
                    "insert into user(code,pwd,locked,uniqueid,timestamp,del) values(?,?,?,?,?,?)",
                    new Object[]{getJsonString(json, "Code"),
                            getJsonString(json, "Pwd"),
                            getJsonBoolean(json, "Locked"),
                            getJsonString(json, "UniqueId"),
                            json.getLong("timestamp"),
                            json.getBoolean("DelStatus")});
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            db.execSQL("insert into area(id,code,name,help,site,sort,description,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?,?)", new Object[]{getJsonInt(json, "Id"), getJsonString(json, "Code"), getJsonString(json, "Name"), getJsonString(json, "HelpCode"), getJsonString(json, "Site"), getJsonInt(json, "Sort"), getJsonString(json, "Description"), getJsonString(json, "UniqueId"), json.getLong("timestamp"), json.getBoolean("DelStatus")});
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
            db.execSQL("insert into board(id,code,name,help,table_class,area,max,description,locked,uniqueid,timestamp,del) values(?,?,?,?,?,?,?,?,?,?,?,?);", new Object[]{getJsonInt(json, "Id"), getJsonString(json, "Code"), getJsonString(json, "Name"), getJsonString(json, "HelpCode"), getJsonString(json, "TableCls"), getJsonString(json, "Area"), getJsonInt(json, "MaxGstNum"), getJsonString(json, "Description"), getJsonBoolean(json, "Locked"), getJsonString(json, "UniqueId"), json.getLong("timestamp"), json.getBoolean("DelStatus")});

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
                            json.getLong("timestamp"),
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
                            json.getLong("timestamp"),
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
                            json.getLong("timestamp"),
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
                            json.getLong("timestamp"),
                            json.getBoolean("DelStatus")});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    //做法必选
    public void addMenuCookCls(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
            db.execSQL(
                    "insert into menu_cook_cls(id,menucode,cookcls,require,uniqueid,timestamp,del) values(?,?,?,?,?,?,?)",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "MenuCode"),
                            getJsonString(json, "CookCls"),
                            getJsonBoolean(json, "Require"),
                            getJsonString(json, "UniqueId"),
                            json.getString("TimeStamp"),
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
                            json.getLong("timestamp"),
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
                            json.getLong("timestamp"),
                            json.getBoolean("DelStatus")});
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
                            json.getLong("timestamp"),
                            json.getBoolean("DelStatus")});
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
    public void addFg(JSONArray array) throws JSONException {
        int len = array != null ? array.length() : 0;
        db.beginTransaction();
        for (int i = 0; i < len; i++) {
            JSONObject json = array.getJSONObject(i);
//				String image = getJsonString(json, "ImageUrl");
            db.execSQL(
                    "insert into fg(menucode,coordinate,page,image) values(?,?,?,?);",
                    new Object[]{
                            getJsonString(json, "MenuCode"),
                            getJsonString(json, "Coordinate"),
                            getJsonInt(json, "Page"),
                            getJsonString(json, "ImageUrl").replace("\\", "/")
//								image.substring(image.lastIndexOf("\\")+1)
                    });
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
                    "insert into mgroup(id,mcode,gcode,gname,max_num,min_num,sort,timestamp,del) values(?,?,?,?,?,?,?,?,?);",
                    new Object[]{getJsonInt(json, "Id"),
                            getJsonString(json, "MenuCode"),
                            getJsonString(json, "GroupCode"),
                            getJsonString(json, "GroupName"),
                            getJsonInt(json, "MaxSelectNumber"),
                            getJsonInt(json, "MinSelectNumber"),
                            getJsonInt(json, "Sort"),
                            json.getLong("timestamp"),
                            json.getBoolean("DelStatus")});
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
                            json.getLong("timestamp"),
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
                                json.getLong("timestamp"),
                                json.getBoolean("DelStatus")});
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

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
            }
        }
        return tablesAreas;
    }

    /**
     * 根据区域获得桌台号
     *
     * @return
     */


    public ArrayList<TableBean> getTableCodeBeans(String code) {
        ArrayList<TableBean> tableBeans = new ArrayList<TableBean>();
        String sql = "select code,name,max,locked from board where area=? and del=0 order by name";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{code});
            while (cursor.moveToNext()) {
                TableBean bean = new TableBean();
                bean.setCode(getString(cursor, "code"));
                bean.setName(getString(cursor, "name"));
                bean.setMax(getInt(cursor, "max"));
                bean.setLocked(getBoolean(cursor, "locked"));
                tableBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tableBeans;

    }

    public void clearAll() {
        db.beginTransaction();
        db.execSQL("delete from dis");
        db.execSQL("delete from dish");
        db.execSQL("delete from images");
        db.execSQL("delete from remark");
        db.execSQL("delete from site");
        db.execSQL("delete from area");
        db.execSQL("delete from board");
        db.execSQL("delete from menu");
        db.execSQL("delete from cook");
        db.execSQL("delete from cook_class");
        db.execSQL("delete from menu_cook");
        db.execSQL("delete from menu_cook_cls");
        db.execSQL("delete from user");
        db.execSQL("delete from detail");
        db.execSQL("delete from dish_table");
        db.execSQL("delete from fg");
        db.execSQL("delete from mgroup");
        db.execSQL("delete from mgdetail");
        db.execSQL("delete from tcReason");

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    //获得菜单页数
    public ArrayList<Pbean> getFlipPages() {
        //select count(*) from fg;
        String sql = "select page,CASE WHEN COORDINATE=='' THEN 0 ELSE COUNT(*) END count  from fg group by page ;";
        ArrayList<Pbean> beans = new ArrayList<Pbean>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    sql,
                    null);
            while (cursor.moveToNext()) {
                Pbean bean = new Pbean();
                bean.setPage(getInt(cursor, "page"));
                bean.setCount(getInt(cursor, "count"));
                beans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return beans;
    }


    public ArrayList<FlipBean> getDishsToFlip() {
        ArrayList<FlipBean> flipBeans = new ArrayList<FlipBean>();
        ArrayList<FouceBean> foodsBeans = new ArrayList<FouceBean>();
        ArrayList<Pbean> beans = getFlipPages();
        //最大页数
        int max = beans.size();
        int INDEX = 0;//起始页
        String sql = "select d.id,d.classcode,d.code,d.name,d.name_en,d.help,d.unit,d.price,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,d.del,f.image,f.coordinate,f.page,d.type from  fg as f   left JOIN   dish as d   on d.code=f.menucode  order by f.page";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    sql,
                    null);
            FlipBean fb = null;
            while (cursor.moveToNext()) {
                int page = getInt(cursor, "page");
                String path = getString(cursor, "image");
                FouceBean bean = new FouceBean();
                bean.setClasscode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getString(cursor, "id"));
                bean.setName(getString(cursor, "name"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPrice_modify(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setDel(getBoolean(cursor, "del"));
                bean.setLocked(getBoolean(cursor, "locked"));
                bean.setType(getString(cursor, "type"));


                bean.setPage(page);
                String info = bean.toString();
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                //--
                //先加入
                if (fb == null) {
                    fb = new FlipBean();
                }
                String vars[] = getString(cursor, "coordinate").split(",");
                if (vars.length == 4) {
                    //只接受正常数值
                    int co[] = new int[vars.length];
                    for (int j = 0; j < vars.length; j++) {
                        co[j] = Integer.parseInt(vars[j]);
                    }
                    bean.setPoints(co);
                    foodsBeans.add(bean);
                }
                if (beans.get(INDEX).getCount() == foodsBeans.size()) {
                    //如果数量已满，就清空
                    P.c("完成了" + INDEX);
                    INDEX++;
                    fb.setPath(path);
                    fb.setFouceBeans(foodsBeans);
                    flipBeans.add(fb);
                    foodsBeans = new ArrayList<FouceBean>();
                    fb = null;
                }


            }
            cursor.close();
        } catch (Exception e) {
            // TODO: handle exception
            P.c(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        P.c("获取的长度" + flipBeans.size());
        return flipBeans;
    }


    //套餐下单

    /**
     * 添加菜品到购物篮【没有附加条件的添加】 cook_codes cook_names cook_prices
     *
     * @param bean
     */
    public boolean addDishToPad(FouceBean bean, ArrayList<ReasonBean> resons,
                                String remark, String details, String detailNames, String num, Handler handler) {
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
                    sql2 = "select * from menu_cook_cls where menucode =?";
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
                    // TODO: handle exception
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
                sql2 = "select * from menu_cook_cls where menucode =?";
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
        //-----------------------
        if (Un_Save(bean.getCode(), String.valueOf(bean.getPrice()), builder_code.toString(), remark, details)) {
            try {
                db.execSQL("insert into dish_table(code,name,name_en,unit,price,more,price_modify,weigh,discount,temp,suit,count,cook_codes,cook_names,cook_prices,remark,details,detailNames,flag,must_value,cook_codes,cook_names,cook_prices,tcMustNum,tcActualNum) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{bean.getCode(), bean.getName(), "", bean.getUnit(), bean.getPrice(), bean.isRequire_cook(), bean.isPrice_modify(), bean.isWeigh(), bean.isDiscount(), bean.isTemp(), bean.isSuit(), num, builder_code.toString(), builder_name.toString(), builder_price.toString(), remark, details, detailNames, System.currentTimeMillis(), isCanSelect, s1, s2, s3, value, value});
                int dd = value;
            } catch (Exception e) {
                // TODO: handle exception
                P.c(e.getMessage());
                return false;
            }
            return true;
        } else {
            if (handler != null) {
                handler.sendEmptyMessage(4);
            }
        }
        return false;
    }

    /**
     * 添加菜品到购物篮 增加必选判断
     *
     * @param bean
     */
    public boolean addDishToPad(FouceBean bean, ArrayList<ReasonBean> resons,
                                String remark, String details, String detailNames, String num, Handler handler, boolean isSelect) {
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
        if (Un_Save(bean.getCode(), String.valueOf(bean.getPrice()), builder_code.toString(), remark, details)) {
            try {
                db.execSQL("insert into dish_table(code,name,name_en,unit,price,more,price_modify,weigh,discount,temp,suit,count,cook_codes,cook_names,cook_prices,remark,details,detailNames,flag,must_value) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{bean.getCode(), bean.getName(), "", bean.getUnit(), bean.getPrice(), bean.isRequire_cook(), bean.isPrice_modify(), bean.isWeigh(), bean.isDiscount(), bean.isTemp(), bean.isSuit(), num, builder_code.toString(), builder_name.toString(), builder_price.toString(), remark, details, detailNames, System.currentTimeMillis(), isSelect});

            } catch (Exception e) {
                // TODO: handle exception
                P.c(e.getMessage());
                return false;
            }
            return true;
        } else {
            if (handler != null) {
                handler.sendEmptyMessage(4);
            }
        }
        return false;
    }

    /**
     * 做法菜单
     *
     * @param id
     * @return
     */
    public ArrayList<ResonMenuBean> getResonMenuBeans(ArrayList<ResonMenuBean> resonMenuBeans, String code) {
        String is = "select count(*) from cook where code=?";//根据04003拿左边必选数量
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(is, new String[]{});
            while (cursor.moveToNext()) {
                //
                if (!cursor.isNull(0)) {
                    // 基本菜品
                    ResonMenuBean bean = new ResonMenuBean();
                    bean.setCode("-1");
                    bean.setName("本菜特有");
                    bean.setMultySelect(true);
                    bean.setMustSelect(false);
                    resonMenuBeans.add(bean);
                }
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        //-------------------------------
        Cursor cursor2 = null;
        String codeName = "";
        try {
            String sql2 = "";
            StringBuffer s = new StringBuffer("");
            boolean require = false;
            sql2 = "select * from menu_cook_cls where menucode =?";
            cursor2 = db.rawQuery(sql2, new String[]{code});
            while (cursor2.moveToNext()) {
                //基本菜品
                ResonMenuBean bean = new ResonMenuBean();
                codeName = getString(cursor2, "cookcls");
                bean.setCode(codeName);
                require = getBoolean(cursor2, "require");
                if (require) {
                    bean.setMustSelect(true);
                } else {
                    bean.setMustSelect(false);
                }
                String sql = "select name,MultySelect from cook_class where code =?";
                Cursor cursor1 = null;
                try {
                    cursor1 = db.rawQuery(sql, new String[]{codeName});
                    while (cursor1.moveToNext()) {
                        String name = getString(cursor1, "name");
                        bean.setName(name);
                        bean.setMultySelect(getBoolean(cursor1, "MultySelect"));
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
            P.c("测试");
        } finally {
            if (cursor2 != null) {
                cursor2.close();
            }
        }
        //-----------------
        return resonMenuBeans;
    }

    //获取做法的数量
    public int getCookClassDataCount(String menucode) {
        Cursor cursor = null;
        int count = 0;
        try {
            String sql = "";
            sql = "select * from menu_cook_cls where menucode =?";
            cursor = db.rawQuery(sql, new String[]{menucode});
            while (cursor.moveToNext()) {
                count = cursor.getCount();
            }
            cursor.close();
        } catch (Exception e) {
            P.c("测试");
            // TODO: handle exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }


    //获取必选数据
    public List<String> getMustData(String menucode) {
        Cursor cursor = null;
        List<String> list = new ArrayList<String>();
        StringBuffer s = new StringBuffer("");
        try {
            String sql = "";
            sql = "select * from menu_cook_cls where menucode =?";
            cursor = db.rawQuery(sql, new String[]{menucode});
            while (cursor.moveToNext()) {
                if (getBoolean(cursor, "require")) {
                    String cookCls = getString(cursor, "cookcls");
                    String sql1 = "select name from cook_class where code =?";
                    Cursor cursor1 = null;
                    try {
                        cursor1 = db.rawQuery(sql1, new String[]{cookCls});
                        while (cursor1.moveToNext()) {
                            String name = getString(cursor1, "name");
                            list.add(name);
                            cursor1.close();
                        }
                    } catch (Exception e) {
                        P.c(e.getLocalizedMessage());
                    } finally {
                        if (cursor1 != null) {
                            cursor1.close();
                        }
                    }

                    cursor1.close();
                }
            }
            cursor.close();
        } catch (Exception e) {
            P.c("测试");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
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
            }
        }
        // db.close();
        return true;
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

        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor = null;
            }
        }
        return flag;

    }

    /**
     * 获得菜篮子
     *
     * @return
     */
    public ArrayList<DishTableBean> getTableBeans() {
        ArrayList<DishTableBean> dishTableBeans = new ArrayList<DishTableBean>();
        Cursor cursor = null;
        String sql = "select i,code,name,unit,price,more,price_modify,weigh,discount,temp,suit,count,cook_codes,cook_names,cook_prices,remark,details,detailNames,flag,must_value,listStr,extra,tc_cook_codes,tc_cook_names,tc_cook_prices from dish_table";
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                DishTableBean tableBean = new DishTableBean();
                tableBean.setI(getInt(cursor, "i"));//这里就拿到59了
                String code = getString(cursor, "code");
                tableBean.setCode(code);
                tableBean.setName(getString(cursor, "name"));
                tableBean.setUnit(getString(cursor, "unit"));
                tableBean.setPrice(getDouble(cursor, "price"));
                tableBean.setMore(getBoolean(cursor, "more"));
                tableBean.setPrice_modify(getBoolean(cursor, "price_modify"));
                tableBean.setWeigh(getBoolean(cursor, "weigh"));
                tableBean.setDiscount(getBoolean(cursor, "discount"));
                tableBean.setTemp(getBoolean(cursor, "temp"));
                tableBean.setSuit(getBoolean(cursor, "suit"));
                tableBean.setCount(getInt(cursor, "count"));
                String cook_codes = getString(cursor, "cook_codes");
                tableBean.setCook_codes(cook_codes);
                tableBean.setCook_names(getString(cursor, "cook_names"));
                tableBean.setCook_prices(getString(cursor, "cook_prices"));
                tableBean.setSuitMenuDetail(getString(cursor, "details"));
                tableBean.setDetailNames(getString(cursor, "detailNames"));
                tableBean.setFlag(getString(cursor, "flag"));
                tableBean.setListStr(getString(cursor, "listStr"));
                tableBean.setExtra(getInt(cursor, "extra"));
                String remark = getString(cursor, "remark");
                tableBean.setRemark(remark);
                tableBean.setCanSendMustFlag(getBoolean(cursor, "must_value"));
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
                //-----------------------------------------套餐做法设置
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
            }
        }
        return dishTableBeans;
    }

    /**
     * 修改菜篮子数量
     *
     * @param num
     * @param id
     */
    public void changeNum(int count, int i) {
        String sql = "update dish_table set count = ? where i = ?";
        db.execSQL(sql, new Object[]{count, i});
    }

    public void delete(int i) {
        String s_ = "delete from dish_table where i=?";
        db.execSQL(s_, new Object[]{i});
    }


    public void clear() {
        String sql = "delete from dish_table";
        db.execSQL(sql);
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
                    String code = cursor.getString(0);
                    String name = cursor.getString(1);
                    String price = cursor.getString(2);
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
            }
        }
        return resMap;
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
        List<String> list = new ArrayList<>();
        HashMap<String, List<String>> map = new HashMap<>();
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
     * 获得此分类的和该菜品 相关的所有可选做法
     *
     * @param ba
     * @return pbc 1是公共   0是私有
     */
    public ArrayList<ReasonBean> getCpBeans(String castcode, String menucode) {//左边code,右边菜品code
        P.c(castcode + "--" + menucode);
        ArrayList<ReasonBean> rs = new ArrayList<ReasonBean>();
        Cursor cursor = null;
        try {
            String sql = "";
            if ("-1".equals(castcode)) {//本菜专属

                sql = "select code,name,case when public==1 then price else (select price from menu_cook where menucode=? and cookcode=code) end price from cook where  code in (select cookcode from menu_cook where menucode=?)";
                cursor = db.rawQuery(sql, new String[]{menucode, menucode});

            } else {
                sql = "select code,name,case when public==1 then price else (select price from menu_cook where menucode=? and cookcode=code) end price from cook where public=1 and cookclass=?";
                cursor = db.rawQuery(sql, new String[]{menucode, castcode});
            }
            P.c(sql);
            while (cursor.moveToNext()) {
                //增加菜品做法
                ReasonBean bean = new ReasonBean();
                bean.setCode(getString(cursor, "code"));
                bean.setName(getString(cursor, "name"));
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
            }
        }
        return rs;
    }


    public void getCountSS(String code1) {
        Cursor cursor = null;
        String sql = "select cc.code,cc.name,(select count(*) from cook as c where c.cookclass=cc.code ) as count from cook_class as cc";
        db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            //增加菜品做法
            String code = getString(cursor, "code");
            String name = getString(cursor, "name");
            int count = getInt(cursor, "count");
        }
        String code = getString(cursor, "code");
        int count = getInt(cursor, "count");
        cursor.close();
    }

    public void updateDishInit(DishTableBean tb) {
        String sql = "update dish_table set cook_codes='',cook_names='',cook_prices='' where i=?";
        db.execSQL(sql, new Object[]{tb.getI()});
    }

    //设置必选参数
    public void updateMustData(DishTableBean tb, boolean isMust, String s, int extra) {
        String sql = "update dish_table set must_value=?,listStr = ?,extra = ? where i=?";
        db.execSQL(sql, new Object[]{isMust, s, extra, tb.getI()});
    }

    /**
     * 比较两个数据
     */
    public boolean getNumData(DishTableBean tb) {
        int tcMustNum = 0;
        int tcActualNum = 0;
        String sql = "select tcMustNum,tcActualNum from dish_table where code =?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{tb.getCode()});
            while (cursor.moveToNext()) {
                tcMustNum = cursor.getInt(0);
                tcActualNum = cursor.getInt(1);
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (tcActualNum == tcMustNum) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 更新数据的规格参数    更新dish_table表,更新resmap数据
     *
     * @param bean
     * @param id
     */
    public void updateDishTable(ArrayList<ReasonBean> bean, DishTableBean tb) {
        String sql = "update dish_table set cook_codes=?,cook_names=?,cook_prices=?,must_value=? where i = ?";
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
        db.execSQL(sql, new Object[]{builder1.toString(),
                builder2.toString(), builder3.toString(), true, tb.getI()});
    }

    /**
     * 更新数据的规格参数  套餐列表更新  更新dish_table表,更新resmap数据
     *
     * @param bean
     * @param id
     */

    public void updateDishTable(ArrayList<ReasonBean> bean, DishTableBean tb, HashMap<String, ArrayList<ReasonBean>> tcBeans, HashMap<String, String> tcListStr, int position, int tcMustNum) {
        StringBuilder builder1 = new StringBuilder("");
        StringBuilder builder2 = new StringBuilder("");
        StringBuilder builder3 = new StringBuilder("");
        StringBuilder builder4 = new StringBuilder("");
        String sql = "update dish_table set tc_cook_codes=?,tc_cook_names=?,tc_cook_prices=? ,tcListStr = ?,must_value=? ,tcActualNum = ? where i = ?";
        //第几个index 第一个就
        int sis = tcBeans.size();
        int siss = tcListStr.size();
        Set<String> keysTc = tcBeans.keySet();
        Set<String> keysTcList = tcListStr.keySet();
        ArrayList<String> keys = new ArrayList<>();
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
        int ssss = sTc;//都为5
        int ssssss = tcMustNum;
        if (sTc >= tcMustNum) {
            isCan = true;
        } else {
            isCan = false;
        }
        db.execSQL(sql, new Object[]{builder1.toString(),
                builder2.toString(), builder3.toString(), builder4.toString(), isCan, tcMustNum, tb.getI()});
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

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return i;
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

    public ArrayList<Dis> getDiss(){
       ArrayList<Dis> diss = new ArrayList<>();
        String sql = "select code,name from dis where locked=0 and del = 0 and ? between vf and vt";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(System.currentTimeMillis())});
            while (cursor.moveToNext()) {
                Dis dis = new Dis();
                dis.setCode(getString(cursor,"code"));
                dis.setName(getString(cursor,"name"));
                diss.add(dis);
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return  diss;
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

    public ArrayList<TcBean> getTc(String foodCode) {
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

    //---------------------------------------------------------------------------------------------
    //分界线
    //---------------------------------------------------------------------------------------------
    public boolean Un_Save(String id, String reasoncode) {
        P.c("点选的" + reasoncode);
        String s_ = "select * from dish_table where id=? and reasoncode=?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(s_, new String[]{id, reasoncode});
            int count = cursor.getCount();
            cursor.close();

            if (count != 0) {
                return false;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // db.close();
        return true;
    }


    private String reason(ArrayList<ReasonBean> bns) {
        StringBuilder builder = new StringBuilder();
        int len = bns != null ? bns.size() : 0;
        for (int i = 0; i < len; i++) {
            ReasonBean rb = bns.get(i);
            if (i != len - 1) {
                builder.append(rb.getCode() + ",");
            } else {
                builder.append(rb.getCode());
            }
        }
        P.c(builder.toString());
        return builder.toString();

    }


    public void updateDishInit(String id, String reasoncode) {
        String sql = "update dish_table set reasoncode='',reasonname='',reasonprice='' where id = ? and reasoncode = ?";
        db.execSQL(sql, new Object[]{id, reasoncode});
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
        String sql = "insert into dish values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        if (Un_SaveDish(dish.getId(), dish.getCoords())) {
            db.execSQL(sql,
                    new Object[]{dish.getId(), dish.getName(),
                            dish.getCode(), dish.getType(), dish.getUnit(),
                            dish.getCoords(), dish.getPrice(), dish.getPage(), dish.getShortCode(), dish.getReasonCode(), dish.getReasonName(), dish.getReasonPrice(), dish.getUnitId()});
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
            }
        }
        return count;
    }

    public String getTeeUnit(String code) {
        String unit = "";
        Cursor cursor = null;
        String sql = "select unitId from dish where code=? group by code";
        try {
            cursor = db.rawQuery(sql, new String[]{code});
            while (cursor.moveToNext()) {
                unit = cursor.getString(0);
            }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return unit;
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


    /**
     * 获取目录
     */
    public ArrayList<MenuBean> getMenus() {
        Cursor cursor = null;
        String sql = "select m.name as menuname,d.name,min(f.page) as page from  fg as f   left JOIN   dish as d   on d.code=f.menucode  join  menu as m on  m.code=d.classcode group by m.code order by m.Sort";
        ArrayList<MenuBean> menus = new ArrayList<MenuBean>();
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                if (!cursor.isNull(0)) {
                    //存在这样的值,那么就进行菜品整理
                    MenuBean bean = new MenuBean();
                    bean.setMenuTitle(getString(cursor, "menuname"));
                    bean.setPage(getInt(cursor, "page"));
                    menus.add(bean);
                }
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {

                cursor.close();
            }
        }
        return menus;
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
            }
        }
        return page;
    }

    /**
     * 菜品检索
     */
    public ArrayList<FouceBean> getSearchDish(String param) {
        ArrayList<FouceBean> fouceBeans = new ArrayList<FouceBean>();
        String sql = "select d.id,d.classcode,d.code,d.name,d.name_en,d.help,d.unit,d.price,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,d.del,f.image,f.coordinate,f.page from  fg as f   left JOIN   dish as d   on d.code=f.menucode  where help like ?";
        Cursor cursor = null;
        try {
            //解析数据
            cursor = db.rawQuery(sql, new String[]{param});
            while (cursor.moveToNext()) {
                int page = getInt(cursor, "page");
                String path = getString(cursor, "image");
                FouceBean bean = new FouceBean();
                bean.setClasscode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getString(cursor, "id"));
                bean.setName(getString(cursor, "name"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPrice_modify(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setDel(getBoolean(cursor, "del"));
                bean.setLocked(getBoolean(cursor, "locked"));
                bean.setPage(page);
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                fouceBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            P.c("模糊查询异常");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fouceBeans;
    }


    /**
     * 查询所属分类
     */
    public ArrayList<FouceBean> findSortClassMenu(String clickCode) {
        ArrayList<FouceBean> fouceBeans = new ArrayList<FouceBean>();
        String sql = "select d.id,d.classcode,d.code,d.name,d.name_en,d.help,d.unit,d.price,d.price_modify,d.weigh,d.discount,d.temp,d.suit,d.require_cook,d.locked,d.del,f.image,f.coordinate,f.page from  fg as f   left JOIN   dish as d   on d.code=f.menucode  where help like ?";
        Cursor cursor = null;
        try {
            //解析数据
            cursor = db.rawQuery(sql, new String[]{clickCode});
            while (cursor.moveToNext()) {
                int page = getInt(cursor, "page");
                String path = getString(cursor, "image");
                FouceBean bean = new FouceBean();
                bean.setClasscode(getString(cursor, "classcode"));
                bean.setCode(getString(cursor, "code"));
                bean.setDiscount(getBoolean(cursor, "discount"));
                bean.setHelp(getString(cursor, "help"));
                bean.setId(getString(cursor, "id"));
                bean.setName(getString(cursor, "name"));
                bean.setPrice(getDouble(cursor, "price"));
                bean.setPrice_modify(getBoolean(cursor, "price_modify"));
                bean.setSuit(getBoolean(cursor, "suit"));
                bean.setTemp(getBoolean(cursor, "temp"));
                bean.setUnit(getString(cursor, "unit"));
                bean.setWeigh(getBoolean(cursor, "weigh"));
                bean.setDel(getBoolean(cursor, "del"));
                bean.setLocked(getBoolean(cursor, "locked"));
                bean.setPage(page);
                bean.setRequire_cook(getBoolean(cursor, "require_cook"));
                fouceBeans.add(bean);
            }
            cursor.close();
        } catch (Exception e) {
            P.c("模糊查询异常");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fouceBeans;
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

    public void changePrice(DishTableBean bean) {
        String sql = "update dish_table set price = ?,isEditPrice=? where code = ?";
        db.execSQL(sql, new Object[]{bean.getPrice(), true, bean.getCode()});
//        db.execSQL(sql, new Object[]{bean.getPrice(), isedi, bean.getCode()});
    }

    public void changeName(DishTableBean bean) {
        String sql = "update dish_table set name = ? where code = ?";
        db.execSQL(sql, new Object[]{bean.getName(), bean.getCode()});
        P.c("改之后:" + bean.toString());
    }
}
