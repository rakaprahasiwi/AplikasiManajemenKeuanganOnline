package net.prahasiwi.laporankeuangan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.helper.DbHelper;

public class SetUrlActivity extends AppCompatActivity {

    EditText etUrl;
    Button btnSubmit;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_url);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ubah server");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        context = this;
        DbHelper db = new DbHelper(this);
        String url = db.getUrlServer();
        etUrl = findViewById(R.id.et_url_server);
        btnSubmit = findViewById(R.id.btn_submit_url);
        etUrl.setText(url);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newurl = etUrl.getText().toString();
                DbHelper db1 = new DbHelper(SetUrlActivity.this);
                db1.setUrlServer(newurl);
                finish();
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
