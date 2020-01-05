package com.example.naqi.bebettermuslim.data;

import android.content.Context;
import android.os.AsyncTask;
import com.example.naqi.bebettermuslim.views.HelperFunctions;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by naqi on 22,January,2019
 */
public class JSONClient extends AsyncTask<String, Void, JSONObject> {
    GetJSONListener getJSONListener;
    Context curContext;

    public JSONClient(Context context, GetJSONListener listener){
        this.getJSONListener = listener;
        curContext = context;
    }



    @Override
    protected JSONObject doInBackground(String... urls) {
        try {
            return HelperFunctions.getJSONObjectFromURL(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject json ) {
        getJSONListener.onRemoteCallComplete(json);
    }
}
