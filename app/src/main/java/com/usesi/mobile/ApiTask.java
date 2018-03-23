package com.usesi.mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.usesi.mobile.ApiTask.HTTP_TYPE.*;

public class ApiTask extends AsyncTask<String, String, String> {

    private ProgressDialog progressDialog;

    private Context context;

    private RequestBody requestBody;

    private String url;

    private String cookie;

    private HTTP_TYPE type;

    void setCookie(String cookiesValue) {
        this.cookie = cookiesValue;
    }

    public enum HTTP_TYPE {
        POST,
        GET
    }

    private ResponseListener listener;

    public interface ResponseListener {
        void jsonResponse(String result);
    }

    void responseCallBack(ResponseListener listener) {
        this.listener = listener;
    }

    ApiTask(Context context) {
        this.context = context;
        type = POST;
    }

    void setParams(RequestBody requestBody, String url) {
        this.requestBody = requestBody;
        this.url = url;
        this.execute();
    }

    void setHttpType(HTTP_TYPE type) {
        this.type = type;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        //progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            if (Constants.apiCall){
                builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
            }
            else{
                builder.addHeader("Cookie",cookie);
            }
            if (type == POST)
                builder.post(requestBody);

            Response response = okHttpClient.newCall(builder.build()).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (listener != null)
            listener.jsonResponse(response);
    }
}
