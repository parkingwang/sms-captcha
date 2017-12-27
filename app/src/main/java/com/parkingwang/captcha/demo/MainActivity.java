/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.captcha.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parkingwang.captcha.CaptchaObserver;
import com.parkingwang.captcha.SmsCaptcha;

/**
 * @author 黄浩杭 (huanghaohang@parkingwang.com)
 */
public class MainActivity extends AppCompatActivity {

    CaptchaObserver mCaptchaObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCaptchaObserver = SmsCaptcha.with(this)
                .captchaLength(4)
                .addressLike("10657%")
                .onReceive(new CaptchaObserver.CaptchaListener() {
                    @Override
                    public void onCaptchaReceived(String code) {
                        Log.e("xxxx", code);
                    }
                })
                .createAndRegister();
    }

    @Override
    protected void onDestroy() {
        mCaptchaObserver.unregister();
        super.onDestroy();
    }
}
