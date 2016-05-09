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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.HashSet;
import java.util.Set;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.ArticleBody;
import importnew.importnewclient.pages.Nodes;
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
        mThridCache = ThridCache.getInstance(context);
        tasks = new HashSet<>();
    }


    /**
     * 一种图片，一种文字
     *
     * @param position
     * @return 0表示文字，1表示图片
     */
    @Override
    public int getItemViewType(int position) {

        Node node = articleBody.get(position);
        if (node instanceof TextNode)
            return 0;
        else if (node instanceof Element) {
            Element element = (Element) node;
            if (element.tagName().equals(Nodes.Tag.IMG))
                return 1;
            else
                return 0;
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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

        Node node = articleBody.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            if (getItemViewType(position) == 0) {
                convertView = mInflater.inflate(R.layout.article_body_text, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.article_body_text);

            } else {
                convertView = mInflater.inflate(R.layout.article_body_img, parent, false);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.article_body_img);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //恢复文本默认设置
        if(viewHolder.textView!=null){
            viewHolder.textView.setTextColor(Color.parseColor("#3C3F41"));
            viewHolder.textView.setTextSize(14);
        }


        if (node instanceof TextNode) {
            viewHolder.textView.setText(((TextNode) node).text());
        } else {
            Element element = (Element) node;
            if (element.tagName().equals(Nodes.Tag.IMG)) {
                viewHolder.imageView.setImageResource(R.drawable.emptyview);
                loadImgs(viewHolder.imageView, element.attr(Nodes.Attribute.SRC));
            } else if (element.tagName().equals(Nodes.Tag.H1)) {
                viewHolder.textView.setTextSize(17);
                viewHolder.textView.setTextColor(Color.BLACK);
                viewHolder.textView.setText(element.text());
            } else if (element.tagName().equals(Nodes.Tag.H2)) {
                viewHolder.textView.setTextSize(16);
                viewHolder.textView.setTextColor(Color.BLACK);
                viewHolder.textView.setText(element.text());

            } else if (element.tagName().equals(Nodes.Tag.H3) || element.tagName().equals(Nodes.Tag.STRONG)) {
                viewHolder.textView.setTextSize(15);
                viewHolder.textView.setTextColor(Color.BLACK);
                viewHolder.textView.setText(element.text());

            } else if (element.tagName().equals(Nodes.Tag.H4)) {
                viewHolder.textView.setTextSize(14);
                viewHolder.textView.setTextColor(Color.BLACK);
                viewHolder.textView.setText(element.text());

            } else if (element.tagName().equals(Nodes.Tag.H5)) {
                viewHolder.textView.setTextSize(13);
                viewHolder.textView.setTextColor(Color.BLACK);
                viewHolder.textView.setText(element.text());
            } else if (element.tagName().equals(Nodes.Tag.H6)) {
                viewHolder.textView.setTextSize(12);
                viewHolder.textView.setTextColor(Color.BLACK);
                viewHolder.textView.setText(element.text());
            } else if (element.tagName().equals(Nodes.Tag.A)) {
                SpannableString spannableString = new SpannableString(element.text());
                spannableString.setSpan(new URLSpan(element.attr(Nodes.Attribute.HREF)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                viewHolder.textView.setMovementMethod(new LinkMovementMethod());
                viewHolder.textView.setText(spannableString);
            } else if (element.childNodeSize() == 2) {

                SpannableString spannableString = new SpannableString(element.text());
                int start = 0, end = 0;
                for (Node nodes : element.dataNodes()) {
                    if (nodes instanceof TextNode) {
                        start += ((TextNode) nodes).text().length();
                    } else {
                        element = (Element) nodes;
                        if (element.tagName().equals(Nodes.Tag.A)) {
                            end = start + element.text().length();
                            spannableString.setSpan(new URLSpan(element.attr(Nodes.Attribute.HREF)), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                    }
                }
                viewHolder.textView.setMovementMethod(new LinkMovementMethod());
                viewHolder.textView.setText(spannableString);

            } else {
                viewHolder.textView.setText(element.text());
            }
        }


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

        return getItemViewType(position) == 1;

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

            Bitmap bitmap = mThridCache.getBitmapFromDiskCache(imageUrl);
            if (bitmap == null) {
                bitmap = mThridCache.getBitmapFromNetwork(imageUrl);
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
