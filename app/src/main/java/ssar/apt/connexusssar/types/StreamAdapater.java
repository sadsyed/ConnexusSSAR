package ssar.apt.connexusssar.types;

import android.app.Activity;
import android.content.Intent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.ConnexusIntentService;
import ssar.apt.connexusssar.MainActivity;
import ssar.apt.connexusssar.R;
import ssar.apt.connexusssar.ViewAStreamActivity;
import ssar.apt.connexusssar.ViewStreamsActivity;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;

/**
 * Created by ssyed on 10/17/14.
 */
public class StreamAdapater extends BaseAdapter {
    public final static String EXTRA_MESSAGE = "ssar.apt.connexusssar.MESSAGE";

    Context context;
    List<Stream> streams = new ArrayList<Stream>();
    ProgressDialog progressDialog;

    private static LayoutInflater layoutInflater = null;

    public StreamAdapater(Context context, List<Stream> streams) {
        this.context = context;
        this.streams = streams;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return streams.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        rowView = layoutInflater.inflate(R.layout.stream_grid, null);
        holder.textView = (TextView) rowView.findViewById(R.id.streamName);
        holder.imageView = (ImageView) rowView.findViewById(R.id.coverImage);

        //load the stream name
        holder.textView.setText(streams.get(position).getStreamname());

        //load the stream cover image using cover url
        LoadImage loadImage = new LoadImage(holder.imageView);
        loadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, streams.get(position).getCoverurl());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Toast.makeText(context, "You clicked " + streams.get(position).getStreamname(), Toast.LENGTH_LONG).show();
            //Need to launch the stream activity here.
            Intent intent = new Intent(context, ViewAStreamActivity.class);
             intent.putExtra(EXTRA_MESSAGE, streams.get(position).getStreamname());
            context.startActivity(intent);
            Toast.makeText(context, "You clicked " + streams.get(position).getStreamname(), Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

    private class LoadImage extends AsyncTask<String, Integer, Drawable> {
        private final WeakReference<ImageView> weakReference;

        public LoadImage(ImageView imageView) {
            weakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading Streams.... ");
            progressDialog.show();*/
        }

        @Override
        protected Drawable doInBackground(String... args) {
            try {
                //Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, "Processing stream: " + new URL(args[0]).getContent().toString());
                return Drawable.createFromStream((InputStream)new URL(args[0]).getContent(), "src");
            } catch (Exception e) {
                Log.i(ConnexusSSARConstants.CONNEXUSSSAR_DEBUG_TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            ImageView imgView = weakReference.get();
            if (imgView != null) {
                imgView.setImageDrawable(drawable);
            }
        }
    }

    public class Holder {
        TextView textView;
        ImageView imageView;
    }
}
