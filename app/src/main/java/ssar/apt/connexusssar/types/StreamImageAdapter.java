package ssar.apt.connexusssar.types;

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

import ssar.apt.connexusssar.R;
import ssar.apt.connexusssar.ViewAStreamActivity;
import ssar.apt.connexusssar.util.ConnexusSSARConstants;

/**
 * Created by Amy on 10/26/2014.
 */
public class StreamImageAdapter extends BaseAdapter {

    Context context;
    List<StreamImage> streamImages = new ArrayList<StreamImage>();
    private static String TAG = new String();

    private static LayoutInflater layoutInflater = null;

    public StreamImageAdapter(Context context, List<StreamImage> streamImages) {
        TAG = StreamImageAdapter.class.getSimpleName();
        this.context = context;
        this.streamImages = streamImages;
        Log.i(TAG,"Stream image adapter context is: " + context.toString());
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(layoutInflater != null) {
            Log.i(TAG, "layout inflater not null");
        } else {
            Log.i(TAG, "layout inflater null");
        }
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
        holder.textView.setText(streamImages.get(position).getImageFilename());

        //load the stream cover image using cover url
        LoadImage loadImage = new LoadImage(holder.imageView);
        if(loadImage != null) {
            loadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, streamImages.get(position).getImageFileUrl());
        } else {
            Log.i(TAG, "load image equal null");
        }
        /*rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked " + streams.get(position).getStreamname(), Toast.LENGTH_LONG).show();
                //Need to launch the stream activity here.
                Intent intent = new Intent(context, ViewAStreamActivity.class);
                intent.putExtra(EXTRA_MESSAGE, streams.get(position).getStreamname());
                context.startActivity(intent);
                Toast.makeText(context, "You clicked " + streams.get(position).getStreamname(), Toast.LENGTH_LONG).show();
            }
        });*/
        if (rowView == null) {
            Log.i(TAG, "row view equals null");
        } else {
            Log.i(TAG, "row view not equal to null");
        }
        return rowView;
    }

    private class LoadImage extends AsyncTask<String, Integer, Drawable> {
        private final WeakReference<ImageView> weakReference;

        public LoadImage(ImageView imageView) {
            if(imageView != null) {
                Log.i(TAG, "Imageview is: " + imageView.toString());
            } else {
                Log.i(TAG, "Imageview equal to null");
            }
            weakReference = new WeakReference<ImageView>(imageView);
            if(weakReference != null) {
                Log.i(TAG,"Weak reference not equal null");
            } else {
                Log.i(TAG, "Weark reference equals null");
            }
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
                Log.i(TAG, "Processing image: " + new URL(args[0]).getContent().toString());
                Drawable drawable = Drawable.createFromStream((InputStream)new URL(args[0]).getContent(), "src");
                Log.i(TAG, "Drawable is: " + drawable.toString());
                return drawable;
            } catch (Exception e) {
                Log.i(TAG, "Error creating drawable: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if(drawable == null) {
                Log.i(TAG, "Drawable equals null");
            } else {
                Log.i(TAG, "Drawable is : " + drawable.toString());
            }
            if(weakReference == null) {
                Log.i(TAG, "weak reference is equal to null in post execute");
            } else {
                Log.i(TAG, "weak reference not equal to null in post execute");
            }
            ImageView imgView = weakReference.get();
            if (imgView != null) {
                Log.i(TAG, "imgView not equall null");
                imgView.setImageDrawable(drawable);
            } else {
                Log.i(TAG, "imgView equal null.");
            }

        }
    }

    public class Holder {
        TextView textView;
        ImageView imageView;
    }
}
