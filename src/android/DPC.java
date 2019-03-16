package com.tentackles.plugin;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.view.View;
import android.util.Log;



import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DPC extends CordovaPlugin {

    // Reference to the web view for static access
    private static WeakReference<CordovaWebView> webView = null;

    // Indicates if the device is ready (to receive events)
    private static Boolean deviceready = false;

    // Queues all events before deviceready
    private static ArrayList<String> eventQueue = new ArrayList<String>();

    /**
     * Called when the activity will start interacting with the user.
     *
     * @param multitasking Flag indicating if multitasking is turned on for app.
     */
    @Override
    public void onResume (boolean multitasking) {
        super.onResume(multitasking);
        deviceready();
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    @Override
    public void onDestroy() {
        deviceready = false;
    }
    /**
     * Called after plugin construction and fields have been initialized.
     * Prefer to use pluginInitialize instead since there is no value in
     * having parameters on the initialize() function.
     */
    @Override
    public void initialize (CordovaInterface cordova, CordovaWebView webView) {
        DPC.webView = new WeakReference<CordovaWebView>(webView);
    }

    /**
     * Call all pending callbacks after the deviceready event has been fired.
     */
    private static synchronized void deviceready() {
        deviceready = true;

        for (String js : eventQueue) {
            sendJavascript(js);
        }

        eventQueue.clear();
    }


    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("greet")) {
            fireEvent("testEvent", new JSONObject());
            
            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);

            return true;

        }if (action.equals("test")){
            fireEvent("testEvent", new JSONObject());
            return true;
        } else {
            
            return false;

        }
    }

    /**
     * Fire given event on JS side. Does inform all event listeners.
     *
     * @param event The event name.
     * @param data  Event object with additional data.
     */
    static void fireEvent (String event, JSONObject data) {
        String params, js;
        Log.d("FIRE_EVENT: ", event);
        try {
            data.put("event", event);
            data.put("foreground", isInForeground());
            data.put("queued", !deviceready);

        
        } catch (JSONException e) {
            e.printStackTrace();
        }

       
        params = data.toString();
        

        js = "DPC.fireEvent(" +
                "\"" + event + "\"," + params + ")";


        sendJavascript(js);
    }

    /**
     * Use this instead of deprecated sendJavascript
     *
     * @param js JS code snippet as string.
     */
    private static synchronized void sendJavascript(final String js) {

//        Log.d("FIRE_EVENT: ", "isDeviceReady " ,deviceready);
//        Log.d("FIRE_EVENT: ", "webView " ,webView);
        
//        if (!deviceready || webView == null) {
//            eventQueue.add(js);
//            return;
//        }

        final CordovaWebView view = webView.get();

        ((Activity)(view.getContext())).runOnUiThread(new Runnable() {
            public void run() {
                Log.d("FIRE_EVENT: ", "fired " );
                view.loadUrl("javascript:" + js);
            }
        });
    }

    /**
     * If the app is running in foreground.
     */
    private static boolean isInForeground() {

        if (!deviceready || webView == null)
            return false;

        CordovaWebView view = webView.get();

        KeyguardManager km = (KeyguardManager) view.getContext()
                .getSystemService(Context.KEYGUARD_SERVICE);

        //noinspection SimplifiableIfStatement
        if (km != null && km.isKeyguardLocked())
            return false;

        return view.getView().getWindowVisibility() == View.VISIBLE;
    }

    /**
     * If the app is running.
     */
    static boolean isAppRunning() {
        return webView != null;
    }

}
