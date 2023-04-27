package in.yale.mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.SystemClock;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.comapi.Callback;
import com.comapi.Comapi;
import com.comapi.ComapiClient;
import com.comapi.ComapiConfig;
import com.comapi.Session;
import com.comapi.internal.network.ComapiResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import java.io.Serializable;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.service.controls.ControlsProviderService.TAG;
import static in.yale.mobile.R.string.logOut;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {


    private WebView webLoad;
    private String search_Text = "";
    private boolean loggedIn,employLoggedIn,cutomersupport,showbranch,show_notify,show_delipickup;
    private TextView userName;
    private String count,fcmtoken;
    private ProgressDialog progressDialog;
    private String strLocationClicked = "notclicked";
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    private String phone_number;
    private String currentVersionCode;
    private String tokencheck;
    private String searchURL;
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
    private FirebaseAnalytics mFirebaseAnalytics;
    private Bundle params;
    private String messageForEmployee = "";
    private String sPackageNameToUse;
    private String packageName = null;
    private String customerEmail = "";
    CookieManager cookieManager;
    public ArrayList<ActivityList.mainaray> menulist;
    public ArrayList<ActivityList.mainaray> menulist1;
    public ArrayList<ActivityList.mainaray> menulist2;
    public ArrayList<ActivityList.mainaray> menulist3;
    public RequestQueue req;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"JavascriptInterface", "ClickableViewAccessibility", "SetJavaScriptEnabled", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        params = new Bundle();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                         fcmtoken = task.getResult().getToken();

                        // Log and toast
                       // String msg = getString(R.string.msg_token_fmt, token);


                      //  Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    });
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//
//                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });


        new GetVersionCode().execute();
        progressView = findViewById(R.id.progressBar);
        progressView.setMax(100);
        //progressView.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
        getVersionInfo();
        CookieManager.getInstance().setCookie(Constants.BASE_URL, "mobile_app_auth=4XcGAuoS3m3zVUChP59iFAs8vuOZ96B3Gxj5n3MqAMwoM3gMNHWE73gqeVP5JS1J");
        webLoad = findViewById(R.id.webLoad);
        WebView.setWebContentsDebuggingEnabled(true);
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);
        popfeedBool =  getIntent().getBooleanExtra("action_feedback_barcode", false);
        poplocBool = getIntent().getBooleanExtra("action_location_barcode", false);

        webLoad.getSettings().setUserAgentString("HTTP_MOBILEAPP");

        //end update



        cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webLoad, true);
        }

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
        webLoad.getSettings().setUserAgentString("HTTP_MOBILEAPP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        NavigationView navigationViewp = findViewById(R.id.nav_view);
      //  navigationViewp.getMenu().setGroupVisible(R.id.grp11a,false);

        webLoad.addJavascriptInterface(new WebAppInterface(), "StatusHandler");
        webLoad.addJavascriptInterface(new WebAppClickInterface(), "ClickHandler");
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
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                if (Progress == 100) {
                    progressView.setVisibility(View.GONE);
                    // drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else
                    progressView.setVisibility(View.VISIBLE);
                // drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
                //    startActivityForResult(intent, FILECHOOSER_RESULTCODE);
                    someActivityResultLauncher.launch(intent);
                } catch (ActivityNotFoundException e) {
                    mUploadMessage = null;
                    return false;
                }

                return true;
            }
        });
        req = Volley.newRequestQueue(this);
        menulist = new ArrayList<>();
        menulist1 = new ArrayList<>();
        menulist2 = new ArrayList<>();
        menulist3 = new ArrayList<>();
        callshopbycategory();
        webLoad.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(webLoad, url, favicon);
                swipe.setRefreshing(false);
                swipe.setEnabled(true);

                if (strLocationClicked.equals("clicked")) {
                    if (url.contains(Constants.BASE_URL)) {
                        //callLoginWebService();
                        strLocationClicked = "notclicked";
                    }
                }
                String ua=new WebView(MainActivity.this).getSettings().getUserAgentString();
                String uad = ua +"/HTTP_MOBILEAPP";
                webLoad.getSettings().setUserAgentString(uad);
                if (url.contains("payment")) {
                  //  webLoad.getSettings().setUserAgentString("");
                }else if(url.contains("success")){
                 //   webLoad.getSettings().setUserAgentString("HTTP_MOBILEAPP");
                  //  webLoad.reload();

                }else{
                  //  webLoad.getSettings().setUserAgentString("HTTP_MOBILEAPP");
                }

            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(webLoad, url);

                if (popfeedBool) {
                    webLoad.evaluateJavascript(" jQuery(document).ready(function(){jQuery('#report-bug-link').click();})", null);
                    popfeedBool = false;
                } else if (poplocBool) {
                    strLocationClicked = "clicked";
                    webLoad.evaluateJavascript("(function() { if(jQuery('.pickup-modal').css('display') != 'none'){  jQuery('.hidden-xs').trigger('click'); } jQuery(window.branchChangeModalContainer).modal('toggleModal');  jQuery('#branchchanger-button').trigger('click'); })();", null);
                    poplocBool = false;
                }

                if (url.contains("payment")) {
                    String ua=new WebView(MainActivity.this).getSettings().getUserAgentString();
                    String uad = ua +"/HTTP_MOBILEAPP";
                    webLoad.getSettings().setUserAgentString(uad);
                }
                webLoad.evaluateJavascript("window.isEmployeeLoggedIn", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        employLoggedIn = s.equals("1");
                        if(!messageForEmployee.equals("")) {
                            MainActivity.this.postMessage(messageForEmployee);
                        }
                    }
                });

            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                String myvar = "<!DOCTYPE html> <html> <head>   <meta charset=\"utf-8\" />   <title>No connection to the internet</title>   <style>       html,body { margin:0; padding:0; }       html {         background: #FFFFFF -webkit-linear-gradient(top, #FFF 0%, #FFFFFF 100%) no-repeat;         background: #FFFFFF linear-gradient(to bottom, #FFF 0%, #FFFFFF 100%) no-repeat;       }       body {         font-family: sans-serif;         color: #000;         text-align: center;         font-size: 150%;       }       h1, h2 { font-weight: normal; }       h1 { margin: 0 auto; padding: 0.15em; font-size: 10em; text-shadow: 0 2px 2px #000; }       h2 { margin-bottom: 2em; }  .btn {   box-sizing: border-box;   appearance: none;   background-color: transparent;   border: 2px solid #000000;   border-radius: 0.6em;   color: #000000;   cursor: pointer;   align-self: center;   font-size: 1rem;   font-weight: 400;   line-height: 1;   margin: 20px;   padding: 1.2em 2.8em;   text-decoration: none;   text-align: center;   text-transform: uppercase;   font-weight: 700; } a {   text-decoration: none;   color: inherit; }  .btn:hover, .btn:focus {   color: #fff;   outline: 0; }  .first {   transition: box-shadow 300ms ease-in-out, color 300ms ease-in-out; }  .first:hover {   box-shadow: 0 0 40px 40px #000000 inset; }     </style> </head> <body> <h1>⚠</h1> <h2>"+description+"</h2> <h4>Error or Disconnected</h4> <button class=\"btn first\"><a href=\""+Constants.BASE_URL+"\">Retry</a></button> </body> </html>";
                webLoad.loadDataWithBaseURL("", myvar, "text/html", "UTF-8", "");
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(!(!url.contains("cart") && url.contains("checkout")) && url.contains(Constants.BASE_URL)){
                  //  url = url + "?mobileapp=1";
                }

                if (url.endsWith(".pdf")) {
                    try {
                        if (packageName != null) {
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            CustomTabColorSchemeParams params = new CustomTabColorSchemeParams.Builder()
                                    .setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                    .setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark))
                                    .setSecondaryToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                                    .build();
                            builder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params);
                            builder.setStartAnimations(MainActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
                            builder.setExitAnimations(MainActivity.this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.intent.setPackage(packageName);
                            customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                        }
                    } catch (Exception ignored) {
                        Toast.makeText(MainActivity.this, "Cant Open file at this Moment!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
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
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = webLoad.getUrl();
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Product detail Page");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
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

                return super.shouldInterceptRequest(view, url);
            }
        });

        //callLoginWebService();
        if (Utils.checkInternet(MainActivity.this)) {
            webLoad.loadUrl(Constants.BASE_URL);
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            String myvar = "<!DOCTYPE html> <html> <head>   <meta charset=\"utf-8\" />   <title>No connection to the internet</title>   <style>       html,body { margin:0; padding:0; }       html {         background: #FFFFFF -webkit-linear-gradient(top, #FFF 0%, #FFFFFF 100%) no-repeat;         background: #FFFFFF linear-gradient(to bottom, #FFF 0%, #FFFFFF 100%) no-repeat;       }       body {         font-family: sans-serif;         color: #000;         text-align: center;         font-size: 150%;       }       h1, h2 { font-weight: normal; }       h1 { margin: 0 auto; padding: 0.15em; font-size: 10em; text-shadow: 0 2px 2px #000; }       h2 { margin-bottom: 2em; }  .btn {   box-sizing: border-box;   appearance: none;   background-color: transparent;   border: 2px solid #000000;   border-radius: 0.6em;   color: #000000;   cursor: pointer;   align-self: center;   font-size: 1rem;   font-weight: 400;   line-height: 1;   margin: 20px;   padding: 1.2em 2.8em;   text-decoration: none;   text-align: center;   text-transform: uppercase;   font-weight: 700; } a {   text-decoration: none;   color: inherit; }  .btn:hover, .btn:focus {   color: #fff;   outline: 0; }  .first {   transition: box-shadow 300ms ease-in-out, color 300ms ease-in-out; }  .first:hover {   box-shadow: 0 0 40px 40px #000000 inset; }     </style> </head> <body> <h1>⚠</h1> <h2>No connection to the internet</h2> <button class=\"btn first\"><a href=\""+Constants.BASE_URL+"\">Retry</a></button> </body> </html>";
            webLoad.loadDataWithBaseURL("", myvar, "text/html", "UTF-8", "");
        }
        callShopbyList();
        callShopbybrand();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        View logoView = toolbar.findViewById(R.id.logo);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundle params = new Bundle();
                String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                params.putString("date",dateStr);
                mFirebaseAnalytics.logEvent("navbar_Logo", params);
                webLoad.loadUrl(Constants.BASE_URL);

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
                webLoad.loadUrl(Constants.BASE_URL);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        View login = headerview.findViewById(R.id.textView);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!employLoggedIn) {
                    //Bundle params = new Bundle();
                    String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                    params.putString("date", dateStr);
                    mFirebaseAnalytics.logEvent("navdrawer_LOGINREGISTER", params);
                    webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/");
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });


        navigationView.setNavigationItemSelectedListener(this);
        barcodeIntentListener(webLoad);
        packageName = getPackageNameToUse();

    }
    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
        if (show_notify) {
            if (loggedIn) {
//                int value = Integer.parseInt(count);
//                if (!(value >= 1)) {
//
//                } else {
//                   // scheduleNotification();
//                }

            }
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });


    private void scheduleNotification() {
        Intent myIntent = new Intent(getApplicationContext() , MyNotificationPublisher.class ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService( ALARM_SERVICE ) ;
        PendingIntent pendingIntent = PendingIntent. getService ( this, 0 , myIntent , 0 ) ;
        Calendar calendar = Calendar.getInstance () ;
//        calendar.set(Calendar.SECOND , 0 ) ;
//        calendar.set(Calendar.MINUTE , 56 ) ;
//        calendar.set(Calendar.HOUR_OF_DAY, 07 ) ;
//        calendar.set(Calendar.AM_PM , Calendar.AM_PM ) ;
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 05);
        calendar.set(Calendar.SECOND, 0);
        //   calendar.add(Calendar.DAY_OF_MONTH , 1 ) ;
        // calendar.getTimeInMillis()
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
       // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP , 1000, 1000, pendingIntent) ;
    }
    private void Dotdigital(String email) {
        FirebaseApp.initializeApp(this);
        ChallengeHandler challengeHandler = new ChallengeHandler();
        challengeHandler.emailAddress=email;
        customerEmail=email;
        ComapiConfig config = new ComapiConfig() .apiSpaceId(Constants.DOTDIGITAL_APISPACEID) .authenticator(challengeHandler);
        Comapi.initialiseShared(getApplication(), config, new Callback<ComapiClient>() {
            @Override
            public void success(ComapiClient client) {
                //Use ComapiClient object to communicate with services
                compapiCommunicate(client);
            }

            @Override
            public void error(Throwable t) {
                //Toast.makeText(MainActivity.this, t.getMessage() + " :Fail initializeComapi", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getPackageNameToUse() {
        if (sPackageNameToUse != null) return sPackageNameToUse;

        PackageManager pm = getPackageManager();
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BASE_URL));
        ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction("android.support.customtabs.action.CustomTabsService");
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }

        if (packagesSupportingCustomTabs.isEmpty()) {
            sPackageNameToUse = null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            sPackageNameToUse = packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
                && !hasSpecializedHandlerIntents(this, activityIntent)
                && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            sPackageNameToUse = defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains("com.android.chrome")) {
            sPackageNameToUse = "com.android.chrome";
        } else if (packagesSupportingCustomTabs.contains("com.chrome.beta")) {
            sPackageNameToUse = "com.chrome.beta";
        } else if (packagesSupportingCustomTabs.contains("com.chrome.dev")) {
            sPackageNameToUse = "com.chrome.dev";
        } else if (packagesSupportingCustomTabs.contains("com.google.android.apps.chrome")) {
            sPackageNameToUse = "com.google.android.apps.chrome";
        }
        return sPackageNameToUse;
    }

    private boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(
                    intent,
                    PackageManager.GET_RESOLVED_FILTER);
            if (handlers.size() == 0) {
                return false;
            }
            for (ResolveInfo resolveInfo : handlers) {
                IntentFilter filter = resolveInfo.filter;
                if (filter == null) continue;
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue;
                if (resolveInfo.activityInfo == null) continue;
                return true;
            }
        } catch (RuntimeException ignored) { }
        return false;
    }

    private void compapiCommunicate(final ComapiClient client)
    {
        if(client.getSession() != null && client.getSession().isSuccessfullyCreated()) {
            client.service().profile().getProfile(client.getSession().getProfileId(), new Callback<ComapiResult<Map<String, Object>>>() {
                @Override
                public void success(ComapiResult<Map<String, Object>> result) {
                    //Toast.makeText(MainActivity.this, result+ " :Success AccountCreation", Toast.LENGTH_LONG).show();
                    Map<String, Object> additionalMap = new HashMap<>();
                    //Add the user's email address to the profile
                    additionalMap.put("email", customerEmail);
                    client.service().profile().patchMyProfile(additionalMap, result.getETag(), new Callback<ComapiResult<Map<String, Object>>>() {
                        @Override
                        public void success(ComapiResult<Map<String, Object>> result) {
                            //Toast.makeText(MainActivity.this, result+" :Success PatchProfile", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void error(Throwable t) {
                            //Toast.makeText(MainActivity.this, t.getMessage()+" :Fail PatchProfile", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void error(Throwable t) {
                    //Toast.makeText(MainActivity.this, t.getMessage() +" :Fail AccountCreation", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            client.service().session().startSession(new Callback<Session>() {
                @Override
                public void success(Session result) {
                    //Toast.makeText(MainActivity.this, result+" :Success startSession", Toast.LENGTH_LONG).show();
                    compapiCommunicate(client);
                }

                @Override
                public void error(Throwable t) {
                    //Toast.makeText(MainActivity.this, t.getMessage() + " :Fail startSession", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void barcodeIntentListener(WebView webLoad)
    {
        if (getIntent().getBooleanExtra("from_scanner", false)) {
            String CustomrUrl = getIntent().getStringExtra("custom_url");
            webLoad.loadUrl(Constants.BASE_URL + CustomrUrl);

        }else if (getIntent().getBooleanExtra("menuItem_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/");

        } else if (getIntent().getBooleanExtra("alertDialog_barcode", false)) {

            String result = getIntent().getStringExtra("search_Text");
            String loadURL;
            try {
                loadURL = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + URLEncoder.encode(result, "UTF-8") + "&search=1/";
            } catch (Exception e) {
                loadURL = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + result + "&search=1/";
            }
            webLoad.loadUrl(loadURL);

        } else if (getIntent().getBooleanExtra("action_login_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/");

        }else if (getIntent().getBooleanExtra("action_changepickup", false)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webLoad.evaluateJavascript("(function() {  jQuery('.change-order-type').trigger('click'); })();", null);
            }

        } else if (getIntent().getBooleanExtra("nav_locations_barcode", false)) {

            webLoad.loadUrl(Constants.LOCATION_URL);

        } else if (getIntent().getBooleanExtra("login_barcode", false)) {

            if (loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "shoppinglist?");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/");

        } else if (getIntent().getBooleanExtra("nav_catalog_barcode", false)) {

            if (loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "yourcatalog?");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/");

        } else if (getIntent().getBooleanExtra("nav_help_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "help-center?");

        } else if (getIntent().getBooleanExtra("nav_employee_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL + "employee/login?");

        } else if (getIntent().getBooleanExtra("nav_login_barcode", false)) {

            if (loggedIn) {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/logout/");
            } else {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/");
            }

        } else if (getIntent().getBooleanExtra("logoView_barcode", false)) {

            webLoad.loadUrl(Constants.BASE_URL);

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
                        //e.printStackTrace();
                    } catch (Exception unused) { }
                }
            });

        }
    }
    private void callShopbybrand() {
        Constants.apiCall = Boolean.TRUE;
        if (Utils.checkInternet(MainActivity.this)) {
            ApiTask apiTask = new ApiTask(this);
            apiTask.setHttpType(ApiTask.HTTP_TYPE.GET);
            apiTask.setParams(null,Constants.BASE_URL + Constants.SHOP_BY_BRAND);
            apiTask.responseCallBack(new ApiTask.ResponseListener() {
                @Override
                public void jsonResponse(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                    } catch (JSONException e) {
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();
                        MenuItem nav_shopbylist = menu.findItem(R.id.nav_shopbybrand);
                        nav_shopbylist.setVisible(FALSE);
                        //e.printStackTrace();
                    } catch (Exception unused) { }
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

    private void callLoginWebService(String data) {

        //Constants.apiCall = FALSE;
        //if (Utils.checkInternet(MainActivity.this)) {
        try {
            JSONObject jsonObject = new JSONObject(data);

            loggedIn = jsonObject.getBoolean("loggedIn");
            cutomersupport = jsonObject.getBoolean("show_customer_support");
            showbranch = jsonObject.getBoolean("show_branch");
            show_notify = jsonObject.getBoolean("enable_abandoned_cart_notification");
         //   show_delipickup = jsonObject.getBoolean("enable_delivery_pickup_menu");

            if(jsonObject.has("email")){
                Dotdigital(jsonObject.getString("email"));
            }
            JSONObject searchURLJsonObject = jsonObject.getJSONObject("searchUrl");
            String beforeDecode = searchURLJsonObject.getString("android");

            searchURL = URLDecoder.decode(beforeDecode, "UTF-8");

            JSONObject branch_number = jsonObject.getJSONObject("branch");
            phone_number = branch_number.getString("phone");

            JSONObject address = jsonObject.getJSONObject("branch");
            String addressLine1 = address.getString("addressLine1");

            String city_name = address.getString("city");

            String state_name = address.getString("state");
            if(jsonObject.has("android_token")) {
                 tokencheck = jsonObject.getString("android_token");
            }
            NavigationView navigationView = findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();

            String release = BuildConfig.VERSION_NAME;

            String reldease =  "Version: " + release;
            MenuItem nav_ver = menu.findItem(R.id.nav_ver);
            nav_ver.setTitle(reldease);

//            MenuItem nav_contact = menu.findItem(R.id.nav_contact);
//            nav_contact.setTitle(phone_number);
            if(!cutomersupport) {
                NavigationView navigationViews = findViewById(R.id.nav_view);
                Menu menud = navigationViews.getMenu();
                MenuItem d = menud.findItem(R.id.nav_service);
                d.setVisible(FALSE);
                MenuItem dd = menud.findItem(R.id.nav_customer);
                dd.setVisible(FALSE);
            }
            if(!showbranch) {
                NavigationView navigationViews = findViewById(R.id.nav_view);
                Menu menud = navigationViews.getMenu();
                MenuItem d = menud.findItem(R.id.nav_branch);
                d.setVisible(FALSE);
                MenuItem dd = menud.findItem(R.id.nav_city);
                dd.setVisible(FALSE);
            }
            if (loggedIn) {
                if(fcmtoken.equals(tokencheck) ) {

                }else{
                    String tkn = "require(['Usesi_PushNotifier/js/fcmToken'],  function(fcmToken) { fcmToken.update('" + fcmtoken + "','android'); });";
                    // webLoad.evaluateJavascript("$('.text-uppercase').html('gg');", null);
                    webLoad.evaluateJavascript(tkn, null);
                }
               // Log.d(TAG, msg);
            }
//            if(show_delipickup) {
//                NavigationView navigationViewp = findViewById(R.id.nav_view);
//
//                Menu menup = navigationViewp.getMenu();
//                MenuItem nav_changepickup = menup.findItem(R.id.changepickup);
//                nav_changepickup.setVisible(TRUE);
//                navigationViewp.getMenu().setGroupVisible(R.id.grp11a,true);
//
//            }

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
                    //Bundle params = new Bundle();
                    String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                    params.putString("date",dateStr);
                    mFirebaseAnalytics.logEvent("navdrawer_BranchChange", params);
                    strLocationClicked = "clicked";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webLoad.evaluateJavascript("(function() { if(jQuery('.pickup-modal').css('display') != 'none'){  jQuery('.hidden-xs').trigger('click'); } jQuery(window.branchChangeModalContainer).modal('toggleModal');  jQuery('#branchchanger-button').trigger('click'); })();", null);
                    }
                    drawer.closeDrawer(GravityCompat.START);
                }
            });

//            MenuItem changepickup = menu.findItem(R.id.changepickup);
//            final DrawerLayout drawerq = findViewById(R.id.drawer_layout);
//            changepickup.getActionView().findViewById(R.id.changepickupdelivery).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //Bundle params = new Bundle();
//
//                    mFirebaseAnalytics.logEvent("navdrawer_Changepickup", params);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        webLoad.evaluateJavascript("(function() {  jQuery('#change-order-type-mobile').trigger('click'); })();", null);
//                    }
//                    drawerq.closeDrawer(GravityCompat.START);
//                }
//            });

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
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", profileName);
                editor.commit();
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
                MenuItem nav_employee = menu.findItem(R.id.nav_employee);
                if (employLoggedIn) {
                    userName.setText(jsonObject.getString("name"));
                    nav_employee.setVisible(false);
                } else {
                    userName.setText("LOGIN / REGISTER");
                    nav_employee.setVisible(true);
                }
                nav_account.setTitle("Login");
                MenuItem nav_login = menu.findItem(R.id.nav_login);
                nav_login.setVisible(false);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == 1 && resultCode == Barcode.RESULT_OK) {
            if (data.getBooleanExtra("action_location_barcode",false)) {
                strLocationClicked = "clicked";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webLoad.evaluateJavascript("(function() { if(jQuery('.pickup-modal').css('display') != 'none'){  jQuery('.hidden-xs').trigger('click'); } jQuery(window.branchChangeModalContainer).modal('toggleModal');  jQuery('#branchchanger-button').trigger('click'); })();", null);
                }
            } else {
                String result = data.getStringExtra("result");
                webLoad.loadUrl(Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + result + "&search=1/");
            }
        } else if (requestCode == 2) {
//            if (resultCode == ActivityList.RESULT_OK) {
//                String result = data.getStringExtra("result");
//                String loadURL = Constants.BASE_URL + result.trim();
//                webLoad.loadUrl(loadURL + "/?mobileapp=1");
//            }
                if (resultCode == ActivityList.RESULT_OK) {
                    String result = data.getStringExtra("result");
                    //  String loadURL = Constants.BASE_URL + result.trim();
                    webLoad.loadUrl(result.trim());
                }


        } else if (requestCode == 3) {
            if (resultCode == ActivityShopList.RESULT_OK) {
                String result = data.getStringExtra("result");
                String loadURL = result.trim();
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
       // MenuItemCompat.setActionView(cartView, R.layout.action_item);
        cartView.setActionView(R.layout.action_item);

        cartView.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/");
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
        //final Bundle params = new Bundle();
        String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
        params.putString("date",dateStr);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            mFirebaseAnalytics.logEvent("navbar_Search", params);
            if(alertCount==0) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
                View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = mView.findViewById(R.id.userInputDialog);
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
//                                    loadURLEncode = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + URLEncoder.encode(search_Text, "UTF-8") + "&search=1/?mobileapp=1";
                                    loadURLEncode = String.format(searchURL,search_Text);
                                } catch (Exception e) {
                                    loadURLEncode = Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + search_Text + "&search=1/";
                                }
                                webLoad.loadUrl(loadURLEncode);
                                mFirebaseAnalytics.logEvent("alert_Ok", params);
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                        alertCount = 0;
                                        mFirebaseAnalytics.logEvent("alert_Cancel", params);
                                    }
                                });

                final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alert_roundedcorner));
                userInputDialogImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseAnalytics.logEvent("alert_BarcodeScanner", params);
                        alertDialogAndroid.cancel();
                        alertCount = 0;
                        Intent i = new Intent(MainActivity.this, Barcode.class);
                      //  startActivityForResult(i, 1);
                        someActivityResultLauncher.launch(i);
                    }
                });
                alertDialogAndroid.show();
                alertCount++;
            }
        }

        if (id == R.id.action_cart) {
            mFirebaseAnalytics.logEvent("navbar_Cart", params);
            webLoad.loadUrl(Constants.BASE_URL + "checkout/cart/");
        } else if (id == R.id.action_location) {
            mFirebaseAnalytics.logEvent("navbar_Branch", params);
            strLocationClicked = "clicked";
            webLoad.evaluateJavascript("(function() { if(jQuery('.pickup-modal').css('display') != 'none'){  jQuery('.hidden-xs').trigger('click'); } jQuery(window.branchChangeModalContainer).modal('toggleModal');  jQuery('#branchchanger-button').trigger('click'); })();", null);
        } else if (id == R.id.action_login) {
            mFirebaseAnalytics.logEvent("navbar_Login", params);
            webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/");
        } else if (id == R.id.action_feedback) {
            mFirebaseAnalytics.logEvent("navbar_Feedback", params);
            webLoad.evaluateJavascript("jQuery(document).ready(function(){jQuery('#report-bug-link').click();})", null);
        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if ((result.getResultCode() == 2) || (result.getResultCode() == 3)){
                        Intent data = result.getData();
                        String resultd = data.getStringExtra("result");
                        //  String loadURL = Constants.BASE_URL + result.trim();
                        webLoad.loadUrl(resultd.trim());
                    }
                    else if(result.getResultCode() == 1) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("action_location_barcode", false)) {
                            strLocationClicked = "clicked";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                webLoad.evaluateJavascript("(function() { if(jQuery('.pickup-modal').css('display') != 'none'){  jQuery('.hidden-xs').trigger('click'); } jQuery(window.branchChangeModalContainer).modal('toggleModal');  jQuery('#branchchanger-button').trigger('click'); })();", null);
                            }
                        } else {
                            String resultk = data.getStringExtra("result");
                            webLoad.loadUrl(Constants.BASE_URL + "hawksearch/keyword/index/?keyword=" + resultk + "&search=1/");
                        }
                    }

                }
            });
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Bundle params = new Bundle();
        String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
        params.putString("date",dateStr);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_branch) {
            // Handle the camera action
        } else if (id == R.id.nav_category) {
            mFirebaseAnalytics.logEvent("navdrawer_SHOPBYCATEGORY", params);
            Intent i = new Intent(this, ActivityList.class);
            i.putExtra("mylist",menulist);
            i.putExtra("mylist1",menulist1);
            i.putExtra("mylist2",menulist2);
            i.putExtra("mylist3",menulist3);
           // startActivityForResult(i, 2);
            someActivityResultLauncher.launch(i);
        } else if (id == R.id.nav_shopbylist) {
            mFirebaseAnalytics.logEvent("navdrawer_SHOPBYLIST", params);
            Intent i = new Intent(this, ActivityShopList.class);
            someActivityResultLauncher.launch(i);
        }else if (id == R.id.nav_shopbybrand) {
            mFirebaseAnalytics.logEvent("navdrawer_SHOPBRAND", params);
            Intent i = new Intent(this, ActivityShopBrand.class);
            someActivityResultLauncher.launch(i);
        }
        else if (id == R.id.nav_locations) {
            mFirebaseAnalytics.logEvent("navdrawer_OURLOCATIONS", params);
            webLoad.loadUrl(Constants.LOCATION_URL);
        }
        else if (id == R.id.nav_list) {
            mFirebaseAnalytics.logEvent("navdrawer_YOURLIST", params);
            if (employLoggedIn || loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "wishlists");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/");
        } else if (id == R.id.nav_catalog) {
            mFirebaseAnalytics.logEvent("navdrawer_YOURCATALOG", params);
            if (employLoggedIn || loggedIn)
                webLoad.loadUrl(Constants.BASE_URL + "yourcatalog");
            else
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/");
        } else if (id == R.id.nav_barcode) {
            mFirebaseAnalytics.logEvent("navdrawer_SCANBARCODE", params);
            Intent i = new Intent(this, Barcode.class);
          //  startActivityForResult(i, 1);
            someActivityResultLauncher.launch(i);
        } else if (id == R.id.nav_photo) {
            mFirebaseAnalytics.logEvent("navdrawer_SUBMITAPHOTO", params);
            startActivity(new Intent(this, SubmitPhoto.class));
        } else if (id == R.id.nav_help) {
            mFirebaseAnalytics.logEvent("navdrawer_HELPCENTER", params);
            webLoad.loadUrl(Constants.BASE_URL + "help-center?");
        } else if (id == R.id.nav_employee) {
            mFirebaseAnalytics.logEvent("navdrawer_EMPLOYEELOGIN", params);
            webLoad.loadUrl(Constants.BASE_URL + "employee/login?");
        }
        //else if (id == R.id.nav_contact) {
//            mFirebaseAnalytics.logEvent("navdrawer_BRANCHNUMBER", params);
//            String number = phone_number;
//            if (((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
//                    != TelephonyManager.PHONE_TYPE_NONE)
//            {
//                if (checkPermission(Manifest.permission.CALL_PHONE)) {
//                    Intent intent = new Intent(Intent.ACTION_CALL);
//                    intent.setData(Uri.parse("tel:" + number));
//                    startActivity(intent);
//                } else {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
//                }
//            } else {
//                Toast.makeText(this, "Phone call not available", Toast.LENGTH_SHORT).show();
//            }
//        }
        else if (id == R.id.nav_customer) {
            mFirebaseAnalytics.logEvent("navdrawer_CUSTOMERSERVICE", params);
            String number = Constants.SERVICE_NUMBER;
            if (((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
                    != TelephonyManager.PHONE_TYPE_NONE)
            {
                if (checkPermission(Manifest.permission.CALL_PHONE)) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + number));
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
                }
            } else {
                Toast.makeText(this, "Phone call not available", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_login) {
            mFirebaseAnalytics.logEvent("navdrawer_LOGOUT", params);
            if (loggedIn) {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/logout/");
            } else {
                webLoad.loadUrl(Constants.BASE_URL + "customer/account/login/");
            }
        }

        if (id == R.id.nav_service || id == R.id.nav_branch || id == R.id.nav_city || id == R.id.nav_customer) {
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults.length <= 0 || (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {

                }
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGeoLocationCallback != null) {
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

        } else if (webLoad.getUrl().equals(Constants.BASE_URL + "checkout/cart/")) {
            swipe.setEnabled(false);
            swipe.setRefreshing(false);

        } else {
            swipe.setRefreshing(true);
            webLoad.reload();
        }
    }

    public void postMessage(String message) {
        String hash = "";
        messageForEmployee = message;
        try {
            final JSONObject object = new JSONObject(message);

            if (!hash.equals(object.getString("hashKey"))) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callLoginWebService(object.getString("data"));
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                hash = object.getString("hashKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postClickMessage(String message) {
        try {
            final JSONObject object = new JSONObject(message);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if ("ShopByCategory".equals(object.getString("Event"))){
                            Intent i = new Intent(MainActivity.this, ActivityList.class);
                            i.putExtra("mylist",menulist);
                            i.putExtra("mylist1",menulist1);
                            i.putExtra("mylist2",menulist2);
                            i.putExtra("mylist3",menulist3);
                            someActivityResultLauncher.launch(i);
                          //  startActivityForResult(i, 2);
                        }else if ("ShopByList".equals(object.getString("Event")))
                        {
                            Intent i = new Intent(MainActivity.this, ActivityShopList.class);
                            someActivityResultLauncher.launch(i);
                          //  startActivityForResult(i, 3);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error! Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void postMessage(String message) {
            MainActivity.this.postMessage(message);
        }
    }

    public class WebAppClickInterface {
        @JavascriptInterface
        public void postMessage(String message) {
            MainActivity.this.postClickMessage(message);
        }
    }
    public void  callshopbycategory() {
        menulist.clear();
        menulist1.clear();
        menulist2.clear();
        menulist3.clear();
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
                                menulist.add(new ActivityList.mainaray(l1, v1, "p1", id1, chy));
                              //  menulist.add(new ActivityList.mainaray(l1, v1, "p1", id1, chy));

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
                                    menulist1.add(new ActivityList.mainaray(l2, v2, p2, id2, chy1));


                                    for(int iii = 0; iii < lev2.length(); iii++) {
                                        JSONObject le3 = lev2.getJSONObject(iii);
                                        String l3 = le3.getString("label");
                                        String v3 = le3.getString("value");
                                        String p3 = le3.getString("parent_id");
                                        String id3 = le3.getString("id");
                                        String chy2 = "";
                                         JSONArray lev3 = new JSONArray();
                                        String idddd1 = le3.optString("child");
                                        if(idddd1 != ""){
                                            chy2 = "true";
                                             lev3 = le3.getJSONArray("child");
                                        }else{
                                            chy2 = "false";
                                        }
                                        menulist2.add(new ActivityList.mainaray(l3, v3, p3, id3, chy2));
                                        for(int iiii = 0; iiii < lev3.length(); iiii++) {
                                            JSONObject le4 = lev3.getJSONObject(iiii);
                                            String l4 = le4.getString("label");
                                            String v4 = le4.getString("value");
                                            String p4 = le4.getString("parent_id");
                                            String id4 = le4.getString("id");
                                            String chy3 = "";
                                            //  JSONArray lev3 = new JSONArray();
                                            String idddd2 = le4.optString("child");
                                            if(idddd2 != ""){
                                                chy3 = "true";
                                                //   lev3 = le3.getJSONArray("child");
                                            }else{
                                                chy3 = "false";
                                            }
                                            menulist3.add(new ActivityList.mainaray(l4, v4, p4, id4, chy3));
                                        }
                                    }

                                }
                            }


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
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 30000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        req.add(requetqi);

    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            String currentVersion;
            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

                // Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
                if (onlineVersion != null && !onlineVersion.isEmpty()) {
                    if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                       // String versionName = getApplicationContext().nonLocalizedLabel.toString();
                        final String appPackageNameq = getString(R.string.flavored_app_name);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Update " + appPackageNameq + "?").setMessage(appPackageNameq + " recommends that you update to the latest version.").setPositiveButton("UPDATE", dialogClickListener)
                                .setNegativeButton("LATER", dialogClickListener);
                        AlertDialog alert11 = builder.create();
                        alert11.show();

                        Button buttonbackground = alert11.getButton(DialogInterface.BUTTON_NEGATIVE);
                        buttonbackground.setTextColor(Color.BLACK);
                        Button buttonbackground1 = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
                        buttonbackground1.setTextColor(Color.BLACK);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}


