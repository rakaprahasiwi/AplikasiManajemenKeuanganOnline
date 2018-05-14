package net.prahasiwi.laporankeuangan.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.adapter.MainAdapter;
import net.prahasiwi.laporankeuangan.adapter.ReportItemCategoryAdapter;
import net.prahasiwi.laporankeuangan.adapter.TodayAdapter;
import net.prahasiwi.laporankeuangan.api.RegisterAPI;
import net.prahasiwi.laporankeuangan.api.ServiceGenerator;
import net.prahasiwi.laporankeuangan.helper.DbHelper;
import net.prahasiwi.laporankeuangan.model.MainData;
import net.prahasiwi.laporankeuangan.model.Report;
import net.prahasiwi.laporankeuangan.model.Value;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.prahasiwi.laporankeuangan.helper.Constant.PATTERN_TV;
import static net.prahasiwi.laporankeuangan.helper.Constant.addTitik;
import static net.prahasiwi.laporankeuangan.helper.Constant.changeFormatToDatabase;
import static net.prahasiwi.laporankeuangan.helper.Constant.changeFormatToView2;
import static net.prahasiwi.laporankeuangan.helper.Constant.sumList;

public class ReportTransactionActivity extends AppCompatActivity {

    ProgressDialog progress;
    RecyclerView recyclerView;
    List<MainData> mainData;
    DbHelper dbHelper;
    String urldatabase,email,dates,stringDateView;
    Integer integerIncome,integerOutcome,integerBalance;
    TextView tvIncome,tvOutcome,tvBalance,tvTransaksi,tvDate1,tvDate2;
    List<Integer> incomelist,outcomelist;
    Context context;
    Calendar date1 = Calendar.getInstance();
    Calendar date2 = Calendar.getInstance();
    ImageView ivDate1,ivDate2,ivSelectDate;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_transaction);

        //Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Laporan transaksi");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        context = this;
        dbHelper = new DbHelper(context);
        urldatabase = dbHelper.getUrlServer();
        email = dbHelper.getEmailDb();

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.rv_laporantransaksi);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ivSelectDate = findViewById(R.id.iv_select_date);
        tvTransaksi = findViewById(R.id.tv_transaksi_laporan);
        loadAllData();
        tvTransaksi.setText("Semua Transaksi");

        //swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAllData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        ivSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDiaogSelect();
            }
        });
    }

    private void openDiaogSelect(){
        final CharSequence[] items = {"Semua", "Atur Sendiri", "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Transaksi");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Semua")) {
                    loadAllData();
                } else if (items[i].equals("Atur Sendiri")) {
                    openDiaogRange();
                } else if (items[i].equals("Batal")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    private void loadAllData(){
        progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setMessage("Loading Laporan...");
        progress.show();

        tvTransaksi.setText("Semua Transaksi");
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
        Call<Value> call = api.read_all(email);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                progress.dismiss();
                if (response.body().getValue().equals("1")){
                    mainData = new ArrayList<>();
                    mainData = response.body().getResult();
                    MainAdapter adapter = new MainAdapter(mainData,context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setNestedScrollingEnabled(false);;
                }else {
                    Toast.makeText(context, ""+response.body().getValue(), Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();

            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(context, "error today transaction"+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDiaogRange() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_range_date, null);
        ivDate1 = mView.findViewById(R.id.iv_button_date1);
        ivDate2 = mView.findViewById(R.id.iv_button_date2);
        tvDate1 = mView.findViewById(R.id.tv_date1);
        tvDate2 = mView.findViewById(R.id.tv_date2);
        updateTextLabel1();
        updateTextLabel2();
        ivDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context, d1, date1.get(Calendar.YEAR), date1.get(Calendar.MONTH), date1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        ivDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context, d2, date2.get(Calendar.YEAR), date2.get(Calendar.MONTH), date2.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        mBuilder.setView(mView)
                .setTitle("Atur Rentang Waktu")
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadDataRange();
                    }
                });
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    DatePickerDialog.OnDateSetListener d1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            date1.set(Calendar.YEAR, year);
            date1.set(Calendar.MONTH, month);
            date1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel1();
        }
    };


    private void updateTextLabel1() {

        SimpleDateFormat dfDay = new SimpleDateFormat(PATTERN_TV);
        String formattedDateDay = dfDay.format(date1.getTime());
        tvDate1.setText(formattedDateDay);
    }

    DatePickerDialog.OnDateSetListener d2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            date2.set(Calendar.YEAR, year);
            date2.set(Calendar.MONTH, month);
            date2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel2();
        }
    };


    private void updateTextLabel2() {

        SimpleDateFormat dfDay = new SimpleDateFormat(PATTERN_TV);
        String formattedDateDay = dfDay.format(date2.getTime());
        tvDate2.setText(formattedDateDay);
    }


    private void loadDataRange(){

        progress = new ProgressDialog(context);
        progress.setCancelable(false);
        progress.setMessage("Loading Laporan...");
        progress.show();

        String date1 = tvDate1.getText().toString();
        String date2 = tvDate2.getText().toString();

        tvTransaksi.setText("Transaksi "+changeFormatToView2(date1)+" s/d "+changeFormatToView2(date2));
        String dateFormat1 = changeFormatToDatabase(date1);
        String dateFormat2 = changeFormatToDatabase(date2);
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
        Call<Value> call = api.read_range(email,dateFormat1,dateFormat2);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                progress.dismiss();
                if (response.body().getValue().equals("1")){
                    mainData = new ArrayList<>();
                    mainData = response.body().getResult();
                    MainAdapter adapter = new MainAdapter(mainData,context);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setNestedScrollingEnabled(false);
                }else {
                    Toast.makeText(context, ""+response.body().getValue(), Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();

            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(context, "error today transaction"+t, Toast.LENGTH_SHORT).show();
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
