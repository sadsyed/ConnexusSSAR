package ssar.apt.connexusssar.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.types.Stream;

/**
 * Created by ssyed on 10/16/14.
 */
public class StreamParser {
    private static final String TAG = StreamParser.class.getSimpleName();

    public List<Stream> jsonToStream(String responseJSON){
        JSONObject json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Gson gson = gsonBuilder.create();
        List<Stream> streams = new ArrayList<Stream>();

        try {
            json = new JSONObject(responseJSON);

            JSONArray jsonArray = json.getJSONArray("streamlist");
            for (int i=0; i<jsonArray.length(); i++) {
                Stream streamObj = gson.fromJson(jsonArray.getJSONObject(i).toString(), Stream.class);
                //Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, streamObj.toString());
                streams.add(streamObj);
            }
        } catch (JSONException e) {
            Log.e(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, e.getStackTrace().toString());
        }

        return streams;
    }
}
