package com.song.songup.common_sdk.retrofit;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.song.songup.common_sdk.BuildConfig;
import com.song.songup.common_sdk.retrofit.gsonhandlenull.DoubleDefault0Adapter;
import com.song.songup.common_sdk.retrofit.gsonhandlenull.IntDefault0Adapter;
import com.song.songup.common_sdk.retrofit.gsonhandlenull.LongDefault0Adapter;
import com.song.songup.common_sdk.retrofit.httlogger.HttpLogger;
import com.song.songup.common_sdk.util.UrlCommond;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ASUS on 2017/7/26.
 */

public class RetrofitBase {
    private static Retrofit retrofit;

    private static final int DEFAULT_TIMEOUT = 30;//超时设置

    public static synchronized void resetRetrofit() {
        retrofit = null;
        buildUrl();
    }

    public static synchronized Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();//https://futurestud.io/tutorials/retrofit-2-how-to-add-query-parameters-to-every-request
            httpClient.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            httpClient.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            httpClient.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            httpClient.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
            httpClient.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
            httpClient.followRedirects(true);
            httpClient.retryOnConnectionFailure(true);

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLogger());
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);//改为简单模式，可以随时打开
                httpClient.interceptors().add(logging);
            }
//            上传请求错误日志
//            if(BuildConfig.MONITOR){
//                // 添加错误日志拦截器
//                httpClient.interceptors().add(new UploadErrorLogInterceptor());
//            }
            Interceptor headers = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    /*HttpUrl newUrl = original.url().newBuilder().host(BuildConfig.DOMAIN_URL).build();
                    Request completeRequest = original.newBuilder().url(newUrl).build();*/
                    Request.Builder requestBuilder = original.newBuilder();
                            /*.header("Connection", "keep-alive")
                            .header("Connection", "close")
                            .header("Host", BuildConfig.DOMAIN_URL)*/
//                            .header("auth-token", HeaderVerifyUtils.getVerifyCode2())
//                            .header("User-Agent", Common.UserAgentString == null ? "" : Common.UserAgentString)
//                            .header("COMPANY_CODE", BuildConfig.COMPANY_CODE);
                    Request request = requestBuilder.build();
                    Response response = chain.proceed(request);
//                    if(response.header("BSID") != null || response.header("bsid") != null){
//                        //表示客户端现在访问的地址是正确的
//                    } else {//表示客户端访问的地址不对了，需要切换域名
////                        String nextUrl = UserInfo.getNextURL();
////                        if(nextUrl != null && !nextUrl.isEmpty()){
////                            UserInfo.setLocalUrl(nextUrl);
////                            RetrofitBase.resetRetrofit();
////                        }
//
//                        ChangeIP.checkIPs(new ChangeIP.AfterIPAction() {
//                            @Override
//                            public void doAction() {
//                                RetrofitBase.resetRetrofit();
//                            }
//                        });
//                    }
                    return response;
                }
            };
            httpClient.interceptors().add(headers);
            httpClient.addInterceptor(new RetryIntercepter(2));
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(long.class, new LongDefault0Adapter())
                    .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                    .registerTypeAdapter(int.class, new IntDefault0Adapter())
                    .registerTypeAdapter(Integer.class, new IntDefault0Adapter())
                    .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                    .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl( UrlCommond.APP_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    private static void buildUrl() {
//        String downloadIP = SharePreferenceProperties.get(ConstantUtil.DOWNLOAD_IP, "").toString();
//        if (!downloadIP.equals("")) {
//             Common.APP_URL = String.format(downloadIP+"/app/0/%s/", MyApplication.UUID);
//        }
    }

    /**
     * 重试拦截器
     */
    public static class RetryIntercepter implements Interceptor {

        public int maxRetry;//最大重试次数
        //        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
        private Map<String, Integer> map = new HashMap<>();

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl url = request.url();
            String path = url.url().getPath();
            recodeAdd(path);
            System.out.println("retryNum=" + path + "====" + getRecode(path));
            Response response = chain.proceed(request);
            while (!response.isSuccessful() && !isOver(path)) {
                recodeAdd(path);
                System.out.println("retryNum=" + getRecode(path));
                response = chain.proceed(request);

            }
            resetRecode(path);
            return response;
        }

        public void resetRecode(String key) {
            if (map.containsKey(key)) {
                map.put(key, 0);
            }
        }

        public Integer getRecode(String key) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            return null;
        }

        public void recodeAdd(String key) {
            if (map.containsKey(key)) {
                Integer num = map.get(key);
                map.put(key, ++num);
            } else {
                map.put(key, 1);
            }
        }

        public boolean isOver(String key) {
            if (TextUtils.isEmpty(key)) {
                return true;
            }
            if (map.containsKey(key)) {
                Integer num = map.get(key);
                return num >= maxRetry;
            } else {
                return false;
            }
        }
    }

}