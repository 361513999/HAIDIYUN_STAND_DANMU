package pad.stand.com.haidiyun.www.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.http.util.EncodingUtils;
import android.os.Handler;
import android.os.Looper;

import pad.stand.com.haidiyun.www.common.P;

/**
 */
public abstract class SocketTransceiver implements Runnable {

	protected Socket socket;
	protected InetAddress addr;
	protected DataInputStream in;
	protected DataOutputStream out;
	private boolean runFlag;
	private Handler handler = new Handler(Looper.getMainLooper());
	/**
	 */
	private Runnable runnable;
	public SocketTransceiver(Socket socket) {
		this.socket = socket;
		this.addr = socket.getInetAddress();
		runnable = new Runnable() {

			@Override
			public void run() {

				SocketTransceiver.this.readTimeOut(addr);
			}
		};
	}

	/**
	 */
	public InetAddress getInetAddress() {
		return addr;
	}

	/**
	 */
	public void start() {
		runFlag = true;
		new Thread(this).start();
	}

	/**
	 */
	public void stop() {
		runFlag = false;
		try {
			/*socket.shutdownOutput();
			socket.shutdownInput();
			in.close();
			out.close();
			socket.close();
			socket = null;*/
			handler.removeCallbacks(runnable);
			if (socket != null) {
				if (!socket.isInputShutdown()) {
					socket.shutdownInput();
				}
				if (!socket.isOutputShutdown()) {
					socket.shutdownOutput();
				}
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				socket.close();
				socket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 */
	public boolean send(String var) {
		if (out != null) {
			try {
				out.write(EncodingUtils.getBytes(var+"<EOF>", "UTF-16LE"));
				out.flush();
				handler.postDelayed(runnable,10000);

				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 */
	public boolean send(String var,int TIME_OUT) {
		if (out != null) {
			try {
				out.write(EncodingUtils.getBytes(var+"<EOF>", "UTF-16LE"));
				out.flush();
				handler.postDelayed(runnable, TIME_OUT);

				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean sendTxt(String var) {
		if (out != null) {
			try {
				out.write(EncodingUtils.getBytes(var, "UTF-8"));
				out.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 */
	private static final int BUF_SIZE = 1024 * 10;
	public void run() {
		try {
			in = new DataInputStream(this.socket.getInputStream());
			out = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			runFlag = false;
		}
		while (runFlag) {
			try {
				byte[] buffer = new byte[BUF_SIZE];
				int length = 0;
//				P.c("socket.isClosed()"+socket.isClosed());
//				P.c("socket.isInputShutdown()"+socket.isInputShutdown());
				while (socket!=null&&!socket.isClosed() && !socket.isInputShutdown()
						&& ((length = in.read(buffer)) != -1)){
//					P.c(EncodingUtils.getString(buffer, 0, length,"UTF-16LE"));
					//P.c(EncodingUtils.getString(buffer, 0, length,"UTF-16LE"));
					this.onReceive(addr, EncodingUtils.getString(buffer, 0, length,"UTF-16LE"));
				}
			} catch (IOException e) {
				runFlag = false;
			}
		}
		/*//
		try {
			in.close();
			out.close();
			socket.close();
			in = null;
			out = null;
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		this.onDisconnect(addr);
	}

	/**
	 */
	public abstract void onReceive(InetAddress addr, String s);

	/**
	 */
	public abstract void onDisconnect(InetAddress addr);
	/**
	 */
	public abstract void readTimeOut(InetAddress addr);
}
