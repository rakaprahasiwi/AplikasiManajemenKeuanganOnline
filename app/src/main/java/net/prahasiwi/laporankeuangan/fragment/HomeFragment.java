package net.prahasiwi.laporankeuangan.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.prahasiwi.laporankeuangan.activity.IncomeActivity;
import net.prahasiwi.laporankeuangan.adapter.TodayAdapter;
import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.api.RegisterAPI;
import net.prahasiwi.laporankeuangan.api.ServiceGenerator;
import net.prahasiwi.laporankeuangan.helper.DbHelper;
import net.prahasiwi.laporankeuangan.model.MainData;
import net.prahasiwi.laporankeuangan.model.Value;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.prahasiwi.laporankeuangan.helper.Constant.PATTERN_DATABASE;
import static net.prahasiwi.laporankeuangan.helper.Constant.PATTERN_TV;
import static net.prahasiwi.laporankeuangan.helper.Constant.addTitik;
import static net.prahasiwi.laporankeuangan.helper.Constant.changeFormatToView;
import static net.prahasiwi.laporankeuangan.helper.Constant.sumList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ProgressDialog progress;
    RecyclerView recyclerView;
    List<MainData> mainData;
    DbHelper dbHelper;
    String urldatabase,email,dates,stringDateView;
    Integer integerIncome,integerOutcome,integerBalance;
    TextView tvIncome,tvOutcome,tvBalance,tvTodayDate;
    List<Integer> incomelist,outcomelist;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.rv);
        tvIncome = view.findViewById(R.id.tv_income);
        tvOutcome = view.findViewById(R.id.tv_outcome);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvTodayDate = view.findViewById(R.id.tv_transaksi_today);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefresh);

        //,tvOutcome,tvBalance,tvTodayDate;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //get email and url server
        dbHelper = new DbHelper(getActivity());
        urldatabase = dbHelper.getUrlServer();
        email = dbHelper.getEmailDb();
        //get date today
        Calendar dateTime = Calendar.getInstance();
        SimpleDateFormat dfDay = new SimpleDateFormat(PATTERN_DATABASE);
        dates = dfDay.format(dateTime.getTime());
        stringDateView = changeFormatToView(dates);
        tvTodayDate.setText("Transaksi hari ini : "+stringDateView);
        //set adapter

        //swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataToday();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        loadDataToday();
        return view;
    }

    private void loadDataToday(){

        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
        Call<Value> call = api.read_today(email,dates);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                progress.dismiss();
                if (response.body().getValue().equals("1")){
                    mainData = new ArrayList<>();
                    mainData = response.body().getResult();
                    TodayAdapter adapter = new TodayAdapter(mainData, getActivity());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setNestedScrollingEnabled(false);
                    incomelist = new ArrayList<>();
                    outcomelist = new ArrayList<>();
                    for (int i = 0; i < mainData.size(); i++){
                        if (mainData.get(i).getType().equals("Pemasukan")){
                            incomelist.add(Integer.parseInt(mainData.get(i).getValue()));
                        } else {
                            outcomelist.add(Integer.parseInt(mainData.get(i).getValue()));
                        }
                    }

                    integerIncome = sumList(incomelist);
                    integerOutcome = sumList(outcomelist);
                    integerBalance = integerIncome - integerOutcome;
                    tvIncome.setText("Rp. "+addTitik(integerIncome+""));
                    tvOutcome.setText("Rp. "+addTitik(""+integerOutcome));
                    tvBalance.setText("Rp. "+addTitik(""+integerBalance));
                }else {
                    Toast.makeText(getActivity(), ""+response.body().getValue(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getActivity(), "error today transaction"+t, Toast.LENGTH_SHORT).show();
            }
        });
    }



}