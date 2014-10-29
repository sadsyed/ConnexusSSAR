package ssar.apt.connexusssar;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;




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
    private static final String CLASSNAME = ConnexusIntentService.class.getSimpleName();

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
            StringEntity stringEntity;
            //Set requestJSON for services which require request input
            switch(requestURL) {
                case ConnexusSSARConstants.MANAGE_STREAM:
                    stringEntity = new StringEntity(requestJSON);
                    stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(stringEntity);
                    break;

                case ConnexusSSARConstants.VIEW_ASTREAM:
                    stringEntity = new StringEntity(requestJSON);
                    stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(stringEntity);
                    break;

                case ConnexusSSARConstants.SEARCH_STREAM:
                    stringEntity = new StringEntity(requestJSON);
                    stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(stringEntity);
                    break;

                case ConnexusSSARConstants.UPLOAD_FILE:
                    post.addHeader("Accept", "application/json");
                    post.addHeader("Content-type", "multipart/form-data");
                    //File fileToUse = new File("/path_to_file/YOLO.jpg");
                    //FileBody data = new FileBody(fileToUse);

                    //String file_type = "JPG" ;
                    //String description = "Oppa Gangnam Style";
                    //String folder_id = "-1";
                    //String source = "MYCOMPUTER" ;

                    MultipartEntity reqEntity = new MultipartEntity();
                    //reqEntity.addPart("file_name", new StringBody( fileToUse.getName() ) );
                    //reqEntity.addPart("folder_id", new StringBody(folder_id));
                    //reqEntity.addPart("description", new StringBody(description));
                    //reqEntity.addPart("source", new StringBody(source));
                    //reqEntity.addPart("file_type", new StringBody(file_type));
                    //reqEntity.addPart("data", data);

                    post.setEntity(reqEntity);
                    break;
            }
/*            if(requestJSON != null && ConnexusSSARConstants.MANAGE_STREAM.equals(requestURL)) {
                StringEntity stringEntity = new StringEntity(requestJSON);
                stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(stringEntity);
            } else if(requestJSON != null && ConnexusSSARConstants.VIEW_ASTREAM.equals(requestURL)) {
                StringEntity stringEntity = new StringEntity(requestJSON);
                stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(stringEntity);
            } else if(ConnexusSSARConstants.UPLOAD_FILE.equals(requestURL)) {
                post.addHeader("Accept", "application/json");
                post.addHeader("Content-type", "multipart/form-data");
                //File fileToUse = new File("/path_to_file/YOLO.jpg");
                //FileBody data = new FileBody(fileToUse);

                //String file_type = "JPG" ;
                //String description = "Oppa Gangnam Style";
                //String folder_id = "-1";
                //String source = "MYCOMPUTER" ;

                MultipartEntity reqEntity = new MultipartEntity();
                //reqEntity.addPart("file_name", new StringBody( fileToUse.getName() ) );
                //reqEntity.addPart("folder_id", new StringBody(folder_id));
                //reqEntity.addPart("description", new StringBody(description));
                //reqEntity.addPart("source", new StringBody(source));
                //reqEntity.addPart("file_type", new StringBody(file_type));
                //reqEntity.addPart("data", data);

                post.setEntity(reqEntity);
            }
*/
            //Execute the POST request
            HttpResponse response = client.execute(post);

            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                /*ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseJSON = out.toString();*/
                responseJSON = EntityUtils.toString(response.getEntity());
                Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + "- " + "Response: " + response.toString());
                Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, CLASSNAME + "- " +"Response JSON: " + responseJSON.toString());
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
