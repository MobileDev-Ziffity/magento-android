package in.yale.mobile;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static in.yale.mobile.ApiTask.HTTP_TYPE.GET;
import static in.yale.mobile.R.string.logOut;
import static java.lang.Boolean.FALSE;


public class Barcode extends AppCompatActivity implements ZXingScannerView.ResultHandler, NavigationView.OnNavigationItemSelectedListener {

    private ZXingScannerView mScannerView;
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    private String phone_number;
    private String count;
    private boolean loggedIn,employLoggedIn,cutomersupport,showbranch;
    private TextView userName;
    private int alertCount = 0;
    private TextView scanImageAction;
    private Switch switchAuto;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Bundle params;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_barcode);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        params = new Bundle();
        RelativeLayout relativeBarcode = findViewById(R.id.relativeBarcode);
        scanImageAction = findViewById(R.id.scanImageAction);
        switchAuto = findViewById(R.id.switchAuto);
        mScannerView = new ZXingScannerView(this);
        relativeBarcode.addView(mScannerView);
        mScannerView.setLaserEnabled(false);
        switchAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    scanImageAction.setText("Tap to Scan");
                    mScannerView.setLaserEnabled(true);
                    mScannerView.resumeCameraPreview(Barcode.this);
                    scanImageAction.setVisibility(View.GONE);
                }else {
                    mScannerView.setLaserEnabled(false);
                    mScannerView.resumeCameraPreview(null);
                    scanImageAction.setVisibility(View.VISIBLE);
                }
            }
        });
        scanImageAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scanImageAction.getText().equals("Tap to Scan")) {
                    mScannerView.setLaserEnabled(true);
                    scanImageAction.setText("Stop Scanning");
                    mScannerView.resumeCameraPreview(Barcode.this);
                } else if (scanImageAction.getText().equals("Stop Scanning")) {
                    mScannerView.setLaserEnabled(false);
                    scanImageAction.setText("Tap to Scan");
                    mScannerView.resumeCameraPreview(null);
                }
            }
        });
        showPermissionDialog();
        callLoginWebService();

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
                startActivity(new Intent(Barcode.this, MainActivity.class)
                        .putExtra("logoView_barcode", true));


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
                startActivity(new Intent(Barcode.this, MainActivity.class)
                        .putExtra("logoView_barcode", true));
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        View login = headerview.findViewById(R.id.textView);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundle params = new Bundle();
                String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                params.putString("date",dateStr);
                mFirebaseAnalytics.logEvent("navdrawer_LOGINREGISTER", params);
                startActivity(new Intent(Barcode.this, MainActivity.class)
                        .putExtra("login_barcode", true));
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.startCamera();          // Start camera on resume
        if(ScannerItemActivity.apiAddToCart){
            callLoginWebService();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
        if (!switchAuto.isChecked()) {
            mScannerView.setLaserEnabled(false);
            scanImageAction.setText("Tap to Scan");
            mScannerView.resumeCameraPreview(null);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                startActivity(new Intent(Barcode.this, MainActivity.class)
                        .putExtra("menuItem_barcode", true));
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
            if (alertCount == 0) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
                View mView = layoutInflaterAndroid.inflate(R.layout.custom_dialog, null);
                android.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.app.AlertDialog.Builder(this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                ImageView userInputDialogImageView = mView.findViewById(R.id.barcode_imageView);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                String search_Text = "";
                                search_Text = userInputDialogEditText.getText().toString();
                                alertCount = 0;
                                startActivity(new Intent(Barcode.this, MainActivity.class)
                                        .putExtra("alertDialog_barcode", true)
                                        .putExtra("search_Text", search_Text));
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

                final android.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alert_roundedcorner));
                userInputDialogImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseAnalytics.logEvent("alert_BarcodeScanner", params);
                        alertDialogAndroid.cancel();
                        alertCount = 0;
                    }
                });
                alertDialogAndroid.show();
                alertCount++;
            }
        }

        if (id == R.id.action_cart) {
            mFirebaseAnalytics.logEvent("navbar_Cart", params);
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("menuItem_barcode", true));

        } else if (id == R.id.action_location) {
            mFirebaseAnalytics.logEvent("navbar_Branch", params);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("action_location_barcode", true);
            setResult(Barcode.RESULT_OK, resultIntent);
            finish();
        } else if (id == R.id.action_login) {
            mFirebaseAnalytics.logEvent("navbar_Login", params);
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("action_login_barcode", true));
        } else if (id == R.id.action_feedback) {
            mFirebaseAnalytics.logEvent("navbar_Feedback", params);
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("action_feedback_barcode", true));
        }
        return super.onOptionsItemSelected(item);
    }

    private void callLoginWebService() {
        String getUrl = Constants.BASE_URL + "?mobileapp=1";


    //    String getUrl = Constants.BASE_URL;
        Constants.apiCall = FALSE;
        String cookies = CookieManager.getInstance().getCookie(getUrl);
        if (Utils.checkInternet(Barcode.this)) {
            ApiTask apiTask = new ApiTask(Barcode.this);
            apiTask.setHttpType(GET);
            apiTask.setCookie(cookies);

            apiTask.responseCallBack(new ApiTask.ResponseListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void jsonResponse(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        loggedIn = jsonObject.getBoolean("loggedIn");
                        cutomersupport = jsonObject.getBoolean("show_customer_support");
                        showbranch = jsonObject.getBoolean("show_branch");
                        JSONObject branch_number = jsonObject.getJSONObject("branch");
                        phone_number = branch_number.getString("phone");

                        JSONObject address = jsonObject.getJSONObject("branch");
                        String addressLine1 = address.getString("addressLine1");

                        String city_name = address.getString("city");

                        String state_name = address.getString("state");

                        NavigationView navigationView = findViewById(R.id.nav_view);
                        Menu menu = navigationView.getMenu();

//                        MenuItem nav_contact = menu.findItem(R.id.nav_contact);
//                        nav_contact.setTitle(phone_number);
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
                                startActivity(new Intent(Barcode.this, MainActivity.class)
                                        .putExtra("action_location_barcode", true));
                                drawer.closeDrawer(GravityCompat.START);
                                finish();
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
            //apiTask.setParams(null, Constants.BASE_URL + "customer/index/status");
            apiTask.setParams(null, Constants.BASE_URL + "customer/index/status?mobileapp=1");
        } else
            Toast.makeText(Barcode.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == ActivityList.RESULT_OK) {
                String result = data.getStringExtra("result");
                String loadURL = Constants.BASE_URL + result.trim();
                startActivity(new Intent(Barcode.this, MainActivity.class)
                        .putExtra("two_barcode", true)
                        .putExtra("two_url", loadURL));
            }
        } else if (requestCode == 3) {
            if (resultCode == ActivityShopList.RESULT_OK) {
                startActivity(new Intent(Barcode.this, MainActivity.class)
                        .putExtra("three_barcode", true)
                        .putExtra("three_url", data.getStringExtra("result")));
            }
        }
    }

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
            startActivityForResult(i, 2);
        } else if (id == R.id.nav_shopbylist) {
            mFirebaseAnalytics.logEvent("navdrawer_SHOPBYLIST", params);
            Intent i = new Intent(this, ActivityShopList.class);
            startActivityForResult(i, 3);
        } else if (id == R.id.nav_locations) {
            mFirebaseAnalytics.logEvent("navdrawer_OURLOCATIONS", params);
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_locations_barcode", true));

        } else if (id == R.id.nav_list) {
            mFirebaseAnalytics.logEvent("navdrawer_YOURLIST", params);
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("login_barcode", true));

        } else if (id == R.id.nav_catalog) {
            mFirebaseAnalytics.logEvent("navdrawer_YOURCATALOG", params);
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_catalog_barcode", true));

        } else if (id == R.id.nav_barcode) {
            mFirebaseAnalytics.logEvent("navdrawer_SCANBARCODE", params);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_photo) {
            mFirebaseAnalytics.logEvent("navdrawer_SUBMITAPHOTO", params);
            startActivity(new Intent(this, SubmitPhoto.class));
        } else if (id == R.id.nav_help) {
            mFirebaseAnalytics.logEvent("navdrawer_HELPCENTER", params);
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_help_barcode", true));

        } else if (id == R.id.nav_employee) {
            mFirebaseAnalytics.logEvent("navdrawer_EMPLOYEELOGIN", params);
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_employee_barcode", true));

        }
       // else if (id == R.id.nav_contact) {
         //   mFirebaseAnalytics.logEvent("navContact", params);
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
      //  }
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
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_login_barcode", true));

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
        switch (requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults.length <= 0 || (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {

                }
        }
    }


    @Override
    public void handleResult(Result result) {
        if (switchAuto.isChecked()) {
            mScannerView.resumeCameraPreview(this);
        }
        String barCodeData = "";
        if (BarcodeFormat.UPC_A == result.getBarcodeFormat()) {
            barCodeData = "00"+result.getText().toString();
        } else {
            barCodeData = result.getText().toString();
        }
        startActivity(new Intent(Barcode.this,ScannerItemActivity.class).putExtra("bar_code_text",barCodeData));
        String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
        params.putString("date",dateStr);
        mFirebaseAnalytics.logEvent("barcode_pdp", params);
    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {


        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            finish();
        }


    };

    private void showPermissionDialog() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private String removeLeadingZero(String result) {
        if (result.charAt(0) == '0') {
            result = result.substring(1);
            result = removeLeadingZero(result);
            return result;
        } return result;
    }

}