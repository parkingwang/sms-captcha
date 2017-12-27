/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.captcha;

import android.content.Context;
import android.os.Handler;
import android.widget.EditText;

import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * 短信验证码
 *
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @since 2017-12-27 0.1
 */
public class SmsCaptcha {
    private static final int DEFAULT_LENGTH = 4;

    private final Context mContext;
    private int mMinLength = DEFAULT_LENGTH;
    private int mMaxLength = DEFAULT_LENGTH;
    private String mTitle = "";
    private String mCaptchaText = "验证码";
    private String mQuerySelection;
    private CaptchaObserver.CaptchaListener mCaptchaListener;

    private SmsCaptcha(Context context) {
        mContext = context;
    }

    /**
     * 在哪个界面中监听
     *
     * @param context Context 对象
     * @return SmsCaptcha
     */
    public static SmsCaptcha with(Context context) {
        return new SmsCaptcha(context);
    }

    /**
     * 验证码固定长度
     *
     * @param length 验证码长度
     * @return SmsCaptcha
     */
    public SmsCaptcha captchaLength(int length) {
        mMinLength = length;
        mMaxLength = length;
        return this;
    }

    /**
     * 验证码长度范围
     *
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return SmsCaptcha
     */
    public SmsCaptcha captchaLength(int minLength, int maxLength) {
        mMinLength = minLength;
        mMaxLength = maxLength;
        return this;
    }

    /**
     * 短信验证码的发送号码。如果不固定，可使用{@link #addressLike(String)}方法来设置。
     *
     * @param address 短信码证码的发送号码
     * @return SmsCaptcha
     */
    public SmsCaptcha address(String address) {
        mQuerySelection = CaptchaObserver.ADDRESS + "='" + address + "' AND read=0";
        return this;
    }

    /**
     * 短信验证码的发送者号码，可模糊搜索。
     *
     * @param address 模糊搜索。如: 10657%
     * @return SmsCaptcha
     */
    public SmsCaptcha addressLike(String address) {
        mQuerySelection = CaptchaObserver.ADDRESS + " LIKE '" + address + "' AND read=0";
        return this;
    }

    /**
     * 短信开头的内容。
     *
     * @param regex 短信开头的内容，如 滴滴出行。如果包含中文方括号，也需要填入，如【滴滴出行】。
     *              支持正则，如 .*滴滴出行。
     * @return SmsCaptcha
     */
    public SmsCaptcha contentStartWith(String regex) {
        mTitle = regex;
        return this;
    }

    /**
     * 验证码对应汉字
     *
     * @param text 验证码对应汉字。默认为：验证码。
     * @return SmsCaptcha
     */
    public SmsCaptcha captchaText(String text) {
        mCaptchaText = text;
        return this;
    }

    /**
     * 解析到验证码之后的进行自动填充。
     *
     * @param editText 自动填充指定的 EditText
     * @return SmsCaptcha
     */
    public SmsCaptcha fillTo(final EditText editText) {
        mCaptchaListener = new CaptchaObserver.CaptchaListener() {
            @Override
            public void onCaptchaReceived(String code) {
                editText.setText(code);
            }
        };
        return this;
    }

    /**
     * 自定义解析到验证码后的行为。
     *
     * @param listener 解析到验证码后的回调。
     * @return SmsCaptcha
     */
    public SmsCaptcha onReceive(CaptchaObserver.CaptchaListener listener) {
        mCaptchaListener = listener;
        return this;
    }

    /**
     * 创建短信订阅者并注册。
     * 注意，需要在界面退出时调用{@link CaptchaObserver#unregister()}，以避免内存泄露。
     *
     * @return CaptchaObserver
     */
    public CaptchaObserver createAndRegister() {
        final String regex = MessageFormat.format("{0}.*{1}.*?(\\d'{'{2},{3}'}').*",
                mTitle, mCaptchaText, mMinLength, mMaxLength);
        final Pattern pattern = Pattern.compile(regex);
        final CaptchaObserver observer = new CaptchaObserver(mContext, new Handler());
        observer.setCaptchaPattern(pattern);
        observer.setQuerySelection(mQuerySelection);
        observer.setCaptchaListener(mCaptchaListener);
        observer.register();
        return observer;
    }
}
