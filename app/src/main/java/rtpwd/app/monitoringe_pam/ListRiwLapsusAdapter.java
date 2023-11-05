package rtpwd.app.monitoringe_pam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ListRiwLapsusAdapter extends FirestoreRecyclerAdapter<LapsusModel, ListRiwLapsusAdapter.ListLapsusHolder> {
    private final Context context;
    private final RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ListRiwLapsusAdapter(@NonNull FirestoreRecyclerOptions<LapsusModel> options, Context context) {
        super(options);
        this.context = context;
        recyclerView = ((Activity) context).findViewById(R.id.rvlaporan);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ListLapsusHolder holder, int position, @NonNull LapsusModel model) {
        holder.jamlaporan.setText("Pukul        : "+formatJam(model.getTglLaporan()));
        holder.namapelapor.setText("Pelapor : "+model.getNamaPelapor());
        holder.tanggallaporan.setText("Tanggal   : "+formatTanggal(model.getTglLaporan()));
        holder.isilaporan.setText(model.getIsiLaporan());
    }

    @NonNull
    @Override
    public ListLapsusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_riwlapsusitem,
                parent, false);
        return new ListLapsusHolder(v);
    }

    class ListLapsusHolder extends RecyclerView.ViewHolder {
        TextView jamlaporan,namapelapor,tanggallaporan,isilaporan;
        Button detail;

        public ListLapsusHolder(View itemView) {
            super(itemView);
            jamlaporan = itemView.findViewById(R.id.jamlaporan);
            namapelapor = itemView.findViewById(R.id.namapelapor);
            tanggallaporan = itemView.findViewById(R.id.tanggallaporan);
            isilaporan = itemView.findViewById(R.id.isilaporan);
            detail = itemView.findViewById(R.id.lihatdetail);
            detail.setOnClickListener(v -> {
                DocumentReference doclapsus = db.collection("lapsus").document(getSnapshots().getSnapshot(getAdapterPosition()).getReference().getId());
                doclapsus.get().addOnSuccessListener(ds -> {
                    LapsusModel lapsusdata =  ds.toObject(LapsusModel.class);
                    Intent intent = new Intent(context, DetailLapsusAct.class);
                    GeoPoint geoloc = Objects.requireNonNull(lapsusdata).getGeo();
                    intent.putExtra("docId", Objects.requireNonNull(lapsusdata).getDocumentId());
                    intent.putExtra("senderid", Objects.requireNonNull(lapsusdata).getNipPelapor());
                    intent.putExtra("namapelapor", Objects.requireNonNull(lapsusdata).getNamaPelapor());
                    intent.putExtra("isilaporan", Objects.requireNonNull(lapsusdata).getIsiLaporan());
                    intent.putExtra("senderlocation_lat", String.valueOf(geoloc.getLatitude()));
                    intent.putExtra("senderlocation_longi", String.valueOf(geoloc.getLongitude()));
                    intent.putExtra("instruksipim", Objects.requireNonNull(lapsusdata).getInstruksiPim());
                    intent.putExtra("tgllaporan", formatTanggal(Objects.requireNonNull(lapsusdata).getTglLaporan()));
                    intent.putExtra("foto", Objects.requireNonNull(lapsusdata).getFoto());
                    intent.putExtra("fotopelapor", Objects.requireNonNull(lapsusdata).getFotoPelapor());
                    context.startActivity(intent);
                }).addOnFailureListener(e -> Toast.makeText(context, "Gagal mengambil data. Mohon periksa koneksi.", Toast.LENGTH_LONG).show());
            });
        }
    }

    private String formatJam(Date tanggal){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTanggal = new SimpleDateFormat("HH:mm");
        return formatTanggal.format(tanggal);
    }
    private String formatTanggal(Date tanggal){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTanggal = new SimpleDateFormat("EEEE, dd MMMM yyy. HH:mm");
        return formatTanggal.format(tanggal);
    }
}
