package rtpwd.app.monitoringe_pam;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ArahanAct extends AppCompatActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arahan);
        setUpProgresDialog();
        progressDialog.show();
        ImageButton back = findViewById(R.id.btn_back_arahan);
        String jab = new PrefManager(this).getJabatan().toLowerCase();
        EditText isiinstruksinya = findViewById(R.id.instruksiharian);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference refinstruksi = db.collection("instruksipimpinan").document("untukpam");
        refinstruksi.get().addOnSuccessListener(ds -> {
            String instruksinya =  Objects.requireNonNull(Objects.requireNonNull(ds.get(jab)).toString());
            isiinstruksinya.setText(instruksinya);
            progressDialog.dismiss();
        }).addOnFailureListener(e -> Toast.makeText(ArahanAct.this, "Gagal memuat instruksi pimpinan. Periksa koneksi Anda.", Toast.LENGTH_LONG).show());
        Button simpaninstruksi = findViewById(R.id.btnsimpaninstruksi);
        simpaninstruksi.setOnClickListener(v -> refinstruksi.update(jab,isiinstruksinya.getText().toString()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ArahanAct.this,"Instruksi berhasil terkirim ke seluruh petugas.",Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(ArahanAct.this,"Gagal mengirim, mohon periksa koneksi internet.",Toast.LENGTH_LONG).show();
            }
        }));
        back.setOnClickListener(v -> finish());
    }

    private void setUpProgresDialog(){
        progressDialog = new ProgressDialog(ArahanAct.this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setMessage("Sedang mengambil data...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
    }

}