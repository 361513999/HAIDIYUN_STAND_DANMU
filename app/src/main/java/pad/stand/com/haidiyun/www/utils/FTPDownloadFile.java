package pad.stand.com.haidiyun.www.utils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import pad.stand.com.haidiyun.www.base.BaseApplication;
import pad.stand.com.haidiyun.www.common.Common;
import pad.stand.com.haidiyun.www.common.P;
import pad.stand.com.haidiyun.www.common.SharedUtils;
import pad.stand.com.haidiyun.www.inter.FtpListener;
import pad.stand.com.haidiyun.www.inter.UpApk;
/**
 * A program demonstrates how to upload files from local computer to a remote
 * FTP server using Apache Commons Net API.
 */
public class FTPDownloadFile {

	private static int port = 21;
	private static String user = "jmj";
	private static String pass = "jmj";
	private SharedUtils utils;
	private FtpListener ftpListener;
	//瀹氫箟涓嬭浇鏂囦欢鏄痗onfig.json
	private String CONFIG = "config.json";
	//瀹氫箟涓嬭浇鏂囦欢璧勬簮鏄痵ource
	private String SOURCE = "source";
	//鏈嶅姟鍣ㄧ鐨剆ource
	private String VIDEO = "video";
	private String APK = "apk";
	private String ADVER = "adver";
	private Handler downMsg;
	public FTPDownloadFile(FtpListener ftpListener,Handler downMsg) {
		// TODO Auto-generated constructor stub
		this.ftpListener = ftpListener;
		this.downMsg = downMsg;
		utils = new SharedUtils(Common.CONFIG);
	}
	/**
	 * 涓婁紶鎶ラ敊鏃ュ織
	 */
	public void uploadBugLog(){
		
		String server = utils.getStringValue("IP");
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if(!log){
				ftpListener.login_status(log);
				return;
			}
		}catch(Exception e){
			
		}
	}
	/**
	 * 涓嬭浇瑙嗛
	 */
	private UpApk upApk;
	
	public void setUpListen(UpApk upApk){
		this.upApk = upApk;
	}
	
	/**
	 *  涓嬭浇骞垮憡
	 */
	public void downloadAdver() {
		String server = utils.getStringValue("IP");
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if(!log){
				ftpListener.login_status(log);
				return;
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setConnectTimeout(6000);
			/*if(getExistFile(file)){
				//瀛樺湪杩欐牱鐨勯厤缃枃锟?
			}*/
			File base = new File(Common.SD);
			if(!base.exists()){
				base.mkdirs();
			}
			//涓嬭浇閰嶇疆鏂囦欢
			File source = new File(Common.SOURCE_ADVER);
			if(!source.exists()){
				source.mkdirs();
			}
			//鍒囨崲鍒板彟锟?锟斤拷鐩綍涓嬮潰锟?
			ftpClient.changeWorkingDirectory(ADVER);
			FTPFile cfile[] = ftpClient.listFiles();
			//涓嬭浇璧勬簮鏂囦欢
			for (int i = 0; i < cfile.length; i++) {
				FTPFile ffi = cfile[i];
//				if (ffi.getName().endsWith(SOURCE)&&ffi.isDirectory()) {
					//杩涘叆source鏂囦欢锟?
					//涓嬭浇source涓嬮潰鐨勫叏閮ㄦ枃锟?
					File temp = new File(Common.SOURCE_ADVER + ffi.getName());
					if (temp.exists()) {
						if (temp.length() != ffi.getSize()) {
						 
							Tip(ffi.getName());
							down1(ftpClient, ffi.getName(),Common.SOURCE_ADVER);
						}
					} else {
						// 鏂囦欢涓嶅瓨鍦紝锟?锟斤拷涓嬭浇
						Tip(ffi.getName());
						down1(ftpClient, ffi.getName(),Common.SOURCE_ADVER);
					}
					temp = null;
//				}
				ffi = null;
			}
			
			ftpListener.down_success(true);
		
		} catch (Exception ex) {
			ftpListener.down_success(false);
			ex.printStackTrace();
		} finally {
			close(ftpClient);
			
		}
		 
	}
	
	public void downloadApk(){

		String server = utils.getStringValue("IP");
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if(!log){
				ftpListener.login_status(log);
				return;
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setConnectTimeout(6000);
			 File apk_source = new File(Common.SOURCE_APK);
				if(!apk_source.exists()){
					apk_source.mkdirs();
				}
			//鍒囨崲鍒板彟锟?锟斤拷鐩綍涓嬮潰锟?
			ftpClient.changeWorkingDirectory(APK);
			FTPFile cfile[] = ftpClient.listFiles();
			//涓嬭浇璧勬簮鏂囦欢
			if(cfile.length!=0){
				if(cfile.length==1){
					FTPFile ffi = cfile[0];
					String current = BaseApplication.application.tripLittle(BaseApplication.application.getVersion());
					 String net = BaseApplication.application.tripLittle(ffi.getName().substring(0,ffi.getName().length()-4));
					 int cur = Integer.parseInt(current);
					 int ne = Integer.parseInt(net);
					 
					 System.out.println("cur"+cur+"    "+ne);
					 
					 if(cur<ne){
						//锟?锟斤拷鏇存柊涓嬭浇
						 downApk(ftpClient, ffi.getName());
						 upApk.success(Common.SOURCE_APK+ffi.getName());
					 }else{
						 //鏃犻』鏇存柊
						 upApk.info();
					 }
					ffi = null;
				}
			}
		} catch (Exception ex) {
			upApk.ex();
			ex.printStackTrace();
			P.c(ex.getLocalizedMessage());
		}  finally {
			close(ftpClient);
		}
	}
	/**
	 * 涓嬭浇瑙嗛
	 */
	public void downloadVideo(){

		String server = utils.getStringValue("IP");
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if(!log){
				ftpListener.login_status(log);
				return;
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setConnectTimeout(6000);
			File source = new File(Common.SOURCE_VIDEO);
			if(!source.exists()){
				source.mkdirs();
			}
			//鍒囨崲鍒板彟锟?锟斤拷鐩綍涓嬮潰锟?
			ftpClient.changeWorkingDirectory(VIDEO);
			FTPFile cfile[] = ftpClient.listFiles();
			//涓嬭浇璧勬簮鏂囦欢
			for (int i = 0; i < cfile.length; i++) {
				FTPFile ffi = cfile[i];
//				if (ffi.getName().endsWith(SOURCE)&&ffi.isDirectory()) {
					//杩涘叆source鏂囦欢锟?
					//涓嬭浇source涓嬮潰鐨勫叏閮ㄦ枃锟?
					File temp = new File(Common.SOURCE_VIDEO + ffi.getName());
					if (temp.exists()) {
						if (temp.length() != ffi.getSize()) {
						 
							Tip(ffi.getName());
							down(ftpClient, ffi.getName());
						}
					} else {
						// 鏂囦欢涓嶅瓨鍦紝锟?锟斤拷涓嬭浇
						Tip(ffi.getName());
						down(ftpClient, ffi.getName());
					}
					temp = null;
//				}
				ffi = null;
			}
			
			ftpListener.down_success(true);

		} catch (Exception ex) {
			ftpListener.down_success(false);
			ex.printStackTrace();
		} finally {
			close(ftpClient);
		}
	}
	
	
	/**
	 *  涓嬭浇鍥剧墖
	 */
	public void downloadStand() {
		String server = utils.getStringValue("IP");
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(server, port);
			boolean log = ftpClient.login(user, pass);
			if(!log){
				ftpListener.login_status(log);
				return;
			}
			
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setConnectTimeout(6000);
			FTPFile file[] = ftpClient.listFiles();
			/*if(getExistFile(file)){
				//瀛樺湪杩欐牱鐨勯厤缃枃锟?
			}*/
			File base = new File(Common.SD);
			if(!base.exists()){
				base.mkdirs();
			}
			//涓嬭浇閰嶇疆鏂囦欢
			for (int i = 0; i < file.length; i++) {
				FTPFile ffi = file[i];
				if (ffi.getName().endsWith(CONFIG)&&ffi.isFile()) {
					File temp = new File(Common.SD + ffi.getName());
					if (temp.exists()) {
						if (temp.length() != ffi.getSize()) {
							 
							Tip(ffi.getName());
							down1(ftpClient, ffi.getName(),Common.SD);
						}
					} else {
						// 鏂囦欢涓嶅瓨鍦紝锟?锟斤拷涓嬭浇
						Tip(ffi.getName());
						down1(ftpClient, ffi.getName(),Common.SD);
					}
					temp = null;
				}
				ffi = null;
			}
			File source = new File(Common.SOURCE);
			if(!source.exists()){
				source.mkdirs();
			}
			//鍒囨崲鍒板彟锟?锟斤拷鐩綍涓嬮潰锟?
			ftpClient.changeWorkingDirectory(SOURCE);
			FTPFile cfile[] = ftpClient.listFiles();
			//涓嬭浇璧勬簮鏂囦欢
			for (int i = 0; i < cfile.length; i++) {
				FTPFile ffi = cfile[i];
//				if (ffi.getName().endsWith(SOURCE)&&ffi.isDirectory()) {
					//杩涘叆source鏂囦欢锟?
					//涓嬭浇source涓嬮潰鐨勫叏閮ㄦ枃锟?
					File temp = new File(Common.SOURCE + ffi.getName());
					if (temp.exists()) {
						if (temp.length() != ffi.getSize()) {
							Tip(ffi.getName());
							down1(ftpClient, ffi.getName(),Common.SOURCE);
						}
					} else {
						// 鏂囦欢涓嶅瓨鍦紝锟?锟斤拷涓嬭浇
						Tip(ffi.getName());
						down1(ftpClient, ffi.getName(),Common.SOURCE);
					}
					temp = null;
//				}
				ffi = null;
			}
			ftpListener.down_success(true);
		} catch (Exception ex) {
			ftpListener.down_success(false);
			ex.printStackTrace();
		} finally {
			close(ftpClient);
		}
		 
	}
	 
	private void Tip(String obj){
		Message msg = new Message();
		msg.what = 2;
		msg.obj = obj;
		downMsg.sendMessage(msg);
	}
	/**
	 *  鍒ゆ柇鏂囦欢鏄惁瀛樺湪
	 * @param file
	 * @return
	 */
	public  boolean getExistFile(FTPFile file[]) {
		for (int i = 0; i < file.length; i++) {
			FTPFile ffi = file[i];
			if (ffi.isFile() && ffi.getName().equals(CONFIG)) {
				return true;
			}
		}
		return false;
	}
	/**
	 *  鍒ゆ柇鏂囦欢澶规槸鍚﹀瓨锟?
	 * @param file
	 * @return
	 */
	public  boolean getExistDirectory(FTPFile file[]) {
		for (int i = 0; i < file.length; i++) {
			FTPFile ffi = file[i];
			if (ffi.isDirectory() && ffi.getName().equals(SOURCE)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 涓嬭浇浜嬩欢
	 */
	private  void down1(FTPClient ftpClient, String ftpFile,String base) {
		try {
			String remoteFile2 = ftpFile;
			File downloadFile2 = new File(base + ftpFile);
			OutputStream outputStream2 = new BufferedOutputStream(
					new FileOutputStream(downloadFile2));
			InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
			byte[] bytesArray = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(bytesArray)) != -1) {
				outputStream2.write(bytesArray, 0, bytesRead);
			}
			boolean success = ftpClient.completePendingCommand();
			if (success) {
				P.c("鎴愬姛涓嬭浇" + ftpFile);
			}
			outputStream2.close();
			inputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void down(FTPClient ftpClient, String ftpFile) {
		try {
			 

			String remoteFile1 = ftpFile;
			File downloadFile1 = new File(new String((Common.SOURCE_VIDEO+ ftpFile).getBytes("ISO-8859-1"),
					"GBK"));
			OutputStream outputStream1 = new BufferedOutputStream(
					new FileOutputStream(downloadFile1));
			boolean success = ftpClient
					.retrieveFile(remoteFile1, outputStream1);
			outputStream1.close();

			if (success) {
//				LogUtils.print("File #1 has been downloaded successfully.");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void downApk(FTPClient ftpClient, String ftpFile) {
		try {
			 

			String remoteFile1 = ftpFile;
			File downloadFile1 = new File(new String((Common.SOURCE_APK+ ftpFile).getBytes("ISO-8859-1"),
					"GBK"));
			OutputStream outputStream1 = new BufferedOutputStream(
					new FileOutputStream(downloadFile1));
			boolean success = ftpClient
					.retrieveFile(remoteFile1, outputStream1);
			outputStream1.close();

			if (success) {
//				LogUtils.print("File #1 has been downloaded successfully.");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 鍏抽棴杩炴帴
	private  void close(FTPClient ftpClient) {
		try {
			if (ftpClient.isConnected()) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}