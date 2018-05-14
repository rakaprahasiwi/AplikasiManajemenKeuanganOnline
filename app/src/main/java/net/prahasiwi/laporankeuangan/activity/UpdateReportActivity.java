package net.prahasiwi.laporankeuangan.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.api.RegisterAPI;
import net.prahasiwi.laporankeuangan.api.ServiceGenerator;
import net.prahasiwi.laporankeuangan.helper.BitmapHelper;
import net.prahasiwi.laporankeuangan.helper.DbHelper;
import net.prahasiwi.laporankeuangan.model.Category;
import net.prahasiwi.laporankeuangan.model.Value;
import net.prahasiwi.laporankeuangan.model.ValueCategory;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.prahasiwi.laporankeuangan.helper.Constant.DATE;
import static net.prahasiwi.laporankeuangan.helper.Constant.DESCRIBE;
import static net.prahasiwi.laporankeuangan.helper.Constant.ID;
import static net.prahasiwi.laporankeuangan.helper.Constant.KEYIMAGE;
import static net.prahasiwi.laporankeuangan.helper.Constant.PATTERN_TV;
import static net.prahasiwi.laporankeuangan.helper.Constant.VALUE;
import static net.prahasiwi.laporankeuangan.helper.Constant.addTitik;
import static net.prahasiwi.laporankeuangan.helper.Constant.changeFormatToDatabase;
import static net.prahasiwi.laporankeuangan.helper.Constant.changeFormatToView;

public class UpdateReportActivity extends AppCompatActivity {

    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    Calendar dateTime;
    ProgressDialog progress;
    ImageView imageProfile, ivDate;
    Bitmap bitmap;
    Spinner spCategory, spTipe;
    EditText etSum, etDescribe;
    TextView tvDate, tvSum;
    FloatingActionButton fabAddimage;
    Button btnSave;
    String urldatabase, email, id;
    DbHelper dbHelper;
    List<Category> results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ubah Transaksi Lalu");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        //init
        dbHelper = new DbHelper(this);
        urldatabase = dbHelper.getUrlServer();
        email = dbHelper.getEmailDb();

        imageProfile = findViewById(R.id.iv_image);
        ivDate = findViewById(R.id.iv_button_date);
        spCategory = findViewById(R.id.spinner_category);
        spTipe = findViewById(R.id.spinner_type);
        etSum = findViewById(R.id.et_sum);
        tvSum = findViewById(R.id.tv_sum);
        etDescribe = findViewById(R.id.et_describe);
        tvDate = findViewById(R.id.tv_date);
        fabAddimage = findViewById(R.id.fab_addphoto);
        btnSave = findViewById(R.id.btn_save);

        Intent intent = getIntent();
        String keyImage = intent.getStringExtra(KEYIMAGE); // 0 or 1
        if (keyImage.equals("0")){
            imageProfile.setImageResource(R.drawable.empty_profile);
            bitmap = null;
        }else {
            bitmap = BitmapHelper.getInstance().getBitmap();
            imageProfile.setImageBitmap(bitmap);
        }
        id = intent.getStringExtra(ID);
        String value = intent.getStringExtra(VALUE);
        String date = intent.getStringExtra(DATE);
        String describe = intent.getStringExtra(DESCRIBE);

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date datess = null;
        try {
            datess = (Date)formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateTime = Calendar.getInstance();
        dateTime.setTime(datess);//set the calendar date to your date

        etSum.setText(value);
        tvSum.setText("Rp. "+addTitik(value));
        etDescribe.setText(describe);
        tvDate.setText(changeFormatToView(date));

        loadSpinnerTipe();
        etSum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvSum.setText("Rp. " + addTitik("" + charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        fabAddimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        ivDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UpdateReportActivity.this, d, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAction();
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    BitmapHelper.getInstance().setBitmap(bitmap);
                    Intent i = new Intent(UpdateReportActivity.this, FullScreenActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel();
        }
    };


    private void updateTextLabel() {

        SimpleDateFormat dfDay = new SimpleDateFormat(PATTERN_TV);
        String formattedDateDay = dfDay.format(dateTime.getTime());
        tvDate.setText(formattedDateDay);
    }

    private void loadSpinnerTipe() {
        final List<String> tipe = new ArrayList<>();
        tipe.add("Pemasukan");
        tipe.add("Pengeluaran");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipe);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipe.setAdapter(dataAdapter);
        spTipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = tipe.get(i);
                //  editTextDeskripsi.setText(text);
                if (i == 0) {
                    loadSpinnerIncome();
                } else if (i == 1) {
                    loadSpinnerOutcome();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private void loadSpinnerIncome() {
        progress = new ProgressDialog(UpdateReportActivity.this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
        Call<ValueCategory> call = api.read_income(email);
        call.enqueue(new Callback<ValueCategory>() {
            @Override
            public void onResponse(Call<ValueCategory> call, Response<ValueCategory> response) {
                progress.dismiss();
                results = response.body().getResult();
                List<String> categories = new ArrayList<>();
                if (results.size() > 0) {
                    for (int i = 0; i < results.size(); i++) {
                        categories.add(results.get(i).getCategory());
                    }

                } else {
                    categories.add("Gaji");
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(UpdateReportActivity.this, android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onFailure(Call<ValueCategory> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(UpdateReportActivity.this, "Error load category", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSpinnerOutcome() {
        progress = new ProgressDialog(UpdateReportActivity.this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
        Call<ValueCategory> call = api.read_outcome(email);
        call.enqueue(new Callback<ValueCategory>() {
            @Override
            public void onResponse(Call<ValueCategory> call, Response<ValueCategory> response) {
                progress.dismiss();
                results = response.body().getResult();
                List<String> categories = new ArrayList<>();
                if (results.size() > 0) {
                    for (int i = 0; i < results.size(); i++) {
                        categories.add(results.get(i).getCategory());
                    }

                } else {
                    categories.add("Makanan");
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(UpdateReportActivity.this, android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onFailure(Call<ValueCategory> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(UpdateReportActivity.this, "Error load category", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Kamera", "Galery", "Hapus Image", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateReportActivity.this);
        builder.setTitle("Atur Gambar");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Kamera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[i].equals("Galery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[i].equals("Hapus Image")) {
                    imageProfile.setImageResource(R.drawable.empty_profile);
                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {

            Bundle bundle = data.getExtras();
            final Bitmap bmp = (Bitmap) bundle.get("data");
            imageProfile.setImageBitmap(getResizedBitmap(bmp, 1080));
            bitmap = getResizedBitmap(bmp, 500);

        } else if (requestCode == SELECT_FILE) {

            try {

                Uri selectedImageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                imageProfile.setImageBitmap(getResizedBitmap(bmp, 1080));

                bitmap = getResizedBitmap(bmp, 500);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }


    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void saveAction() {
        final String value = etSum.getText().toString();
        final String description = etDescribe.getText().toString();
        if (value.trim().equals("") || Integer.parseInt(value) < 1) {
            Toast.makeText(UpdateReportActivity.this, "Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show();

        } else {
            if (description.equals("")) {
                Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                Bitmap bmp = null;//BitmapFactory.decodeResource(getResources(), R.drawable.empty_profile);
                String encodedImage = null;//Base64.encodeToString(byteArray, Base64.DEFAULT);
                if (bitmap != null) {
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream1);
                    byte[] byteArray = stream1.toByteArray();
                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
                final String type = spTipe.getSelectedItem().toString();
                final String category = spCategory.getSelectedItem().toString();
                final String date = changeFormatToDatabase(tvDate.getText().toString());

                progress = new ProgressDialog(UpdateReportActivity.this);
                progress.setCancelable(false);
                progress.setMessage("Loading ...");
                progress.show();
                ServiceGenerator serviceGenerator = new ServiceGenerator();
                RegisterAPI api = serviceGenerator.getClient(urldatabase).create(RegisterAPI.class);
                Call<Value> call = api.update(
                        ""+id,
                        "" + type,
                        encodedImage + "",
                        value + "",
                        category + "",
                        description + "",
                        email + "",
                        date + "");
                call.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        progress.dismiss();
                        if (response.body().getValue().equals("1")) {
                            Toast.makeText(UpdateReportActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateReportActivity.this, ReportTransactionActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                            startActivity(intent);
                        } else if (response.body().getValue().equals("0")) {
                            Toast.makeText(UpdateReportActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        progress.dismiss();
                        Toast.makeText(UpdateReportActivity.this, "" + t, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
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
