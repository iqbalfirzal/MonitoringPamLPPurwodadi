package rtpwd.app.monitoringe_pam;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class RiwayatLapsusAct extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference dbRef = db.collection("lapsus");
    private BlurView blurbgform;
    private ListRiwLapsusAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_lapsus);
        blurbgform = findViewById(R.id.listriwlapsus);
        recyclerView = findViewById(R.id.rvlaporan);
        ImageButton backlistkontrol = findViewById(R.id.btn_back_listriwlapsus);
        backlistkontrol.setOnClickListener(v -> finish());
        setUpRecyclerView();
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

    @SuppressLint("SetTextI18n")
    private void setUpRecyclerView() {
        Query query = dbRef.orderBy("tgllaporan", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<LapsusModel> options = new FirestoreRecyclerOptions.Builder<LapsusModel>().setQuery(query, LapsusModel.class).build();
        adapter = new ListRiwLapsusAdapter(options,this );
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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