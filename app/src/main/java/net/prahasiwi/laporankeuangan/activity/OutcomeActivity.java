package net.prahasiwi.laporankeuangan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.adapter.ListAdapterKategory;
import net.prahasiwi.laporankeuangan.api.RegisterAPI;
import net.prahasiwi.laporankeuangan.api.ServiceGenerator;
import net.prahasiwi.laporankeuangan.helper.DbHelper;
import net.prahasiwi.laporankeuangan.model.Category;
import net.prahasiwi.laporankeuangan.model.Value;
import net.prahasiwi.laporankeuangan.model.ValueCategory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutcomeActivity extends AppCompatActivity {
    ProgressDialog progress;
    RecyclerView rvIncome;
    FloatingActionButton fabAddCategory;
    DbHelper dbHelper;
    String addkategori,urldatabase,email;
    List<Category> results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        //Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Kategori pengeluaran");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        dbHelper = new DbHelper(this);
        urldatabase = dbHelper.getUrlServer();
        email = dbHelper.getEmailDb();
        rvIncome = findViewById(R.id.rv_income);
        fabAddCategory = findViewById(R.id.fab_income);
        loadDataCategory();
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    private void openDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_kategory, null);
        final EditText etAddKategory = (EditText) mView.findViewById(R.id.et_add_kategory);
        mBuilder.setView(mView)
                .setTitle("Tambah Kategori")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // listKategoryPemasukan.replaceAll(String::toLowerCase);
                        addkategori = etAddKategory.getText().toString();
                        if (addkategori.equals("")){
                            Toast.makeText(OutcomeActivity.this, "null name category", Toast.LENGTH_SHORT).show();
                        }else{
                            saveToServer();
                        }
                    }
                });
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void saveToServer() {
        progress = new ProgressDialog(OutcomeActivity.this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
        Call<Value> call = api.add_outcome(email, addkategori);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                progress.dismiss();
                String message = response.body().getMessage();
                if (response.body().getValue().equals("1")) {
                    Toast.makeText(OutcomeActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                    loadDataCategory();
                } else {
                    Toast.makeText(OutcomeActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(OutcomeActivity.this, "Jaringan error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadDataCategory(){
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
        Call<ValueCategory> call = api.read_outcome(email);
        call.enqueue(new Callback<ValueCategory>() {
            @Override
            public void onResponse(Call<ValueCategory> call, Response<ValueCategory> response) {
                //Toast.makeText(OutcomeActivity.this, ""+response.body().getValue(), Toast.LENGTH_SHORT).show();
                results = new ArrayList<>();
                results = response.body().getResult();
                List<String> resultString = new ArrayList<>();
                for (int i = 0; i < results.size(); i++){
                    resultString.add(results.get(i).getCategory());
                }
                rvIncome.setLayoutManager(new LinearLayoutManager(OutcomeActivity.this));
                ListAdapterKategory adapterKategory = new ListAdapterKategory(OutcomeActivity.this,results,resultString,2);
                rvIncome.setAdapter(adapterKategory);
            }

            @Override
            public void onFailure(Call<ValueCategory> call, Throwable t) {
                Toast.makeText(OutcomeActivity.this, "jaringan error", Toast.LENGTH_SHORT).show();
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