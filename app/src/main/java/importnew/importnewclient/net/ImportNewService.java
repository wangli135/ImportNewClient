package importnew.importnewclient.net;

import java.util.List;

import importnew.importnewclient.bean.ArticleBlock;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * 首页
 * Created by Xingfeng on 2016/4/30.
 */
public interface ImportNewService {

    @GET
    Call<List<ArticleBlock>> listArticleBlocks();

}
