package lpambarawa.app.monitoring_pam;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class TrafficModel {
    @DocumentId
    private String documentId;
    private String foto;
    private String jeniskendaraan;
    private Date keluarjam;
    private String keperluan;
    private Date masukjam;
    private String platnomor;
    private Boolean sudahkeluar;


    public TrafficModel() {
    }

    public TrafficModel(String documentId, String foto, String jeniskendaraan, Date keluarjam, String keperluan, Date masukjam, String platnomor, Boolean sudahkeluar) {
        this.documentId = documentId;
        this.foto = foto;
        this.jeniskendaraan = jeniskendaraan;
        this.keluarjam = keluarjam;
        this.keperluan = keperluan;
        this.masukjam = masukjam;
        this.platnomor = platnomor;
        this.sudahkeluar = sudahkeluar;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getFoto() {
        return foto;
    }

    public String getJenisKendaraan() {
        return jeniskendaraan;
    }

    public Date getKeluarJam() {
        return keluarjam;
    }

    public String getKeperluan() {
        return keperluan;
    }

    public Date getMasukJam() {
        return masukjam;
    }

    public String getPlatNomor() {
        return platnomor;
    }

    public Boolean getSudahKeluar() {
        return sudahkeluar;
    }
}
