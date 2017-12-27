/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.captcha;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Telephony;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 * @since 17-12-27 0.1
 */
public class CaptchaObserver extends ContentObserver {

    private static final Uri URI_SMS = Uri.parse("content://sms");
    private static final Uri URI_SMS_INBOX_INSERT = Uri.parse("content://sms/inbox-insert");
    private static final String SMS_INBOX = "content://sms/inbox";

    static final String ADDRESS = "address";
    private static final String BODY = "body";
    private static final String[] PROJECTION = new String[]{
            Telephony.Sms._ID,
            ADDRESS,
            BODY
    };

    private final Context mContext;
    private Pattern mCaptchaPattern;
    private CaptchaListener mCaptchaListener;
    private String mQuerySelection;

    CaptchaObserver(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (!URI_SMS_INBOX_INSERT.equals(uri)) {
            return;
        }

        final Uri queryUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            queryUri = Telephony.Sms.Inbox.CONTENT_URI;
        } else {
            queryUri = Uri.parse(SMS_INBOX);
        }
        Cursor cursor = mContext.getContentResolver().query(queryUri, PROJECTION,
                mQuerySelection, null, Telephony.Sms._ID + " DESC");
        if (cursor == null) {
            return;
        }
        if (cursor.getCount() <= 0) {
            closeCursor(cursor);
            return;
        }
        cursor.moveToFirst();
        do {
            final String body = cursor.getString(cursor.getColumnIndex(BODY));
            final Matcher matcher = mCaptchaPattern.matcher(body);
            if (!matcher.find() || matcher.groupCount() != 1) {
                continue;
            }
            String code = matcher.group(1);
            if (TextUtils.isDigitsOnly(code)) {
                if (mCaptchaListener != null) {
                    mCaptchaListener.onCaptchaReceived(code);
                }
                break;
            }
        } while (cursor.moveToNext());
        closeCursor(cursor);
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    void setCaptchaPattern(Pattern captchaPattern) {
        mCaptchaPattern = captchaPattern;
    }

    void setQuerySelection(String querySelection) {
        mQuerySelection = querySelection;
    }

    void setCaptchaListener(CaptchaListener captchaListener) {
        mCaptchaListener = captchaListener;
    }

    void register() {
        mContext.getContentResolver().registerContentObserver(URI_SMS, true, this);
    }

    /**
     * 取消注册短信订阅者。
     */
    public void unregister() {
        mContext.getContentResolver().unregisterContentObserver(this);
    }


    public interface CaptchaListener {
        /**
         * 收到验证码后的回调
         *
         * @param code 验证码
         */
        void onCaptchaReceived(String code);
    }
}
