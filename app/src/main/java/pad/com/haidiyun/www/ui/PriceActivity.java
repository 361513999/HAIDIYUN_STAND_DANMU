package pad.com.haidiyun.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import pad.stand.com.haidiyun.www.R;
import pad.stand.com.haidiyun.www.base.AppManager;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.stand.com.haidiyun.www.ui.BaseActivity;


/**
 * Created by Administrator on 2017/7/5/005.
 */

public class PriceActivity extends BaseActivity {

    /*@Override
    public void keyEvent(KeyEvent event) {

    }*/

    @Override
    public void keyEvent(KeyEvent event) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flip_price_view);
        Intent intent = getIntent();
        if (intent.hasExtra("obj")) {
            DishTableBean bean = (DishTableBean) intent.getSerializableExtra("obj");
            Intent inte = new Intent(PriceActivity.this,
                    CardValActivity.class);
            inte.putExtra("obj", bean);
            startActivityForResult(inte, ENTER_PRICE_VAL);
        } else {
            AppManager.getAppManager().finishActivity();
        }
    }

    private final int ENTER_PRICE_VAL = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENTER_PRICE_VAL && resultCode == 1000) {
            // 时价
//            if (data.hasExtra("obj")) {
//                CommonPricePop pricePop = new CommonPricePop(
//                        PriceActivity.this,
//                        (DishTableBean) data.getSerializableExtra("obj"));
//                pricePop.showSheet();
//            }
        }
    }
}
