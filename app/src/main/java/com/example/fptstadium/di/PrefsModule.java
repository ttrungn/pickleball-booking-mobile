package com.example.fptstadium.di;


import android.content.Context;

import com.example.fptstadium.utils.PrefsHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class PrefsModule {

    @Provides
    @Singleton
    public PrefsHelper providePrefsHelper(@ApplicationContext Context context) {
        return new PrefsHelper(context);
    }
}
