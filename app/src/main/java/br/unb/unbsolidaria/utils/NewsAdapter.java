package br.unb.unbsolidaria.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.entities.News;
import br.unb.unbsolidaria.entities.Voluntary;
import br.unb.unbsolidaria.views.voluntary.OpportunityAcitivity;
import br.unb.unbsolidaria.views.voluntary.ViewOpportunities;

/**
 * Created by eduar on 05/01/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private Context mContext;
    private List<News> mList;
    private LayoutInflater mLayoutInflater;
    private float scale;
    private int width;
    private int height;

    private Voluntary mLoggedUser;

    public NewsAdapter(Context c, List<News> l, Voluntary loggedUser){
        mContext = c;
        mList = l;
        mLoggedUser = loggedUser;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scale = mContext.getResources().getDisplayMetrics().density;
        width = mContext.getResources().getDisplayMetrics().widthPixels - (int) (14 * scale + 0.5f);
        height = (width / 16) * 9;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.card_opportunity, parent, false);
        NewsAdapter.MyViewHolder mvh = new NewsAdapter.MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.MyViewHolder holder, final int position) {
        holder.tvTitulo.setText(mList.get(position).getTitulo());
        holder.tvdescription.setText(mList.get(position).getDescricao());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.ivOrg.setImageResource(mList.get(position).getPhoto());
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mList.get(position).getPhoto());
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

            bitmap = ImageHelper.getRoundedCornerBitmap(mContext, bitmap, 4, width, height, false, false, true, true);
            holder.ivOrg.setImageBitmap(bitmap);
        }

        holder.btnSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, OpportunityAcitivity.class);
                intent.putExtra("id",position);
                if (mLoggedUser != null){
                    intent.putExtra(ViewOpportunities.VIEW_MESSAGE, mLoggedUser);
                }
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivOrg;
        public TextView tvTitulo;
        public TextView tvLocal;
        public TextView tvVaga;
        public TextView tvdescription;
        public Button btnSeeMore;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivOrg = (ImageView) itemView.findViewById(R.id.iv_org);
            tvTitulo = (TextView) itemView.findViewById(R.id.tv_title);
            tvLocal = (TextView) itemView.findViewById(R.id.tv_local);
            tvVaga = (TextView) itemView.findViewById(R.id.tv_vaga);
            tvdescription = (TextView) itemView.findViewById(R.id.tv_descricao);
            btnSeeMore = (Button) itemView.findViewById(R.id.btn_seemore);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
