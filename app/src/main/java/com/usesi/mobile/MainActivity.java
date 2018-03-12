package com.usesi.mobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import static com.usesi.mobile.ApiTask.HTTP_TYPE.GET;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

        private WebView webLoad;
        private String search_Text = "";
        private ProgressDialog progress;
        private boolean loggedIn;
        private static String getUrl;
        private TextView userName;
        private  String count;
        private ProgressDialog progressDialog;
        private  String strLocationClicked = "notclicked";

    @SuppressLint({"JavascriptInterface", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CookieManager.getInstance().setCookie(Constants.BASE_URL + "?mobileapp=1", "mobile_app_auth=4XcGAuoS3m3zVUChP59iFAs8vuOZ96B3Gxj5n3MqAMwoM3gMNHWE73gqeVP5JS1J");
        webLoad = (WebView) findViewById(R.id.webLoad);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webLoad, true);
        final WebSettings webSettings = webLoad.getSettings();
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
                else if (url.equals(Constants.BASE_URL + "customer/account/")){
                    callLoginWebService();
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }


        });

        webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");
        callLoginWebService();
        webLoad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        webLoad.evaluateJavascript("document.getElementById('product-addtocart-button').textContent", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                Spanned decodedString = Html.fromHtml(s);
                                Log.d("tag", "decodedString IN: === " + decodedString);

                                if (decodedString.equals("\n Deliver\n ")){
                                    Log.i("TAG", "onScroll: ");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                             callLoginWebService();

                                        }
                                    },6000);
                                }
                            }
                        });
                    }
                }
                    return false;
                }

        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        View backHistoryViewView = toolbar.findViewById(R.id.backHistory);
        backHistoryViewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webLoad.canGoBack()){
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.show();
                    webLoad.goBack();
                }
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
                progressDialog.show();
                webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void callLoginWebService()
    {
        Log.d("TAG", "onFling: ");
        getUrl = webLoad.getUrl();
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
                        Log.d("tag", "Logged IN: === " + loggedIn);

                        JSONObject branch_number = jsonObject.getJSONObject("branch");
                        String phone_number = branch_number.getString("phone");

                        JSONObject address = jsonObject.getJSONObject("branch");
                        String addressLine1 = address.getString("addressLine1");

                        JSONObject city = jsonObject.getJSONObject("branch");
                        String city_name = address.getString("city");

                        JSONObject state = jsonObject.getJSONObject("branch");
                        String state_name = address.getString("state");

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();

                        MenuItem nav_contact = menu.findItem(R.id.nav_contact);
                        nav_contact.setTitle(phone_number);

                        MenuItem nav_branch = menu.findItem(R.id.nav_branch);
                        nav_branch.setTitle("BRANCH :" + addressLine1);

                        MenuItem nav_city = menu.findItem(R.id.nav_city);
                        nav_city.setTitle(city_name + ", " + state_name);

                        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                        Menu menuBar = toolbar.getMenu();

                        MenuItem nav_cart = menuBar.findItem(R.id.action_cart);
                        View actionView = MenuItemCompat.getActionView(nav_cart);
                        TextView extCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

                        MenuItem nav_account = menuBar.findItem(R.id.action_login);


                        if (jsonObject.has("cart")){
                            JSONObject cart = jsonObject.getJSONObject("cart");
                            count = cart.getString("count");
                            int value = Integer.parseInt(count);
                            if (!(value >= 1))
                            {
                                Log.d("tag", "IF condition True: === " + result);
                                extCartItemCount.setVisibility(View.GONE);
                            }
                            else
                            {
                                Log.d("tag", "Else Condition: === " + result);
                                extCartItemCount.setText(count);
                            }
                        }
                        else
                        {
                            extCartItemCount.setVisibility(View.GONE);
                        }

                        if (loggedIn == true) {
                            String profileName = jsonObject.getString("name");
                            Log.d("tag", "Logged IN: === " + profileName);
                            View header = navigationView.getHeaderView(0);
                            userName = (TextView) header.findViewById(R.id.textView);
                            userName.setText(profileName.toUpperCase());

                            MenuItem nav_login = menu.findItem(R.id.nav_login);
                            nav_login.setTitle("LOGOUT");

                            nav_account.setTitle("My Account");
                        } else {
                            View header = navigationView.getHeaderView(0);
                            userName = (TextView) header.findViewById(R.id.textView);
                            userName.setText("GUEST USER");

                            MenuItem nav_login = menu.findItem(R.id.nav_login);
                            nav_login.setTitle("LOGIN");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            apiTask.setParams(null, "https://alpha.usesi.com/customer/index/status?mobileapp=1");

        } else
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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

    private static View getToolbarLogoView(Toolbar toolbar) {
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
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
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.show();
                    webLoad.loadUrl(Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + search_Text + "&search=1/?mobileapp=1");
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
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_branch) {
            // Handle the camera action
        } else if (id == R.id.nav_category) {
            startActivity(new Intent(this, ActivityList.class));
        } else if (id == R.id.nav_locations) {
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
            webLoad.loadUrl(Constants.BASE_URL + "yourcatalog?mobileapp=1");
        } else if (id == R.id.nav_barcode) {
            startActivity(new Intent(this, Barcode.class));
        } else if (id == R.id.nav_photo) {
            startActivity(new Intent(this, SubmitPhoto.class));
        } else if (id == R.id.nav_order) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            webLoad.loadUrl(Constants.BASE_URL + "quickorder?mobileapp=1");
        } else if (id == R.id.nav_help) {
            progressDialog.show();
            webLoad.loadUrl(Constants.BASE_URL + "help-center?mobileapp=1");
        } else if (id == R.id.nav_login) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.show();
            if (loggedIn) {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/logout?mobileapp=1");
            } else {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/referer/aHR0cHM6Ly93d3cudXNlc2kuY29tL2N1c3RvbWVyL2FjY291bnQvaW5kZXgvP21vYmlsZWFwcD0x/?mobileapp=1");
            }
        }

        if (id == R.id.nav_service || id == R.id.nav_branchnumber || id == R.id.nav_branch || id == R.id.nav_city || id == R.id.nav_contact || id == R.id.nav_customer) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            return true;
        } else {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

}
