package com.example.fptstadium.di;


import com.example.fptstadium.api.AuthService;
import com.example.fptstadium.api.BookingService;
import com.example.fptstadium.api.TimeSlotService;
import com.example.fptstadium.api.PaymentService;
import com.example.fptstadium.api.PricingService;

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

    @Provides
    @Singleton
    public PricingService providePricingService(Retrofit retrofit) {
        return retrofit.create(PricingService.class);
    }

    @Provides
    @Singleton
    public TimeSlotService provideTimeSlotService(Retrofit retrofit) {
        return retrofit.create(TimeSlotService.class);
    }
    @Provides
    @Singleton
    public PaymentService providePaymentService(Retrofit retrofit) {
        return retrofit.create(PaymentService.class);
    }

    @Provides
    @Singleton
    public BookingService provideBookingService(Retrofit retrofit) {
        return retrofit.create(BookingService.class);
    }
}
