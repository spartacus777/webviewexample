package browser.com.kudos.webviewexample;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class App extends Application {
    private static Context context;
    private static Handler handler;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();

    }

    public static Context getContext(){
        return context;
    }

    public static Handler getUIHandler(){
        return handler;
    }

}
