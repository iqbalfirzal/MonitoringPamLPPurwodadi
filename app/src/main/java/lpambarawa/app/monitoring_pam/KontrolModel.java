package lpambarawa.app.monitoring_pam;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class KontrolModel {
    @DocumentId
    private String documentId;
    private String foto;
    private GeoPoint geo;
    private Date jamkontrol;
    private String keterangan;
    private String petugas;
    private String poskontrol;
    private String regu;


    public KontrolModel() {
    }

    public KontrolModel(String documentId, String foto, GeoPoint geo, Date jamkontrol, String keterangan, String petugas, String poskontrol, String regu) {
        this.documentId = documentId;
        this.foto = foto;
        this.geo = geo;
        this.jamkontrol = jamkontrol;
        this.keterangan = keterangan;
        this.petugas = petugas;
        this.poskontrol = poskontrol;
        this.regu = regu;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getFoto() {
        return foto;
    }

    public GeoPoint getGeo() {
        return geo;
    }

    public Date getJamKontrol() {
        return jamkontrol;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getPetugas() {
        return petugas;
    }

    public String getPosKontrol() {
        return poskontrol;
    }

    public String getRegu() {
        return regu;
    }
}
