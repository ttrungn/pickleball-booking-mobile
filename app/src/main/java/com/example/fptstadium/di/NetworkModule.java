package com.example.fptstadium.di;


import android.content.Context;

import com.example.fptstadium.api.AuthService;
import com.example.fptstadium.api.FieldService;
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
    // For emulator: 10.0.2.2 maps to host machine's localhost
    // For physical device: use your machine's actual IP address (e.g., http://192.168.1.100:8080/api/v1/)
    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/";


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

                    Request newRequest = builder.build();
                    return chain.proceed(newRequest);
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

    @Provides
    @Singleton
    public FieldService provideFieldService(Retrofit retrofit) {
        return retrofit.create(FieldService.class);
    }
}
