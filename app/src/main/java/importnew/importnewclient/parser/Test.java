package importnew.importnewclient.parser;

import java.io.IOException;
import java.util.List;

import importnew.importnewclient.bean.Article;
import importnew.importnewclient.net.URLManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Xingfeng on 2016/5/14.
 */
public class Test {

    public static void main(String[] args) {

        try {
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new Request.Builder().url(URLManager.BASIC + 5).build();
            Response response = httpClient.newCall(request).execute();
            String html = response.body().string();

            if (html != null && !html.equals("")) {

                List<Article> list = ArticlesParser.parserArtciles(html);
                for (Article article : list)
                    System.out.println(article);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
