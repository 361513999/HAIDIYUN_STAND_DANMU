
package com.android.settings;

import com.android.settings.MessageBean;
interface OrderManager{
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   // void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
  //          double aDouble, String aString);
//
     MessageBean getDemand();//连接成功后获取
     List<MessageBean> getDemandlist();//获取所有广告位的信息
     void setDemandIn(in MessageBean msg);//数据传给广告APP
     void setDemandOut(out MessageBean msg);//待定
}
