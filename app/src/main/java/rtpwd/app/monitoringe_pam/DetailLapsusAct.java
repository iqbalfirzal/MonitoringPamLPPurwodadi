package rtpwd.app.monitoringe_pam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailLapsusAct extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RequestQueue mRequestQue;
    private final String SENDNOTIFURL = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lapsus);
        mRequestQue = Volley.newRequestQueue(this);
        CircularImageView fotopelapornya = findViewById(R.id.fotopelapor);
        TextView waktu = findViewById(R.id.waktulaporannya);
        TextView pelapor = findViewById(R.id.namapelapornya);
        TextView nippelapor = findViewById(R.id.nippelapornya);
        TextView laporannya = findViewById(R.id.laporannya);
        TextView ketfoto = findViewById(R.id.ketfoto);
        ImageView fotonya = findViewById(R.id.fotonya);
        EditText instruksinya = findViewById(R.id.instruksi);
        ImageButton back = findViewById(R.id.btn_back_detaillapsus);
        Button openmap = findViewById(R.id.btn_openmap);
        Button kirim = findViewById(R.id.kiriminstruksi);
        if (getIntent().hasExtra("docId")){
            String idDokumen = getIntent().getStringExtra("docId");
            String idpelapor = getIntent().getStringExtra("senderid");
            String namapelapor = getIntent().getStringExtra("namapelapor");
            String isilaporan = getIntent().getStringExtra("isilaporan");
            String lokasi_lat = getIntent().getStringExtra("senderlocation_lat");
            String lokasi_longi = getIntent().getStringExtra("senderlocation_longi");
            String instruksipim = getIntent().getStringExtra("instruksipim");
            String tgllaporan = getIntent().getStringExtra("tgllaporan");
            String foto = getIntent().getStringExtra("foto");
            String fotopelapor = getIntent().getStringExtra("fotopelapor");
            Glide.with(this)
                    .load(fotopelapor)
                    .placeholder(R.drawable.ic_account)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .centerCrop()
                    .into(fotopelapornya);
            if(foto.equals("")){
                ketfoto.setVisibility(View.VISIBLE);
                fotonya.setVisibility(View.GONE);
            }else{
                ketfoto.setVisibility(View.GONE);
                fotonya.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(foto)
                        .placeholder(R.drawable.ic_photo)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .centerCrop()
                        .into(fotonya);
            }
            waktu.setText(tgllaporan);nippelapor.setText(idpelapor);pelapor.setText(namapelapor);laporannya.setText(isilaporan);instruksinya.setText(instruksipim);
            openmap.setOnClickListener(v -> {
                String strUri = "http://maps.google.com/maps?q=loc:" + lokasi_lat + "," + lokasi_longi + "(pinned)";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            });
            back.setOnClickListener(v -> finish());
            fotonya.setOnClickListener(v -> showPhoto(foto));
            kirim.setOnClickListener(v -> db.collection("lapsus").document(idDokumen)
                    .update("instruksipim", "[ "+new PrefManager(this).getJabatan()+" ] "+instruksinya.getText().toString()+"\n___\n")
                    .addOnSuccessListener(aVoid -> sendNotif(idpelapor,instruksinya.getText().toString())).addOnFailureListener(e -> Toast.makeText(DetailLapsusAct.this,"Gagal mengubah data.Mohon periksa koneksi.",Toast.LENGTH_LONG).show()));
        }else {
            finish();
        }
    }

    private void sendNotif(String senderid,String pesan){
        JSONObject json = new JSONObject();
        try {
            json.put("to","/topics/"+"umjoL1srorNjDvpmeocBJ1kN7pVTb4t9zgmsPCHIs");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "Intruksi dari Pimpinan atas laporan Anda : ");
            notificationObj.put("body", "[ "+new PrefManager(this).getJabatan()+" ] "+pesan);
            notificationObj.put("click_action", "BUKA_ACTIVITY_LAPSUS");

            JSONObject extraData = new JSONObject();
            extraData.put("sendto", senderid);
            extraData.put("messagetype","balasanlapsus");

            json.put("notification",notificationObj);
            json.put("data",extraData);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SENDNOTIFURL,
                    json,
                    response -> {Toast.makeText(DetailLapsusAct.this,"Instruksi berhasil terkirim.",Toast.LENGTH_LONG).show(); finish();}, error -> Log.d("LAPSUS SEND NOTIF ERROR", "onError: "+error.networkResponse)
            ){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAA9qTy6zA:APA91bH8PJ5l5GfbXpr4XUErtv-Bbkc08ok3hotwKi1P39XCJWkCBtWAUqq5Q0gzaSguASf0LpODm77J5lz-K_MV6__DwDnQ_1Y0dgWFnS_-plT5ie8tU3rOaYcnNzbCNkMVYdGU0_dr");
                    return header;
                }
            };
            mRequestQue.add(request);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showPhoto(final String urlfoto){
            Intent intent = new Intent(this, ShowPhoto.class);
            intent.putExtra("urlfoto", urlfoto);
            this.startActivity(intent);
    }

}