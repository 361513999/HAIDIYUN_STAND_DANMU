package pad.com.haidiyun.www.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.hdy.upload.aidl.IUploadService;

import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.SharedUtils;

public class B_Service extends Service {
    SharedUtils sharedUtils;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        sharedUtils = new SharedUtils(Common.CONFIG);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub
            return "testDate";
        }

    };


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
