package com.song.songup.common_sdk.retrofit.httlogger;

import android.util.Log;


import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UploadErrorLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int code = response.code();
        String message  = response.message();
        HttpUrl httpUrl = request.url();
        String host = httpUrl.host();

        if(code != 200){
            // 如果是执行上传日志接口报错，就不要重复执行了
            if(host.equals("errorlog.bwit.cc")){
                return response;
            }
//            uploadLog(BuildConfig.app_name, BuildConfig.COMPANY_CODE, httpUrl.toString(), code, UserInfo.getUserId(), message);
        }
        return response;
    }

    /**
     * 上传retrofit错误日志
     */
//    private void uploadLog(String company, String company_code, String url, int status, String user_id, String info){
//        RetrofitBase.getRetrofit().create(UploadLogRequest.class).getResult("http://errorlog.bwit.cc", company, company_code,
//                url, status, "Android", user_id, info)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<UploadErrorResponse>() {
//
//                    @Override
//                    public void accept(UploadErrorResponse response) throws Exception {
//                        Log.d("upload error log res = ", response.getMsg());
//                    }
//
//                });
//    }
}
