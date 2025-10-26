package com.example.fptstadium.di;


import android.content.Context;

import com.example.fptstadium.api.AuthService;
import com.example.fptstadium.utils.PrefsHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    //TODO: Thay đổi base url khi run app
    private static final String BASE_URL = "";


    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(PrefsHelper preferHelpers) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder();

                    String token = preferHelpers.getToken();
                    if (token != null && !token.isEmpty()) {
                        builder.header("Authorization", "Bearer " + token);
                    }


                    return chain.proceed(chain.request());
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


}
