package lpambarawa.app.monitoring_pam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Welcome extends AppCompatActivity {
    private FirebaseAuth auth;
    private RadioGroup rgpiljab;
    private RadioButton rbpiljab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        auth = FirebaseAuth.getInstance();
        ImageView loading = findViewById(R.id.loading);
        LinearLayout laypiljab = findViewById(R.id.laypiljab);
        rgpiljab = findViewById(R.id.radioJab);
        Button btnsimpan = findViewById(R.id.btnsimpan);
        if (!new PrefManager(this).isUserLogedOut()) {
            masuk();
        }else{
            loading.setVisibility(View.GONE);
            laypiljab.setVisibility(View.VISIBLE);
        }
        btnsimpan.setOnClickListener(v -> {
            int selectedId = rgpiljab.getCheckedRadioButtonId();
            rbpiljab = findViewById(selectedId);
            new PrefManager(Welcome.this).saveUserDetails(rbpiljab.getText().toString());
            masuk();
        });
    }

    private void masuk(){
        boolean loggedIn = isLoggedIn();
        if (loggedIn) {
            Intent intent = new Intent(Welcome.this, MainActivity.class);
            startActivity(intent);
        }else{
            auth.signInWithEmailAndPassword("pemantau1@lpambarawa.go.id", "pemantau1")
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Welcome.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Welcome.this, "Login gagal, mohon cek koneksi", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }
}