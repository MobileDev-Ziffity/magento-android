package in.usesi.mobile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ActivityShopList extends AppCompatActivity {

    private AdapterShoobyList adapterShoobyList;
    private TextView txtTitle;
    private ArrayList<ShopDetails> listShopDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Constants.apiCall = Boolean.TRUE;
        Toolbar toolbar = findViewById(R.id.toolBar);
        listShopDetails = new ArrayList<ShopDetails>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // onBackPressed();
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.listView);
        txtTitle = findViewById(R.id.txtTitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterShoobyList = new AdapterShoobyList(this);
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ApiTask apiTask = new ApiTask(this);
        apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
        apiTask.setParams(null,Constants.BASE_URL + Constants.SHOP_BY_LIST);
        apiTask.responseCallBack(new ApiTask.ResponseListener() {
            @Override
            public void jsonResponse(String result) {
                try {
                    JSONObject jObject = new JSONObject(result.trim());

                    Iterator<?> keys = jObject.keys();

                    while( keys.hasNext() ) {
                        String key = (String)keys.next();
                            if (jObject.get(key) instanceof JSONObject) {
                                String id = ((JSONObject) jObject.get(key)).getString("id");
                                String url = ((JSONObject) jObject.get(key)).getString("url");
                                String name = ((JSONObject) jObject.get(key)).getString("name");
                                String level = ((JSONObject) jObject.get(key)).getString("level");
                                String parent = ((JSONObject) jObject.get(key)).getString("parent_id");
                                listShopDetails.add(new ShopDetails(id, url, name, level, parent));
                        }
                    }

                    adapterShoobyList.setData(listShopDetails);
                    recyclerView.setAdapter(adapterShoobyList);
                    adapterShoobyList.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
