package com.example.appdaddy.moviemash;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.sinch.verification.Config;
import com.sinch.verification.SinchVerification;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by AppDaddy on 3/10/17.
 */

public class MovieMashApplication extends Application {
//    private static Config mConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

//        mConfig = SinchVerification.config().applicationKey(Constants.SINCH_API_KEY).context(getApplicationContext()).build();
    }

//    public static Config getSinchConfig(){
//        return mConfig;
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
