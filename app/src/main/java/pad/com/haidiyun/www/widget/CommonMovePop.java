package pad.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import pad.stand.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.MoveAdapter;
import pad.com.haidiyun.www.bean.MoveItem;
import pad.com.haidiyun.www.inter.LoginS;


public class CommonMovePop {
	 private Context context;
	/**
	 * 删除弹出框
	 */
	private IDialog dlg;
	private LinearLayout par;
	public CommonMovePop(Context context) {
		this.context = context;
	}
	  private Bitmap drawableToBitamp(Drawable drawable)
      {
	  Bitmap bitmap;
          int w = drawable.getIntrinsicWidth();
          int h = drawable.getIntrinsicHeight();
          Bitmap.Config config =
                  drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                          : Bitmap.Config.RGB_565;
         bitmap = Bitmap.createBitmap(w,h,config);
         //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
         Canvas canvas = new Canvas(bitmap);
         drawable.setBounds(0, 0, w, h);   
         drawable.draw(canvas);
		return bitmap;
     }
	  private LoginS ls = new LoginS() {
		
		@Override
		public void success() {
			// TODO Auto-generated method stub
			sendPop = null;
			close();
		}
	};
	  private Common433SendPop sendPop;
	private ArrayList<MoveItem> moveItems = new ArrayList<MoveItem>();
	{
		add("加水","#EB6483");
		add("打包","#25DB61");
		add("加汤","#DAD966");
		add("催菜","#5AABF6");
		add("加餐具","#F42CE8");
		add("呼叫","#8D44F0");
		add("清洁","#ECBE64");
		add("加位","#30E5D6");
		add("结账","#3809F7");

	}
	private void add(String txt, String color){
		MoveItem i = new MoveItem();
		i.setTxt(txt);
		i.setColor(color);
		moveItems.add(i);

	}
	public Dialog showSheet() {
		DisplayMetrics dm = new DisplayMetrics();
		   dm = context.getResources().getDisplayMetrics();
		  dlg = new IDialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);		} else {			dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);		}
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.flip_float_service_foc, null);
		final LinearLayout content = (LinearLayout) layout.findViewById(R.id.content);

		par =(LinearLayout) layout.findViewById(R.id.par);
		par.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		final GridView views = (GridView) layout.findViewById(R.id.views);
		views.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				sendPop = new Common433SendPop(context,moveItems.get(position).getTxt().toString(),ls);
				sendPop.showSheet();
				close();
			}
		});
		dlg.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				MoveAdapter moveAdapter = new MoveAdapter(context,moveItems,(content.getWidth()-4)/3);
				views.setAdapter(moveAdapter);
			}
		});
		par.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				close();
			}
		});
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				return true;
			}
		});
		final int cFullFillWidth = dm.widthPixels;
		layout.setMinimumWidth(cFullFillWidth);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
	private void close(){
		if(dlg!=null&&dlg.isShowing()){
			dlg.cancel();
			dlg= null;
		}
	}
}
