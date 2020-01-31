package in.yale.mobile;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ActivityShopList extends AppCompatActivity {

    private AdapterShoobyList adapterShoobyList;
    private ArrayList<ShopDetails> listShopDetails;
    private ShimmerFrameLayout shimmerFrameLayout;

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

        shimmerFrameLayout = findViewById(R.id.shimmer);
        final RecyclerView recyclerView = findViewById(R.id.listView);
        TextView txtTitle = findViewById(R.id.txtTitle);
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
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapterShoobyList);
                    adapterShoobyList.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }

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
        finish();
    }
}
