package pad.stand.com.haidiyun.www.common;

import android.hardware.UsbSupply;

public class Led {
    //static SharedUtils sharedUtils = new SharedUtils(Common.CONFIG);
    public static void led(int index){

        close();
        UsbSupply.LedControlOpen();
        UsbSupply.LedControlSet(index);
        //sharedUtils.setIntValue("led",index);
    }

    public static void ledClose(){
        UsbSupply.LedControlClose();
    }

    public static void close(){

        UsbSupply.LedControlOpen();
        UsbSupply.LedControlSet(2);
        UsbSupply.LedControlSet(4);
        UsbSupply.LedControlSet(6);
    }
    public static void reset(int index){

        close();
        UsbSupply.LedControlOpen();

        UsbSupply.LedControlSet(index);
       // sharedUtils.setIntValue("led",index);

    }
}
