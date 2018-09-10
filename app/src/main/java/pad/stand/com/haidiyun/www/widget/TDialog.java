package pad.stand.com.haidiyun.www.widget;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import pad.stand.com.haidiyun.www.common.P;

/**
 * 重新定义的dialog
 *
 * @author Administrator
 */
public class TDialog extends Dialog {
    private Context context;
    private L l;
    public TDialog(final Context context) {
        super(context);
        this.context = context;
        set();
//        calcelT();
        reg();
    }


    public TDialog(final Context context, int theme) {
        super(context, theme);
        this.context = context;

        reg();
        set();
//        calcelT();
//        reg();
    }
    private void close(){
        this.cancel();

    }
    private void reg(){
        l = new L();
        IntentFilter filterO = new IntentFilter();
        filterO.addAction("com.zed.play.start");
        context.registerReceiver(l, filterO);
    }
      class L extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("com.zed.play.start")){
                    close();
                }
        }
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(listener);
        Intent intentService = new Intent();
                intentService.setAction("pad.com.change.vis");
                context.sendBroadcast(intentService);
                if(l!=null){
                    context.unregisterReceiver(l);
                }
    }

    protected void calcelT(){
        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                P.c("发送~~~");
//                Intent intentService = new Intent();
//                intentService.setAction("pad.com.change.vis");
//                context.sendBroadcast(intentService);
//                if(l!=null){
//                    context.unregisterReceiver(l);
//                }
            }
        });
    }
    private void set() {
//		this.setOnKeyListener(new OnKeyListener() {
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//				if(event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_UP||event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN){
//					return  true;
//				}
//				return false;
//			}
//		});
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);

    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}
