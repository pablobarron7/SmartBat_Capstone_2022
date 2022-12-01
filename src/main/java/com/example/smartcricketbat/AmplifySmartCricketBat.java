package com.example.smartcricketbat;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;

public class AmplifySmartCricketBat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // Add these lines to add the AWSApiPlugin plugins
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("AmplifySmartCricketBat", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("AmplifySmartCricketBat", "Could not initialize Amplify", error);
        }
    }
}
