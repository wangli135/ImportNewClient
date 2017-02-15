package importnew.importnewclient.parser;

import java.util.List;

import importnew.importnewclient.bean.Article;

/**
 * 文章列表解析器接口
 * 根据html内容解析得到Article列表
 * Created by Xingfeng on 2017-02-14.
 */
public interface ArticlesParser {

   List<Article> parser(String html);

}
