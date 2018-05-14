package net.prahasiwi.laporankeuangan.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.helper.BitmapHelper;
import net.prahasiwi.laporankeuangan.helper.Constant;

import static net.prahasiwi.laporankeuangan.helper.Constant.CATEGORY;
import static net.prahasiwi.laporankeuangan.helper.Constant.DATE;
import static net.prahasiwi.laporankeuangan.helper.Constant.DESCRIBE;
import static net.prahasiwi.laporankeuangan.helper.Constant.IMAGE;
import static net.prahasiwi.laporankeuangan.helper.Constant.KEYIMAGE;
import static net.prahasiwi.laporankeuangan.helper.Constant.TYPE;
import static net.prahasiwi.laporankeuangan.helper.Constant.VALUE;
import static net.prahasiwi.laporankeuangan.helper.Constant.changeFormatToView;

public class DetailActivity extends AppCompatActivity {

    TextView tvCategory,tvValue,tvDates,tvDescribe,tvType;
    ImageView ivProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Detail");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        tvType = findViewById(R.id.tv_type);
        tvCategory = findViewById(R.id.tv_category);
        tvValue = findViewById(R.id.tv_value);
        tvDates = findViewById(R.id.tv_date);
        tvDescribe = findViewById(R.id.tv_describe);
        ivProfile = findViewById(R.id.iv_img_open);


        Intent intent = getIntent();
        String keyImage = intent.getStringExtra(KEYIMAGE);
        if (keyImage.equals("0")){
            ivProfile.setImageResource(R.drawable.empty_profile);
        }else {
            ivProfile.setImageBitmap(BitmapHelper.getInstance().getBitmap());
        }
        String type = intent.getStringExtra(TYPE);
        tvType.setText("Kategori "+type);
        tvCategory.setText(intent.getStringExtra(CATEGORY));
        tvValue.setText(intent.getStringExtra(VALUE));
        tvDates.setText(changeFormatToView(intent.getStringExtra(DATE)));
        tvDescribe.setText(intent.getStringExtra(DESCRIBE));

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent full = new Intent(getApplication(),FullScreenActivity.class);
                startActivity(full);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
