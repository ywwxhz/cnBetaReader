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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BlackLight.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ywwxhz.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* This class is modified from qii/weiciyuan */
public class SpannableStringUtils
{
	private static final String TAG = SpannableStringUtils.class.getSimpleName();

	private static final Pattern PATTERN_EMOTICON = Pattern.compile("\\[(\\S+?)\\]");
	
	public static SpannableString span(Context context, String text) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(text);
		// Match Emoticons
		Matcher matcher = PATTERN_EMOTICON.matcher(text);
		while (matcher.find()) {
			// Don't be too long
			if (matcher.end() - matcher.start() < 8) {
				String iconName = matcher.group(0);
				Bitmap bitmap = Emoticons.EMOTICON_BITMAPS_SCALED.get(iconName);
				
				if (bitmap != null) {
					ImageSpan span = new ImageSpan(context, bitmap, ImageSpan.ALIGN_BASELINE);
					ssb.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return SpannableString.valueOf(ssb);
	}
}
