package in.yale.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static in.yale.mobile.R.string.SHOP_BY_CATEGORY;

public class ActivityList extends AppCompatActivity {

    private AdapterCategory adapterCategory;

    private List<Values> listValues;

    private List<String> listSingleData;

    private TextView txtTitle;

    private List<String> listSlashData;

    private List<String> listSlash,result;

    private ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Constants.apiCall = Boolean.TRUE;
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // onBackPressed();
            }
        });

        shimmerFrameLayout = findViewById(R.id.shimmer);
        final RecyclerView recyclerView = findViewById(R.id.listView);
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Shop By Category");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterCategory = new AdapterCategory(this);
        adapterCategory.setListener(new AdapterCategory.OnTitleSelected() {
            @Override
            public void showTitle(String title,int j) {
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
        apiTask.setParams(null,Constants.CATEGORY_URL);
        apiTask.responseCallBack(new ApiTask.ResponseListener() {
            @Override
            public void jsonResponse(String result) {
                try{
                    Gson gson = new GsonBuilder().create();
                    ResponseCategory responseCategory =  gson.fromJson(result,ResponseCategory.class);
                    listValues = new ArrayList<>();
                    listSingleData =new ArrayList<>();
                    listSlashData = new ArrayList<>();
                    listSlash = new ArrayList<>();
                    listValues.addAll(responseCategory.getValues());

                    for (int i=0;i<listValues.size();i++){
                        String path = listValues.get(i).getPath();
                        listSlashData.add(listValues.get(i).getPath().trim());
                        if (!path.contains("/"))
                        listSingleData.add(path);
                        else {
                            List<String> separatorData = Arrays.asList(path.split("/"));
                        }
                    }
                    for (int q=0;q<listSingleData.size();q++) {
                        String singlePath = listSingleData.get(q).concat("/");
                        boolean bool = false;
                        for(int p=0;p<listSlashData.size();p++){
                            Matcher match = Pattern.compile(singlePath).matcher(listSlashData.get(p));
                            if(match.find()){
                                bool = true;
                            }
                        }
                        if(!bool){
                            listSingleData.remove(q);
                        }
                    }
                    adapterCategory.setData(listSingleData);
                    adapterCategory.setAllValues(listValues);
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapterCategory);
                    adapterCategory.notifyDataSetChanged();
                }catch(Exception e){  Log.e("List Err", "ERROR getting list"); }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            shimmerFrameLayout.startShimmerAnimation();
        }catch(Exception er){er.printStackTrace();}
    }

    @Override
    protected void onPause() {
        shimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }

    @Override
    public void onBackPressed() {

            adapterCategory.setListTitle();

    }

}
