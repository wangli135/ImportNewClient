
package importnew.importnewclient.ui;

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
import android.view.View;
import android.widget.Toast;

import importnew.importnewclient.R;
import importnew.importnewclient.utils.Constants;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int HOMEPAGE_INDEX = 0;
    private static final int ALL_ARTICLE_INDEX = 1;
    private static final int HOT_ARTICLE_INDEX = 2;
    private static final int MORE_CONTENT_INDEX = 3;

    private int[] choicesId = {R.string.homepage, R.string.all_artciles, R.string.hot_articles, R.string.more_contents};

    private Toolbar toolbar;

    private DrawerLayout drawer;

    private Fragment mFragment;

    /**
     * 正在显示的Fragment
     */
    private int numOfFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            numOfFragment = savedInstanceState.getInt(Constants.Key.NUM_OF_FRAGMENT);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //双击列表返回顶部
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (System.currentTimeMillis() - lastBackTime < 2000) {
                    Intent intent = new Intent("com.importnew.listview.selection");
                    sendBroadcast(intent);
                }

                lastBackTime = System.currentTimeMillis();
            }
        });
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        switch (numOfFragment) {
            case HOMEPAGE_INDEX:
                mFragment = new HomePageFragment();
                break;
            case ALL_ARTICLE_INDEX:
                mFragment = new AllArticlesFragment();
                break;
            case HOT_ARTICLE_INDEX:
                mFragment = new HotArticleFragment();
                break;
            case MORE_CONTENT_INDEX:
                mFragment = new MoreContentsFragment();
                break;
        }

        getSupportActionBar().setTitle(choicesId[numOfFragment]);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_contents, mFragment).commit();
    }

    private long lastBackTime;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (System.currentTimeMillis() - lastBackTime < 2000)
                super.onBackPressed();
            else {
                Toast.makeText(this, "再次点击退出应用", Toast.LENGTH_SHORT).show();
                lastBackTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {

            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager manager = getSupportFragmentManager();

        if (id == R.id.homepage) {

            toolbar.setTitle(choicesId[HOMEPAGE_INDEX]);
            if (numOfFragment != HOMEPAGE_INDEX) {
                mFragment = new HomePageFragment();
                manager.beginTransaction().replace(R.id.main_contents, mFragment).commit();
                numOfFragment = HOMEPAGE_INDEX;
            }

        } else if (id == R.id.all_artciles) {

            toolbar.setTitle(choicesId[ALL_ARTICLE_INDEX]);
            if (numOfFragment != ALL_ARTICLE_INDEX) {
                mFragment = new AllArticlesFragment();
                manager.beginTransaction().replace(R.id.main_contents, mFragment).commit();
                numOfFragment = ALL_ARTICLE_INDEX;
            }

        } else if (id == R.id.hot_artciles) {

            toolbar.setTitle(choicesId[HOT_ARTICLE_INDEX]);
            if (numOfFragment != HOT_ARTICLE_INDEX) {
                mFragment = new HotArticleFragment();
                manager.beginTransaction().replace(R.id.main_contents, mFragment).commit();
                numOfFragment = HOT_ARTICLE_INDEX;
            }
        } else if (id == R.id.more_contents) {

            toolbar.setTitle(choicesId[MORE_CONTENT_INDEX]);
            if (numOfFragment != MORE_CONTENT_INDEX) {
                mFragment = new MoreContentsFragment();
                manager.beginTransaction().replace(R.id.main_contents, mFragment).commit();
                numOfFragment = MORE_CONTENT_INDEX;
            }
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.Key.NUM_OF_FRAGMENT, numOfFragment);
    }
}
