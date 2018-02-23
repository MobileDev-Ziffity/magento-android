package com.usesi.mobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.client.protocol.RequestAddCookies;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.usesi.mobile.ApiTask.HTTP_TYPE.GET;
import static com.usesi.mobile.ApiTask.HTTP_TYPE.POST;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {
        private static final String TAG = "webviewLog";
        private  WebView webLoad;
        private String search_Text = "";
        private ProgressDialog progress;
        private boolean loggedIn;
        private static String getUrl;
        private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CookieManager.getInstance().setCookie("https://www.usesi.com/?mobileapp=1", "mobile_app_auth=4XcGAuoS3m3zVUChP59iFAs8vuOZ96B3Gxj5n3MqAMwoM3gMNHWE73gqeVP5JS1J");

        webLoad = (WebView) findViewById(R.id.webLoad);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webLoad, true);
        WebSettings webSettings = webLoad.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webLoad.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(webLoad, url, favicon);
                getUrl = view.getUrl();
                Constants.apiCall = Boolean.FALSE;
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
                                Log.d("tag", "jsonResponse: === " + result);

                                loggedIn = jsonObject.getBoolean("loggedIn");

                                JSONObject branch_number = jsonObject.getJSONObject("branch");
                                String phone_number = branch_number.getString("phone");

                                JSONObject address = jsonObject.getJSONObject("branch");
                                String addressLine1 = address.getString("addressLine1");

                                JSONObject city = jsonObject.getJSONObject("branch");
                                String city_name = address.getString("city");

                                JSONObject state = jsonObject.getJSONObject("branch");
                                String state_name = address.getString("state");

                                JSONObject cart = jsonObject.getJSONObject("cart");
                                String count = cart.getString("count");

                                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                                Menu menu = navigationView.getMenu();

                                MenuItem nav_contact = menu.findItem(R.id.nav_contact);
                                nav_contact.setTitle(phone_number);

                                MenuItem nav_branch = menu.findItem(R.id.nav_branch);
                                nav_branch.setTitle("BRANCH :" + addressLine1);

                                MenuItem nav_city = menu.findItem(R.id.nav_city);
                                nav_city.setTitle(city_name+ ", " + state_name);

                                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                                Menu menuBar = toolbar.getMenu();

                                MenuItem nav_cart = menuBar.findItem(R.id.action_cart);
                                View actionView = MenuItemCompat.getActionView(nav_cart);
                                TextView extCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

                                MenuItem nav_account = menuBar.findItem(R.id.action_login);

                                if (count == null || count.equals(null) || count == "null" || count.equals("null"))  {
                                    extCartItemCount.setVisibility(View.GONE);
                                }
                                else{
                                    extCartItemCount.setText(count);
                                }



                                if (loggedIn){
                                    String profileName = jsonObject.getString("name");
                                    View header=navigationView.getHeaderView(0);
                                    userName = (TextView)header.findViewById(R.id.textView);
                                    userName.setText(profileName.toUpperCase());

                                    MenuItem nav_login = menu.findItem(R.id.nav_login);
                                    nav_login.setTitle("LOGOUT");

                                    nav_account.setTitle("My Account");
                                }
                                else{
                                    View header=navigationView.getHeaderView(0);
                                    userName = (TextView)header.findViewById(R.id.textView);
                                    userName.setText("GUEST USER");

                                    MenuItem nav_login = menu.findItem(R.id.nav_login);
                                    nav_login.setTitle("LOGIN");
                                }




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    apiTask.setParams(null, "https://www.usesi.com/customer/index/status?mobileapp=1");

                }else
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(webLoad, url);


            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });


        webLoad.loadUrl("https://www.usesi.com/?mobileapp=1");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        View logoView = toolbar.findViewById(R.id.logo);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webLoad.loadUrl("https://www.usesi.com/?mobileapp=1");

            }
        });
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                webLoad.loadUrl("https://www.usesi.com/?mobileapp=1");
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);


    }

    public String getCookie(String siteName,String CookieName){
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp=cookies.split(";");
        for (String ar1 : temp ){
            if(ar1.contains(CookieName)){
                String[] temp1=ar1.split("=");
                CookieValue = temp1[1];
                break;
            }
        }
        return CookieValue;
    }

    private static View getToolbarLogoView(Toolbar toolbar){
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        if(hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("SEARCH");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint("Search by Keyword or Part number");
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    search_Text = input.getText().toString();
                    webLoad.loadUrl("https://www.usesi.com/hawksearch/keyword/index/?keyword="+search_Text+"&search=1/?mobileapp=1");
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        if (id == R.id.action_cart) {
            webLoad.loadUrl("https://www.usesi.com/checkout/cart/?mobileapp=1");
        }
        if (id == R.id.action_location) {
            webLoad.evaluateJavascript("jQuery(document).ready(function(){jQuery('.header-branch-popup .btn-link').click();})", null);
        }
        if (id == R.id.action_login) {
            webLoad.loadUrl("https://www.usesi.com/customer/account/login/referer/aHR0cHM6Ly93d3cudXNlc2kuY29tL2N1c3RvbWVyL2FjY291bnQvaW5kZXgvP21vYmlsZWFwcD0x/?mobileapp=1");
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

          if (id == R.id.nav_branch) {
            // Handle the camera action
        } else if (id == R.id.nav_category) {
                startActivity(new Intent(this,ActivityList.class));
        } else if (id == R.id.nav_locations) {
           webLoad.loadUrl("https://careers.usesi.com/companies");
        } else if (id == R.id.nav_list) {
              Log.v("TAGVALUE", Boolean.toString(loggedIn));
              if (loggedIn)
                 webLoad.loadUrl("https://www.usesi.com/shoppinglist?mobileapp=1");
              else
                  webLoad.loadUrl("https://www.usesi.com/customer/account/login/referer/aHR0cHM6Ly93d3cudXNlc2kuY29tL2N1c3RvbWVyL2FjY291bnQvaW5kZXgvP21vYmlsZWFwcD0x/?mobileapp=1");
        } else if (id == R.id.nav_catalog) {
            webLoad.loadUrl("https://www.usesi.com/yourcatalog?mobileapp=1");
        } else if (id == R.id.nav_barcode) {
              startActivity(new Intent(this,Barcode.class));
        } else if (id == R.id.nav_photo) {
              startActivity(new Intent(this,SubmitPhoto.class));
        }else if (id == R.id.nav_order) {
            webLoad.loadUrl("https://www.usesi.com/quickorder?mobileapp=1");
        } else if (id == R.id.nav_help){
            webLoad.loadUrl("https://www.usesi.com/help-center?mobileapp=1");
        } else if (id == R.id.nav_login){
              if (loggedIn){
                  webLoad.loadUrl("https://www.usesi.com/customer/account/logout?mobileapp=1");
              }
              else {
                  webLoad.loadUrl("https://www.usesi.com/customer/account/login/referer/aHR0cHM6Ly93d3cudXNlc2kuY29tL2N1c3RvbWVyL2FjY291bnQvaW5kZXgvP21vYmlsZWFwcD0x/?mobileapp=1");
              }
        }

        if (id == R.id.nav_service || id == R.id.nav_branchnumber || id == R.id.nav_branch || id == R.id.nav_city || id == R.id.nav_contact || id == R.id.nav_customer)
        {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        else {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

}
