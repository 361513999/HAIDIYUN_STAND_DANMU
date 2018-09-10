
package pad.com.haidiyun.www.download;

/**
 * Download listener, you add many listeners to a download task. And the
 * listeners can be auto removed after download task finished or you delete the
 * download task manually.
 * 
 * @author offbye@gmail.com
 */
public interface DownloadListener {
    /**
     * Download Finish
     * 
     * @param filepath
     */
    void onDownloadFinish(String filepath);

    /**
     * Download Start
     */
    void onDownloadStart();

    /**
     * Download Pause
     */
    void onDownloadPause();

    /**
     * Download Stop
     */
    void onDownloadStop();

    /**
     * Download Fail
     */
    void onDownloadFail();

    /**
     * Download Progress update
     * can be used to display speed and percent.
     * 
     * @param finishedSize 宸插畬鎴愮殑澶у皬
     * @param totalSize 涓嬭浇鐨勶拷?澶у皬
     * @param speed download speed
     */
    void onDownloadProgress(int finishedSize, int totalSize, int speed);
}
