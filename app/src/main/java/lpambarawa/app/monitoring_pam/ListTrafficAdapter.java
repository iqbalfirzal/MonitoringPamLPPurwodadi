package lpambarawa.app.monitoring_pam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ListTrafficAdapter extends FirestoreRecyclerAdapter<TrafficModel, ListTrafficAdapter.ListTrafficHolder> {
    private final Context context;
    private final RecyclerView recyclerView;

    public ListTrafficAdapter(@NonNull FirestoreRecyclerOptions<TrafficModel> options, Context context) {
        super(options);
        this.context = context;
        recyclerView = ((Activity) context).findViewById(R.id.rvparkir);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ListTrafficHolder holder, int position, @NonNull TrafficModel model) {
        holder.nomormasuk.setText(String.valueOf(position+1));
        holder.platnomor.setText("Plat Nomor : "+model.getPlatNomor());
        holder.jeniskendaraan.setText("Kendaraan     : "+model.getJenisKendaraan());
        holder.keperluan.setText("Keperluan      : "+model.getKeperluan());
        holder.masukjam.setText("Masuk jam     : "+formatJam(model.getMasukJam()));
        if(model.getSudahKeluar()){
            holder.keluarjam.setText("Keluar jam      : "+formatJam(model.getKeluarJam()));
        }else{
            holder.keluarjam.setText("KENDARAAN MASIH TERPARKIR");
        }
        String urlfoto = model.getFoto();
        if(urlfoto.equals("")){
            holder.btnlihatfoto.setVisibility(View.GONE);
        }else {
            holder.btnlihatfoto.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.ic_photo)
                            .error(R.drawable.ic_photo)).load(urlfoto).into(holder.foto);
        }
    }

    @NonNull
    @Override
    public ListTrafficHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_kendaraanitem,
                parent, false);
        return new ListTrafficHolder(v);
    }

    class ListTrafficHolder extends RecyclerView.ViewHolder {
        TextView platnomor,jeniskendaraan,keperluan,masukjam,keluarjam,nomormasuk;
        Button btnlihatfoto;
        ImageView foto;
        LinearLayout layfotolist;

        public ListTrafficHolder(View itemView) {
            super(itemView);
            platnomor = itemView.findViewById(R.id.platnomortext);
            jeniskendaraan = itemView.findViewById(R.id.jeniskendaraantext);
            keperluan = itemView.findViewById(R.id.keperluantext);
            masukjam = itemView.findViewById(R.id.masukjamtext);
            keluarjam = itemView.findViewById(R.id.keluarjamtext);
            nomormasuk = itemView.findViewById(R.id.nomormasuk);
            btnlihatfoto = itemView.findViewById(R.id.lihatfoto);
            foto = itemView.findViewById(R.id.fotolist);
            layfotolist = itemView.findViewById(R.id.layfotolist);
            btnlihatfoto.setOnClickListener(v -> {
                if(layfotolist.getVisibility()==View.VISIBLE){
                    layfotolist.setVisibility(View.GONE);
                    btnlihatfoto.setText("LIHAT FOTO");
                }else{
                    layfotolist.setVisibility(View.VISIBLE);
                    btnlihatfoto.setText("SEMBUNYIKAN");
                }
            });
            layfotolist.setOnClickListener(v -> showPhoto(getAdapterPosition()));
        }
    }

    private String formatJam(Date tanggal){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTanggal = new SimpleDateFormat("HH:mm:ss");
        return formatTanggal.format(tanggal);
    }

    private void showPhoto(final int position){
        getSnapshots().getSnapshot(position).getReference().get().addOnSuccessListener(ds -> {
            String urlfoto = Objects.requireNonNull(ds.get("foto")).toString();
            Intent intent = new Intent(context, ShowPhoto.class);
            intent.putExtra("urlfoto", urlfoto);
            context.startActivity(intent);
        }).addOnFailureListener(e -> Toast.makeText(context, "Gagal menampilkan foto. Mohon periksa koneksi.", Toast.LENGTH_LONG).show());
    }
}
