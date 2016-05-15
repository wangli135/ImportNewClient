package importnew.importnewclient.parser;

import java.io.IOException;

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
            Request request = new Request.Builder().url("http://www.importnew.com/18308.html").build();
            Response response = httpClient.newCall(request).execute();
            String html = response.body().string();

            if (html != null && !html.equals("")) {

                System.out.println(ArticleBodyParser.parserArticleBody(html));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
