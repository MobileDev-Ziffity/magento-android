package in.yale.mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import static in.yale.mobile.ApiTask.HTTP_TYPE.GET;
import static in.yale.mobile.R.string.logOut;
import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {


    private WebView webLoad;
    private String search_Text = "";
    private boolean loggedIn,employLoggedIn;
    private TextView userName;
    private String count;
    private ProgressDialog progressDialog;
    private String strLocationClicked = "notclicked";
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    private String phone_number;
    private String currentVersionCode;
    private String latestVersion;
    private String mandatoryUpdate;
    private SwipeRefreshLayout swipe;
    private ValueCallback<Uri[]> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 10;
    private ProgressBar progressView;
    private FrameLayout frame;
    private int alertCount = 0;
    private Boolean popfeedBool,poplocBool;
    private AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 17326;
    private String mGeoLocationRequestOrigin = null;
    private GeolocationPermissions.Callback mGeoLocationCallback = null;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"JavascriptInterface", "ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Check Google play services are updated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        sendRegistrationToServer(msg);
                    }
                });


        progressView = findViewById(R.id.progressBar);
        progressView.setMax(100);
        getVersionInfo();
        CookieManager.getInstance().setCookie(Constants.BASE_URL + "?mobileapp=1", "mobile_app_auth=4XcGAuoS3m3zVUChP59iFAs8vuOZ96B3Gxj5n3MqAMwoM3gMNHWE73gqeVP5JS1J");
        webLoad = findViewById(R.id.webLoad);
        webLoad.setWebContentsDebuggingEnabled(true);
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);
        popfeedBool =  getIntent().getBooleanExtra("action_feedback_barcode", false);
        poplocBool = getIntent().getBooleanExtra("action_location_barcode", false);

        //update
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(listener);

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                    requestUpdate(appUpdateInfo);
                    Toast.makeText(MainActivity.this, "Kindly Update the App", Toast.LENGTH_SHORT).show();
                }
                else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    notifyUser();
                }
                else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                    notifyUser();
                }
            }
        });
        //end update


        CookieManager.getInstance().setAcceptThirdPartyCookies(webLoad, true);
        final WebSettings webSettings = webLoad.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webLoad.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                if (url.contains("https://secure.billtrust.com")) {
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url));
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(MainActivity.this,
                            Environment.DIRECTORY_DOWNLOADS, "invoice.pdf");
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File",
                            Toast.LENGTH_LONG).show();
                    webLoad.reload();
                }
            }
        });


        webLoad.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                mGeoLocationRequestOrigin = null;
                mGeoLocationCallback = null;
                // Ask for location permission
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(R.string.permission_location_rationale)
                                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mGeoLocationRequestOrigin = origin;
                                        mGeoLocationCallback = callback;
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                                    }
                                }).show();
                    } else {
                        // No explanation needed, we can request the permission
                        mGeoLocationRequestOrigin = origin;
                        mGeoLocationCallback = callback;
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }

                } else {
                    // Tell Webview that permission has been granted
                    callback.invoke(origin, true, true);
                }
            }

            public void onProgressChanged(WebView view, int Progress) {
                progressView.setVisibility(View.VISIBLE);
                progressView.setProgress(Progress);
                if (Progress == 100) {
                    progressView.setVisibility(View.GONE);
                } else
                    progressView.setVisibility(View.VISIBLE);
                super.onProgressChanged(view, Progress);
            }

            @SuppressLint("NewApi")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                    mUploadMessage = null;
                }

                mUploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();

                try {
                    startActivityForResult(intent, FILECHOOSER_RESULTCODE);
                } catch (ActivityNotFoundException e) {
                    mUploadMessage = null;
                    return false;
                }

                return true;
            }
        });

        webLoad.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(webLoad, url, favicon);
                swipe.setRefreshing(false);
                swipe.setEnabled(true);

                if (strLocationClicked.equals("clicked")) {
                    if (url.contains(Constants.BASE_URL)) {
                        callLoginWebService();
                        strLocationClicked = "notclicked";
                    }
                }
            }


            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(webLoad, url);

                if (url.equals(Constants.BASE_URL + "customer/account/logoutSuccess/")) {
                    callLoginWebService();
                } else if (url.equals(Constants.BASE_URL + "customer/account/")) {
                    callLoginWebService();
                } else if (url.equals(Constants.BASE_URL + "checkout/cart/delete/")) {
                    callLoginWebService();
                }

                if (popfeedBool) {
                    webLoad.evaluateJavascript(" jQuery(document).ready(function(){jQuery('#report-bug-link').click();})", null);
                    popfeedBool = false;
                } else if (poplocBool) {
                    strLocationClicked = "clicked";
                    webLoad.evaluateJavascript("jQuery(document).ready(function($) { if ($('.pickup-modal').css('display') != 'none') { $('.hidden-xs').click(); } $(window.branchChangeModalContainer).modal('toggleModal'); })", null);
                    poplocBool = false;
                }

                webLoad.evaluateJavascript("window.isEmployeeLoggedIn", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if(s.equals("1")){
                            employLoggedIn = true;
                        }else{
                            employLoggedIn = false;
                        }
                    }
                });

            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(!(!url.contains("cart") && url.contains("checkout")) && !url.contains("?mobileapp=1") && url.contains(Constants.BASE_URL)){
                    url = url + "?mobileapp=1";
                }

                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.startsWith("mailto:")) {
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                    return true;
                }

                if (url.startsWith("share:")) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = webLoad.getUrl();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Product detail Page");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    return true;
                }
                if (url.startsWith("print:")) {
                    printAction();
                    return true;
                }

                view.loadUrl(url);

                return true;
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.contains(Constants.BASE_URL + "customer/section/load/?sections=cart%2Cmessages&update_section_id=true")) {
                    webLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            callLoginWebService();
                        }
                    });
                } else if (url.contains(Constants.BASE_URL + "customer/section/load/?sections=shoppinglist&update_section_id=true")) {
                    webLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            callLoginWebService();
                        }
                    });
                } else if (url.contains(Constants.BASE_URL + "customer/section/load/?sections=cart")) {
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
        callShopbyList();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        View logoView = toolbar.findViewById(R.id.logo);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        View login = headerview.findViewById(R.id.textView);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        if (getIntent().getBooleanExtra("from_scanner", false)) {
            String CustomrUrl = getIntent().getStringExtra("custom_url");
            webLoad.loadUrl(Constants.BASE_URL + CustomrUrl);

        }else if (getIntent().getBooleanExtra("menuItem_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/?mobileapp=1");

        } else if (getIntent().getBooleanExtra("alertDialog_barcode", false)) {

            String result = getIntent().getStringExtra("search_Text");
            String loadURL;
            try {
                loadURL = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + URLEncoder.encode(result, "UTF-8") + "&search=1/?mobileapp=1";
            } catch (Exception e) {
                loadURL = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + result + "&search=1/?mobileapp=1";
            }
            webLoad.loadUrl(loadURL);

        } else if (getIntent().getBooleanExtra("action_login_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_locations_barcode", false)) {

            webLoad.loadUrl(Constants.LOCATION_URL);

        } else if (getIntent().getBooleanExtra("login_barcode", false)) {

            if (loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "shoppinglist?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_catalog_barcode", false)) {

            if (loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "yourcatalog?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_help_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "help-center?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_employee_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "employee/login?mobileapp=1");

        } else if (getIntent().getBooleanExtra("nav_login_barcode", false)) {

            if (loggedIn) {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/logout?mobileapp=1");
            } else {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
            }

        } else if (getIntent().getBooleanExtra("logoView_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "?mobileapp=1");

        } else if (getIntent().getBooleanExtra("two_barcode", false)) {

            webLoad.loadUrl(getIntent().getStringExtra("two_url"));

        } else if (getIntent().getBooleanExtra("three_barcode", false)) {

            webLoad.loadUrl(getIntent().getStringExtra("three_url"));

        } else if (getIntent().getBooleanExtra("pushNotifyBoolean", false)) {
           try {
                if (Patterns.WEB_URL.matcher(getIntent().getStringExtra("pushNotify")).matches()) {
                    webLoad.loadUrl(getIntent().getStringExtra("pushNotify"));
                }
            } catch (Exception ef) { }
        }
    }

    private void notifyUser() {

        Snackbar snackbar =
                Snackbar.make(findViewById(android.R.id.content),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("INSTALL", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appUpdateManager != null) {
                    appUpdateManager.completeUpdate();
                }
            }
        });
        snackbar.setActionTextColor(
                getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED){

                notifyUser();
            }
            else if (installState.installStatus() == InstallStatus.INSTALLED){
                if (appUpdateManager != null){
                    appUpdateManager.unregisterListener(listener);
                }
            }
        }
    };

    private void requestUpdate(AppUpdateInfo appUpdateInfo){
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.FLEXIBLE,MainActivity.this,MY_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }


    private void printAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter printAdapter = webLoad.createPrintDocumentAdapter();
            String jobName = "Print";
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }

    private void callShopbyList() {
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
                    } catch (JSONException e) {
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();
                        MenuItem nav_shopbylist = menu.findItem(R.id.nav_shopbylist);
                        nav_shopbylist.setVisible(FALSE);
                        e.printStackTrace();
                    } catch (Exception ef) { }
                }
            });

        }
    }

    private void getVersionInfo() {
        int currentVersion = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionCode = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void callLoginWebService() {
        String getUrl = webLoad.getUrl();
        Constants.apiCall = FALSE;
        String cookies = CookieManager.getInstance().getCookie(getUrl);
        if (Utils.checkInternet(MainActivity.this)) {

            ApiTask apiTask = new ApiTask(MainActivity.this);
            apiTask.setHttpType(GET);
            apiTask.setCookie(cookies);

            apiTask.responseCallBack(new ApiTask.ResponseListener() {
                @SuppressLint("SetTextI18n")
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

                        MenuItem nav_service = menu.findItem(R.id.nav_customer);
                        nav_service.setTitle(Constants.SERVICE_NUMBER);

                        MenuItem nav_branch = menu.findItem(R.id.nav_branch);
                        nav_branch.setTitle("BRANCH :" + addressLine1);

                        MenuItem nav_city = menu.findItem(R.id.nav_city);
                        nav_city.setTitle(city_name + ", " + state_name);
                        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
                        nav_city.getActionView().findViewById(R.id.changeBranch).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                strLocationClicked = "clicked";
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    webLoad.evaluateJavascript("jQuery(document).ready(function($) { if ($('.pickup-modal').css('display') != 'none') { $('.hidden-xs').click(); } $(window.branchChangeModalContainer).modal('toggleModal'); })", null);
                                }
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        });
                        Toolbar toolbar = findViewById(R.id.toolbar);
                        Menu menuBar = toolbar.getMenu();

                        MenuItem nav_cart = menuBar.findItem(R.id.action_cart);
                        View actionView = MenuItemCompat.getActionView(nav_cart);
                        TextView extCartItemCount = actionView.findViewById(R.id.cart_badge);

                        MenuItem nav_account = menuBar.findItem(R.id.action_login);


                        if (jsonObject.has("cart")) {
                            JSONObject cart = jsonObject.getJSONObject("cart");
                            count = cart.getString("count");
                            int value = Integer.parseInt(count);
                            if (!(value >= 1)) {
                                extCartItemCount.setVisibility(View.GONE);
                                extCartItemCount.setText(count);
                            } else {
                                extCartItemCount.setVisibility(View.VISIBLE);
                                extCartItemCount.setText(count);
                            }
                        } else {
                            extCartItemCount.setVisibility(View.GONE);
                        }

                        if (loggedIn) {
                            String profileName = jsonObject.getString("name");
                            View header = navigationView.getHeaderView(0);
                            userName = header.findViewById(R.id.textView);
                            userName.setText(profileName.toUpperCase());
                            MenuItem nav_login = menu.findItem(R.id.nav_login);
                            nav_login.setVisible(true);
                            nav_login.setTitle(logOut);
                            nav_account.setTitle("My Account");
                        } else {
                            View header = navigationView.getHeaderView(0);
                            userName = header.findViewById(R.id.textView);
                            userName.setText("LOGIN / REGISTER");
                            nav_account.setTitle("Login");
                            MenuItem nav_login = menu.findItem(R.id.nav_login);
                            nav_login.setVisible(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            apiTask.setParams(null, Constants.BASE_URL + "customer/index/status?mobileapp=1");

        } else
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Barcode.RESULT_OK) {
            if (data.getBooleanExtra("action_location_barcode",false)) {
                strLocationClicked = "clicked";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webLoad.evaluateJavascript("jQuery(document).ready(function($) { if ($('.pickup-modal').css('display') != 'none') { $('.hidden-xs').click(); } $(window.branchChangeModalContainer).modal('toggleModal'); })", null);
                }
            } else {
                String result = data.getStringExtra("result");
                webLoad.loadUrl(Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + result + "&search=1/?mobileapp=1");
            }
        } else if (requestCode == 2) {
            if (resultCode == ActivityList.RESULT_OK) {
                String result = data.getStringExtra("result");
                String loadURL = Constants.BASE_URL + result.trim();
                webLoad.loadUrl(loadURL + "/?mobileapp=1");
            }
        } else if (requestCode == 3) {
            if (resultCode == ActivityShopList.RESULT_OK) {
                String result = data.getStringExtra("result");
                String loadURL = result.trim() + "/?mobileapp=1";
                webLoad.loadUrl(loadURL);
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mUploadMessage == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mUploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            }
            mUploadMessage = null;
        } else if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "App Download Failed", Toast.LENGTH_SHORT).show();
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
        } else {
            if (webLoad.canGoBack()) {
                String currentURL = webLoad.getUrl();
                if (currentURL.equals(Constants.BASE_URL + "checkout/#payment")) {
                    webLoad.goBack();
                } else {
                    webLoad.goBack();
                }
            } else {
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
            if(alertCount==0) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
                View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                ImageView userInputDialogImageView = mView.findViewById(R.id.barcode_imageView);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                search_Text = userInputDialogEditText.getText().toString();
                                alertCount = 0;
                                String loadURLEncode;
                                try {
                                    loadURLEncode = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + URLEncoder.encode(search_Text, "UTF-8") + "&search=1/?mobileapp=1";
                                } catch (Exception e) {
                                    loadURLEncode = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + search_Text + "&search=1/?mobileapp=1";
                                }
                                webLoad.loadUrl(loadURLEncode);
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                        alertCount = 0;
                                    }
                                });

                final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                userInputDialogImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogAndroid.cancel();
                        alertCount = 0;
                        Intent i = new Intent(MainActivity.this, Barcode.class);
                        startActivityForResult(i, 1);

                    }
                });
                alertDialogAndroid.show();
                alertCount++;
            }
        }

        if (id == R.id.action_cart) {
            webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/?mobileapp=1");
        } else if (id == R.id.action_location) {
            strLocationClicked = "clicked";
            webLoad.evaluateJavascript("jQuery(document).ready(function($) { if ($('.pickup-modal').css('display') != 'none') { $('.hidden-xs').click(); } $(window.branchChangeModalContainer).modal('toggleModal'); })", null);
        } else if (id == R.id.action_login) {
            webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
        } else if (id == R.id.action_feedback) {
            webLoad.evaluateJavascript("jQuery(document).ready(function(){jQuery('#report-bug-link').click();})", null);
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
        } else if (id == R.id.nav_shopbylist) {
            Intent i = new Intent(this, ActivityShopList.class);
            startActivityForResult(i, 3);
        } else if (id == R.id.nav_locations) {
            webLoad.loadUrl(Constants.LOCATION_URL + "?mobileapp=1");
        } else if (id == R.id.nav_list) {
            if (employLoggedIn || loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "wishlists?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
        } else if (id == R.id.nav_catalog) {
            if (employLoggedIn || loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "yourcatalog?mobileapp=1");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
        } else if (id == R.id.nav_barcode) {
            Intent i = new Intent(this, Barcode.class);
            startActivityForResult(i, 1);
        } else if (id == R.id.nav_photo) {
            startActivity(new Intent(this, SubmitPhoto.class));
        } else if (id == R.id.nav_help) {
            webLoad.loadUrl(Constants.BASE_URL + "help-center?mobileapp=1");
        } else if (id == R.id.nav_employee) {
            webLoad.loadUrl(Constants.BASE_URL + "employee/login?mobileapp=1");
        } else if (id == R.id.nav_contact) {
            String number = phone_number;
            if (checkPermission(Manifest.permission.CALL_PHONE)) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
            }
        } else if (id == R.id.nav_customer) {
            String number = Constants.SERVICE_NUMBER;
            if (checkPermission(Manifest.permission.CALL_PHONE)) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
            }
        } else if (id == R.id.nav_login) {
            if (loggedIn) {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/logout/?mobileapp=1");
            } else {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/?mobileapp=1");
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
        switch (requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults.length <= 0 || (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {

                }
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGeoLocationCallback != null ) {
                        mGeoLocationCallback.invoke(mGeoLocationRequestOrigin, true, true);
                    }
                } else {
                    if (mGeoLocationCallback != null) {
                        mGeoLocationCallback.invoke(mGeoLocationRequestOrigin, false, false);
                    }
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        if (strLocationClicked.equals("clicked")) {
            swipe.setEnabled(false);
            swipe.setRefreshing(false);

        } else if (webLoad.getUrl().equals(Constants.BASE_URL + "checkout/")) {
            swipe.setEnabled(false);
            swipe.setRefreshing(false);

        } else {
            swipe.setRefreshing(true);
            webLoad.reload();
        }
    }



}
