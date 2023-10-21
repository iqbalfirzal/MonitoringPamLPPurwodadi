package lpambarawa.app.monitoring_pam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

public class ListKontrolAdapter extends FirestoreRecyclerAdapter<KontrolModel, ListKontrolAdapter.ListKontrolHolder> {
    private final Context context;

    public ListKontrolAdapter(@NonNull FirestoreRecyclerOptions<KontrolModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ListKontrolHolder holder, int position, @NonNull KontrolModel model) {
        String lati,longi;
        holder.jamkontrol.setText("Pukul\n"+formatJam(model.getJamKontrol()));
        holder.regutext.setText("Regu     : "+model.getRegu());
        holder.petugastext.setText("Petugas  : "+model.getPetugas());
        if(model.getKeterangan()!=null){
            holder.keterangantext.setText("Keterangan  : "+model.getKeterangan());
        }else {
            holder.keterangantext.setText("Keterangan  : -");
        }
        holder.poskontroltext.setText("Titik Kontrol : "+model.getPosKontrol());
        String urlfoto = model.getFoto();
        if(urlfoto.equals("")){
            holder.btnlihatfoto.setVisibility(View.GONE);
        }else {
            holder.btnlihatfoto.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.ic_photo)
                    .error(R.drawable.ic_photo)).load(urlfoto).into(holder.foto);
        }
        if(model.getGeo()!=null){
            lati = String.valueOf(model.getGeo().getLatitude());longi = String.valueOf(model.getGeo().getLongitude());
        }else{
            lati="0";longi="0";
        }
        holder.btnlihatlokasi.setOnClickListener(v -> {
            String strUri = "http://maps.google.com/maps?q=loc:" + lati + "," + longi + "(pinned)";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public ListKontrolHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_laporanitem,
                parent, false);
        return new ListKontrolHolder(v);
    }

    class ListKontrolHolder extends RecyclerView.ViewHolder {
        TextView jamkontrol,regutext,petugastext,keterangantext,poskontroltext;
        Button btnlihatfoto,btnlihatlokasi;
        ImageView foto;
        LinearLayout layfotolist;

        @SuppressLint("SetTextI18n")
        public ListKontrolHolder(View itemView) {
            super(itemView);
            jamkontrol = itemView.findViewById(R.id.jamkontrol);
            regutext = itemView.findViewById(R.id.regutext);
            petugastext = itemView.findViewById(R.id.petugastext);
            keterangantext = itemView.findViewById(R.id.keterangantext);
            poskontroltext = itemView.findViewById(R.id.poskontroltext);
            btnlihatfoto = itemView.findViewById(R.id.lihatfoto);
            btnlihatlokasi = itemView.findViewById(R.id.lihatlokasi);
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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatTanggal = new SimpleDateFormat("HH:mm");
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
