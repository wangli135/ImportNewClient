
package importnew.importnewclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import importnew.importnewclient.R;
import importnew.importnewclient.bean.Article;
import importnew.importnewclient.bean.ArticleBody;
import importnew.importnewclient.utils.Constants;
import importnew.importnewclient.view.TestActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseFragment.OnArticleSelectedListener {

    private int[] choicesId = {R.string.homepage, R.string.all_artciles, R.string.hot_articles, R.string.more_contents};

    private Toolbar toolbar;

    private Fragment mFragment;

    private List<Fragment> fragmentList;

    private Article selectedArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initFragments();

    }

    private void initFragments() {
        mFragment = new HomePageFragment();
        toolbar.setTitle(choicesId[0]);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_contents, mFragment).commit();
    }

    @Override
    public void onArticleSelectedListener(Article article) {

        selectedArticle = article;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.Code.REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            boolean isFavourite = data.getBooleanExtra(Constants.Key.IS_FAVOURITE, false);
            if (selectedArticle != null)
                selectedArticle.setFavourite(false);

            if (selectedArticle.getBody() == null) {
                ArticleBody articleBody = (ArticleBody) data.getSerializableExtra(Constants.Key.ARTICLE_BODY);
                selectedArticle.setBody(articleBody);
            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

           /* Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);*/

            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager manager = getSupportFragmentManager();

        if (id == R.id.homepage) {

            toolbar.setTitle(choicesId[0]);
            if (!(mFragment instanceof HomePageFragment))
                mFragment = new HomePageFragment();
            manager.beginTransaction().replace(R.id.main_contents, mFragment).commit();

        } else if (id == R.id.all_artciles) {

            toolbar.setTitle(choicesId[1]);
            if (!(mFragment instanceof AllArticlesFragment))
                mFragment = new AllArticlesFragment();
            manager.beginTransaction().replace(R.id.main_contents, mFragment).commit();

        } else if (id == R.id.hot_artciles) {

            toolbar.setTitle(choicesId[2]);
            if (!(mFragment instanceof HotArticleFragment))
                mFragment = new HotArticleFragment();
            manager.beginTransaction().replace(R.id.main_contents, mFragment).commit();

        } else if (id == R.id.more_contents) {

            toolbar.setTitle(choicesId[3]);
            if (!(mFragment instanceof MoreContentsFragment))
                mFragment = new MoreContentsFragment();
            manager.beginTransaction().replace(R.id.main_contents, mFragment).commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
