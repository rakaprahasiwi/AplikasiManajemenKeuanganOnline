package net.prahasiwi.laporankeuangan.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.prahasiwi.laporankeuangan.adapter.FragPagerAdapter;
import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.api.RegisterAPI;
import net.prahasiwi.laporankeuangan.api.ServiceGenerator;
import net.prahasiwi.laporankeuangan.helper.DbHelper;
import net.prahasiwi.laporankeuangan.model.Value;
import net.prahasiwi.laporankeuangan.model.ValueCategory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    DbHelper dbHelper;
    ViewPager viewPager;
    String urldatabase,email,m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragPagerAdapter(getSupportFragmentManager(), MainActivity.this));


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        dbHelper = new DbHelper(this);
        fab = findViewById(R.id.fab_add_transaksi);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,InputDataActivity.class));
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
           }
           @Override
           public void onPageSelected(int position) {
               if (position == 0){
                    fab.show();
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this,InputDataActivity.class));
                        }
                    });
               }else {
                   fab.hide();
               }
           }

           @Override
           public void onPageScrollStateChanged(int state) {

           }
       });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && viewPager.getCurrentItem() == 1) {
            viewPager.setCurrentItem(0, true);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            //Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingActivity.class));
        }else if (item.getItemId() == R.id.action_logout){
           // Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
            dbHelper.delete();
            Intent intent = new Intent(this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    public void defaultCategory(){
//        ServiceGenerator serviceGenerator = new ServiceGenerator();
//        RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
//        Call<Value> call = api.add_income(email, "Gaji");
//        Call<Value> call2 = api.add_outcome(email, "Makanan");
//        call.enqueue(new Callback<Value>() {
//            @Override
//            public void onResponse(Call<Value> call, Response<Value> response) {
//                //progress.dismiss();
//                String message = response.body().getMessage();
//                if (response.body().getValue().equals("1")) {
//                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
//                    //loadDataCategory();
//                } else {
//                    Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<Value> call, Throwable t) {
//                //progress.dismiss();
//                Toast.makeText(MainActivity.this, "Jaringan error"+t, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        call2.enqueue(new Callback<Value>() {
//            @Override
//            public void onResponse(Call<Value> call2, Response<Value> response) {
//                //progress.dismiss();
//                String message = response.body().getMessage();
//                if (response.body().getValue().equals("1")) {
//                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
//                   // loadDataCategory();
//                } else {
//                    Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Value> call, Throwable t) {
//                //progress.dismiss();
//                Toast.makeText(MainActivity.this, "Jaringan error", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}