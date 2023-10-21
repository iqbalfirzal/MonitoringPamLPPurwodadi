package lpambarawa.app.monitoring_pam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView kontrolpam,jumlahpetugas;
    private Date ldate,gdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("tessssssss12365421765");
        kontrolpam = findViewById(R.id.datakontrolpam);
        jumlahpetugas = findViewById(R.id.datapetugas);
        Button instruksiharian = findViewById(R.id.btn_instruksi);
        Button ekontrol = findViewById(R.id.btn_econtrol);
        instruksiharian.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ArahanAct.class);
            startActivity(intent);
        });
        ekontrol.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, KontrolAct.class);
            intent.putExtra("pilihkontrolnya", "kontrolpengamanan");
            startActivity(intent);
        });
        FrameLayout layclickcontrolpam = findViewById(R.id.layclickcontrolpam);
        FrameLayout layclickjumlahptgs = findViewById(R.id.layclickjumlahpetugas);
        layclickcontrolpam.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, KontrolAct.class);
            intent.putExtra("pilihkontrolnya", "kontrolpengamanan");
            startActivity(intent);
        });
        layclickjumlahptgs.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this,"Data Terbarui ✓",Toast.LENGTH_LONG).show();
        });
        countData();
    }

    private void countData() {
        Date today = new Date();
        SimpleDateFormat todayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateFormated = todayDateFormat.format(today);
        String lessDate = dateFormated+" 23:59:59";
        String greatDate = dateFormated+" 00:00:01";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        try{
            ldate = dateFormat.parse(lessDate);
            gdate = dateFormat.parse(greatDate);
        } catch (ParseException e){
            e.printStackTrace();
        }assert ldate != null;assert gdate != null;

        Query querykontrolpam = db.collection("kontrolpengamanan").orderBy("jamkontrol")
                .whereGreaterThan("jamkontrol", new Timestamp(gdate))
                .whereLessThan("jamkontrol", new Timestamp(ldate));
        querykontrolpam.get().addOnCompleteListener(task -> {
            int lappamsize=0;
            if(task.isSuccessful()){
                lappamsize = Objects.requireNonNull(task.getResult()).size();
            }else {
                Toast.makeText(MainActivity.this,"Gagal membaca data harian, mohon periksa koneksi internet.",Toast.LENGTH_LONG).show();
            }
            kontrolpam.setText(String.valueOf(lappamsize));
        });

        Query queryjumlahpetugas = db.collection("petugas");
        AggregateQuery countQuery = queryjumlahpetugas.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                jumlahpetugas.setText(String.valueOf((int) snapshot.getCount()));
            } else {
                jumlahpetugas.setText(0);
                Toast.makeText(MainActivity.this,"Gagal membaca data jumlah harian, mohon periksa koneksi internet.",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        countData();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}