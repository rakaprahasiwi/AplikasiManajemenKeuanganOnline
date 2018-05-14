package net.prahasiwi.laporankeuangan.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.activity.IncomeActivity;
import net.prahasiwi.laporankeuangan.api.RegisterAPI;
import net.prahasiwi.laporankeuangan.api.ServiceGenerator;
import net.prahasiwi.laporankeuangan.helper.DbHelper;
import net.prahasiwi.laporankeuangan.model.Category;
import net.prahasiwi.laporankeuangan.model.Value;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PRAHASIWI on 30/01/2018.
 */

public class ListAdapterKategory extends RecyclerView.Adapter<ListAdapterKategory.VHolder> {

    Context context;
    int idKategory;
    List<Category> listKategory;
    List<String> listString;

    public ListAdapterKategory(Context context, List<Category> kategory, List<String> lisString, int id) {
        this.listString = lisString;
        this.listKategory = kategory;
        this.context = context;
        this.idKategory = id;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_kategori, parent, false);
        return new VHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return listString.size();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        TextView tvKategori;

        public VHolder(View itemView) {
            super(itemView);
            tvKategori = (TextView) itemView.findViewById(R.id.tv_kategori);
        }
    }

    @Override
    public void onBindViewHolder(VHolder holder, final int position) {
        holder.tvKategori.setText(listString.get(position));
        //Action onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick(listKategory.get(position).getCategory(), position);
            }
        });
    }

    private void itemClick(final String s, final int pos) {
        final CharSequence[] items = {"Ubah", "Hapus", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Kategori " + s + "?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Ubah")) {
                    openDialogUbah(s, pos);
                } else if (items[i].equals("Hapus")) {
                    if (listString.size() > 1)
                        openDialogHapus(pos);
                    else
                        Toast.makeText(context, "Category can't empty ...", Toast.LENGTH_SHORT).show();
                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
                notifyDataSetChanged();
            }
        });
        builder.show();
    }

    private void openDialogUbah(final String label, final int pos) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_add_kategory, null);
        final EditText etAddKategory = (EditText) mView.findViewById(R.id.et_add_kategory);
        etAddKategory.setText(label);
        mBuilder.setView(mView)
                .setTitle("Ubah Kategori")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (etAddKategory.getText().toString().isEmpty()) {
                            Toast.makeText(context, "Nama item harus diisi", Toast.LENGTH_SHORT).show();

                        } else {
                            DbHelper dbHelper = new DbHelper(context);
                            final String urldatabase = dbHelper.getUrlServer();
                            ServiceGenerator serviceGenerator = new ServiceGenerator();
                            RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
                            if (idKategory == 1) {
                                Call<Value> call = api.update_income(listKategory.get(pos).getId(),
                                        etAddKategory.getText().toString());
                                call.enqueue(new Callback<Value>() {
                                    @Override
                                    public void onResponse(Call<Value> call, Response<Value> response) {
                                        Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<Value> call, Throwable t) {
                                        Toast.makeText(context, "Error jaringan " + t, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else if (idKategory == 2) {
                                Call<Value> call = api.update_outcome(listKategory.get(pos).getId(),
                                        etAddKategory.getText().toString());
                                call.enqueue(new Callback<Value>() {
                                    @Override
                                    public void onResponse(Call<Value> call, Response<Value> response) {
                                        Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<Value> call, Throwable t) {
                                        Toast.makeText(context, "Error jaringan " + t, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            listString.set(pos, etAddKategory.getText().toString());
                            notifyDataSetChanged();
                        }

                    }
                });
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void openDialogHapus(final int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Perhatian !!!");
        alert.setMessage("Apa anda yakin akan menghapus '"+listString.get(pos)+"'?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                String id = listKategory.get(pos).getId();
                String category = listKategory.get(pos).getCategory();
                DbHelper dbHelper = new DbHelper(context);
                final String urldatabase = dbHelper.getUrlServer();
                ServiceGenerator serviceGenerator = new ServiceGenerator();
                RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
                if (idKategory == 1){
                    Call<Value> call = api.delete_income(id,category);
                    call.enqueue(new Callback<Value>() {
                        @Override
                        public void onResponse(Call<Value> call, Response<Value> response) {
                            Toast.makeText(context, listString.get(pos)+" terhapus", Toast.LENGTH_SHORT).show();
                            listString.remove(pos);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<Value> call, Throwable t) {
                            Toast.makeText(context, "error gagal hapus", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.dismiss();
                } else if (idKategory == 2) {
                    Call<Value> call = api.delete_outcome(id,category);
                    call.enqueue(new Callback<Value>() {
                        @Override
                        public void onResponse(Call<Value> call, Response<Value> response) {
                            Toast.makeText(context, listString.get(pos)+" terhapus", Toast.LENGTH_SHORT).show();
                            listString.remove(pos);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<Value> call, Throwable t) {
                            Toast.makeText(context, "error gagal hapus", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.dismiss();
                }


            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();
    }
}
