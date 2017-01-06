package br.unb.unbsolidaria.communication;

import java.util.List;

import br.unb.unbsolidaria.entities.News;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by eduar on 05/01/2017.
 */

public interface NewsService {
    @GET("noticias/")
    Call<List<News>> getNews();
}
