package in.yale.mobile;


import android.content.Context;
import android.net.ConnectivityManager;

class Utils {

    static boolean checkInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
