package com.zc.changeskin.attr;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.zc.changeskin.constant.SkinConfig;
import java.util.ArrayList;
import java.util.List;
import pad.stand.com.haidiyun.www.R;

/**
 */
public class SkinAttrSupport {

	private static SkinAttrType getSupportAttrType(String attrName) {
		for (SkinAttrType attrType : SkinAttrType.values()) {
			if (attrType.getAttrType().equals(attrName))
				return attrType;
		}
		return null;
	}

	/**
	 * 浼犲叆activity锛屾壘鍒癱ontent鍏冪礌锛岋拷?褰掗亶鍘嗘墍鏈夌殑瀛怴iew锛屾牴鎹畉ag鍛藉悕锛岃褰曢渶瑕佹崲鑲ょ殑View
	 * 
	 * @param activity
	 */
	public static List<SkinView> getSkinViews(Object activity) {
		List<SkinView> skinViews = new ArrayList<SkinView>();
		if(activity instanceof Activity){
			ViewGroup content = (ViewGroup) ((Activity)activity)
					.findViewById(android.R.id.content);
			addSkinViews(content, skinViews);
		}else if (activity instanceof View) {
			addSkinViews((View)activity, skinViews);
		}
		
		return skinViews;
	}
	public static void addSkinViews(View view, List<SkinView> skinViews) {
		SkinView skinView = getSkinView(view);
		if (skinView != null)
			skinViews.add(skinView);
		if (view instanceof ViewGroup) {
			ViewGroup container = (ViewGroup) view;
			for (int i = 0, n = container.getChildCount(); i < n; i++) {
				View child = container.getChildAt(i);
				addSkinViews(child, skinViews);
			}
		}

	}

	public static SkinView getSkinView(View view) {
		Object tag = view.getTag(R.id.skin_tag_id);
		if (tag == null) {
			tag = view.getTag();
		}
		if (tag == null)
			return null;
		if (!(tag instanceof String))
			return null;
		String tagStr = (String) tag;

		List<SkinAttr> skinAttrs = parseTag(tagStr);
		if (!skinAttrs.isEmpty()) {
			changeViewTag(view);
			return new SkinView(view, skinAttrs);
		}
		return null;
	}

	private static void changeViewTag(View view) {
		Object tag = view.getTag(R.id.skin_tag_id);
		if (tag == null) {
			tag = view.getTag();
			view.setTag(R.id.skin_tag_id, tag);
			view.setTag(null);
		}
	}

	// skin:left_menu_icon:src|skin:color_red:textColor
	private static List<SkinAttr> parseTag(String tagStr) {
		List<SkinAttr> skinAttrs = new ArrayList<SkinAttr>();
		if (TextUtils.isEmpty(tagStr))
			return skinAttrs;

		String[] items = tagStr.split("[|]");
		for (String item : items) {
			if (!item.startsWith(SkinConfig.SKIN_PREFIX))
				continue;
			String[] resItems = item.split(":");
			if (resItems.length != 3)
				continue;

			String resName = resItems[1];
			String resType = resItems[2];

			SkinAttrType attrType = getSupportAttrType(resType);
			if (attrType == null)
				continue;
			SkinAttr attr = new SkinAttr(attrType, resName);
			skinAttrs.add(attr);
		}
		return skinAttrs;
	}
}
