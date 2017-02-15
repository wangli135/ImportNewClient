package importnew.importnewclient.view;

import java.util.List;

import importnew.importnewclient.bean.ArticleBlock;

/**
 * 主页Fragment对应的View接口
 * Created by Xingfeng on 2017-02-14.
 */
public interface IHomePageView extends IBaseView {

    /**
     * 设置ListView的Adapter
     *
     * @param articleBlocks
     */
    void setAdapter(List<ArticleBlock> articleBlocks);

}
