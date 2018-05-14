package net.prahasiwi.laporankeuangan.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.activity.DetailActivity;
import net.prahasiwi.laporankeuangan.activity.MainActivity;
import net.prahasiwi.laporankeuangan.activity.UpdateActivity;
import net.prahasiwi.laporankeuangan.api.RegisterAPI;
import net.prahasiwi.laporankeuangan.api.ServiceGenerator;
import net.prahasiwi.laporankeuangan.helper.BitmapHelper;
import net.prahasiwi.laporankeuangan.helper.DbHelper;
import net.prahasiwi.laporankeuangan.model.MainData;
import net.prahasiwi.laporankeuangan.model.Value;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.prahasiwi.laporankeuangan.helper.Constant.CATEGORY;
import static net.prahasiwi.laporankeuangan.helper.Constant.DATE;
import static net.prahasiwi.laporankeuangan.helper.Constant.DESCRIBE;
import static net.prahasiwi.laporankeuangan.helper.Constant.ID;
import static net.prahasiwi.laporankeuangan.helper.Constant.KEYIMAGE;
import static net.prahasiwi.laporankeuangan.helper.Constant.TYPE;
import static net.prahasiwi.laporankeuangan.helper.Constant.VALUE;
import static net.prahasiwi.laporankeuangan.helper.Constant.addTitik;

/**
 * Created by PRAHASIWI on 24/03/2018.
 */

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.VHolder> {
    List<MainData> mainData;
    Context context;

    public TodayAdapter(List<MainData> mainData, Context context) {
        this.mainData = mainData;
        this.context = context;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_data, parent, false);
        return new VHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mainData.size();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        TextView tvcategory,tvdescribe,tvvalue;
        ImageView ivimage;
        public VHolder(View itemView) {
            super(itemView);
            tvcategory = itemView.findViewById(R.id.tv_category);
            tvdescribe = itemView.findViewById(R.id.tv_describe);
            tvvalue = itemView.findViewById(R.id.tv_value);
            ivimage = itemView.findViewById(R.id.iv_image);
        }
    }
    @Override
    public void onBindViewHolder(VHolder holder, final int position) {

        if (mainData.get(position).getImage().equals("null")||
                mainData.get(position).getImage().equals("")){
            holder.ivimage.setImageResource(R.drawable.empty_profile);
        }else {
            String encodedImage = mainData.get(position).getImage();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.ivimage.setImageBitmap(decodedByte);
        }

        int color;
        if (mainData.get(position).getType().equals("Pemasukan")) {
            color = Color.parseColor("#264d00"); // warna hijau
        }else {
            color = Color.parseColor("#005580"); // warna biru

        }
        holder.tvcategory.setText(mainData.get(position).getCategory());
        holder.tvdescribe.setText(mainData.get(position).getDescribes());
        holder.tvvalue.setText("Rp. "+ addTitik(mainData.get(position).getValue()));
        holder.tvvalue.setTextColor(color);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(context, "short click", Toast.LENGTH_SHORT).show();
                openDetail(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemOnLongClick(position);
                return true;
            }
        });
    }

    private void openDetail(int pos){
        Intent intent = new Intent(context, DetailActivity.class);
        Bitmap bmp;
        String key;
        if (mainData.get(pos).getImage().equals("null")||
                mainData.get(pos).getImage().equals("")){
            bmp = null;
            key = "0";
        }else {
            String encodedImage = mainData.get(pos).getImage();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            key = "1";
        }

        BitmapHelper.getInstance().setBitmap(bmp);
        intent.putExtra(KEYIMAGE, key);
        intent.putExtra(TYPE, mainData.get(pos).getType());
        intent.putExtra(CATEGORY, mainData.get(pos).getCategory());
        intent.putExtra(VALUE,"Rp. " + addTitik("" + mainData.get(pos).getValue()));
        intent.putExtra(DESCRIBE, mainData.get(pos).getDescribes());
        intent.putExtra(DATE, mainData.get(pos).getDates());
        context.startActivity(intent);
    }

    private void itemOnLongClick(final int pos) {
        final CharSequence[] items = {"Buka","Ubah", "Hapus","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Plihan");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Buka")) {
                    openDetail(pos);
                } else if (items[i].equals("Ubah")) {
                    updateData(pos);
                } else if (items[i].equals("Hapus")) {
                    dialogDelete(pos);
                } else if (items[i].equals("Batal")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    private void updateData(final int pos){
        Intent intent = new Intent(context, UpdateActivity.class);
        Bitmap bmp;
        String key;
        if (mainData.get(pos).getImage().equals("null")||
                mainData.get(pos).getImage().equals("")){
            bmp = null;
            key = "0";
        }else {
            String encodedImage = mainData.get(pos).getImage();
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            key = "1";
        }

        BitmapHelper.getInstance().setBitmap(bmp);
        intent.putExtra(KEYIMAGE, key);
        intent.putExtra(ID, mainData.get(pos).getId());
        intent.putExtra(VALUE,mainData.get(pos).getValue());
        intent.putExtra(DESCRIBE, mainData.get(pos).getDescribes());
        intent.putExtra(DATE, mainData.get(pos).getDates());
        context.startActivity(intent);
    }

    private void dialogDelete(final int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Perhatian !!!");
        alert.setMessage("Apa anda yakin menghapus item ini?");
        alert.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                deleteData(pos);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void deleteData(final int pos) {
        DbHelper dbHelper = new DbHelper(context);
        String urldatabase = dbHelper.getUrlServer();
        String id = mainData.get(pos).getId();
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
        Call<Value> call = api.delete(id);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                Toast.makeText(context, "Error, gagal hapus data . . .", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
