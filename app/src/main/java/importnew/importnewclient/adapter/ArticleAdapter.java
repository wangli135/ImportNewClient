package importnew.importnewclient.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.utils.ThridCache;


/**
 * Created by Xingfeng on 2016/5/3.
 */
public class ArticleAdapter extends BaseAdapter {

    private List<Article> articles;
    private LayoutInflater mInflater;

    //三级缓存
    private ThridCache mThridCache;

    private Set<BitmapWorkerTask> tasks;

    public ArticleAdapter(Context context,List<Article> articles){
        this.articles=articles;
        mInflater=LayoutInflater.from(context);
        mThridCache=ThridCache.getInstance(context);
        tasks=new HashSet<>();
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.article_hoizontall_layout,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.title=(TextView)convertView.findViewById(R.id.article_title);
            viewHolder.img=(ImageView)convertView.findViewById(R.id.article_img);
            viewHolder.desc=(TextView)convertView.findViewById(R.id.article_desc);
            viewHolder.commentNum=(TextView)convertView.findViewById(R.id.article_comment_num);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        Article article=articles.get(position);
        viewHolder.title.setText(article.getTitle());
        viewHolder.desc.setText(article.getDesc());
        viewHolder.commentNum.setText(article.getCommentNum()+"条评论");

        viewHolder.img.setImageResource(R.drawable.emptyview);
        loadBitmaps(article.getImgUrl(),viewHolder.img);
        return convertView;
    }

    private void loadBitmaps(String url, final ImageView imageView){

        //Step 1：从内存中检索
        Bitmap bitmap=mThridCache.getBitmapFromMemory(url);
        if(bitmap!=null&&imageView!=null){
            imageView.setImageBitmap(bitmap);
        }else{
            //Step 2:从硬盘中获取
            BitmapWorkerTask task=new BitmapWorkerTask(imageView);
            tasks.add(task);
            task.execute(url);
        }
    }

    public void cancelAllTasks(){
        for(BitmapWorkerTask task:tasks){
            task.cancel(true);
        }
    }

    public void flushCache(){
        if(mThridCache!=null){
            mThridCache.flushCache();
        }
    }

    class ViewHolder {
        TextView title;
        ImageView img;
        TextView desc;
        TextView commentNum;
    }


    /**
     * 获取Bitmap，先从硬盘缓存中，再从网络
     */
    class BitmapWorkerTask extends AsyncTask<String,Void,Bitmap>{

        private String imageUrl;
        private ImageView imageView;

        public BitmapWorkerTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            imageUrl=params[0];

            Bitmap bitmap=mThridCache.getBitmapFromDiskCache(imageUrl);
            if(bitmap==null){
                bitmap=mThridCache.getBitmapFromNetwork(imageUrl);
            }

            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null&&imageView!=null){
                imageView.setImageBitmap(bitmap);
            }
            tasks.remove(this);
        }

    }
}
