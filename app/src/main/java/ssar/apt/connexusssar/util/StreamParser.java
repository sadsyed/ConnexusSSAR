package ssar.apt.connexusssar.util;

import android.util.Log;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.types.Stream;
import ssar.apt.connexusssar.types.StreamImage;

/**
 * Created by ssyed on 10/16/14.
 */
public class StreamParser {
    private static final String CLASSNAME = StreamParser.class.getSimpleName();

    public Stream jsonToSingleStream(String serviceURL, String responseJSON) {
        JSONObject json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Gson gson = gsonBuilder.create();
        Stream stream = new Stream();

        try {
            Log.i(CLASSNAME, CLASSNAME + ": Request URL: " + serviceURL);
            Log.i(CLASSNAME, CLASSNAME + ": Response JSON: " + responseJSON);

            json = new JSONObject(responseJSON);

            switch(serviceURL) {
                case ConnexusSSARConstants.VIEW_ASTREAM:
                    json = json.getJSONObject("stream");
                    break;
                default:
                    break;
            }

            Log.i(CLASSNAME, CLASSNAME + ": Parse stream JSON to Java stream object:");
            stream = gson.fromJson(json.toString(), Stream.class);
        } catch (JSONException e) {
            Log.e(CLASSNAME, CLASSNAME + e);
        }

        return stream;
    }

    public List<Stream> jsonToStream(String serviceURL, String responseJSON){
        JSONObject json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Gson gson = gsonBuilder.create();
        List<Stream> streams = new ArrayList<Stream>();

        try {
            Log.i(CLASSNAME, CLASSNAME + ": Request URL: " + serviceURL);
            Log.i(CLASSNAME, CLASSNAME + ": Response JSON: " + responseJSON);

            json = new JSONObject(responseJSON);
            JSONArray jsonArray = new JSONArray();

            Log.i(CLASSNAME, "Service url is: " + serviceURL);


            switch(serviceURL) {
                case ConnexusSSARConstants.VIEW_ALL_STREAMS:
                    jsonArray = json.getJSONArray("streamlist");
                    break;
                case ConnexusSSARConstants.SEARCH_STREAM:
                    Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + ": responseJSON: " + jsonArray);
                    jsonArray = json.getJSONArray("streamlist");
                    break;
                case ConnexusSSARConstants.MANAGE_STREAM:
                    jsonArray = json.getJSONArray("subscribedstreamlist");
                    break;
                default:
                    break;
            }

           /* if(ConnexusSSARConstants.VIEW_ALL_STREAMS.equals(serviceURL)) {
                jsonArray = json.getJSONArray("streamlist");
            } else if (ConnexusSSARConstants.MANAGE_STREAM.equals(serviceURL)) {
                jsonArray = json.getJSONArray("subscribedstreamlist");
            }*/
            Log.i(CLASSNAME, CLASSNAME + ": Parse stream JSON to Java stream object:");
            if(jsonArray.length() > 0 ) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Stream streamObj = gson.fromJson(jsonArray.getJSONObject(i).toString(), Stream.class);
                    streams.add(streamObj);
                }
            } else {
                Log.i(CLASSNAME, "jsonArray empty.");
            }
        } catch (JSONException e) {
            Log.e(CLASSNAME, CLASSNAME + e);
        }
        return streams;
    }
    public List<StreamImage> jsonToStreamImages(String serviceURL, String responseJSON) {
        JSONObject json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Gson gson = gsonBuilder.create();
        List<StreamImage> streamImages = new ArrayList<StreamImage>();

        try {
            Log.i(CLASSNAME, CLASSNAME + ": Request URL: " + serviceURL);
            Log.i(CLASSNAME, CLASSNAME + ": Response JSON: " + responseJSON);

            json = new JSONObject(responseJSON);
            JSONArray jsonArray = new JSONArray();

            Log.i(CLASSNAME, "Service url is: " + serviceURL);
            switch(serviceURL) {
                case ConnexusSSARConstants.NEARBY_STREAMS:
                    jsonArray = json.getJSONArray("imageList");
                    break;
                default:
                    break;
            }
            if(jsonArray.length() > 0 ) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    StreamImage streamImageObj = gson.fromJson(jsonArray.getJSONObject(i).toString(), StreamImage.class);
                    //Log.i(CLASSNAME, CLASSNAME + streamImageObj.toString());
                    streamImages.add(streamImageObj);
                }
            } else {
                Log.i(CLASSNAME, "jsonArray empty.");
            }
        } catch (JSONException e) {
            Log.e(CLASSNAME, CLASSNAME + e);
        }
        return streamImages;
    }
}
