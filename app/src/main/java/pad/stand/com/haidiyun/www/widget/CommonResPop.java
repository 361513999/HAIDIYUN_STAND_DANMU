package pad.stand.com.haidiyun.www.widget;

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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.adapter.RessonMenuAdapter;
import pad.stand.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.bean.FoodsBean;
import pad.stand.com.haidiyun.www.bean.ReasonBean;
import pad.stand.com.haidiyun.www.bean.ResonMenuBean;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.FileUtils;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.db.DB;
import pad.stand.com.haidiyun.www.inter.NumSel;
import pad.stand.com.haidiyun.www.inter.ReasonI;
import pad.stand.com.haidiyun.www.widget.dialog.Animations.Effectstype;

public class CommonResPop {
    private Context context;
    /**
     * 下单提示
     */
    private IDialog dlg;
    private ReasonI reasonI;
    private ArrayList<ResonMenuBean> resonMenuBeans = new ArrayList<ResonMenuBean>();
    private DishTableBean dishTableBean;
    private FoodsBean foodsBean;
    boolean isTrue = false;
    int position;
    String clickCode = "";
    int leftIndex = 0;
    private SharedUtils sharedUtils;

    public CommonResPop(Context context, ReasonI reasonI, DishTableBean dishTableBean, FoodsBean foodsBean) {
        this.context = context;
        this.reasonI = reasonI;
        this.dishTableBean = dishTableBean;
        this.foodsBean = foodsBean;
    }

    public CommonResPop(Context context, ReasonI reasonI, DishTableBean dishTableBean, FoodsBean foodsBean, boolean isTrue, int position, String clickCode) {
        this.context = context;
        this.reasonI = reasonI;
        this.dishTableBean = dishTableBean;
        this.foodsBean = foodsBean;
        this.isTrue = isTrue;
        this.position = position;
        this.clickCode = clickCode;
    }

    private View parent_d;
    private Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    menuAdapter.updata(resonMenuBeans);
                    if (resonMenuBeans.size() != 0) {
                        select(selectMenu);
                    }
                    break;
                case 1:
//                    leftIndex = (int) msg.obj;
                    reason_list.removeAllViews();
                    reason_list.removeAllViewsInLayout();
                    int count = reasonBeans.size();
                    P.c("分类下面的做法" + count);
                    CheckBox boxs[] = new CheckBox[count];
                    CheckBox cb = null;
                    Set<String> keys = resMap.keySet();
                    Set<String> tcKeys = tcResMap.keySet();
                    Iterator<String> it = keys.iterator();
                    for (int i = 0; i < count; i++) {
                        cb = (CheckBox) CheckBox.inflate(context, R.layout.reason_ck, null);
                        LayoutParams pa = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        pa.setMargins(2, 2, 2, 2);
//					 cb.setId((int)System.currentTimeMillis());
                        cb.setLayoutParams(pa);
                        ReasonBean bean = reasonBeans.get(i);
                        cb.setEms(bean.getName().length() + 2);
                        sharedUtils = new SharedUtils(Common.CONFIG);
                        if (sharedUtils.getBooleanValue("is_lan")) {
                            //变为英文
                            cb.setText(bean.getNameEn());
                        } else {
                            cb.setText(bean.getName());
                        }
                        cb.setTag(bean.getCode());
                        cb.setTag(R.id.reason_id, bean.getPrice());
                        boxs[i] = cb;
                        P.c("绘制" + boxs[i].getTag().toString());
                        if (keys.contains(boxs[i].getTag().toString())) {
                            boxs[i].setChecked(true);
                        }
                        //tcresmap存的是所有的,待修改
                        if (tcKeys.contains(boxs[i].getTag().toString())) {//tcresmap
                            boxs[i].setChecked(true);
                        }
                        /*while(it.hasNext()){
                            String key = it.next();
							P.c(boxs[i].getTag().toString()+"标记:"+key);
							if(boxs[i].getTag().toString().equals(key)){
								boxs[i].setChecked(true);
							}
							key = null;
						}*/


                        reason_list.addView(boxs[i]);
                        cb = null;
                    }
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

    private boolean checkMul() {
        P.c(reasonBeans.size() + "isMultySelect" + isMultySelect);
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


    private boolean isMustSelect = false;
    private OnCheckedChangeListener cl = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
            if (arg0 instanceof CheckBox) {
                if (arg1) {
                    ReasonBean bean = new ReasonBean();
                    bean.setCode(arg0.getTag().toString());
                    bean.setName(arg0.getText().toString());
                    bean.setPrice(Double.parseDouble(arg0.getTag(R.id.reason_id).toString()));
                    P.c("加入key" + arg0.getTag());


                    if (checkMul()) {
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
                } else {
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
                        if (listTcIndex != null) {
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

    private void select(final int index) {
        this.selectMenu = index;
        menuAdapter.selectPosition(index);
        new Thread() {
            public void run() {
                if (dishTableBean != null) {
                    reasonBeans = DB.getInstance().getCpBeans(resonMenuBeans.get(index).getCode(), dishTableBean.getCode());
                } else if (foodsBean != null) {

                    reasonBeans = DB.getInstance().getCpBeans(resonMenuBeans.get(index).getCode(), foodsBean.getCode());
                }
                isMultySelect = resonMenuBeans.get(index).isMultySelect();
                //待修改
                isMustSelect = resonMenuBeans.get(index).isMustSelect();
                Message msg = new Message();//请求消息错误
                msg.what = 1;
                msg.obj = index;
                leftIndex = index;
                handler.sendMessage(msg);
            }

            ;
        }.start();
    }

    private Map<String, String> mustSel = new HashMap<String, String>();
    private Map<String, ReasonBean> resMap;
    private TextView delete, view, add;
    int value = 0;
    private Map<String, ReasonBean> numMap;
    private Map<String, ReasonBean> tcResMap;
    HashMap<String, HashMap<String, ReasonBean>> listMap;
    HashMap<String, ArrayList<ReasonBean>> tcBeans;
    HashMap<String, String> tcListStr;
    List<String> listIndex;
    List<String> listTcIndex;
    int extra;

    public Dialog showSheet() {
        if (dishTableBean != null) {
            //来自菜篮子
            resMap = DB.getInstance().getSelectedRessons(dishTableBean.getI());
        } else if (foodsBean != null) {
            //来自开始点餐
            resMap = new HashMap<String, ReasonBean>();
        }

        dlg = new IDialog(context, R.style.menu_pop_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.reason_pop_view, null);
        parent_d = layout.findViewById(R.id.parent_d);
        dlg.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {

                FileUtils.start(Effectstype.Slit, parent_d);
            }
        });
        reason_menu = (ListView) layout.findViewById(R.id.reason_menu);
        menuAdapter = new RessonMenuAdapter(context, resonMenuBeans);
        reason_menu.setAdapter(menuAdapter);
        send = (TextView) layout.findViewById(R.id.send);
        send.setEnabled(false);
        TextView cancle = (TextView) layout.findViewById(R.id.cancle);
        //---
        reason_list = (AutoWrapLinearLayout) layout.findViewById(R.id.reason_list);

        new Thread() {
            public void run() {
                resonMenuBeans.clear();
                if (dishTableBean != null) {
                    //拿到所有必选code的map
                    value = DB.getInstance().getNum(dishTableBean.getI(), clickCode);
                    mustSel = DB.getInstance().getMustSel(dishTableBean.getCode());
                    if (isTrue) {
                        resonMenuBeans = DB.getInstance().getResonMenuBeans(resonMenuBeans, clickCode, mustSel);
                    } else {
                        resonMenuBeans = DB.getInstance().getResonMenuBeans(resonMenuBeans, dishTableBean.getCode(), mustSel);
                    }
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
//                        0,
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
                } else if (foodsBean != null) {
                    mustSel = DB.getInstance().getMustSel(foodsBean.getCode());
                    DB.getInstance().getResonMenuBeans(
                            resonMenuBeans, foodsBean.getCode(), mustSel);
                    tcResMap = new HashMap<String, ReasonBean>();
                }
                for (int i = 0; i < resonMenuBeans.size(); i++) {
                    P.c(resonMenuBeans.get(i).getCode() + "===>" + resonMenuBeans.get(i).isMustSel());
                }


                handler.sendEmptyMessage(0);
            }

            ;
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
                String temp = check();
                if (temp != null) {
                    NewDataToast.makeText(temp + "做法类必选");
                    return;
                }


///
                ArrayList<ReasonBean> beans = new ArrayList<ReasonBean>();
                ArrayList<ReasonBean> listSuit = new ArrayList<ReasonBean>();
                P.c("数量" + resMap.size());
                String numView = view.getText().toString();
                if (resMap.size() != 0) {
                    Set<String> keys = resMap.keySet();
                    Iterator<String> it = keys.iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        P.c(key);
                        beans.add(resMap.get(key));
                        key = null;
                    }
//                    String al = dishTableBean.toString();
                    if (dishTableBean != null) {
                        if (numView.equals("1")) {
                            numView = dishTableBean.getCount() + "";
                        }
                        //加listindex 加extra
                        StringBuffer s = new StringBuffer("");
                        if (listTcIndex != null) {
                            if (isTrue) {
                                for (int i = 0; i < listTcIndex.size(); i++) {
                                    s = s.append(listTcIndex.get(i) + ",");
                                }
                            } else {
                                for (int i = 0; i < listIndex.size(); i++) {
                                    s = s.append(listIndex.get(i) + ",");
                                }
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
//                            String sss = s.toString();
                            tcListStr.put(clickCode, s.toString());
                            reasonI.select(beans, dishTableBean, tcBeans, tcListStr, position, value);
                        } else {//普通菜品有必选
                            /*reasonI.select(beans, dishTableBean);
                            DB.getInstance().updateMustData(dishTableBean, true, s.toString(), extra);*/
                            reasonI.select(beans, dishTableBean, numView);
                        }
                    } else if (foodsBean != null) {
                        reasonI.insert(beans, foodsBean, numView);
                    }
                    /*if (dishTableBean != null) {
                        reasonI.select(beans, dishTableBean, numView);
                    } else if (foodsBean != null) {
                        reasonI.insert(beans, foodsBean, numView);
                    }*/
                } else {
                    if (dishTableBean != null) {
                        reasonI.init(dishTableBean, numView);
                    } else if (foodsBean != null) {
                        reasonI.init(foodsBean, numView);
                    }

                }

					/*if(beans.size()!=0){
                        //如果是-1那么不进行设置
						//选择了某个
						reasonI.select(beans,dishTableBean);
					}else{
						reasonI.init(dishTableBean);
//						NewDataToast.makeText(context, "请选择一份规格");
					}*/
                if (dlg != null) {
                    dlg.cancel();
                    dlg = null;
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
        delete = (TextView) layout.findViewById(R.id.delete);
        view = (TextView) layout.findViewById(R.id.view);
        add = (TextView) layout.findViewById(R.id.add);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                double num = 0;
                if (dishTableBean != null) {
                    num = dishTableBean.getOrderMinLimit();
                } else if (foodsBean != null) {
                    num = foodsBean.getOrderMinLimit();
                }
                CommonNum commonNum = new CommonNum(context, numSel, null, num);
                commonNum.showSheet();
            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = view.getText().toString();
                if (Double.parseDouble(txt) - 1 > 0) {
                    view.setText(String.valueOf(formatOne(Double.parseDouble(txt) - 1)));
                }
            }
        });
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = view.getText().toString();
                view.setText(String.valueOf(formatOne(Double.parseDouble(txt) + 1)));
            }
        });

        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

    private String check() {

        StringBuilder selectBuilder = new StringBuilder();
        if (resMap.size() != 0) {
            Set<String> keys = resMap.keySet();
            Iterator<String> it = keys.iterator();
            int coun = 0;
            while (it.hasNext()) {
                String key = it.next();
                P.c("点选的" + key);

                selectBuilder.append("'" + key + "'");
                coun++;
                if (coun != resMap.size()) {
                    selectBuilder.append(",");
                }
            }
        }
//        [004, 006, 003]
        ArrayList<String> select = DB.getInstance().getUnSel(selectBuilder.toString());


        for (int j = 0; j < resonMenuBeans.size(); j++) {
            P.c(resonMenuBeans.get(j).isMustSel() + "存在的类" + resonMenuBeans.get(j).getCode());
            if (resonMenuBeans.get(j).isMustSel()) {
                boolean is = true;
                for (int i = 0; i < select.size(); i++) {
                    if (resonMenuBeans.get(j).getCode().equals(select.get(i))) {
                        is = false;
                    }
                }
                if (is) {
                    return resonMenuBeans.get(j).getName();
                }


            }

        }

        return null;
    }

    private double formatOne(double total) {
        BigDecimal b = new BigDecimal(total);
        // 保留2位小数
        double total_v = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return total_v;
    }

    private NumSel numSel = new NumSel() {
        @Override
        public void change(String o, Object ob) {
            view.setText(String.valueOf(Double.parseDouble(o)));
        }

        @Override
        public void changeWeigh(String o, Object object, ArrayList<ReasonBean> resons, String remark, T_Image im) {

        }
    };
}
