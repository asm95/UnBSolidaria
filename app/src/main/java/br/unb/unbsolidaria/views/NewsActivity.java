package br.unb.unbsolidaria.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.entities.News;
import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.Voluntary;
import br.unb.unbsolidaria.persistence.DBHandler;
import br.unb.unbsolidaria.utils.ImageHelper;
import br.unb.unbsolidaria.views.voluntary.ViewOpportunities;

/**
 * Created by eduar on 05/01/2017.
 */

public class NewsActivity extends AppCompatActivity {

    private float scale;
    private int width;
    private int height;

    private Voluntary loggedVoluntary;

    private Singleton singleton = Singleton.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.noticiaToolbar_new);
        setSupportActionBar(toolbar);

        loggedVoluntary = (Voluntary)getIntent().getSerializableExtra(ViewNews.VIEW_MESSAGE);
        int id = getIntent().getIntExtra("id", 0);
        List<News> mList = singleton.getNewsList();

        DBHandler bd = DBHandler.getInstance();
        //Opportunity opportunity = bd.getOpportunity(id);

        TextView description = (TextView) findViewById(R.id.tv_new_description);
        TextView local = (TextView) findViewById(R.id.tv_new_local);
        TextView vagas = (TextView) findViewById(R.id.tv_new_vaga);
        TextView start = (TextView) findViewById(R.id.tv_new_start);


        description.setText( getString(R.string.ov_description, mList.get(id).getTexto()) );

        start.setText( getString(R.string.ov_dateStart, mList.get(id).getDataCadastro()) );

        scale = this.getResources().getDisplayMetrics().density;
        width = this.getResources().getDisplayMetrics().widthPixels - (int) (14 * scale + 0.5f);
        height = (width / 16) * 9;


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mList.get(id).getTitulo());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.noticia_share:
                Snackbar.make(findViewById(android.R.id.content), "Compartilhar Noticia", Snackbar.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                return super.onOptionsItemSelected(item);
        }
    }
}
