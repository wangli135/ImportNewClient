package importnew.importnewclient.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.ArticleBody;
import importnew.importnewclient.bean.Tag;
import importnew.importnewclient.utils.ThridCache;

/**
 * Created by Xingfeng on 2016/5/5.
 */
public class ArticleBodyAdapter extends BaseAdapter {

    private ArticleBody articleBody;
    private LayoutInflater mInflater;

   private ThridCache mThridCache;

    private Set<BitmapWorkerTask> tasks;


    public ArticleBodyAdapter(Context context, ArticleBody articleBody) {
        this.articleBody = articleBody;
        mInflater = LayoutInflater.from(context);
       mThridCache=ThridCache.getInstance(context);
        tasks = new HashSet<>();
    }


    @Override
    public int getItemViewType(int position) {
        return articleBody.get(position).getTag().ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return Tag.values().length;
    }

    @Override
    public int getCount() {
        return articleBody.size();
    }

    @Override
    public Object getItem(int position) {
        return articleBody.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ArticleBody.Node node = articleBody.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            if (getItemViewType(position) <= Tag.H3.ordinal() || getItemViewType(position) == Tag.STRONG.ordinal()) {
                convertView = mInflater.inflate(R.layout.article_body_h, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.article_h);
                if (getItemViewType(position) == Tag.H1.ordinal())
                    viewHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                else if (getItemViewType(position) == Tag.H2.ordinal())
                    viewHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                else if (getItemViewType(position) == Tag.H3.ordinal() || getItemViewType(position) == Tag.STRONG.ordinal())
                    viewHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            } else if (getItemViewType(position) == Tag.IMG.ordinal()) {
                convertView = mInflater.inflate(R.layout.article_body_img, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.article_body_img);
            } else if (getItemViewType(position) == Tag.A.ordinal()) {
                convertView = mInflater.inflate(R.layout.article_body_p, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.article_p);
                viewHolder.textView.setTextColor(Color.BLUE);
            } else if (getItemViewType(position) == Tag.P.ordinal()) {


                convertView = mInflater.inflate(R.layout.article_body_p, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.article_p);


            } else {
                convertView = mInflater.inflate(R.layout.article_body_p, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.article_p);

            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (node.getTag() == Tag.A) {
            SpannableString spannableString = new SpannableString(node.getText());
            spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, node.getText().length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new URLSpan(node.getUrl()), 0, node.getText().length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            viewHolder.textView.setText(spannableString);
            viewHolder.textView.setMovementMethod(new LinkMovementMethod());
        } else if (node.getTag() == Tag.P) {

            SpannableString spannableString = new SpannableString(node.getText());
            int start = 0, end = 0;
            for (ArticleBody.Node childNode : node.childNodes()) {

                if (childNode.getTag() == Tag.STRONG) {
                    end = start + childNode.getText().length();
                    spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    start=end;
                } else if (childNode.getTag() == Tag.A) {
                    end= start + childNode.getText().length();
                    spannableString.setSpan(new URLSpan(childNode.getUrl()), start,end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    viewHolder.textView.setMovementMethod(new LinkMovementMethod());
                    start=end;
                }
                else {
                    start += childNode.getText().length();
                }
            }

            viewHolder.textView.setText(spannableString);
        } else if (node.getTag() == Tag.IMG) {
            viewHolder.imageView.setImageResource(R.drawable.img_empty);
            loadImgs(viewHolder.imageView, node.getUrl());
        } else
            viewHolder.textView.setText(node.getText());

        return convertView;
    }

    public void cancelAllTasks() {
        for (BitmapWorkerTask task : tasks) {
            task.cancel(true);
        }
    }

    public void flushCache() {
        if (mThridCache != null) {
            mThridCache.flushCache();
        }
    }

    private void loadImgs(ImageView imageView, String imageUrl) {

        //Step 1：从内存中检索
        Bitmap bitmap = mThridCache.getBitmapFromMemory(imageUrl);
        if (bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            //Step 2:从硬盘中获取
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            tasks.add(task);
            task.execute(imageUrl);
        }

    }

    @Override
    public boolean isEnabled(int position) {

        return articleBody.get(position).getTag() == Tag.IMG || articleBody.get(position).getTag() == Tag.A;

    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

    /**
     * 获取Bitmap，先从硬盘缓存中，再从网络
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        private String imageUrl;
        private ImageView imageView;

        public BitmapWorkerTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            imageUrl = params[0];

            Bitmap bitmap=mThridCache.getBitmapFromDiskCache(imageUrl);
            if(bitmap==null){
                bitmap=mThridCache.getBitmapFromNetwork(imageUrl);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
            tasks.remove(this);
        }

    }
}
