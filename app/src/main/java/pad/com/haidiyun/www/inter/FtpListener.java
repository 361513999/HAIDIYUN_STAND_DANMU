package pad.com.haidiyun.www.inter;

public interface FtpListener {
	/**
	 * 登录状态
	 * @param is
	 */
   public void login_status(boolean is);
   /**
    * 数据下载成功
    */
   public void down_success(boolean is);

}
