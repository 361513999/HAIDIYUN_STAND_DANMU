package pad.com.haidiyun.www.common;

import android.annotation.SuppressLint;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author  
 * 
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	public static String getTimeAll(long time){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return formatter.format(time);
	}
	public static String getTimeLog(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(new Date(time));
	}
	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(time));
	}
	public static String getDate(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
		return format.format(new Date(time));
	}
	public static String getDate_(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(time));
	}
	public static String getNextDay(String date, boolean next){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d1 = df.parse(date);
			Calendar g = Calendar.getInstance();
			g.setTime(d1);
			if(next){
				g.add(Calendar.DAY_OF_MONTH,1);
			}else{
				g.add(Calendar.DAY_OF_MONTH,-1);
			}
			Date d2 = g.getTime();
			return  df.format(d2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return getDate(System.currentTimeMillis());
	}

	public static String getNextDate(String date, boolean next){
		DateFormat df = new SimpleDateFormat("yyyy.M");
		try {
			Date d1 = df.parse(date);
			Calendar g = Calendar.getInstance();
			g.setTime(d1);
			if(next){
				g.add(Calendar.MONTH,1);
			}else{
				g.add(Calendar.MONTH,-1);
			}
			Date d2 = g.getTime();
			return  df.format(d2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return getSelectDate(System.currentTimeMillis());
	}
	public static String getSelectDate(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.M");
		return format.format(new Date(time));
	}
	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "浠婂ぉ " + getHourAndMin(timesamp);
			break;
		case 1:
			result = "鏄ㄥぉ " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "鍓嶅ぉ " + getHourAndMin(timesamp);
			break;

		default:
			// result = temp + "澶╁墠 ";
			result = getTime(timesamp);
			break;
		}

		return result;
	}

	public static double doubleReverse(Double price) {
		BigDecimal b = new BigDecimal(price);
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
}
