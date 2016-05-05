package importnew.importnewclient.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.ArticleBody;
import importnew.importnewclient.bean.Tag;

/**
 * Created by Xingfeng on 2016/5/5.
 */
public class ArticleBodyAdapter extends BaseAdapter {

    private ArticleBody articleBody;
    private LayoutInflater mInflater;

    public ArticleBodyAdapter(Context context, ArticleBody articleBody) {
        this.articleBody = articleBody;
        mInflater = LayoutInflater.from(context);
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
        ViewHolder viewHolder=null;
        if(convertView==null){
            if(getItemViewType(position)<3){
                convertView=mInflater.inflate(R.layout.article_body_h,parent,false);
                viewHolder=new ViewHolder();
                viewHolder.textView=(TextView)convertView.findViewById(R.id.article_h);

                if(getItemViewType(position)==0)
                    viewHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                else if(getItemViewType(position)==1)
                    viewHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                else if(getItemViewType(position)==2)
                    viewHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            }else if(getItemViewType(position)==4||getItemViewType(position)==3){
                convertView=mInflater.inflate(R.layout.article_body_p,parent,false);
                viewHolder=new ViewHolder();
                viewHolder.textView=(TextView)convertView.findViewById(R.id.article_p);

            }
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(node.getText());
        return convertView;
    }


    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    static class ViewHolder {
        TextView textView;
    }


}
