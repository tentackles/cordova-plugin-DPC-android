package com.tentackles.plugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class DPC extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("greet")) {

            startService(new Intent(this, DPCService.class));

            return true;

        } else {
            
            return false;

        }
    }
}
