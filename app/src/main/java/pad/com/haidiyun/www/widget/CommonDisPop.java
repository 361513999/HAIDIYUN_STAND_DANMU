package pad.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.DisAdapter;
import pad.com.haidiyun.www.bean.Dis;
import pad.com.haidiyun.www.db.DB;

public class CommonDisPop {
	 private Context context;
	/**
	 * 下单提示
	 */
	private IDialog dlg;
	private Handler menuSelect;
    private String name;
	public CommonDisPop(Context context, Handler menuSelect, String name) {
		this.context = context;
		this.menuSelect = menuSelect;
		this.name = name;
	}
	public Dialog showSheet() {
		  dlg = new IDialog(context, R.style.menu_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.flip_menu_pop_view, null);
		ListView menus_list= (ListView) layout.findViewById(R.id.menus_list);
		final ArrayList<Dis> menuBeans =  DB.getInstance().getDiss();
		DisAdapter menusAdapter = new DisAdapter(context, menuBeans);
		menus_list.setAdapter(menusAdapter);
		menus_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
				Map<String,String> map = new HashMap<>();
				map.put("name",name);
				map.put("dis",menuBeans.get(arg2).getCode());
				Message msg = new Message();
				msg.what = 67;
				msg.obj = map;
				menuSelect.sendMessage(msg);




				if(dlg!=null){//咖喱 15  跑到炒饭去了      炒饭16  跑到面食去了
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
