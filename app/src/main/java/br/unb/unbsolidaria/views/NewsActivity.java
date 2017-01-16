package br.unb.unbsolidaria.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.entities.News;
import br.unb.unbsolidaria.entities.Voluntary;

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

        TextView description = (TextView) findViewById(R.id.tv_new_description);
        TextView start = (TextView) findViewById(R.id.tv_new_start);


        String dateText = mList.get(id).getDataCadastro();

        Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})(.*)");
        Matcher matcher = pattern.matcher(dateText);
        if (matcher.find()){
            dateText = matcher.group(3) + "-" + matcher.group(2) + "-" + matcher.group(1);
        }

        description.setText( getString(R.string.ov_description, mList.get(id).getTexto()) );

        start.setText( "Data Publicada: " + dateText );

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
