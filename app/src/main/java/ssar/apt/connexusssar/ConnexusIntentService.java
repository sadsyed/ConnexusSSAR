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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ConnexusIntentService extends IntentService {
    private static final String TAG = ConnexusIntentService.class.getSimpleName();

    public static final String REQUEST_URL = "requestURL";
    public static final String RESPONSE_JSON = "responseJSON";

    public ConnexusIntentService() {
        super("ConnexusIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String requestURL = intent.getStringExtra(REQUEST_URL);
        String responseJSON = "";

        //Create HttpClient and HttpPost objects to execute the POST request
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(requestURL);

        //Execute the POST request
        try {
            HttpResponse response = client.execute(post);
            Log.i(TAG, response.toString());

            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                /*ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseJSON = out.toString();*/
                responseJSON = EntityUtils.toString(response.getEntity());
            } else {
                Log.w("HTTP1:", statusLine.getReasonPhrase());
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.w("HTTP ClientProtocolException:", e);
            responseJSON = e.getMessage();
        } catch (IOException e) {
            Log.w("HTTP IoException:", e);
            responseJSON = e.getMessage();
        } catch (Exception e) {
            Log.w("HTTP Exception:", e);
            responseJSON = e.getMessage();
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ViewStreamsActivity.ConnexusRequestReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RESPONSE_JSON, responseJSON);
        sendBroadcast(broadcastIntent);
    }
}
