package com.example.tacademy.networkexample;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    EditText keywordView;
    ListView listView;
    SwipeRefreshLayout refreshLayout;
    Region_Adapter mAdapter;

    boolean isLast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        keywordView = (EditText)findViewById(R.id.edit_keyword);
        listView = (ListView)findViewById(R.id.listView);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        mAdapter = new Region_Adapter();
        listView.setAdapter(mAdapter);

        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String regions = mAdapter.getKeyword();
                if (!TextUtils.isEmpty(regions)) {
                    try {
                        NetworkManager.getInstance().getRegion_Restaurants(MainActivity.this, regions, new NetworkManager.OnResultListener<Region_Restaurants>() {
                            @Override
                            public void onSuccess(Request request, Region_Restaurants result) {
                                mAdapter.clearAll();
                                mAdapter.addAll(result.items);
                                mAdapter.setTotalCount(result.total);
                                refreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onFailure(Request request, int code, Throwable cause) {
                                refreshLayout.setRefreshing(false);
                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isLast && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    getMoreItem();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
                    isLast = true;
                } else {
                    isLast = false;
                }
            }
        });



        Button btn = (Button)findViewById(R.id.btn_search);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkManager.getInstance().testSSL(MainActivity.this, new NetworkManager.OnResultListener<String>() {
                    @Override
                    public void onSuccess(Request request, String result) {
                        Toast.makeText(MainActivity.this, "string : " + result, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(MainActivity.this, "error" , Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    boolean isMoreData = false;
    ProgressDialog dialog = null;
    private void getMoreItem() {
        if (isMoreData) return;
        isMoreData = true;
        if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() > mAdapter.getCount()) {
            String regions = mAdapter.getKeyword();
            try {
                NetworkManager.getInstance().getRegion_Restaurants(this, regions, new NetworkManager.OnResultListener<Region_Restaurants>() {
                    @Override
                    public void onSuccess(Request request, Region_Restaurants result) {
                        mAdapter.addAll(result.items);
                        isMoreData = false;
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        isMoreData = false;
                        dialog.dismiss();
                    }
                });
                dialog = new ProgressDialog(this);
                dialog.setTitle("Loading....");
                dialog.show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                isMoreData = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
