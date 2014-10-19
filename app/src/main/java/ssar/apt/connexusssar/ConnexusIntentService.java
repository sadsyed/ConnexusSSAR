package ssar.apt.connexusssar;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ssar.apt.connexusssar.util.ConnexusSSARConstants;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ConnexusIntentService extends IntentService {
    private static final String TAG = ConnexusIntentService.class.getSimpleName();

    public static final String REQUEST_URL = "requestURL";
    public static final String REQUEST_JSON = "requestJSON";
    public static final String RESPONSE_JSON = "responseJSON";

    public ConnexusIntentService() {
        super("ConnexusIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String requestURL = intent.getStringExtra(REQUEST_URL);
        String requestJSON = intent.getStringExtra(REQUEST_JSON);
        String responseJSON;

        //Create HttpClient and HttpPost objects to execute the POST request
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(requestURL);

        try {
            //Set requestJSON for services which require request input
            if(requestJSON != null && ConnexusSSARConstants.MANAGE_STREAM.equals(requestURL)) {
                StringEntity stringEntity = new StringEntity(requestJSON);
                stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(stringEntity);
            }

            //Execute the POST request
            HttpResponse response = client.execute(post);

            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                /*ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseJSON = out.toString();*/
                responseJSON = EntityUtils.toString(response.getEntity());
                Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "ConnexusIntentService response: " + response.toString());
            } else {
                Log.w(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, statusLine.getReasonPhrase());
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.w(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, e);
            responseJSON = e.getMessage();
        } catch (IOException e) {
            Log.w(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, e);
            responseJSON = e.getMessage();
        } catch (Exception e) {
            Log.w(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, e);
            responseJSON = e.getMessage();
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ViewStreamsActivity.ConnexusRequestReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RESPONSE_JSON, responseJSON);
        sendBroadcast(broadcastIntent);
    }
}
