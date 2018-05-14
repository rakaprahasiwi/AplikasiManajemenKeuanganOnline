package net.prahasiwi.laporankeuangan.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.model.Report;

import java.util.ArrayList;
import java.util.List;

import static net.prahasiwi.laporankeuangan.helper.Constant.addTitik;

/**
 * Created by PRAHASIWI on 20/04/2018.
 */

public class ReportItemCategoryAdapter extends RecyclerView.Adapter<ReportItemCategoryAdapter.VHolder> {

    List<Report> reports;
    List<String> hashReport,value;
    int code;
    public ReportItemCategoryAdapter(List<Report> reports, List<String> hashReport,int code) {
        this.reports = reports;
        this.hashReport = hashReport;
        this.code = code;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_category, parent, false);
        return new ReportItemCategoryAdapter.VHolder(itemView);
    }


    @Override
    public int getItemCount() {
        return hashReport.size();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        TextView tvCategory,tvValue;
        public VHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_report_category);
            tvValue= itemView.findViewById(R.id.tv_report_value);
        }
    }

    @Override
    public void onBindViewHolder(VHolder holder, int position) {
        hitungValue();
        int color;
        if (code == 1) {
            color = Color.parseColor("#264d00"); // warna hijau
        }else {
            color = Color.parseColor("#005580"); // warna biru
        }
        holder.tvCategory.setText(hashReport.get(position));
        holder.tvValue.setText("Rp. "+addTitik(value.get(position)));
        holder.tvValue.setTextColor(color);

    }

    private void hitungValue(){
        value = new ArrayList<>();
        for (int i = 0; i < hashReport.size();i++){
            int sum = 0;
            for (int j = 0; j < reports.size(); j++){
                if (hashReport.get(i).equals(reports.get(j).getCategory())){
                    sum = sum + Integer.parseInt(reports.get(j).getValue());
                }
            }
            value.add(String.valueOf(sum));
        }

    }
}
