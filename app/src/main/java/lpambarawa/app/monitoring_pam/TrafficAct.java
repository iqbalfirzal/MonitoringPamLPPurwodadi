package lpambarawa.app.monitoring_pam;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class TrafficAct extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference dbRef = db.collection("parkir");
    private BlurView blurbgform;
    private ListTrafficAdapter adapter;
    private RecyclerView recyclerView;
    private TextView datatanggal;
    private Date ldate,gdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        blurbgform = findViewById(R.id.listparkir);
        datatanggal = findViewById(R.id.tanggaldata);
        datatanggal.setText(formatTanggal(new Date()));
        recyclerView = findViewById(R.id.rvparkir);
        Button pilihtanggal = findViewById(R.id.btn_pilihtanggal);
        ImageButton backlisttraffic = findViewById(R.id.btn_back_listparkir);
        pilihtanggal.setOnClickListener(v -> selectDate());
        backlisttraffic.setOnClickListener(v -> finish());
        setUpRecyclerView(null);
        blurinForm();
    }

    private void blurinForm(){
        float radius = 20f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        blurbgform.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }

    private void selectDate(){
        adapter.stopListening();
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                     String datepicked = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                     datatanggal.setText(formatTanggalFromString(datepicked));
                     setUpRecyclerView(datepicked);
                     adapter.startListening();
                }, mYear, mMonth, mDay);
        datePickerDialog.setOnCancelListener(dialogInterface -> {
            datatanggal.setText(formatTanggal(new Date()));
            setUpRecyclerView(null);
            adapter.startListening();
        });
        datePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setUpRecyclerView(String datepicked) {
        if(datepicked==null){
            Date today = new Date();
            SimpleDateFormat todayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateFormated = todayDateFormat.format(today);
            String lessDate = dateFormated+" 23:59:59";
            String greatDate = dateFormated+" 00:00:01";
            eksekusiDong(lessDate,greatDate);
        }else{
            String lessDate = datepicked+" 23:59:59";
            String greatDate = datepicked+" 00:00:01";
            eksekusiDong(lessDate,greatDate);
        }
    }

    @SuppressLint("SetTextI18n")
    private void eksekusiDong(String less, String great) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        try{
            ldate = dateFormat.parse(less);
            gdate = dateFormat.parse(great);
        } catch (ParseException e){
            e.printStackTrace();
        }assert ldate != null;assert gdate != null;
        Query query = dbRef.orderBy("masukjam")
                .whereGreaterThan("masukjam", new Timestamp(gdate))
                .whereLessThan("masukjam", new Timestamp(ldate));
        FirestoreRecyclerOptions<TrafficModel> options = new FirestoreRecyclerOptions.Builder<TrafficModel>().setQuery(query, TrafficModel.class).build();
        adapter = new ListTrafficAdapter(options,this );
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private String formatTanggal(Date tanggal){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTanggal = new SimpleDateFormat("EEEE, dd MMMM yyy");
        return formatTanggal.format(tanggal);
    }

    private String formatTanggalFromString(String date){
        Date dateformated=null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try{
            dateformated = dateFormat.parse(date);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return formatTanggal(dateformated);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}