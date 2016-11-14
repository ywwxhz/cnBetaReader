/* 

* Copyright (C) 2014 Peter Cai

*

* This file is part of BlackLight

*

* BlackLight is free software: you can redistribute it and/or modify

* it under the terms of the GNU General Public License as published by

* the Free Software Foundation, either version 3 of the License, or

* (at your option) any later version.

*

* BlackLight is distributed in the hope that it will be useful,

* but WITHOUT ANY WARRANTY; without even the implied warranty of

* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the

* GNU General Public License for more details.

*

* You should have received a copy of the GNU General Public License

* along with BlackLight. If not, see <http://www.gnu.org/licenses/>.

*/


package com.ywwxhz.lib;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.ywwxhz.lib.kits.UIKit;

import java.util.HashMap;
import static com.ywwxhz.cnbetareader.BuildConfig.DEBUG;


/*

This class maps emoticon strings to asset imgs

Thanks sina for those emoticons

*/

public class Emoticons{

	public static final String TAG = Emoticons.class.getSimpleName();

	public static final HashMap<String, String> EMOTICONS = new HashMap<String, String>();

	public static final HashMap<String, Bitmap> EMOTICON_BITMAPS = new HashMap<String, Bitmap>();

	public static final HashMap<String, Bitmap> EMOTICON_BITMAPS_SCALED = new HashMap<String, Bitmap>();


	static {

		EMOTICONS.put("[s:爱心]", "emoji/love.png");
		EMOTICONS.put("[s:汗]", "emoji/han.png");
		EMOTICONS.put("[s:黑]", "emoji/hei.png");
		EMOTICONS.put("[s:加班]", "emoji/jiaban.png");
		EMOTICONS.put("[s:贱笑]", "emoji/jianxiao.png");
		EMOTICONS.put("[s:惊讶]", "emoji/jingya.png");
		EMOTICONS.put("[s:抠鼻]", "emoji/koubi.png");
		EMOTICONS.put("[s:哭]", "emoji/ku.png");
		EMOTICONS.put("[s:喷]", "emoji/pen.png");
		EMOTICONS.put("[s:沙发]", "emoji/safa.png");
		EMOTICONS.put("[s:生气]", "emoji/angry.png");
		EMOTICONS.put("[s:双负五]", "emoji/fuwu.png");
		EMOTICONS.put("[s:笑]", "emoji/xiao.png");
		EMOTICONS.put("[s:晕]", "emoji/yun.png");

	}


	public static void init(Context context) {

		int size = UIKit.getFontHeight(context, 15f);


		if (DEBUG) {

			Log.d(TAG, "Font size = " + size);

		}


		AssetManager am = context.getAssets();

		for (String key : EMOTICONS.keySet()) {

			try {

				Bitmap bitmap = BitmapFactory.decodeStream(am.open(EMOTICONS.get(key)));

				EMOTICON_BITMAPS.put(key, bitmap);


				// Scale by font size

				Matrix matrix = new Matrix();

				matrix.postScale((float) size / bitmap.getWidth(), (float) size / bitmap.getHeight());


				if (DEBUG) {

					Log.d(TAG, "width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());

					Log.d(TAG, "scaleX = " + (float) size / bitmap.getWidth() + " scaleY = " + (float) size / bitmap.getHeight());

				}


				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

				EMOTICON_BITMAPS_SCALED.put(key, bitmap);

			} catch (Exception e) {

				// just jump it

				if (DEBUG) {

					Log.d(TAG, Log.getStackTraceString(e));

				}

			}

		}

	}


}
