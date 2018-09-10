package pad.stand.com.haidiyun.www.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;

public class DBHelper extends SQLiteOpenHelper {
    /**
     * 数据库操作
     *
     * @param context
     * @param name
     * @param factory
     * @param versionê
     */
    private String DATABASE_PATH;
    @SuppressWarnings("unused")
    private Context context;

    public DBHelper(Context context) {
        super(context, Common.DB_NAME, null, Common.DB_VERSION);

    }

    public DBHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        this.context = context;
        DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        P.c("数据库版本" + version);
    }

    private void create(SQLiteDatabase db) {
        db.beginTransaction();

        /**
         * ClassCode   菜类编码
         Code          菜品编码
         Name         菜品名称
         EName       菜品英文名称
         HelpCode   首字母
         Unit            单位
         Price           单价
         PriceModify   是否允许点单时修改价格
         NeedWeigh   是否需要过磅
         AllowDiscount 是否允许打折
         IsTmp            是否临时菜
         IsSuit             是否套菜
         Remark          备注
         SrvFrom          供应时间开始
         SrvTo              供应时段截止
         Description     菜品介绍
         Locked           是否停用

         */
        //菜品
        db.execSQL("create table dish(i integer primary key autoincrement,id int,pcode varchar,classcode varchar,code varchar,name varchar,name_en varchar,help varchar,unit varchar,price double,price1 double,price2 double,price_modify boolean,weigh boolean,discount boolean,temp boolean,suit boolean,require_cook boolean, description varchar,locked boolean,uniqueid varchar,timestamp long,del boolean,show boolean,Sort int,type varchar,srvf varchar,srvt varchar,orderMinLimit double,chargeMode varchar)");
        //菜品图片
        db.execSQL("create table images(i integer primary key autoincrement,id int,code varchar,path varcahr,sort int,uniqueid varchar,timestamp long,del boolean,w int,h int)");
        //口味
        db.execSQL("create table remark(i integer primary key autoincrement,id int,code varchar,name varchar,description varchar,help varchar,sort int,uniqueid varchar,timestamp long,del boolean)");
        //营业点
        db.execSQL("create table site(i integer primary key autoincrement,id int,code varchar,name varchar,help varchar,sort int,description varchar,uniqueid varchar,timestamp long,del boolean)");
        //区域
        db.execSQL("create table area(i integer primary key autoincrement,id int,code varchar,name varchar,help varchar,site varchar,sort int,description varchar,uniqueid varchar,timestamp long,del boolean)");
        db.execSQL("create table lt(i integer primary key autoincrement,code varchar,lt boolean,ltype varchar,ltnum int,ltmax int)");
        /**
         * State   状态   F  表示空台，T表示已经被占用
         Code  台号编码
         Name  台号名称
         HelpCode  首字母
         TableCls    桌台类型  如 散台  包厢等
         SiteCode   桌台所属营业点
         Area         桌台所属区域  如1楼，2楼
         MinConsumn  最低消费  忽略
         MaxGstNum    最大客人数
         DefaultGstCount 开单时  默认客人数
         Description      桌台说明
         Locked             是否锁定

         */
        //桌台
        db.execSQL("create table  board(i integer primary key autoincrement,id int,code varchar,name varchar,help varchar,table_class varchar,area varchar,max int,description varchar,locked boolean,uniqueid varchar,timestamp long,del boolean)");
        //分类
        db.execSQL("create table menu (i integer primary key autoincrement,id int,parent_code varchar,code varchar,name varchar,help varchar,name_en varchar,level int,sitecode varchar,sort int, description varchar,uniqueid varchar,timestamp long,del boolean)");
        //口味
        db.execSQL("create table cook(i integer primary key autoincrement,id int,cookclass varchar,code varchar,name varchar,help varchar,public boolean,price double,sort int,description varchar,uniqueid varchar,timestamp long,del boolean)");
        //口味类别
        db.execSQL("create table cook_class(i integer primary key autoincrement,id int,code varchar,name varchar,sort int,description varchar,uniqueid varchar,timestamp long,del boolean,MultySelect boolean)");
        //口味关系
        db.execSQL("create table menu_cook(i integer primary key autoincrement,id int,menucode varchar,cookcode varchar,require boolean,price double,uniqueid varchar,timestamp long,del boolean)");
        //口味分类
        db.execSQL("create table cook_lit(i integer primary key autoincrement,menucode varchar,cookcls varchar,require  boolean,uniqueid varchar,del boolean)");
        //用户
        db.execSQL("create table user(i integer primary key autoincrement,code varchar,description varchar,pwd varchar,locked boolean,uniqueid varchar,timestamp long,del boolean)");
        //套餐
        db.execSQL("create table detail(i integer primary key autoincrement,id int ,suitcode varchar,code varchar,number int,price double,require boolean,uniqueid varchar,timestamp long,del boolean)");
        //菜篮子
        db.execSQL("CREATE TABLE dish_table(i integer primary key autoincrement,code varchar,name varchar,name_en varchar,unit varchar,price double,price1 double,price2 double,more boolean,price_modify boolean,weigh boolean,discount boolean,temp boolean,suit boolean,count double,cook_codes varchar,cook_names varchar,cook_prices varchar,remark varchar,details varchar,detailNames varchar,flag long,lt int,mst boolean,must_value boolean,listStr varchar,extra int,tc_cook_codes varchar,tc_cook_names varchar,tc_cook_prices varchar,tcListStr varchar,tcMustNum varchar,tcActualNum varchar,orderMinLimit double,isEditPrice boolean,isjj boolean);");
        db.execSQL("create table mgroup(i integer primary key autoincrement,id int,mcode varchar,gcode varchar,gname varchar,description varchar,max_num int,min_num int,sort int,timestamp long,del boolean)");
        db.execSQL("create table mgdetail(i integer primary key autoincrement,id int,mcode varchar,gcode varchar ,scode varchar,num int,price double,require boolean,sort int,timestamp long,del boolean)");
        db.execSQL("create table tcReason(i integer primary key autoincrement,id int ,code varchar,name varchar,sort int,timestamp long,del boolean)");
        db.execSQL("create table tableType(i integer primary key autoincrement,id int ,code varchar,name varchar,sort int,timestamp long,del boolean,extra varchar)");
        db.execSQL("CREATE TABLE temp_table(i integer,code varchar,name varchar,name_en varchar,unit varchar,price double,price1 double,price2 double,more boolean,price_modify boolean,weigh boolean,discount boolean,temp boolean,suit boolean,count double,cook_codes varchar,cook_names varchar,cook_prices varchar,remark varchar,details varchar,detailNames varchar,flag long,lt int,mst boolean,must_value boolean,listStr varchar,extra int,tc_cook_codes varchar,tc_cook_names varchar,tc_cook_prices varchar,tcListStr varchar,tcMustNum varchar,tcActualNum varchar,orderMinLimit double,isEditPrice boolean,isjj boolean,tableCode varchar);");


        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void drop(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL("DROP TABLE IF EXISTS dish");
        db.execSQL("DROP TABLE IF EXISTS images");
        db.execSQL("DROP TABLE IF EXISTS remark");
        db.execSQL("DROP TABLE IF EXISTS site");
        db.execSQL("DROP TABLE IF EXISTS area");
        db.execSQL("DROP TABLE IF EXISTS board");
        db.execSQL("DROP TABLE IF EXISTS menu");
        db.execSQL("DROP TABLE IF EXISTS cook");
        db.execSQL("DROP TABLE IF EXISTS cook_class");
        db.execSQL("DROP TABLE IF EXISTS menu_cook");
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS lt");
        db.execSQL("DROP TABLE IF EXISTS detail");
        db.execSQL("DROP TABLE IF EXISTS dish_table");
        db.execSQL("DROP TABLE IF EXISTS mgroup");
        db.execSQL("DROP TABLE IF EXISTS mgdetail");
        db.execSQL("DROP TABLE IF EXISTS tcReason");
        db.execSQL("DROP TABLE IF EXISTS tableType");
        db.execSQL("DROP TABLE IF EXISTS cook_lit");
        db.execSQL("DROP TABLE IF EXISTS temp_table");
        db.setTransactionSuccessful();
        db.endTransaction();
        //此处是删除数据表，在实际的业务中一般是需要数据备份的
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        P.c("oldVersion" + oldVersion + "newVersion" + newVersion);
        drop(db);
        create(db);
    }


    public boolean checkDataBase() {
        SQLiteDatabase db = null;
        try {
            String databaseFilename = DATABASE_PATH + Common.DB_NAME;
            db = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {

        }
        if (db != null) {
            db.close();
        }
        return db != null ? true : false;
    }


}
