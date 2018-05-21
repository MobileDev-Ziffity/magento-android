package in.usesi.mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static in.usesi.mobile.ApiTask.HTTP_TYPE.GET;
import static in.usesi.mobile.R.string.guestUser;
import static in.usesi.mobile.R.string.logOut;
import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

        private WebView webLoad;
        private String search_Text = "";
        private boolean loggedIn;
        private TextView userName;
        private  String count;
        private ProgressDialog progressDialog;
        private  String strLocationClicked = "notclicked";
        private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
        private  String phone_number;
        private  float currentVersionCode;
        private  String latestVersion;
        private  String mandatoryUpdate;
        private SwipeRefreshLayout swipe;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"JavascriptInterface", "ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getVersionInfo();
        CookieManager.getInstance().setCookie(Constants.BASE_URL + "?mobileapp=1", "mobile_app_auth=4XcGAuoS3m3zVUChP59iFAs8vuOZ96B3Gxj5n3MqAMwoM3gMNHWE73gqeVP5JS1J");
        webLoad = findViewById(R.id.webLoad);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webLoad, true);
        final WebSettings webSettings = webLoad.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webLoad.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(webLoad, url, favicon);
                swipe.setRefreshing(false);
                if (strLocationClicked.equals("clicked")) {
                    if (url.contains(Constants.BASE_URL)) {
                            callLoginWebService();
                        strLocationClicked = "notclicked";
                    }
                }
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            
            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(webLoad, url);

                if (url.equals(Constants.BASE_URL + "customer/account/logoutSuccess/")) {
                    callLoginWebService();
                }
                else if (url.equals(Constants.BASE_URL + "customer/account/")) {
                    callLoginWebService();
                }
                else if (url.equals(Constants.BASE_URL + "checkout/cart/delete/"))
                {
                    callLoginWebService();
                }
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    view.reload();
                    return true;
                }

                if (url.startsWith("mailto:")) {
                    //Handle mail Urls
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                    view.reload();
                    return true;
                }

                view.loadUrl(url);
                return true;
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url)
            {
                if (url.contains(Constants.BASE_URL + "customer/section/load/?sections=cart%2Cmessages&update_section_id=true"))
                {
                    webLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            callLoginWebService();
                        }
                    });
                }
                else if (url.contains(Constants.BASE_URL + "customer/section/load/?sections=shoppinglist&update_section_id=true"))
                {
                    webLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            callLoginWebService();
                        }
                    });
                }
                return super.shouldInterceptRequest(view, url);
            }
        });

        webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");
        callLoginWebService();
        callVerionWebService();
        callShopbyList();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        View logoView = toolbar.findViewById(R.id.logo);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.show();
                webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");

            }
        });

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        View logo = headerview.findViewById(R.id.imageView);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.show();
                webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void callVerionWebService()
    {
        Constants.apiCall = Boolean.TRUE;
        if (Utils.checkInternet(MainActivity.this)) {
            ApiTask apiTask = new ApiTask(this);
            apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
            apiTask.setParams(null, Constants.VERSION_CHECK);
            apiTask.responseCallBack(new ApiTask.ResponseListener() {
                @Override
                public void jsonResponse(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                        JSONArray Jarray  = object.getJSONArray(Constants.VERSION_REGION);

                        for (int i = 0; i < Jarray.length(); i++)
                        {
                            JSONObject Jsonobject = Jarray.getJSONObject(i);
                            JSONObject android = Jsonobject.getJSONObject("android");
                            JSONObject latest_version = android.getJSONObject("latest_version");
                            latestVersion = latest_version.getString("version");
                            mandatoryUpdate = latest_version.getString("mandatory");
                        }

                        float latestVersionCode = Float.parseFloat(latestVersion);

                        if (currentVersionCode >= latestVersionCode){

                        }
                        else
                        {
                            if (mandatoryUpdate.equals("1")) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                alertDialogBuilder.setTitle("Alert!!");
                                alertDialogBuilder.setMessage("Please update the App.");
                                alertDialogBuilder.setPositiveButton("Update",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                final String appPackageName = getPackageName(); //
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        });

                                alertDialogBuilder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                            else
                            {

                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    }

    private void callShopbyList()
    {
        Constants.apiCall = Boolean.TRUE;
        if (Utils.checkInternet(MainActivity.this)) {
            ApiTask apiTask = new ApiTask(this);
            apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
            apiTask.setParams(null, Constants.BASE_URL + Constants.SHOP_BY_LIST);
            apiTask.responseCallBack(new ApiTask.ResponseListener() {
                @Override
                public void jsonResponse(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                    }catch (JSONException e) {
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();
                        MenuItem nav_shopbylist = menu.findItem(R.id.nav_shopbylist);
                        nav_shopbylist.setVisible(FALSE);
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private void getVersionInfo() {
        int currentVersion = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionCode = Float.parseFloat(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void callLoginWebService()
    {
        String getUrl = webLoad.getUrl();
        Constants.apiCall = FALSE;
        String cookies = CookieManager.getInstance().getCookie(getUrl);
        if (Utils.checkInternet(MainActivity.this)) {

            ApiTask apiTask = new ApiTask(MainActivity.this);
            apiTask.setHttpType(GET);
            apiTask.setCookie(cookies);

            apiTask.responseCallBack(new ApiTask.ResponseListener() {
                @Override
                public void jsonResponse(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        loggedIn = jsonObject.getBoolean("loggedIn");

                        JSONObject branch_number = jsonObject.getJSONObject("branch");
                        phone_number = branch_number.getString("phone");

                        JSONObject address = jsonObject.getJSONObject("branch");
                        String addressLine1 = address.getString("addressLine1");

                        String city_name = address.getString("city");

                        String state_name = address.getString("state");

                        NavigationView navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();

                        MenuItem nav_contact = menu.findItem(R.id.nav_contact);
                        nav_contact.setTitle(phone_number);

                        MenuItem nav_branch = menu.findItem(R.id.nav_branch);
                        nav_branch.setTitle("BRANCH :" + addressLine1);

                        MenuItem nav_city = menu.findItem(R.id.nav_city);
                        nav_city.setTitle(city_name + ", " + state_name);

                        Toolbar toolbar = findViewById(R.id.toolbar);
                        Menu menuBar = toolbar.getMenu();

                        MenuItem nav_cart = menuBar.findItem(R.id.action_cart);
                        View actionView = MenuItemCompat.getActionView(nav_cart);
                        TextView extCartItemCount = actionView.findViewById(R.id.cart_badge);

                        MenuItem nav_account = menuBar.findItem(R.id.action_login);


                        if (jsonObject.has("cart")){
                            JSONObject cart = jsonObject.getJSONObject("cart");
                            count = cart.getString("count");
                            int value = Integer.parseInt(count);
                            if (!(value >= 1))
                            {
                                extCartItemCount.setVisibility(View.GONE);
                                extCartItemCount.setText(count);
                            }
                            else
                            {
                                extCartItemCount.setVisibility(View.VISIBLE);
                                extCartItemCount.setText(count);
                            }
                        }
                        else
                        {
                            extCartItemCount.setVisibility(View.GONE);
                        }

                        if (loggedIn) {
                            String profileName = jsonObject.getString("name");
                            View header = navigationView.getHeaderView(0);
                            userName = header.findViewById(R.id.textView);
                            userName.setText(profileName.toUpperCase());
                            MenuItem nav_login = menu.findItem(R.id.nav_login);
                            nav_login.setTitle(logOut);
                            nav_account.setTitle("My Account");
                        } else {
                            View header = navigationView.getHeaderView(0);
                            userName = header.findViewById(R.id.textView);
                            userName.setText(guestUser);
                            MenuItem nav_login = menu.findItem(R.id.nav_login);
                            nav_login.setTitle("LOGIN");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            apiTask.setParams(null, Constants.BASE_URL + "customer/index/status?mobileapp=1");

        } else
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected   void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Barcode.RESULT_OK){
                String result = data.getStringExtra("result");
                webLoad.loadUrl(Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + result + "&search=1/?mobileapp=1");
            }
        }
        else if (requestCode == 2) {
            if(resultCode == ActivityList.RESULT_OK){
                String result = data.getStringExtra("result");
                String loadURL = Constants.BASE_URL + result.trim();
                webLoad.loadUrl(loadURL);
            }
        }
        else if (requestCode == 3) {
            if(resultCode == ActivityShopList.RESULT_OK){
                String result = data.getStringExtra("result");
                String loadURL = result;
                webLoad.loadUrl(loadURL);
            }
        }
    }

    public String getCookie(String siteName, String CookieName) {
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp = cookies.split(";");
        for (String ar1 : temp) {
            if (ar1.contains(CookieName)) {
                String[] temp1 = ar1.split("=");
                CookieValue = temp1[1];
                break;
            }
        }
        return CookieValue;
    }

    protected static View getToolbarLogoView(Toolbar toolbar) {
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<>();
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (webLoad.canGoBack()){
                String currentURL = webLoad.getUrl();
                if (currentURL.equals(Constants.BASE_URL + "checkout/#payment"))
                {
                    webLoad.goBack();
                }
                else {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.show();
                    webLoad.goBack();
                }
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem cartView = menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(cartView, R.layout.action_item);
        MenuItemCompat.getActionView(cartView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.show();
                webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/?mobileapp=1");
            }
        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
            View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
            alertDialogBuilderUserInput.setView(mView);

            final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            // ToDo get user input here
                            search_Text = userInputDialogEditText.getText().toString();
                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.show();
                            webLoad.loadUrl(Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + search_Text + "&search=1/?mobileapp=1");
                        }
                    })

                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    dialogBox.cancel();
                                }
                            });

            AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
        }

        if (id == R.id.action_cart) {
            webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/?mobileapp=1");
        }
        if (id == R.id.action_location) {
            strLocationClicked = "clicked";
            webLoad.evaluateJavascript("jQuery(document).ready(function(){jQuery('.header-branch-popup .btn-link').click();})", null);
        }
        if (id == R.id.action_login) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/referer/aHR0cHM6Ly93d3cudXNlc2kuY29tL2N1c3RvbWVyL2FjY291bnQvaW5kZXgvP21vYmlsZWFwcD0x/?mobileapp=1");
        }
        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_branch) {
            // Handle the camera action
        } else if (id == R.id.nav_category) {
            Intent i = new Intent(this, ActivityList.class);
            startActivityForResult(i, 2);
        }else if (id == R.id.nav_shopbylist) {
            Intent i = new Intent(this, ActivityShopList.class);
            startActivityForResult(i, 3);
        }else if (id == R.id.nav_locations) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            webLoad.loadUrl(Constants.LOCATION_URL);
        } else if (id == R.id.nav_list) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            if (loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "shoppinglist?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/referer/aHR0cHM6Ly93d3cudXNlc2kuY29tL2N1c3RvbWVyL2FjY291bnQvaW5kZXgvP21vYmlsZWFwcD0x/?mobileapp=1");
        } else if (id == R.id.nav_catalog) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            if (loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "yourcatalog?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/referer/aHR0cHM6Ly93d3cudXNlc2kuY29tL2N1c3RvbWVyL2FjY291bnQvaW5kZXgvP21vYmlsZWFwcD0x/?mobileapp=1");
        } else if (id == R.id.nav_barcode) {
            Intent i = new Intent(this, Barcode.class);
            startActivityForResult(i, 1);
        } else if (id == R.id.nav_photo) {
            startActivity(new Intent(this, SubmitPhoto.class));
        } else if (id == R.id.nav_order) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            webLoad.loadUrl(Constants.BASE_URL + "quickorder?mobileapp=1");
        } else if (id == R.id.nav_help) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            webLoad.loadUrl(Constants.BASE_URL + "help-center?mobileapp=1");
        }else if (id == R.id.nav_contact){
            String number = phone_number;
            if (checkPermission(Manifest.permission.CALL_PHONE)) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
            }
        }else if (id == R.id.nav_customer){
            String number = "+1-781-297-5666";
            if (checkPermission(Manifest.permission.CALL_PHONE)) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
            }
        }else if (id == R.id.nav_login) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            if (loggedIn) {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/logout?mobileapp=1");
            } else {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/referer/aHR0cHM6Ly93d3cudXNlc2kuY29tL2N1c3RvbWVyL2FjY291bnQvaW5kZXgvP21vYmlsZWFwcD0x/?mobileapp=1");
            }
        }

        if (id == R.id.nav_service || id == R.id.nav_branchnumber || id == R.id.nav_branch || id == R.id.nav_city || id == R.id.nav_contact || id == R.id.nav_customer) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            return false;
        } else {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE :
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
        }
    }

    @Override
    public void onRefresh() {
        swipe.setRefreshing(true);
        webLoad.reload();

    }
}
