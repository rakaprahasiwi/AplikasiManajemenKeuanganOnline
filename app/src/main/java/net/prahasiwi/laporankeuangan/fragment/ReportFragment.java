package net.prahasiwi.laporankeuangan.fragment;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.activity.InputDataActivity;
import net.prahasiwi.laporankeuangan.activity.ReportTransactionActivity;
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
import static net.prahasiwi.laporankeuangan.helper.Constant.PATTERN_TV2;
import static net.prahasiwi.laporankeuangan.helper.Constant.addTitik;
import static net.prahasiwi.laporankeuangan.helper.Constant.changeFormatToDatabase;
import static net.prahasiwi.laporankeuangan.helper.Constant.changeFormatToView;
import static net.prahasiwi.laporankeuangan.helper.Constant.changeFormatToView2;
import static net.prahasiwi.laporankeuangan.helper.Constant.sumList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    ProgressDialog progress;
    CardView cvLaporanTransaksi;
    RecyclerView rvIncomeReport,rvOutcomeReport;
    List<MainData> mainData;
    DbHelper dbHelper;
    String urldatabase,email,stringDateView;
    Integer integerIncome,integerOutcome,integerBalance;
    TextView tvIncome,tvOutcome,tvBalance,tvTransaksi,tvDate1,tvDate2;
    List<Integer> incomelist,outcomelist;
    List<Report> categoryIncome,categoryOutcome;
    List<String> incomeString,outcomeString;
    List listHashIn, listHashOut;
    Context context;
    Calendar date1 = Calendar.getInstance();
    Calendar date2 = Calendar.getInstance();
    ImageView ivDate1,ivDate2,ivSelectDate;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        //get email and url server
        dbHelper = new DbHelper(getActivity());
        urldatabase = dbHelper.getUrlServer();
        email = dbHelper.getEmailDb();

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefresh);

        cvLaporanTransaksi = view.findViewById(R.id.cv_laporantransaksi);
        rvIncomeReport = view.findViewById(R.id.rv_laporanincome);
        rvOutcomeReport = view.findViewById(R.id.rv_laporanoutcome);
        tvIncome = view.findViewById(R.id.tv_income);
        tvOutcome = view.findViewById(R.id.tv_outcome);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvTransaksi = view.findViewById(R.id.tv_transaksi_laporan);
        ivSelectDate = view.findViewById(R.id.iv_select_date);
        context = getActivity();
        rvIncomeReport.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvOutcomeReport.setLayoutManager(new LinearLayoutManager(getActivity()));

        //swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadData();
        tvTransaksi.setText("Semua Transaksi");
        cvLaporanTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ReportTransactionActivity.class));
            }
        });

        ivSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDiaogSelect();
            }
        });
        return view;
    }

    private void loadData(){

        progress = new ProgressDialog(getActivity());
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
                    incomelist = new ArrayList<>();
                    outcomelist = new ArrayList<>();
                    categoryIncome = new ArrayList<>();
                    categoryOutcome = new ArrayList<>();
                    incomeString = new ArrayList<>();
                    outcomeString = new ArrayList<>();
                    for (int i = 0; i < mainData.size(); i++){
                        if (mainData.get(i).getType().equals("Pemasukan")){
                            incomelist.add(Integer.parseInt(mainData.get(i).getValue()));
                            categoryIncome.add(new Report(mainData.get(i).getCategory(),
                                    mainData.get(i).getValue()));
                            incomeString.add(mainData.get(i).getCategory()+"");
                        } else {
                            outcomelist.add(Integer.parseInt(mainData.get(i).getValue()));
                            categoryOutcome.add(new Report(mainData.get(i).getCategory(),
                                    mainData.get(i).getValue()));
                            outcomeString.add(mainData.get(i).getCategory()+"");
                        }
                    }
                    listHashIn = new ArrayList(new HashSet(incomeString));
                    listHashOut = new ArrayList(new HashSet(outcomeString));
                    List<Report> jajalan = new ArrayList<>();
                    for (int i = 0; i < listHashIn.size();i++){
                        jajalan.add(new Report(listHashIn.get(i)+"",listHashIn.get(i)+""));
                    }

                    ReportItemCategoryAdapter adapterIn = new ReportItemCategoryAdapter(categoryIncome,listHashIn,1);
                    rvIncomeReport.setAdapter(adapterIn);
                    ReportItemCategoryAdapter adapterOut = new ReportItemCategoryAdapter(categoryOutcome,listHashOut,2);
                    rvOutcomeReport.setAdapter(adapterOut);
                    integerIncome = sumList(incomelist);
                    integerOutcome = sumList(outcomelist);
                    integerBalance = integerIncome - integerOutcome;
                    tvIncome.setText("Rp. "+addTitik(integerIncome+""));
                    tvOutcome.setText("Rp. "+addTitik(""+integerOutcome));
                    tvBalance.setText("Rp. "+addTitik(""+integerBalance));
                }else {
                    Toast.makeText(getActivity(), ""+response.body().getValue(), Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();

            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getActivity(), "error today transaction"+t, Toast.LENGTH_SHORT).show();
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
                    loadData();
                } else if (items[i].equals("Atur Sendiri")) {
                    openDiaogRange();
                } else if (items[i].equals("Batal")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
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
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
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
        tvDate1.setText((formattedDateDay));
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
        tvDate2.setText((formattedDateDay));
    }


    private void loadDataRange(){

        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);
        progress.setMessage("Loading Laporan...");
        progress.show();

        String date1 = tvDate1.getText().toString();
        String date2 = tvDate2.getText().toString();
        tvTransaksi.setText("Transaksi "+changeFormatToView2(date1)+" s/d "+changeFormatToView2(date2));
        String dateFormat1 = changeFormatToDatabase(date1);
        String dateFormat2 = changeFormatToDatabase(date2);

//        Toast.makeText(context, dateFormat1+" "+dateFormat2, Toast.LENGTH_SHORT).show();
//        progress.dismiss();
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
                    incomelist = new ArrayList<>();
                    outcomelist = new ArrayList<>();
                    categoryIncome = new ArrayList<>();
                    categoryOutcome = new ArrayList<>();
                    incomeString = new ArrayList<>();
                    outcomeString = new ArrayList<>();
                    for (int i = 0; i < mainData.size(); i++){
                        if (mainData.get(i).getType().equals("Pemasukan")){
                            incomelist.add(Integer.parseInt(mainData.get(i).getValue()));
                            categoryIncome.add(new Report(mainData.get(i).getCategory(),
                                    mainData.get(i).getValue()));
                            incomeString.add(mainData.get(i).getCategory()+"");
                        } else {
                            outcomelist.add(Integer.parseInt(mainData.get(i).getValue()));
                            categoryOutcome.add(new Report(mainData.get(i).getCategory(),
                                    mainData.get(i).getValue()));
                            outcomeString.add(mainData.get(i).getCategory()+"");
                        }
                    }
                    listHashIn = new ArrayList(new HashSet(incomeString));
                    listHashOut = new ArrayList(new HashSet(outcomeString));
                    List<Report> jajalan = new ArrayList<>();
                    for (int i = 0; i < listHashIn.size();i++){
                        jajalan.add(new Report(listHashIn.get(i)+"",listHashIn.get(i)+""));
                    }

                    ReportItemCategoryAdapter adapterIn = new ReportItemCategoryAdapter(categoryIncome,listHashIn,1);
                    rvIncomeReport.setAdapter(adapterIn);
                    ReportItemCategoryAdapter adapterOut = new ReportItemCategoryAdapter(categoryOutcome,listHashOut,2);
                    rvOutcomeReport.setAdapter(adapterOut);
                    integerIncome = sumList(incomelist);
                    integerOutcome = sumList(outcomelist);
                    integerBalance = integerIncome - integerOutcome;
                    tvIncome.setText("Rp. "+addTitik(integerIncome+""));
                    tvOutcome.setText("Rp. "+addTitik(""+integerOutcome));
                    tvBalance.setText("Rp. "+addTitik(""+integerBalance));
                }else {
                    Toast.makeText(getActivity(), ""+response.body().getValue(), Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();

            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getActivity(), "error today transaction"+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
