package in.yale.mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityList extends AppCompatActivity {

    public AdapterCategory adapterCategory;
    public ArrayList<mainaray> menulist  = new ArrayList<>();
    public ArrayList<mainaray> menulist1 = new ArrayList<>();
    public ArrayList<mainaray> menulist2 = new ArrayList<>();
    private List<Values> listValues;

    private List<String> listSingleData;

    public TextView txtTitle;

    private List<String> listSlashData;

    private List<String> listSlash,result;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;

    public RequestQueue req;


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
        req = Volley.newRequestQueue(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        menulist = new ArrayList<>();
//        menulist1 = new ArrayList<>();
//        menulist2 = new ArrayList<>();

        menulist = (ArrayList<mainaray>) getIntent().getSerializableExtra("mylist");
        menulist1 = (ArrayList<mainaray>) getIntent().getSerializableExtra("mylist1");
        menulist2 = (ArrayList<mainaray>) getIntent().getSerializableExtra("mylist2");

        shimmerFrameLayout = findViewById(R.id.shimmer);
        recyclerView = findViewById(R.id.listView);
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText("Shop By Category");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterCategory = new AdapterCategory(ActivityList.this, menulist,menulist1,menulist2);
//new
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapterCategory);
        adapterCategory.notifyDataSetChanged();


        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (menulist != null && !menulist.isEmpty()) {
        //if(menulist.isEmpty()){

        }else{
            shimmerFrameLayout.startShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            callshopbycategory();
        }

    }
    public void  callshopbycategory() {
//        menulist.clear();
//        menulist1.clear();
//        menulist2.clear();
        menulist  = new ArrayList<>();
        menulist1  = new ArrayList<>();
        menulist2  = new ArrayList<>();
        String OB_Urla = Constants.BASE_URL + Constants.CATEGORY_URL;
        //  Log.d("OB_Urla",OB_Urla );

        final JsonArrayRequest requetqi = new JsonArrayRequest(Request.Method.GET,OB_Urla, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray responses) {
                        try {

                            for(int i = 0; i < responses.length(); i++) {

                                JSONObject jresponse = responses.getJSONObject(i);
                              //  String ll = jresponse.optString("parent_id");
                                String l1 = jresponse.getString("label");
                                String v1 = jresponse.getString("value");
                             //   String p1 = jresponse.getString("parent_id");
                                String id1 = jresponse.getString("id");


                                String chy = "";
                                JSONArray lev1 = new JSONArray();
                                String idd1 = jresponse.optString("child");
                                if(idd1 != ""){
                                    chy = "true";
                                     lev1 = jresponse.getJSONArray("child");
                                }else{
                                    chy = "false";
                                }
                                menulist.add(new mainaray(l1, v1, "p1", id1, chy));


                               for(int ii = 0; ii < lev1.length(); ii++) {
                                   JSONObject le2 = lev1.getJSONObject(ii);
                                    String l2 = le2.getString("label");
                                   String v2 = le2.getString("value");
                                   String p2 = le2.getString("parent_id");
                                   String id2 = le2.getString("id");
                                   String chy1 = "";
                                   JSONArray lev2 = new JSONArray();
                                   String iddd1 = le2.optString("child");
                                   if(iddd1 != ""){
                                       chy1 = "true";
                                       lev2 = le2.getJSONArray("child");
                                   }else{
                                       chy1 = "false";
                                   }
                                   menulist1.add(new mainaray(l2, v2, p2, id2, chy1));


                                    for(int iii = 0; iii < lev2.length(); iii++) {
                                        JSONObject le3 = lev2.getJSONObject(iii);
                                        String l3 = le3.getString("label");
                                        String v3 = le3.getString("value");
                                        String p3 = le3.getString("parent_id");
                                        String id3 = le3.getString("id");
                                        String chy2 = "";
                                       // JSONArray lev2 = new JSONArray();
                                        String idddd1 = le3.optString("child");
                                        if(idddd1 != ""){
                                            chy2 = "true";
                                           // lev2 = le3.getJSONArray("child");
                                        }else{
                                            chy2 = "false";
                                        }
                                        menulist2.add(new mainaray(l3, v3, p3, id3, chy2));
                                    }

                                }
                            }
//                            adapterCategory.setData(listSingleData);
//                    admenulist1apterCategory.setAllValues(listValues);
                            adapterCategory = new AdapterCategory(ActivityList.this, menulist,menulist1,menulist2);

                            shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapterCategory);
                    adapterCategory.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //readdata();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }


        });

        requetqi.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 40000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 40000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        req.add(requetqi);

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
        if(adapterCategory != null) {
            adapterCategory.setListTitle();

        }else{
            finish();
        }


    }

   // public static class mainaray extends AppCompatActivity {
    public static class mainaray extends AppCompatActivity  implements Serializable {
        private final String label;
        private final String value;
        private final String parentid;
        private final String ids;
        private final String child;
        public mainaray(String lab, String val, String pid, String id, String chy) {
            label = lab;
            value = val;
            parentid = pid;
            ids = id;
            child = chy;
        }
        public String getlab () {
            return label;
        }
        public String getvalue () {
            return value;
        }
        public String getparentid () {
            return parentid;
        }
        public String getid () {
            return ids;
        }
        public String getchild () {
            return child;
        }
    }

}
