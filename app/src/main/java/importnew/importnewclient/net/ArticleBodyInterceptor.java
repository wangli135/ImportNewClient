package importnew.importnewclient.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import importnew.importnewclient.parser.ArticleBodyParser;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

/**
 * 文章内容拦截器
 * Created by Xingfeng on 2016/5/15.
 */
public class ArticleBodyInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        Response response = chain.proceed(chain.request());
        //是文章的URL
        if (isArticleUrl(response.request().url().toString())) {
            response = response.newBuilder().body(filter(response.body())).build();
        }

        return response;
    }

    private boolean isArticleUrl(String url) {

        Pattern pattern = Pattern.compile("http.+((importnew)|(jobbole))\\.com/\\d{2,}+");
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    private ResponseBody filter(final ResponseBody responseBody) {
        return new ResponseBody() {
            @Override
            public MediaType contentType() {
                return responseBody.contentType();
            }

            @Override
            public long contentLength() {
                return -1;
            }

            @Override
            public BufferedSource source() {

                try {
                    String html = ArticleBodyParser.parserArticleBody(responseBody.string());

                    return Okio.buffer(Okio.source(new ByteArrayInputStream(html.getBytes())));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
    }
}
