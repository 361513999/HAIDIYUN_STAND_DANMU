package pad.stand.com.haidiyun.cy.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import java.lang.ref.WeakReference;

import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.ui.BaseActivity;

public abstract class CyBaseActivity extends BaseActivity {
    public LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        base_handler = new Base_Handler(CyBaseActivity.this);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    }
    public Handler getHandler(){
        return  base_handler;
    }
    private Base_Handler base_handler;

    /**
     * 接收handler消息处理方法
     *
     * @param what
     */
    public abstract void process(Message msg);

    private class Base_Handler extends Handler {
        WeakReference<BaseActivity> mLeakActivityRef;

        public Base_Handler(BaseActivity leakActivity) {
            mLeakActivityRef = new WeakReference<BaseActivity>(leakActivity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (mLeakActivityRef.get() != null) {
                process(msg);
            }
        }
    }


    @Override
    public void keyEvent(KeyEvent event) {

    }


}
