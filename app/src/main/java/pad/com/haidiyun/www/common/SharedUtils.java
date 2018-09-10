package pad.com.haidiyun.www.common;

import android.content.Context;
import android.content.SharedPreferences;

import pad.stand.com.haidiyun.www.base.BaseApplication;

public class SharedUtils {
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	@SuppressWarnings("deprecation")
	public SharedUtils(String NAME) {
		// TODO Auto-generated constructor stub
		preferences = BaseApplication.application.getSharedPreferences(NAME,  Context.MODE_MULTI_PROCESS| Context.MODE_PRIVATE);
	}


	/**
	 * 娓呯┖
	 */
	public void clear() {
		editor = preferences.edit();
		editor.clear();
		editor.commit();
		
	}
	/**
	 * 鍒ゆ柇鏄惁瀛樺湪
	 *
	 * @param tag
	 * @return
	 */
	public boolean isHere(String tag) {
		return preferences.contains(tag);
	}

	/**
	 * 璁剧疆String绫诲瀷鐨剆haredpreferences
	 *
	 * @param key
	 * @return
	 */
	public void setStringValue(String key, String value) {
		editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	public void setIntValue(String key, int value){
		editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	public int getIntValue(String key){
		return preferences.getInt(key, 0);
	}
	/**
	 * 鑾峰緱String绫诲瀷鐨剆haredpreferences
	 *
	 * @param key
	 * @return
	 */
	public String getStringValue(String key) {
		return preferences.getString(key, "");
	}
	/**
	 */
	public String getValue(String key) {
		return preferences.getString(key, "0");
	}

	/**
	 * 璁剧疆boolean绫诲瀷鐨剆haredpreferences
	 *
	 * @param key
	 * @return
	 */
	public void setBooleanValue(String key, boolean value) {
		editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	public void clear(String key) {
		editor = preferences.edit();
		editor.remove(key);
		editor.commit();
	}
	/**
	 * 鑾峰緱boolean绫诲瀷鐨剆haredpreferences
	 *
	 * @param key
	 * @return
	 */
	public Boolean getBooleanValue(String key) {
		// 榛樿涓篺alse
		return preferences.getBoolean(key, false);
	}
}
