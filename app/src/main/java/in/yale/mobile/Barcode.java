package in.yale.mobile;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.google.zxing.Result;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static in.yale.mobile.ApiTask.HTTP_TYPE.GET;
import static in.yale.mobile.R.string.logOut;
import static java.lang.Boolean.FALSE;


public class Barcode extends AppCompatActivity implements ZXingScannerView.ResultHandler, NavigationView.OnNavigationItemSelectedListener {

    private ZXingScannerView mScannerView;
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;
    private String phone_number;
    private String count;
    private boolean loggedIn;
    private TextView userName;
    private int alertCount = 0;
    private TextView scanImageAction;
    private Switch switchAuto;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_barcode);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
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

                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                        alertCount = 0;
                                    }
                                });

                final android.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                userInputDialogImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogAndroid.cancel();
                        alertCount = 0;
                    }
                });
                alertDialogAndroid.show();
                alertCount++;
            }
        }

        if (id == R.id.action_cart) {
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("menuItem_barcode", true));

        } else if (id == R.id.action_location) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("action_location_barcode", true);
            setResult(Barcode.RESULT_OK, resultIntent);
            finish();
        } else if (id == R.id.action_login) {
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("action_login_barcode", true));
        } else if (id == R.id.action_feedback) {
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("action_feedback_barcode", true));
        }
        return super.onOptionsItemSelected(item);
    }

    private void callLoginWebService() {
        String getUrl = Constants.BASE_URL + "?mobileapp=1";
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
            apiTask.setParams(null, Constants.BASE_URL + "customer/index/status?mobileapp=1");

        } else
            Toast.makeText(Barcode.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == ActivityList.RESULT_OK) {
                String result = data.getStringExtra("result");
                String loadURL = Constants.BASE_URL + result.trim();
                startActivity(new Intent(Barcode.this, MainActivity.class)
                        .putExtra("two_barcode", true)
                        .putExtra("two_url",loadURL));
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
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_locations_barcode", true));

        } else if (id == R.id.nav_list) {
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("login_barcode", true));

        } else if (id == R.id.nav_catalog) {
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_catalog_barcode", true));

        } else if (id == R.id.nav_barcode) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_photo) {
            startActivity(new Intent(this, SubmitPhoto.class));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_help_barcode", true));

        } else if (id == R.id.nav_employee) {
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_employee_barcode", true));

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
            startActivity(new Intent(Barcode.this, MainActivity.class)
                    .putExtra("nav_login_barcode", true));

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
        }
    }


    @Override
    public void handleResult(Result result) {
        if (switchAuto.isChecked()) {
            mScannerView.resumeCameraPreview(this);
        }
        final String barCodeData = result.getText().toString();
        startActivity(new Intent(Barcode.this,ScannerItemActivity.class).putExtra("bar_code_text",barCodeData));

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