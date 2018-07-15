/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mark on 18. 7. 15.
 */
public final class JsonUtil {

    private static String loadJSONFromAsset(Context context, int rawId) {
        String json = "";
        try {
            InputStream is = context.getResources().openRawResource(rawId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static JSONObject jsonObjectFromFile(Context context, int rawId) {
        try {
            return new JSONObject(loadJSONFromAsset(context, rawId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
