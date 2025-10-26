package com.example.fptstadium.di;


import com.example.fptstadium.api.AuthService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class ServiceModule {

    @Provides
    @Singleton
    public AuthService provideAuthService(Retrofit retrofit) {
        return retrofit.create(AuthService.class);
    }
}
