package rtpwd.app.monitoringe_pam;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class KontrolAct extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String collections, datepicked, shifts="Shift Pagi";
    private BlurView blurbgform;
    private Spinner pilihlaporan, pilihshift;
    private ListKontrolAdapter adapter;
    private RecyclerView recyclerView;
    private TextView datatanggal,datajumlah;
    private Date ldate,gdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontrol);
        if (getIntent().getStringExtra("pilihkontrolnya").equals("kontrolpengamanan")){
            collections = "kontrolpengamanan";
            jalaninAjaDulu("pengamanan");
        }else{
           collections = "kontrolpengamanan";
           jalaninAjaDulu("pengamanan");
        }
    }

    private void jalaninAjaDulu(String selectedspincoll){
        blurbgform = findViewById(R.id.listkontrol);
        datatanggal = findViewById(R.id.tanggaldata);
        datatanggal.setText(formatTanggal(new Date()));
        datajumlah = findViewById(R.id.jumlahdata);
        pilihlaporan = findViewById(R.id.pilihlaporan_opt);
        pilihshift = findViewById(R.id.pilihshift_opt);
        recyclerView = findViewById(R.id.rvkontrol);
        Button pilihtanggal = findViewById(R.id.btn_pilihtanggal);
        ImageButton backlistkontrol = findViewById(R.id.btn_back_listkontrol);
        pilihtanggal.setOnClickListener(v -> selectDate());
        backlistkontrol.setOnClickListener(v -> finish());
        blurinForm();
        setUpRecyclerView(collections, shifts,null);
        setUpSpinnerCollection(selectedspincoll);
        setUpSpinnerShift();
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

    private void setUpSpinnerCollection(String selectedcoll){
        List<String> list = new ArrayList<>();
        list.add("pengamanan");list.add("pengamanan");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihlaporan.setAdapter(dataAdapter);
        int spinnerPosition = dataAdapter.getPosition(selectedcoll);
        pilihlaporan.setSelection(spinnerPosition);
        pilihlaporan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItem().toString().equals("pengamanan")){
                    adapter.stopListening();
                    collections = "kontrolpengamanan";
                    setUpRecyclerView(collections, shifts, datepicked);
                    adapter.startListening();
                }else{
                    adapter.stopListening();
                    collections = "kontrolpengamanan";
                    setUpRecyclerView(collections, shifts, datepicked);
                    adapter.startListening();
                }

            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setUpSpinnerShift(){
        List<String> list = new ArrayList<>();
        list.add("Shift Pagi");list.add("Shift Siang");list.add("Shift Malam");list.add("24 Jam");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihshift.setAdapter(dataAdapter);
        int spinnerPosition = dataAdapter.getPosition("Shift Pagi");
        pilihshift.setSelection(spinnerPosition);
        pilihshift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItem().toString().equals("Shift Pagi")){
                    adapter.stopListening();
                    shifts = "Shift Pagi";
                    setUpRecyclerView(collections, "Shift Pagi", datepicked);
                    adapter.startListening();
                }else if(adapterView.getSelectedItem().toString().equals("Shift Siang")){
                    adapter.stopListening();
                    shifts = "Shift Siang";
                    setUpRecyclerView(collections, "Shift Siang", datepicked);
                    adapter.startListening();
                }else if(adapterView.getSelectedItem().toString().equals("Shift Malam")){
                    adapter.stopListening();
                    shifts = "Shift Malam";
                    setUpRecyclerView(collections, "Shift Malam", datepicked);
                    adapter.startListening();
                }else {
                    adapter.stopListening();
                    shifts = "24 Jam";
                    setUpRecyclerView(collections, "24 Jam", datepicked);
                    adapter.startListening();
                }

            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void selectDate(){
        adapter.stopListening();
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    datepicked = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    datatanggal.setText(formatTanggalFromString(datepicked));
                    setUpRecyclerView(collections, shifts,datepicked);
                    adapter.startListening();
                }, mYear, mMonth, mDay);
        datePickerDialog.setOnCancelListener(dialogInterface -> {
            datatanggal.setText(formatTanggal(new Date()));
            setUpRecyclerView(collections, shifts,null);
            adapter.startListening();
        });
        datePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setUpRecyclerView(String pilihlaporan, String pilihshift, String datepicked) {
        if(datepicked==null){
            Date today = new Date();
            SimpleDateFormat todayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateFormated = todayDateFormat.format(today);
            persiapanEksekusiDong(pilihlaporan, pilihshift, dateFormated);
        }else{
            persiapanEksekusiDong(pilihlaporan, pilihshift, datepicked);
        }
    }

    private void persiapanEksekusiDong(String pilihlaporan, String pilihshift, String thedate){
        switch (pilihshift) {
            case "Shift Pagi":
                eksekusiDong(pilihlaporan, thedate + " 13:00:00", thedate + " 07:00:01");
                break;
            case "Shift Siang":
                eksekusiDong(pilihlaporan, thedate + " 19:00:00", thedate + " 13:00:01");
                break;
            case "Shift Malam":
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                try{
                    ldate = dateFormat.parse(thedate);
                } catch (ParseException e){
                    e.printStackTrace();
                }
                eksekusiDong(pilihlaporan, getTomorrow(ldate) + " 07:00:00", thedate + " 19:00:01");
                break;
            default:
                eksekusiDong(pilihlaporan, thedate + " 23:59:59", thedate + " 00:00:01");
                break;
        }

    }

    @SuppressLint("SimpleDateFormat")
    private static String getTomorrow(Date datenya) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(datenya);
        cal.add(Calendar.DATE, 1);
        return new SimpleDateFormat("dd/MM/yyy").format(cal.getTime());
    }

    @SuppressLint("SetTextI18n")
    private void eksekusiDong(String pilih, String less, String great) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        try{
            ldate = dateFormat.parse(less);
            gdate = dateFormat.parse(great);
        } catch (ParseException e){
            e.printStackTrace();
        }assert ldate != null;assert gdate != null;
        Query query = db.collection(pilih).orderBy("jamkontrol")
                .whereGreaterThan("jamkontrol", new Timestamp(gdate))
                .whereLessThan("jamkontrol", new Timestamp(ldate));
        FirestoreRecyclerOptions<KontrolModel> options = new FirestoreRecyclerOptions.Builder<KontrolModel>().setQuery(query, KontrolModel.class).build();
        adapter = new ListKontrolAdapter(options,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                datajumlah.setText(String.valueOf(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount()));
            }});
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