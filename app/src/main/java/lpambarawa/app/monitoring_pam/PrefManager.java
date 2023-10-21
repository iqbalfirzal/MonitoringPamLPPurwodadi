package lpambarawa.app.monitoring_pam;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private final Context context;

    PrefManager(Context context) {
        this.context = context;
    }

    void saveUserDetails(String jabatan) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jabatan", jabatan);
        editor.apply();
    }

    String getJabatan() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("jabatan", "");
    }

    boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("jabatan", "").isEmpty();
    }
}
