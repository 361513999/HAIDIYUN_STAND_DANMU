package com.android.settings;

interface IBackService{
	void setTime(int hour,int minute);
	void setDate( int year, int month, int day);
	String getdeviceInfoString(String table ,int index) ;
}