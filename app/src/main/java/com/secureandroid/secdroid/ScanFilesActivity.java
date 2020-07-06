package com.secureandroid.secdroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ScanFilesActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    public RelativeLayout relativeLayout;
    public String resourceKey;
    public String fileName;
    public String message;

    RealPathUtil obj;
    Context context;

    private Button uploadFiles;
    private Button uploadApps;
    private int STORAGE_PERMISSION_CODE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_files);

        setBottomNavigationView();

        File f = new File("/data/data/com.secureandroid.secdroid/shared_prefs/keyFile.xml");
        if (!f.exists()){
            getApiKey();
        }

        uploadFiles = (Button) findViewById(R.id.uploadFilesId);
        uploadApps = (Button) findViewById(R.id.uploadAppsId);
        relativeLayout = findViewById(R.id.sRelativeLayoutId);

        uploadFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(ScanFilesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, 1);
                }
                else{
                    requestStoragePermission();
                }
            }
        });

        uploadApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ListAppsActivity.class));
            }
        });
    }

    public void getApiKey(){
      AlertDialog.Builder mbuilder = new AlertDialog.Builder(ScanFilesActivity.this);
      View view = getLayoutInflater().inflate(R.layout.dialogview, null);

        mbuilder.setView(view);
        final AlertDialog mdialog = mbuilder.create();

        final EditText editText =(EditText) view.findViewById(R.id.apiKeyETextId);
        Button button =(Button) view.findViewById(R.id.submitButtonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Api key saved", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("keyFile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("apiKey",editText.getText().toString());
                    editor.commit();

                    mdialog.dismiss();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Enter your api key", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mdialog.setCanceledOnTouchOutside(false);
        mdialog.setCancelable(false);
        mdialog.show();

    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(ScanFilesActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri path = data.getData();
                    try {
                        String realPath = obj.getRealPath(this, path);
                        Log.d("actualpath", realPath);
                        Toast.makeText(ScanFilesActivity.this, realPath, Toast.LENGTH_SHORT).show();


                        File file = new File(realPath);

                        double fileSizeBytes = file.length();
                        double fileSizeKb = fileSizeBytes/1000;
                        double fileSizeMb = fileSizeKb/1000;

                        if (hasInternetConnectivity()){
                            if (fileSizeMb<32.00)
                            {
                                UploadTask uploadTask=new UploadTask();
                                uploadTask.execute(new String[]{realPath});
                            }
                            else{
                                String title = "Attention";
                                String description ="Upload files lesser than 32 Mb";
                                createDialog(title, description);
                            }
                        }
                        else {
                            final Snackbar snackbar = Snackbar.make(relativeLayout,"Please check your internet connection",Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }

                    }
                    catch (java.lang.IllegalArgumentException e)
                    {
                        Log.d("theexpcepoc", e.toString());
                        final Snackbar snackbar = Snackbar.make(relativeLayout, "Oops something went wrong. Trying choosing files using File Manager.", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }
                }
                break;
        }
    }

    public boolean hasInternetConnectivity()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return (activeNetwork!=null && activeNetwork.isConnectedOrConnecting());
    }

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

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("keyFile", Context.MODE_PRIVATE);
            if (sharedPreferences.contains("apiKey"))
            {
                apikey = sharedPreferences.getString("apiKey", "Wrong apikey");
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
                fileName = file.getName();
                Log.d("name file", fileName);
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        createDialog("Attention", "something went wrong");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String message=response.body().string();
                        Log.d("Result",""+message);
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            resourceKey = jsonObject.getString("resource");
                            message = jsonObject.getString("verbose_msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();

                            Intent sIntent = new Intent(ScanFilesActivity.this, VirustotalResultActivity.class);
                            sIntent.putExtra("nameOfFile", fileName);
                            sIntent.putExtra("resourceKey", resourceKey);
                            startActivity(sIntent);

                    }
                });
                return true;
            }
            catch (Exception e){
                e.printStackTrace();
                progressDialog.dismiss();
                createDialog("Attention", "something went wrong");
                return false;
            }
        }

        public void viewProgressDialog()
        {
            progressDialog = new ProgressDialog(ScanFilesActivity.this);
            progressDialog.setTitle("Uploading");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);

        }
    }

    public void setBottomNavigationView()
    {
        bottomNavigationView = findViewById(R.id.sBottomNavigationBarId);

        bottomNavigationView.setSelectedItemId(R.id.scanButtonId);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.homeButtonId:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.appsButtonId:
                        startActivity( new Intent(getApplicationContext(),ListAppsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.scanButtonId:
                        return true;
                    case R.id.trafficButtonId:
                        startActivity(new Intent(getApplicationContext(),TrafficViewActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }
    private void createDialog(String title,String description)
    {
        new AlertDialog.Builder(ScanFilesActivity.this)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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