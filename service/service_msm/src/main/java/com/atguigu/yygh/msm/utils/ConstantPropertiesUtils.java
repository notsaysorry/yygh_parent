package com.atguigu.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {

    @Value("${tencentcloud.sms.regionId}")
    private String regionId;

    @Value("${tencentcloud.sms.secretId}")
    private String secretId;

    @Value("${tencentcloud.sms.secretKey}")
    private String secretKey;

    @Value("${tencentcloud.sms.sdkAppId}")
    private String sdkAppId;

    @Value("${tencentcloud.sms.signName}")
    private String signName;

    @Value("${tencentcloud.sms.templateId}")
    private String templateId;

    public static String REGION_Id;
    public static String SECRETE_ID;
    public static String SECRETE_KEY;
    public static String SDK_APP_ID;
    public static String SIGN_NAME;
    public static String TEMPLATE_ID;

    @Override
    public void afterPropertiesSet() throws Exception {
        REGION_Id=regionId;
        SECRETE_ID=secretId;
        SECRETE_KEY=secretKey;
        SDK_APP_ID=sdkAppId;
        SIGN_NAME=signName;
        TEMPLATE_ID=templateId;
    }
}
