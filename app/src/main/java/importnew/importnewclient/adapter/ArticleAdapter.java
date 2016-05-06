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

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.utils.BitmapUtil;
import importnew.importnewclient.utils.MyDiskLruCache;
import importnew.importnewclient.utils.MyLruCache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Xingfeng on 2016/5/3.
 */
public class ArticleAdapter extends BaseAdapter {

    private List<Article> articles;
    private LayoutInflater mInflater;
    private MyLruCache myLruCache;
    private MyDiskLruCache myDiskLruCache;

    private Set<BitmapWorkerTask> tasks;

    public ArticleAdapter(Context context,List<Article> articles){
        this.articles=articles;
        mInflater=LayoutInflater.from(context);
        myLruCache=MyLruCache.newInstance();
        myDiskLruCache=new MyDiskLruCache(context,"thumb");
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

        viewHolder.img.setImageResource(R.drawable.img_empty);
        loadBitmaps(article.getImgUrl(),viewHolder.img);
        return convertView;
    }

    private void loadBitmaps(String url, final ImageView imageView){

        //Step 1：从内存中检索
        Bitmap bitmap=myLruCache.getBitmapFromCache(url);
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
        if(myDiskLruCache!=null){
            myDiskLruCache.flushCache();
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

            FileDescriptor fileDescriptor=null;
            FileInputStream fileInputStream=null;

            DiskLruCache.Snapshot snapshot=null;

            try{

                //硬盘缓存中获取
                snapshot=myDiskLruCache.getCache(imageUrl);
                if(snapshot==null){
                    //网络下载
                    if(downloadBitmap(imageUrl)){
                        snapshot=myDiskLruCache.getCache(imageUrl);
                    }
                }

                if(snapshot!=null){
                    fileInputStream=(FileInputStream)snapshot.getInputStream(0);
                    fileDescriptor=fileInputStream.getFD();
                }

                //将缓存数据解析成Bitmap
                Bitmap bitmap = null;
                if (fileDescriptor != null) {
                    bitmap = BitmapUtil.decodeSampledBitmapFromFileDescriptor(fileDescriptor,imageView.getWidth(),imageView.getHeight());
                }

                if(bitmap!=null){
                    myLruCache.addBitmapToCache(imageUrl,bitmap);
                }

                return bitmap;

            }catch (Exception e){

            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null&&imageView!=null){
                imageView.setImageBitmap(bitmap);
            }
            tasks.remove(this);
        }

        /**
         * 网络下载，并将下载内容写进硬盘
         * @param imageUrl
         */
        private boolean downloadBitmap(String imageUrl){

            try {
                OkHttpClient client=new OkHttpClient.Builder().build();
                Request request=new Request.Builder().url(imageUrl).build();
                Response response=client.newCall(request).execute();
                InputStream inputStream=response.body().byteStream();
                myDiskLruCache.putCache(imageUrl,inputStream);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
