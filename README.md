# SmsCaptcha

SmsCaptcha 是一个实现自动获取短信验证码的库。它通过注册 `ContentObserver` 来监听新短信并进行查询和解析验证码。

[ ![Download](https://api.bintray.com/packages/parkingwang/maven/SmsCaptcha/images/download.svg?version=0.1) ](https://bintray.com/parkingwang/maven/SmsCaptcha/0.1/link)
 
# 使用方法

添加依赖：

```groovy
compile 'com.parkingwang:sms-captcha:0.1'
// 或
implementation 'com.parkingwang:sms-captcha:0.1'
```

Java 代码：

注册观察者

```java
        mCaptchaObserver = SmsCaptcha.with(this)
                .captchaLength(4)
                .addressLike("10657%")
                .fillTo(mSmsCode)
                .createAndRegister();
```

界面销毁时注销观察者

```java
        mCaptchaObserver.unregister();
```

完整注册代码：

```java
        mCaptchaObserver = SmsCaptcha.with(this)
                // 验证码长度
                .captchaLength(4)
                // 验证码长度可能不固定
                //.captchaLength(4, 6)
                .address("10086")
                //发送地址以10657开头
                //.addressLike("10657%")
                .contentStartWith("尊敬的客户")
                // 短信开头
                //.contentStartWith(".*客户")
                .captchaText("动态密码")
                // 自动填充
                //.fillTo(mEditText)
                // 自定义获取到验证码后的行为
                .onReceive(new CaptchaObserver.CaptchaListener() {
                    @Override
                    public void onCaptchaReceived(String code) {
                        Log.e("xxxx", code);
                    }
                })
                .createAndRegister();
```

# API 文档

http://parkingwang.github.io/sms-captcha

# 注

部分手机做了验证码防窃取，会导致无法获取包含验证码的短信，如华为EMUI等。

