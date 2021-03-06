package com.citywindowpro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.citywindowpro.Adapter.FiveMainProductAdapter;
import com.citywindowpro.Adapter.SliderAdapter;
import com.citywindowpro.Adapter.ViewPagerAdapter;
import com.citywindowpro.Model.FiveMainsubcatgory;
import com.citywindowpro.Utils.Constants;
import com.citywindowpro.Utils.RecyclerItemClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class YummyFoodsActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    ProgressDialog dialog;
    List<FiveMainsubcatgory> listuser;

    ViewPager yummy_intro_images;
    CirclePageIndicator yindicator;
    public ArrayList<Integer> imgArray = new ArrayList<Integer>();
    public Runnable mRun;
    private ViewPagerAdapter mAdapter;
    public int pos = 0;
    private final Handler handler = new Handler();
    RecyclerView recyclerView;
    List<SliderAdapter> imagelist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yummy_foods);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        yummy_intro_images = (ViewPager) findViewById(R.id.yummy_intro_images);
        yindicator = (CirclePageIndicator) findViewById(R.id.yindicator);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        listuser = new ArrayList<>();
        loadtopslider();
        subcategoryload();

    }

    public void loadtopslider() {
        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(800000);
            RequestParams params = new RequestParams();
            params.add("cat_id", Constants.cat_id);
            client.post(Constants.liveuri + "slider_cat.php", params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("res_error", responseString);
                    dialog.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    if (responseString != null) {
                        imagelist.clear();

                        try {
                            Log.d("res_top_slider", responseString);
                            JSONObject jsonObj = new JSONObject(responseString);
                            JSONArray resultsarray = jsonObj.getJSONArray("response");
                            for (int i = 0; i < resultsarray.length(); i++) {
                                SliderAdapter temp = new SliderAdapter();
                                temp.setSlider_image(Constants.liveimageuri + resultsarray.getJSONObject(i).getString("sl_img"));
                                imagelist.add(temp);
                                dialog.dismiss();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            e.printStackTrace();
                        }
                        setTopSlider();
                    }
                }
            });
        }
    }

    private void setTopSlider() {

        mAdapter = new ViewPagerAdapter(this, imagelist);
        yummy_intro_images.setAdapter(mAdapter);
        yummy_intro_images.setCurrentItem(0);
        yummy_intro_images.setOnPageChangeListener(this);
        yummy_intro_images.setCurrentItem(pos, true);
        yindicator.setViewPager(yummy_intro_images);


        mRun = new Runnable() {
            @Override
            public void run() {
                if (pos < mAdapter.getCount()) {
                    yummy_intro_images.setCurrentItem(pos, true);
                    handler.postDelayed(this, 3000);
                    yindicator.setViewPager(yummy_intro_images);
                    pos++;
                } else {
                    pos = 0;
                    yummy_intro_images.setCurrentItem(pos, true);
                    handler.postDelayed(this, 3000);
                    yindicator.setViewPager(yummy_intro_images);
                    pos++;
                }
            }
        };
        handler.post(mRun);


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    public void subcategoryload() {
        if (isNetworkAvailable()) {
            dialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(800000);
            RequestParams params = new RequestParams();
            params.add("cat_id", Constants.cat_id);
            client.post(Constants.liveuri + "sel_sub_category.php", params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    dialog.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    if (responseString != null) {
                        listuser.clear();

                        try {
                            Log.d("res", responseString);
                            //Toast.makeText(getContext().getApplicationContext(), res.toString(), Toast.LENGTH_SHORT).show();
                            JSONObject jsonObj = new JSONObject(responseString);
                            JSONArray resultsarray = jsonObj.getJSONArray("response");
                            // looping through All Contacts
                            for (int i = 0; i < resultsarray.length(); i++) {
                                FiveMainsubcatgory temp = new FiveMainsubcatgory();
                                temp.setSubid(resultsarray.getJSONObject(i).getString("sub_id"));
                                temp.setSubname(resultsarray.getJSONObject(i).getString("sub_name"));
                                temp.setImg(Constants.liveimageuri + resultsarray.getJSONObject(i).getString("img"));
                                listuser.add(temp);
                                dialog.dismiss();
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                            e.printStackTrace();
                        }


                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
                        //  call the constructor of FashionistaMainAdapter to send the reference and data to Adapter
                        FiveMainProductAdapter customAdapter = new FiveMainProductAdapter(YummyFoodsActivity.this, listuser);
                        recyclerView.setAdapter(customAdapter);
                        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(YummyFoodsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Constants.sub_id = listuser.get(position).getSubid();
                                startActivity(new Intent(getApplicationContext(), FiveMainProductSubcategory1Activity.class));
                            }
                        }));
                    }
                }
            });
        } else {
            Toast.makeText(YummyFoodsActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
