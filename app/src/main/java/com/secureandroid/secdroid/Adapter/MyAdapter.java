package com.secureandroid.secdroid.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

//TODO: uncomment importing of appdetialist
import com.google.android.material.snackbar.Snackbar;
import com.secureandroid.secdroid.AppDetialsActivity;

import com.secureandroid.secdroid.Model.appdetials;
import com.secureandroid.secdroid.R;
import com.secureandroid.secdroid.VirustotalResultActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    public Context context;
    private List<appdetials> applists;

    public String resourceKey;
    public String fileSelected;


    public MyAdapter(Context context, List<appdetials> applists) {
        this.context = context;
        this.applists = applists;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lsit_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {

        appdetials item = applists.get(position);

        holder.appName.setText(item.getAppName());
        holder.appPermission.setText(item.getAppPermissions());
        holder.appIcon.setBackground(item.getAppIcon());
    }

    @Override
    public int getItemCount() {
        return applists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView appName;
        public TextView appPermission;
        public ImageView appIcon;
        public Button scanButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            appName = (TextView) itemView.findViewById(R.id.appNameId);
            appPermission = (TextView) itemView.findViewById(R.id.appPermissionId);
            appIcon = (ImageView) itemView.findViewById(R.id.appIconId);
            scanButton = (Button) itemView.findViewById(R.id.scanButtonId);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getpathofapk();

                }


            });
        }

        private void getpathofapk() {
            int positionClicked = getAdapterPosition();

            PackageManager packageManager = context.getPackageManager();
            List<ApplicationInfo> packagesName = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

            File apk = new File(packagesName.get(positionClicked).publicSourceDir);
            if (apk.exists())
            {
                String pathOfApk = apk.getAbsolutePath();

                double fileSizeBytes = apk.length();
                double fileSizeKb = fileSizeBytes/1000;
                double fileSizeMb = fileSizeKb/1000;

                fileSelected = apk.getName();

                SharedPreferences sharedPreferences = context.getSharedPreferences("keyFile", Context.MODE_PRIVATE);

                if (hasInternetConnectivity() && sharedPreferences.contains("apiKey")){

                    if (fileSizeMb<32.00)
                    {
                        UploadTask appUploadTask = new UploadTask();
                        appUploadTask.execute(new String[] {pathOfApk});
                    }
                    else{
                        String title = "Attention";
                        String description ="Upload files lesser than 32 Mb";
                        new androidx.appcompat.app.AlertDialog.Builder(context)
                                .setTitle(title)
                                .setMessage(description)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();;
                    }

                }
                else if (!sharedPreferences.contains("apiKey")){
                    String title = "Api key needed";
                    String message = "Enter your virustotal api key in Scan menu";
                    new AlertDialog.Builder(context)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(true)
                            .create()
                            .show();
                }
                else {
                    final Snackbar snackbar = Snackbar.make(itemView,"Please check your internet connection",Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }



                Log.d("path of file", pathOfApk);
            }
        }

        public boolean hasInternetConnectivity()
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return (activeNetwork!=null && activeNetwork.isConnectedOrConnecting());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            appdetials item = applists.get(position);
            Drawable icon = item.getAppIcon();

            Bitmap bitmap = getBitmapFromDrawable(icon);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();


            Intent intent = new Intent(context, AppDetialsActivity.class);
            intent.putExtra("appname", item.getAppName());
            intent.putExtra("Permission", item.getallPermissions());
            intent.putExtra("appIcon", bytes);
            intent.putExtra("pkgName", item.getPackagesName());

            context.startActivity(intent);
        }

        @NonNull
         private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
             Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
             Canvas canvas = new Canvas(bmp);
             drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
             drawable.draw(canvas);
             return bmp;
        }
    }


// to upload file
    public class UploadTask extends AsyncTask<String,String,String> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewProgressDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(uploadFile(strings[0])){
                return "true";
            }
            else{
                return "failed";
            }
        }

        private boolean uploadFile(String path){
            File file=new File(path);
            String apikey ="";

            SharedPreferences sharedPreferences = context.getSharedPreferences("keyFile", Context.MODE_PRIVATE);
            if (sharedPreferences.contains("apiKey"))
            {
                  apikey = sharedPreferences.getString("apiKey", "Wrong apiKey");
            }

            try{
                RequestBody requestBody=new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("*/*"), file))
                        .addFormDataPart("apikey", apikey)
                        .build();

                Request request=new Request.Builder()
                        .url("https://www.virustotal.com/vtapi/v2/file/scan")
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        createDialog("Attention", "Something went wrong");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String message=response.body().string();
                        Log.d("Result",""+message);
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            resourceKey = jsonObject.getString("resource");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();

                        Intent intent = new Intent(context, VirustotalResultActivity.class);
                        intent.putExtra("nameOfFile", fileSelected);
                        intent.putExtra("resourceKey", resourceKey);
                        context.startActivity(intent);

                    }
                });
                return true;
            }
            catch (Exception e){
                e.printStackTrace();
                progressDialog.dismiss();
                createDialog("Attention", "Something went wrong");
                return false;
            }
        }

    private void createDialog(String title,String description)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();;
    }

        public void viewProgressDialog()
        {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);

        }
    }
}
