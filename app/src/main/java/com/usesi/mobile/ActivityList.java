package com.usesi.mobile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityList extends AppCompatActivity {

    private AdapterCategory adapterCategory;

    private List<Values> listValues;

    private List<String> listSingleData;

    private int k=0;

    private TextView txtTitle;

    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Constants.apiCall = Boolean.TRUE;
         toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listView);
        txtTitle  = (TextView) findViewById(R.id.txtTitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterCategory = new AdapterCategory(this);
        adapterCategory.setListener(new AdapterCategory.OnTitleSelected() {
            @Override
            public void showTitle(String title,int j) {
                Log.d("tag", "showTitle: "+title);
                txtTitle.setText(title);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ApiTask apiTask = new ApiTask(this);
        apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
        apiTask.setParams(null,"http://manage.hawksearch.com/sites/uselectric/?hawkoutput=json&hawkpath=category");
        apiTask.responseCallBack(new ApiTask.ResponseListener() {
            @Override
            public void jsonResponse(String result) {
                Log.d("tag", "category search " +result);
                Gson gson = new GsonBuilder().create();
                ResponseCategory responseCategory =  gson.fromJson(result,ResponseCategory.class);
                listValues = new ArrayList<>();
                listSingleData =new ArrayList<>();
                listValues.addAll(responseCategory.getValues());

                for (int i=0;i<listValues.size();i++){
                    String path = listValues.get(i).getPath();
                    if (!path.contains("/"))
                    listSingleData.add(path);
                    else {
                        List<String> separatorData = Arrays.asList(path.split("/"));

                    }
                }
                adapterCategory.setData(listSingleData);
                adapterCategory.setAllValues(listValues);
                recyclerView.setAdapter(adapterCategory);
                adapterCategory.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        adapterCategory.setListTitle();
        adapterCategory.setListener(new AdapterCategory.OnTitleSelected() {
            @Override
            public void showTitle(String title,int j) {
                if (j==1) {
                    txtTitle.setText("Shop by Category");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }else txtTitle.setText(title);
            }
        });
    }
}
