package importnew.importnewclient.net;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import importnew.importnewclient.bean.ArticleBlock;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Xingfeng on 2016/5/1.
 */
public class ArticleBlockConverterFactory extends Converter.Factory {

    public static ArticleBlockConverterFactory create(){
        return create(new ArticleBlock());
    }

    public static ArticleBlockConverterFactory create(ArticleBlock articleBlock){
        return new ArticleBlockConverterFactory(articleBlock);
    }



    private final ArticleBlock articleBlock;

    private ArticleBlockConverterFactory(ArticleBlock articleBlock){
        if(articleBlock==null)
            throw new NullPointerException("articleBlock==null");
        this.articleBlock=articleBlock;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.responseBodyConverter(type, annotations, retrofit);
    }

}
