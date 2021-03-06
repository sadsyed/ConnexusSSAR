package ssar.apt.connexusssar.types;

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

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ssar.apt.connexusssar.R;
import ssar.apt.connexusssar.ViewAStreamActivity;

/**
 * Created by Amy on 11/2/2014.
 */
public class StreamImageAdapterClickable extends BaseAdapter {

    Context context;
    List<StreamImage> streamImages = new ArrayList<StreamImage>();
    private static String TAG = new String();
    public final static String EXTRA_MESSAGE = "ssar.apt.connexusssar.MESSAGE";

    private static LayoutInflater layoutInflater = null;

    public StreamImageAdapterClickable(Context context, List<StreamImage> streamImages) {
        TAG = StreamImageAdapterClickable.class.getSimpleName();
        this.context = context;
        Log.i(TAG, "Context is for combine stream image adapter:" + context.toString());
        this.streamImages = streamImages;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return streamImages.size();
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

        //load the imagefile name
        holder.textView.setText(streamImages.get(position).getImageStreamName());

        //load the stream cover image using cover url
        LoadImage loadImage = new LoadImage(holder.imageView);
        if(loadImage != null) {
            loadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, streamImages.get(position).getImageFileUrl());
        } else {
            Log.i(TAG, "load image equal null");
        }
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewAStreamActivity.class);
                intent.putExtra(EXTRA_MESSAGE, streamImages.get(position).getImageStreamName());
                context.startActivity(intent);
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
                Drawable drawable = Drawable.createFromStream((InputStream)new URL(args[0]).getContent(), "src");
                return drawable;
            } catch (Exception e) {
                Log.i(TAG, "Error creating drawable: " + e.toString());
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
