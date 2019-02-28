package com.talkfun.cloudlive.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by asus on 2015/11/24.
 */
public class ExpressionUtil {
    private static String TAG = ExpressionUtil.class.getName();
    public static int edtImgWidth = 50;
    public static int edtImgHeight = 50;
    public static int tvImgWidth = 0;
    public static int tvImgHeight = 0;

    /**
     * 添加表情
     *
     * @param context
     * @param imgId
     * @param character
     * @return
     */
    public static SpannableString getExpressionString(Context context, int imgId, String character) {
        if (TextUtils.isEmpty(character)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgId);
        bitmap = Bitmap.createScaledBitmap(bitmap,    edtImgWidth, edtImgHeight, true);
        ImageSpan imageSpan = new ImageSpan(context, bitmap);
        SpannableString spannable = new SpannableString(character);
        spannable.setSpan(imageSpan, 0, character.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
     *
     * @param context
     * @param resType 表情资源文件类型（drawable 或 mipmp)
     * @param content
     * @return
     */
    public static SpannableString getExpressionString(Context context, String content, String resType) {
        SpannableString spannableString = new SpannableString(content);
        // 正则表达式比配字符串里是否含有表情
        String pattenStr = "\\[[^\\]]+\\]";
        // 通过传入的正则表达式来生成一个pattern
        Pattern patten = Pattern.compile(pattenStr, Pattern.CASE_INSENSITIVE);
        try {
            spannableString = switchPattern(content, patten);
            dealExpression(context, resType, spannableString, patten, 0);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return spannableString;
    }


    /**
     * 将其它类型的图片正则转成已有图片的正则
     */
    private static SpannableString switchPattern(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        String cy = "";
        while (matcher.find()) {

            String key = matcher.group();
            if (matcher.start() < 0) {
                continue;
            }
            if (TextUtils.isEmpty(key)) {
                continue;
            }
            String resName = key.substring(1, key.length() - 1);
            cy = "";
            if (resName.equals("fl")) {
                cy = "flower";
            } else if (resName.equals("S_FLOWER")) {
                cy = "flower";
            } else if (resName.equals("lg")) {
                cy = "aha";
            } else if (resName.equals("qu")) {
                cy = "why";
            } else if (resName.equals("kl")) {
                cy = "pitiful";
            } else if (resName.equals("jy")) {
                cy = "amaz";
            } else if (resName.equals("fd")) {
                cy = "hard";
            } else if (resName.equals("gz")) {
                //下面四个表情不能匹配欢拓的表情
                cy = "cool";
            } else if (resName.equals("hx")) {
                cy = "love";
            } else if (resName.equals("tx")) {
                cy = "bye";
            } else if (resName.equals("ag")) {
                cy = "good";
            }

            if (!TextUtils.isEmpty(cy)) {
                content = content.substring(0, content.indexOf(key) + 1) +
                        cy +
                        content.substring(content.indexOf(key) + key.length() - 1, content.length());
            }
        }
        return new SpannableString(content);
    }


    /**
     * 去除表情
     *
     * @param content
     * @return
     */
    public static SpannableString removeExpression(Context context, String content) {
        // 正则表达式比配字符串里是否含有表情
        String pattenStr = "\\[[^\\]]+\\]";
        // 通过传入的正则表达式来生成一个pattern
        Pattern patten = Pattern.compile(pattenStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = patten.matcher(content);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < 0) {
                continue;
            }
            if (TextUtils.isEmpty(key)) {
                continue;
            }
            String resName = key.substring(1, key.length() - 1);
            int resId = context.getResources().getIdentifier(resName, "mipmap",
                    context.getPackageName());
            if (resId == 0) {
                continue;
            }
            content = content.substring(0, content.indexOf(key)) + content.substring(content.indexOf(key) + key.length(), content.length());
        }
        return new SpannableString(content);
    }

    /**
     * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
     *
     * @param context
     * @param spannableString
     * @param patten
     * @param start
     * @throws Exception
     */
    private static void dealExpression(Context context, String resType,
                                       SpannableString spannableString, Pattern patten, int start)
            throws Exception {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            // String value = emojiMap.get(key);
            if (TextUtils.isEmpty(key)) {
                continue;
            }
            String resName = key.substring(1, key.length() - 1);
            int resId = context.getResources().getIdentifier(resName, resType,
                    context.getPackageName());
            // 通过上面匹配得到的字符串来生成图片资源id，下边的方法可用，但是你工程混淆的时候就有事了，你懂的。不是我介绍的重点
            // Field field=R.drawable.class.getDeclaredField(value);
            // int resId=Integer.parseInt(field.get(null).toString());
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(
                        context.getResources(), resId);
                //  bitmap = Bitmap.createScaledBitmap(bitmap, edtImgWidth, edtImgHeight, true);
                bitmap = Bitmap.createScaledBitmap(bitmap, tvImgWidth, tvImgHeight, true);
                // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                ImageSpan imageSpan = new ImageSpan(bitmap);
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                int end = matcher.start() + key.length();
                // 将该图片替换字符串中规定的位置中
                spannableString.setSpan(imageSpan, matcher.start(), end,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if (end < spannableString.length()) {
                    // 如果整个字符串还未验证完，则继续。。
                    dealExpression(context, resType, spannableString, patten, end);
                }
                break;
            }
        }
    }


    /**
     * 手动删除文字内容，如果是表情要整个删除
     *
     * @param content
     * @return
     */
    public static int dealContent(Context context, String content, int index) {
        if (content.charAt(index - 1) == ']' && content.contains("[")) {
            String substring = content.substring(content.lastIndexOf('['), index);
            String pattenStr = "\\[[^\\]]+\\]";
            // 通过传入的正则表达式来生成一个pattern
            Pattern pattern = Pattern.compile(pattenStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(substring);
            while (matcher.find()) {
                String resName = substring.substring(1, substring.length() - 1);
                int resId = context.getResources().getIdentifier(resName, "mipmap",
                        context.getPackageName());
                if (resId != 0) {
                    return substring.length();
                }
            }
        }
        return 1;
    }

}
