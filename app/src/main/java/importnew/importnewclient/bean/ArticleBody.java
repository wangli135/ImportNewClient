package importnew.importnewclient.bean;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 文章正文部分
 * Created by Xingfeng on 2016/5/5.
 */
public class ArticleBody implements Iterable<Node> {

    /**
     * 文章标题部分节点
     */
    private Element entryHeader;

    /**
     * 文章正文内容部分
     */
    private Element entry;

    /**
     * 文章中所有的节点链表
     */
    private ArrayList<Node> elements;

    public ArticleBody() {
        elements = new ArrayList<>();
    }

    public void addEntryHeader(Element entryHeader) {
        this.entryHeader = entryHeader;
        elements.add(entryHeader.child(0));
    }

    public void addEntry(Element entry) {
        this.entry = entry;

        //将内容节点添加到链表中
        addArticleElementToList(entry);
    }

    /**
     * 将父节点中有用的节点添加到链表中
     */
    private void addArticleElementToList(Element parent) {

        for (Node childNode : parent.childNodes()) {

            if (childNode instanceof Element) {
                Element element = (Element) childNode;

                //br、hr
                if (element.childNodeSize() == 0) {
                    elements.add(element);
                } else if (element.childNodeSize() == 1 && element.hasText()) {
                    elements.add(element);
                } else if (element.childNodeSize() == 1) {
                    addArticleElementToList(element);
                } else if (element.childNodeSize() == 2) {

                    if (element.childNode(0) instanceof TextNode || element.childNode(1) instanceof TextNode) {
                        elements.add(element);
                    } else
                        addArticleElementToList(element);

                } else {
                    addArticleElementToList(element);
                }
            } else if (childNode instanceof TextNode) {
                elements.add(childNode);
            }

        }

    }


    /**
     * 获得文章标题
     *
     * @return
     */
    public String getArticleTitle() {

        Element title = (Element) elements.get(0);
        return title.text();
    }

    public int size() {
        return elements.size();
    }

    public Node get(int index) {
        return elements.get(index);
    }

    @Override
    public Iterator<Node> iterator() {
        return elements.iterator();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

}
