package pad.stand.com.haidiyun.www.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.hdy.upload.aidl.IUploadService;

import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.SharedUtils;

public class B_Service extends Service {
    SharedUtils sharedUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedUtils = new SharedUtils(Common.CONFIG);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return iBackService;
    }

    IUploadService.Stub iBackService = new IUploadService.Stub() {

        @Override
        public String getMark() throws RemoteException {
            String table_code = sharedUtils.getStringValue("table_name");
            if (table_code.equals("")) {
                return "";
            } else {
                return table_code;
            }
        }

        @Override
        public String getStatusCD() throws RemoteException {

            return "testDate";
        }

    };


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
