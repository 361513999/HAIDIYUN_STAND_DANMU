package pad.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.RessonMenuAdapter;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.bean.ResonMenuBean;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.ReasonI;
import pad.com.haidiyun.www.widget.dialog.Animations.Effectstype;

public class CommonResPop {
    private static final String TAG = "CommonResPop";
    private Context context;
    /**
     * 点击做法
     */
    private IDialog dlg;
    private ReasonI reasonI;
    private ArrayList<ResonMenuBean> resonMenuBeans;
    private DishTableBean dishTableBean;
    private FouceBean foodsBean;
    int flagMust = 0;
    int leftIndex = 0;
    List<String> listIndex;
    List<String> listTcIndex;
    int extra;
    boolean isTrue = false;
    int position;
    String clickCode = "";
    private View parent_d;
    int value = 0;

    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    menuAdapter.updata(resonMenuBeans);
                    if (resonMenuBeans.size() != 0) {
                        select(selectMenu);
                    }
                    int size = resonMenuBeans.size();
                    if (resonMenuBeans.size() != 0) {
                        for (int i = 0; i < size; i++) {
                            boolean isMust = resonMenuBeans.get(i).isMustSelect();
                            if (isMust) {//必选(比如成度是必选的,程度+1)
                                flagMust++;
                            }
                        }
                    }
                    break;
                case 1://点击左边
                    leftIndex = (int) msg.obj;
                    reason_list.removeAllViews();
                    reason_list.removeAllViewsInLayout();
                    int count = reasonBeans.size();
                    P.c("分类下面的做法" + count);
                    CheckBox boxs[] = new CheckBox[count];
                    CheckBox cb = null;
                    Set<String> keys = resMap.keySet();
                    Set<String> tcKeys = tcResMap.keySet();
                    for (int i = 0; i < count; i++) {
                        cb = (CheckBox) CheckBox.inflate(context, R.layout.flip_reason_ck, null);
                        LayoutParams pa = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        pa.setMargins(2, 2, 2, 2);
                        cb.setLayoutParams(pa);
                        ReasonBean bean = reasonBeans.get(i);
                        cb.setEms(bean.getName().length() + 2);
                        cb.setText(bean.getName());
                        cb.setTag(bean.getCode());
                        cb.setTag(R.id.reason_id, bean.getPrice());
                        boxs[i] = cb;
                        P.c("绘制" + boxs[i].getTag().toString());
                        if (keys.contains(boxs[i].getTag().toString())) {//resmap
                            boxs[i].setChecked(true);
                        }
                        //tcresmap存的是所有的,待修改
                        if (tcKeys.contains(boxs[i].getTag().toString())) {//tcresmap
                            boxs[i].setChecked(true);
                        }
                        reason_list.addView(boxs[i]);
                        cb = null;
                    }
                    //count右边item数量
                    for (int j = 0; j < count; j++) {
                        boxs[j].setOnCheckedChangeListener(cl);
                    }
                    send.setEnabled(true);
                    break;

                default:
                    break;
            }
        }

        ;
    };

    public CommonResPop(Context context, ReasonI reasonI, DishTableBean dishTableBean, FouceBean foodsBean) {
        this.context = context;
        this.reasonI = reasonI;
        this.dishTableBean = dishTableBean;
        this.foodsBean = foodsBean;
    }

    public CommonResPop(Context context, ReasonI reasonI, DishTableBean dishTableBean, FouceBean foodsBean, boolean isTrue, int position, String clickCode) {
        this.context = context;
        this.reasonI = reasonI;
        this.dishTableBean = dishTableBean;
        this.foodsBean = foodsBean;
        this.isTrue = isTrue;
        this.position = position;
        this.clickCode = clickCode;
    }

    private boolean checkMul() {
        if (!isMultySelect) {
            for (int i = 0; i < reasonBeans.size(); i++) {

                if (resMap.containsKey(reasonBeans.get(i).getCode())) {
                    P.c("存在了");
                    return true;
                }
            }
        }
        P.c("不存在了");
        return false;
    }

    private boolean checkMulTc() {
        if (!isMultySelect) {
            for (int i = 0; i < reasonBeans.size(); i++) {
                if (tcResMap.containsKey(reasonBeans.get(i).getCode())) {
                    P.c("存在了");
                    return true;
                }
            }
        }
        P.c("不存在了");
        return false;
    }

    private OnCheckedChangeListener cl = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            // TODO Auto-generated method stub
            if (arg0 instanceof CheckBox) {
                if (arg1) {//选中
                    ReasonBean bean = new ReasonBean();
                    bean.setCode(arg0.getTag().toString());
                    bean.setName(arg0.getText().toString());
                    bean.setPrice(Double.parseDouble(arg0.getTag(R.id.reason_id).toString()));
                    P.c("加入key" + arg0.getTag());

                    if (checkMul()) {//存在该值
                        arg0.setChecked(false);
                    } else {
                        P.c("resMap" + arg0.getTag().toString());
                        resMap.put(arg0.getTag().toString(), bean);
                        if (isMustSelect) {
                            numMap.put(arg0.getTag().toString(), bean);
                        }
                    }
                    if (isTrue) {
                        if (checkMulTc()) {//存在该值
                            arg0.setChecked(false);
                        } else {//不存在
                            tcResMap.put(arg0.getTag().toString(), bean);
                        }
                    }
                } else {//未选中
                    if (resMap.containsKey(arg0.getTag().toString())) {
                        P.c("移除key" + arg0.getTag());
                        resMap.remove(arg0.getTag().toString());
                    }
                    if (isTrue) {
                        if (tcResMap.containsKey(arg0.getTag().toString())) {
                            tcResMap.remove(arg0.getTag().toString());
                        }
                    }
                    if (numMap.containsKey(arg0.getTag().toString())) {
                        if (isMustSelect) {
                            numMap.remove(arg0.getTag().toString());
                        }
                    }
                }
                int index = leftIndex;
                if (isMustSelect) {
                    //再点的时候map被清空了
                    int sizeNum = numMap.size();//必选的
                    if (!isTrue && listIndex != null) {

                        if (sizeNum > 0 && !listIndex.contains(index + "")) {
                            listIndex.add(index + "");
                            extra++;
//                        把index存起来如果相同就不用加了
                        } else if (sizeNum == 0 && listIndex.contains(index + "")) {
                            if (extra > 0) {
                                listIndex.remove(index + "");
                                extra--;
                            }
                        }
                    } else if (isTrue) {//套餐的
                        if (sizeNum > 0 && !listTcIndex.contains(index + "")) {
                            listTcIndex.add(index + "");
                            extra++;
//                        把index存起来如果相同就不用加了
                        } else if (sizeNum == 0 && listTcIndex.contains(index + "")) {
                            if (extra > 0) {
                                listTcIndex.remove(index + "");
                                extra--;
                            }
                        }
                    }
                    numMap.clear();
                }
            }
        }
    };

    private TextView send;
    private AutoWrapLinearLayout reason_list;
    private ListView reason_menu;
    private RessonMenuAdapter menuAdapter;
    private int selectMenu = 0;
    private ArrayList<ReasonBean> reasonBeans;
    private boolean isMultySelect = false;
    private boolean isMustSelect = false;

    //点击左边栏
    private void select(final int index) {
        this.selectMenu = index;
        menuAdapter.selectPosition(index);

        new Thread() {
            public void run() {
                if (dishTableBean != null) {//套餐
                    //套餐列表
                    if (isTrue) {
                        reasonBeans = DB.getInstance().getCpBeans(resonMenuBeans.get(index).getCode(), clickCode);
                    } else {
                        reasonBeans = DB.getInstance().getCpBeans(resonMenuBeans.get(index).getCode(), dishTableBean.getCode());
                    }
                } else if (foodsBean != null) {
                    reasonBeans = DB.getInstance().getCpBeans(resonMenuBeans.get(index).getCode(), foodsBean.getCode());
                }
                isMultySelect = resonMenuBeans.get(index).isMultySelect();
                isMustSelect = resonMenuBeans.get(index).isMustSelect();
                Message msg = new Message();//请求消息错误
                msg.what = 1;
                msg.obj = index;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private Map<String, ReasonBean> resMap;
    private Map<String, ReasonBean> numMap;
    private Map<String, ReasonBean> tcResMap;
    HashMap<String, HashMap<String, ReasonBean>> listMap;
    HashMap<String, ArrayList<ReasonBean>> tcBeans;
    HashMap<String, String> tcListStr;

    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.menu_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.flip_reason_pop_view, null);
        parent_d = layout.findViewById(R.id.parent_d);
        dlg.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {
                FileUtils.start(Effectstype.Slit, parent_d);
            }
        });
        reason_menu = (ListView) layout.findViewById(R.id.reason_menu);
        resonMenuBeans = new ArrayList<ResonMenuBean>();
        menuAdapter = new RessonMenuAdapter(context, resonMenuBeans);
        reason_menu.setAdapter(menuAdapter);
        send = (TextView) layout.findViewById(R.id.send);
        send.setEnabled(false);
        TextView cancle = (TextView) layout.findViewById(R.id.cancle);
        reason_list = (AutoWrapLinearLayout) layout.findViewById(R.id.reason_list);
        new Thread() {
            public void run() {
                resonMenuBeans.clear();
                if (dishTableBean != null) {
                    value = DB.getInstance().getNum(dishTableBean.getI(), clickCode);
                    if (isTrue) {
                        resonMenuBeans = DB.getInstance().getResonMenuBeans(resonMenuBeans, clickCode);
                    } else {
                        resonMenuBeans = DB.getInstance().getResonMenuBeans(resonMenuBeans, dishTableBean.getCode());
                    }
                    //来自菜篮子
                    resMap = DB.getInstance().getSelectedRessons(dishTableBean.getI());
                    //------------------------------------------
                    listMap = DB.getInstance().getSelectedTcRessons(dishTableBean.getI(), clickCode);
                    tcBeans = DB.getInstance().getSelectedTcRessonsTcBeans(dishTableBean.getI(), clickCode);
                    String codeAll = dishTableBean.getSuitMenuDetail();
                    tcResMap = listMap.get(clickCode);
                    if (tcResMap == null) {
                        tcResMap = new HashMap<String, ReasonBean>();
                    }
                    if (tcBeans == null || tcBeans.size() == 0) {
                        tcBeans = new HashMap<String, ArrayList<ReasonBean>>();
                        if (codeAll != null) {
                            String[] all = codeAll.split(",");
                            for (int i = 0; i < all.length; i++) {
                                tcBeans.put(all[i], null);
                            }
                        }
                    }
                    tcListStr = DB.getInstance().getSelectedTcRessonsTcBeansListStr(dishTableBean.getI(), null);
                    HashMap<String, List<String>> map = DB.getInstance().getListStr(dishTableBean.getI(), null);
                    if (!isTrue && map != null) {
                        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                            listIndex = entry.getValue();
                            extra = entry.getKey().toString().split(",").length;
                        }
                    }
                    if (listIndex == null && !isTrue) {//并且不是套餐
                        listIndex = new ArrayList<String>();
                    }
                    //套餐为空
                    listTcIndex = new ArrayList<String>();
                    if (tcListStr == null || tcListStr.size() == 0) {
                        tcListStr = new HashMap<String, String>();
                        if (codeAll != null) {
                            String[] all = codeAll.split(",");
                            for (int i = 0; i < all.length; i++) {
                                tcListStr.put(all[i], null);
                            }
                        }
                    } else {//tcListStr不为空的情况
                        //如果是套餐获取一下套餐的初始值情况
                        String listStr = tcListStr.get(clickCode);
                        if (listStr != null) {
                            extra = listStr.split(",").length;
                        }
                        if (listStr != null && !"".equals(listStr)) {
                            String[] str = listStr.split(",");
                            for (int i = 0; i < str.length; i++) {
                                listTcIndex.add(str[i]);
                            }
                        }
                    }
                } else if (foodsBean != null) {//必选口味
                    resonMenuBeans = DB.getInstance().getResonMenuBeans(resonMenuBeans, foodsBean.getCode());
                    //来自开始点餐
                    resMap = new HashMap<String, ReasonBean>();
                    tcResMap = new HashMap<String, ReasonBean>();
                }
                //默认选择打开第一项
                handler.sendEmptyMessage(0);
            }
        }.start();
        numMap = new HashMap<String, ReasonBean>();
        reason_menu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                select(arg2);
            }
        });

        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int flag = flagMust;
                if ((flag > 0 && extra == flag) || flag == 0 || foodsBean != null) {
                    ArrayList<ReasonBean> beans = new ArrayList<ReasonBean>();
                    ArrayList<ReasonBean> listSuit = new ArrayList<ReasonBean>();
                    P.c("数量" + resMap.size());
                    if (resMap.size() != 0) {
                        Set<String> keys = resMap.keySet();
                        Iterator<String> it = keys.iterator();
                        while (it.hasNext()) {
                            String key = it.next();
                            P.c(key);
                            ReasonBean bean = resMap.get(key);
                            beans.add(bean);
                            key = null;
                        }
                        if (dishTableBean != null) {
                            //加listindex 加extra
                            StringBuffer s = new StringBuffer("");
                            if (isTrue) {
                                for (int i = 0; i < listTcIndex.size(); i++) {
                                    s = s.append(listTcIndex.get(i) + ",");
                                }
                            } else {
                                for (int i = 0; i < listIndex.size(); i++) {
                                    s = s.append(listIndex.get(i) + ",");
                                }
                            }
                            //加数据到套餐做法里面
                            if (isTrue) {//是套餐
                                if (tcResMap.size() != 0) {
                                    Set<String> keysTc = tcResMap.keySet();
                                    Iterator<String> itTc = keysTc.iterator();
                                    while (itTc.hasNext()) {
                                        String key = itTc.next();
                                        P.c(key);
                                        ReasonBean bean = tcResMap.get(key);
                                        listSuit.add(bean);
                                        key = null;
                                        tcBeans.put(clickCode, listSuit);
                                    }
                                }
//                                2,表示0,1,2中的2汤类
                                String sss = s.toString();
//                                1,2,3,
                                tcListStr.put(clickCode, s.toString());
                                reasonI.select(beans, dishTableBean, tcBeans, tcListStr, position, value);
                            } else {//普通菜品有必选
                                reasonI.select(beans, dishTableBean);
                                DB.getInstance().updateMustData(dishTableBean, true, s.toString(), extra);
                            }
                        } else if (foodsBean != null) {
                            reasonI.insert(beans, foodsBean, true);
                        }
                    } else {//resmap数量为0
                        if (dishTableBean != null) {
                            reasonI.init(dishTableBean);
                        } else if (foodsBean != null) {
                            //口味必选
                            reasonI.init(foodsBean);
                        }

                    }
                    if (dlg != null) {
                        dlg.cancel();
                        dlg = null;
                    }
                } else {//有必选做法未选择
                    if (dishTableBean != null) {
                        List<String> list;
                        if (!isTrue) {//套餐
                            list = DB.getInstance().getMustData(dishTableBean.getCode());
                        } else {
                            list = DB.getInstance().getMustData(clickCode);
                        }
                        StringBuffer mustData = new StringBuffer("");
                        int size = list.size();
                        for (int i = 0; i < size; i++) {
                            if (i != size - 1) {
                                mustData.append(list.get(i) + ",");
                            } else {
                                mustData.append(list.get(i));
                            }
                        }
                        NewDataToast.makeText(mustData + "为必选做法");
                        DB.getInstance().updateMustData(dishTableBean, false, "", 0);
                    } else if (foodsBean != null) {
//                        NewDataToast.makeText("为必选做法");

                    }
                }
            }
        });
        cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dlg != null) {
                    dlg.cancel();
                    dlg = null;
                }
            }
        });
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }
}
