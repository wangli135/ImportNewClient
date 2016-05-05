package importnew.importnewclient.bean;

import java.io.File;
import java.util.ArrayList;

/**
 * 文章正文部分
 * Created by Xingfeng on 2016/5/5.
 */
public class ArticleBody{

    public static class Node{

        private Tag tag;
        private String text;

        public Node() {
        }

        public Node(Tag tag, String text) {
            this.tag = tag;
            this.text = text;
        }

        public Tag getTag() {
            return tag;
        }

        public void setTag(Tag tag) {
            this.tag = tag;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    private ArrayList<Node> articleBodys;

    public ArticleBody(){
        articleBodys=new ArrayList<>();
    }

    public void add(Node node){
        articleBodys.add(node);
    }

    public Iterable<Node> iterable(){
        return articleBodys;
    }

    public boolean isEmpty(){
        return articleBodys.isEmpty();
    }

    public int size(){
        return articleBodys.size();
    }

    public Node get(int index){
        return articleBodys.get(index);
    }

    @Override
    public String toString() {

        StringBuilder sb=new StringBuilder();
        for(Node node:articleBodys)
        sb.append(node.getTag()+": "+node.getText()+ File.separator);

        return sb.toString();
    }
}
