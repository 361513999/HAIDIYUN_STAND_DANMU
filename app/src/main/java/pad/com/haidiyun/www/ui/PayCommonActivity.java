package pad.com.haidiyun.www.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pad.stand.com.haidiyun.www.R;

/**
 * Created by Administrator on 2017/9/6/006.
 */

public class PayCommonActivity extends Fragment {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flip_next_layout, container, false);
        return view;

    }
}
