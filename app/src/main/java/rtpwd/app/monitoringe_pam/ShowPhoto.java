package rtpwd.app.monitoringe_pam;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class ShowPhoto extends AppCompatActivity {
    private BlurView blurlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String linkfoto = bundle.getString("urlfoto");
        ImageView foto = findViewById(R.id.pptoshow);
        blurlay = findViewById(R.id.photoshowlay);
        FloatingActionButton fabClose = findViewById(R.id.close_fab);
        exeBlur();
        Glide.with(this).setDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.ic_photo).error(R.drawable.ic_photo)).load(linkfoto).into(foto);
        fabClose.setOnClickListener(v -> finish());
    }

    private void exeBlur(){
        float radius = 20f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        blurlay.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }


}