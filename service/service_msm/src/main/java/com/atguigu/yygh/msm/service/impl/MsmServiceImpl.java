package com.atguigu.yygh.msm.service.impl;

import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.msm.utils.ConstantPropertiesUtils;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {

    @Override
    public boolean send(String phone, String code) {
        //判断手机号是否为空
        if(StringUtils.isEmpty(phone)) {
            return false;
        }
        //整合腾讯云短信服务
        //实例化一个认证对象
        Credential cred = new Credential(ConstantPropertiesUtils.SECRETE_ID, ConstantPropertiesUtils.SECRETE_KEY);

        // 实例化一个http选项，可选，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        // SDK默认使用POST方法。
        httpProfile.setReqMethod("POST");
        // SDK有默认的超时时间，非必要请不要进行调整
        httpProfile.setConnTimeout(60);
        /* 指定接入地域域名，默认就近地域接入域名为 sms.tencentcloudapi.com ，也支持指定地域域名访问，例如广州地域的域名为 sms.ap-guangzhou.tencentcloudapi.com */
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        /* 非必要步骤:
         * 实例化一个客户端配置对象，可以指定超时时间等配置 */
        ClientProfile clientProfile = new ClientProfile();
        /* SDK默认用TC3-HMAC-SHA256进行签名
         * 非必要请不要修改这个字段 */
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
        /* 实例化要请求产品(以sms为例)的client对象
         * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，支持的地域列表参考 https://cloud.tencent.com/document/api/382/52071#.E5.9C.B0.E5.9F.9F.E5.88.97.E8.A1.A8 */
        SmsClient client = new SmsClient(cred, ConstantPropertiesUtils.REGION_Id,clientProfile);
        /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
         * 你可以直接查询SDK源码确定接口有哪些属性可以设置
         * 属性可能是基本类型，也可能引用了另一个数据结构
         * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
        SendSmsRequest req = new SendSmsRequest();
        /* 填充请求参数,这里request对象的成员变量即对应接口的入参
         * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
         * 基本类型的设置:
         * 帮助链接：
         * 短信控制台: https://console.cloud.tencent.com/smsv2
         * sms helper: https://cloud.tencent.com/document/product/382/3773 */
        /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
        String sdkAppId = ConstantPropertiesUtils.SDK_APP_ID;
        req.setSmsSdkAppId(sdkAppId);

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
        String signName = ConstantPropertiesUtils.SIGN_NAME;
        req.setSignName(signName);

        /* 国际/港澳台短信 SenderId: 国内短信填空，默认未开通，如需开通请联系 [sms helper] */
        String senderid = "";
        req.setSenderId(senderid);

        /* 用户的 session 内容: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
        String sessionContext = "xxx";
        req.setSessionContext(sessionContext);

        /* 短信号码扩展号: 默认未开通，如需开通请联系 [sms helper] */
        String extendCode = "";
        req.setExtendCode(extendCode);

        /* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 */
        String templateId = ConstantPropertiesUtils.TEMPLATE_ID;
        req.setTemplateId(templateId);

        /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
        String regionPhone = "+86" + phone;
        String[] phoneNumberSet = {regionPhone};
        req.setPhoneNumberSet(phoneNumberSet);

        /* 模板参数: 若无模板参数，则设置为空 */
        String[] templateParamSet = {code};
        req.setTemplateParamSet(templateParamSet);

        /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
         * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
        SendSmsResponse res = null;
        try {
            res = client.SendSms(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            return false;
        }
        // 输出json格式的字符串回包
        System.out.println(SendSmsResponse.toJsonString(res));
        String resCode = res.getSendStatusSet()[0].getCode();
        if ("Ok".equalsIgnoreCase(resCode)){
            return true;
        }
        return false;
    }

    public boolean send(String phone, Map<String, Object> param) {
        //判断手机号是否为空
        if(StringUtils.isEmpty(phone)) {
            return false;
        }
        //整合腾讯云短信服务
        //实例化一个认证对象
        Credential cred = new Credential(ConstantPropertiesUtils.SECRETE_ID, ConstantPropertiesUtils.SECRETE_KEY);

        // 实例化一个http选项，可选，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        // SDK默认使用POST方法。
        httpProfile.setReqMethod("POST");
        // SDK有默认的超时时间，非必要请不要进行调整
        httpProfile.setConnTimeout(60);
        /* 指定接入地域域名，默认就近地域接入域名为 sms.tencentcloudapi.com ，也支持指定地域域名访问，例如广州地域的域名为 sms.ap-guangzhou.tencentcloudapi.com */
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        /* 非必要步骤:
         * 实例化一个客户端配置对象，可以指定超时时间等配置 */
        ClientProfile clientProfile = new ClientProfile();
        /* SDK默认用TC3-HMAC-SHA256进行签名
         * 非必要请不要修改这个字段 */
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
        /* 实例化要请求产品(以sms为例)的client对象
         * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，支持的地域列表参考 https://cloud.tencent.com/document/api/382/52071#.E5.9C.B0.E5.9F.9F.E5.88.97.E8.A1.A8 */
        SmsClient client = new SmsClient(cred, ConstantPropertiesUtils.REGION_Id,clientProfile);
        /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
         * 你可以直接查询SDK源码确定接口有哪些属性可以设置
         * 属性可能是基本类型，也可能引用了另一个数据结构
         * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
        SendSmsRequest req = new SendSmsRequest();
        /* 填充请求参数,这里request对象的成员变量即对应接口的入参
         * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
         * 基本类型的设置:
         * 帮助链接：
         * 短信控制台: https://console.cloud.tencent.com/smsv2
         * sms helper: https://cloud.tencent.com/document/product/382/3773 */
        /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
        String sdkAppId = ConstantPropertiesUtils.SDK_APP_ID;
        req.setSmsSdkAppId(sdkAppId);

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
        String signName = ConstantPropertiesUtils.SIGN_NAME;
        req.setSignName(signName);

        /* 国际/港澳台短信 SenderId: 国内短信填空，默认未开通，如需开通请联系 [sms helper] */
        String senderid = "";
        req.setSenderId(senderid);

        /* 用户的 session 内容: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
        String sessionContext = "xxx";
        req.setSessionContext(sessionContext);

        /* 短信号码扩展号: 默认未开通，如需开通请联系 [sms helper] */
        String extendCode = "";
        req.setExtendCode(extendCode);

        /* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 */
        String templateId = ConstantPropertiesUtils.TEMPLATE_ID;
        req.setTemplateId(templateId);

        /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
        String regionPhone = "+86" + phone;
        String[] phoneNumberSet = {regionPhone};
        req.setPhoneNumberSet(phoneNumberSet);

        /* 模板参数: 若无模板参数，则设置为空 */
        String[] templateParamSet = {"1111"};
        req.setTemplateParamSet(templateParamSet);

        /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
         * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
        SendSmsResponse res = null;
        try {
            res = client.SendSms(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            return false;
        }
        // 输出json格式的字符串回包
        System.out.println(SendSmsResponse.toJsonString(res));
        String resCode = res.getSendStatusSet()[0].getCode();
        if ("Ok".equalsIgnoreCase(resCode)){
            return true;
        }
        return false;
    }

    @Override
    public boolean send(MsmVo msmVo) {
        if(!StringUtils.isEmpty(msmVo.getPhone())) {
            Map<String, Object> param = msmVo.getParam();
            return this.send(msmVo.getPhone(),param);
        }
        return false;
    }

}
