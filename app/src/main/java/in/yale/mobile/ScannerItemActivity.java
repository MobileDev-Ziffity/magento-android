package in.yale.mobile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScannerItemActivity extends AppCompatActivity implements View.OnTouchListener, View.OnFocusChangeListener {

    private LinearLayout llr_shipment_deliver_details;
    private String barcodeText = "",pdata = "",placecode = "";
    private TextView txtSku, txtItemName,deliverMessage,stockMessage,deliverText,pickupText;
    private WebView priceText;
    private TextView soldText;
    private EditText txtMinQuantity;
    private ProgressDialog progressDialog;
    private ImageView imgItem;
    private String CustomURL = "";
    private String formKey;
    private String addToCart;
    private int quantity = 1;
    private int qty = 0;
    private ApiTask apiTask;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RelativeLayout relativeSecond;
    private Button addtoCart;
    public static Boolean apiAddToCart = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_item);

        LinearLayout llr_deliveryDetails = (LinearLayout) findViewById(R.id.llr_deliveryDetails);
        llr_shipment_deliver_details = (LinearLayout) findViewById(R.id.llr_shipment_deliver_details);

        relativeSecond = findViewById(R.id.relativeSecond);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        ImageView imgAdd = findViewById(R.id.img_add);
        ImageView imgSub = findViewById(R.id.img_sub);

        TextView txt_route = findViewById(R.id.txt_route);
        txt_route.setText(Html.fromHtml("<u>Where can i get this?</u>"));
        imgItem = findViewById(R.id.img_item);
        txtMinQuantity= findViewById(R.id.txt_min_quantity);
        deliverMessage = findViewById(R.id.delivery_message);
        stockMessage = findViewById(R.id.stock_message);
        barcodeText = getIntent().getStringExtra("bar_code_text");
        txtSku = findViewById(R.id.txt_sku);
        txtItemName = findViewById(R.id.txt_item_name);
        deliverText = findViewById(R.id.deliver_text);
        pickupText = findViewById(R.id.pickup_text);
        priceText = findViewById(R.id.price_text);
        Button cancel = findViewById(R.id.cancel);
        addtoCart = findViewById(R.id.addToCart);
        soldText = findViewById(R.id.sold_text);
        relativeSecond.setOnTouchListener(this);
        priceText.setOnTouchListener(this);
        txt_route.setOnTouchListener(this);
        txtMinQuantity.setOnFocusChangeListener(this);
        skuScannedItem();


        addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addedToCart();
                apiAddToCart = true;
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiAddToCart = false;
                finish();
            }
        });
        txt_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llr_shipment_deliver_details.getVisibility() == View.VISIBLE) {
                    llr_shipment_deliver_details.setVisibility(View.GONE);
                } else {
                    llr_shipment_deliver_details.setVisibility(View.VISIBLE);
                }
            }
        });


        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                        txtMinQuantity.requestFocus();
                        quantity = Integer.valueOf(txtMinQuantity.getText().toString());
                        quantity += qty;
                        txtMinQuantity.setText(String.valueOf(quantity));
                    }finally {
                        txtMinQuantity.clearFocus();
                    }
                }catch (Exception e){ }
            }
        });

        imgSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    try {
                        txtMinQuantity.requestFocus();
                        if (Integer.valueOf(txtMinQuantity.getText().toString()) == qty) {

                        } else {
                            quantity = Integer.valueOf(txtMinQuantity.getText().toString());
                            quantity -= qty;
                            txtMinQuantity.setText(String.valueOf(quantity));
                        }
                    }finally {
                        txtMinQuantity.clearFocus();
                    }
                }catch (Exception e){ }

            }
        });


        txtItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScannerItemActivity.this, MainActivity.class)
                        .putExtra("custom_url", CustomURL)
                        .putExtra("from_scanner", true));
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(shimmerFrameLayout.getVisibility()!=View.GONE){
                relativeSecond.setVisibility(View.GONE);
            }
            shimmerFrameLayout.startShimmerAnimation();
        }catch(Exception er){er.printStackTrace();}
    }

    @Override
    protected void onPause() {
        shimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
    }


    private void skuScannedItem() {

        String skuUrl = "https://manage.hawksearch.com/sites/uselectric/?mpp=&pg=&category=&hawkcustom=&sort=&hawkspellcheck=0&hawkoutput=json&gzip=1&branch_id=&search=1&keyword=";
        Constants.apiCall = Boolean.TRUE;
        ApiTask apiTask = new ApiTask(ScannerItemActivity.this);
        apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
        apiTask.setParams(null, skuUrl + barcodeText);

        apiTask.responseCallBack(new ApiTask.ResponseListener() {
            @Override
            public void jsonResponse(String result) {
                try {

                    JSONObject jsonObject = new JSONObject(result.trim());

                    if (jsonObject.getJSONArray("Results").length() > 0) {

                        JSONObject obj = jsonObject.getJSONArray("Results").getJSONObject(0);


                        txtSku.setText("SKU: "  +obj.getString("SKU"));
                        txtItemName.setText(Html.fromHtml(obj.getString("ItemName")));
                        CustomURL = obj.getString("CustomURL");

                        Glide.with(ScannerItemActivity.this).load(obj.getJSONObject("Custom").getString("image")).into(imgItem);

                        proxyCallSku(obj.getString("SKU"));


                    }else {
                        if(barcodeText.length() == 12) {
                            proxyCallSku(barcodeText.substring(0,11));
                        }else if(barcodeText.length() == 14){
                            proxyCallSku(barcodeText.substring(2,12));
                        }else {
                            finish();
                            if(!TextUtils.isEmpty(txtSku.getText().toString())) {
                                Toast.makeText(ScannerItemActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ScannerItemActivity.this, "Product Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private void proxyCallSku(String sku) {
        Constants.apiCall = Boolean.FALSE;
        String cookies = CookieManager.getInstance().getCookie(Constants.BASE_URL);
        ApiTask apiTask = new ApiTask(ScannerItemActivity.this);
        apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
        apiTask.setCookie(cookies);
        apiTask.setParams(null, Constants.BASE_URL + "hawksearch/simpleproxy?mpp=&pg=&category=&hawkcustom=&sort=&hawkspellcheck=0&hawkoutput=json&skus=\""+ sku + "\"&skipHawkSearch=1&branch_id=");
        apiTask.responseCallBack(new ApiTask.ResponseListener() {
            @Override
            public void jsonResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result.trim());

                        JSONObject obj = jsonObject.getJSONArray("Results").getJSONObject(0);
                        addToCart = obj.getString("addtocart");
                        formKey = jsonObject.getString("formkey");
                        String strPrice = "<!DOCTYPE html><html><head><style>.special-price .price {color: #E31B23;font-weight: 600; font-size: 22px}.price sup {font-size:70%;position:relative;line-height:0}.special-price small{color:#514D6A; font-family:sourcesanspro_regular}.text-blue {color: #0190DA;}.text-blue small {display: inline-block;line-height: 1.3;}</style></head><body style=\"font-size: 16px; font-family: sourcesanspro_bold; text-align: Right; color: #000000\">" + obj.getString("price")+ "</body></html>";
                        Matcher match = Pattern.compile("Call for price").matcher(obj.getString("price"));
                        if(match.find()){
                            addtoCart.setVisibility(View.GONE);
                        }else {
                            addtoCart.setVisibility(View.VISIBLE);
                        }
                        pdata = obj.getString("magentopid");
                        placecode = obj.getString("PickupBranch");
                        priceText.loadData(strPrice, "text/html", "UTF-8");
                        pickupText.setText(Html.fromHtml(obj.getString("Pickup")));
                        deliverText.setText(obj.getString("Deliver"));
                        txtMinQuantity.setText(obj.getString("pickupBranchMinSaleQty"));
                        qty = Integer.valueOf(obj.getString("pickupBranchMinSaleQty"));
                        if(qty > 1) {
                            soldText.setText("Sold in multiples of "+qty);
                            soldText.setVisibility(View.VISIBLE);
                        }
                        deliverMessage.setText(obj.getString("deliverStockMsg"));
                        stockMessage.setText(obj.getString("pickupStockMsg"));

                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        relativeSecond.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();

                }


            }
        });

    }

    private void addedToCart() {
        Constants.apiCall = Boolean.FALSE;
        String cookies = CookieManager.getInstance().getCookie(Constants.BASE_URL);
        ApiTask apiTask = new ApiTask(ScannerItemActivity.this);
        apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
        apiTask.setCookie(cookies);
        // Cookies key as Form Key
        if(cookies != null){
            String[] keyPairsCookie = cookies.split(";");
            for(int i =0; i < keyPairsCookie.length ; i++) {
                String[] indexes = keyPairsCookie[i].split("=");
                if(indexes[0].trim().equals("form_key")) {
                    formKey = indexes[1];
                    break;
                }
            }
        }
        // End Cookies key
        apiTask.setParams(null, addToCart+"form_key/"+formKey+"/qty/"+quantity+"/delivery_type/"+"Deliver");
        apiTask.responseCallBack(new ApiTask.ResponseListener() {
            @Override
            public void jsonResponse(String result) {
                try {
                    if(!TextUtils.isEmpty(txtSku.getText().toString())) {
                        Toast.makeText(ScannerItemActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ScannerItemActivity.this, "Product Not Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        try {
            if(TextUtils.isEmpty(txtMinQuantity.getText().toString().trim()) || Integer.valueOf(txtMinQuantity.getText().toString().trim()) == 0 ) {
                txtMinQuantity.setText(String.valueOf(qty));
            }else if(Integer.valueOf(txtMinQuantity.getText().toString().trim()) % qty != 0) {
                txtMinQuantity.setText(String.valueOf(Integer.valueOf(txtMinQuantity.getText().toString().trim()) + qty - (Integer.valueOf(txtMinQuantity.getText().toString().trim()) % qty)));
            }
            txtMinQuantity.clearFocus();
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch(Exception e){}
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        try {
            if(!b){
                quantity = Integer.valueOf(txtMinQuantity.getText().toString());
                Constants.apiCall = Boolean.TRUE;
                ApiTask apiTask = new ApiTask(ScannerItemActivity.this);
                apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
                apiTask.setParams(null, Constants.BASE_URL + "pddata/index?pdata="+pdata+"&rqty="+txtMinQuantity.getText().toString().trim()+"&placecode="+placecode+"&reqPage=plp&_=");
                apiTask.responseCallBack(new ApiTask.ResponseListener() {
                    @Override
                    public void jsonResponse(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result.trim());
                            deliverText.setText(Html.fromHtml(jsonObject.getString("delivery")));
                            pickupText.setText(Html.fromHtml(jsonObject.getString("pickup")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



