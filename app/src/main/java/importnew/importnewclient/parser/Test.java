package importnew.importnewclient.parser;

import java.io.IOException;

import importnew.importnewclient.net.ArticleBodyInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Xingfeng on 2016/5/14.
 */
public class Test {

    public static void main(String[] args) {

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new ArticleBodyInterceptor())
                    .build();

            Request request = new Request.Builder()
                    .url("http://www.importnew.com/18308.html")
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
            response.body().close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
