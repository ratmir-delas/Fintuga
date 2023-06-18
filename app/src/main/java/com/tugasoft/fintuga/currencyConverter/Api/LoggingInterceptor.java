package com.tugasoft.fintuga.currencyConverter.Api;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoggingInterceptor implements Interceptor {
    public Response intercept(Chain chain) throws IOException {
        Response proceed = chain.proceed(chain.request());
        String string = proceed.body().string();
        Log.i("Interceptor ", string);
        return proceed.newBuilder().body(ResponseBody.create(proceed.body().contentType(), string)).build();
    }
}
