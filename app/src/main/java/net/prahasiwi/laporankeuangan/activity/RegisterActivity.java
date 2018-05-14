package net.prahasiwi.laporankeuangan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.prahasiwi.laporankeuangan.R;
import net.prahasiwi.laporankeuangan.api.RegisterAPI;
import net.prahasiwi.laporankeuangan.api.ServiceGenerator;
import net.prahasiwi.laporankeuangan.helper.DbHelper;
import net.prahasiwi.laporankeuangan.model.Value;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.prahasiwi.laporankeuangan.helper.Constant.regEx;

public class RegisterActivity extends AppCompatActivity {

    ProgressDialog progress;
    DbHelper dbHelper;
    String setUrl;
    private EditText fullName, emailId, mobileNumber, password, confirmPassword;
    private TextView login;
    private Button signUpButton;
    private CheckBox terms_conditions;
    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        scrollView = (ScrollView) findViewById(R.id.scrollview);
        fullName = (EditText) findViewById(R.id.fullName);
        emailId = (EditText) findViewById(R.id.userEmailId);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        signUpButton = (Button) findViewById(R.id.signUpBtn);
        login = (TextView) findViewById(R.id.already_user);
        terms_conditions = (CheckBox) findViewById(R.id.terms_conditions);
        dbHelper = new DbHelper(RegisterActivity.this);
        setUrl = dbHelper.getUrlServer();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
    }
    private void checkValidation(){
        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0) {

            Snackbar.make(scrollView, "Your Email invalid.", Snackbar.LENGTH_LONG).show();

            // Check if email id valid or not
        }else if (!m.find()) {

            Snackbar.make(scrollView, "Your Email invalid.", Snackbar.LENGTH_LONG).show();
            // Check if both password should be equal
        } else if (!getConfirmPassword.equals(getPassword)) {
            Snackbar.make(scrollView, "Both password doesn't match.", Snackbar.LENGTH_LONG).show();
            // Make sure user should check Terms and Conditions checkbox
        } else if (!terms_conditions.isChecked()) {
            Snackbar.make(scrollView, "Please select Terms and Conditions.", Snackbar.LENGTH_LONG).show();
            // Else do signup or do your stuff
        } else {
            loadserver();
        }

    }

    private void loadserver() {
        progress = new ProgressDialog(RegisterActivity.this);
        progress.setCancelable(false);
        progress.setMessage("Loading ...");
        progress.show();
        String username = fullName.getText().toString();
        String email = emailId.getText().toString();
        String phone = mobileNumber.getText().toString();
        String pass = password.getText().toString();
        String confirmpass = confirmPassword.getText().toString();

        if (username.equals("") || pass.equals("") || email.equals("")) {
            progress.dismiss();
            Toast.makeText(RegisterActivity.this, "MainData ada yang kosong", Toast.LENGTH_SHORT).show();
        } else {
            if (!pass.equals(confirmpass)) {
                progress.dismiss();
                Toast.makeText(RegisterActivity.this, "Konfirmasi Passwor Salah", Toast.LENGTH_SHORT).show();
            } else {

                ServiceGenerator serviceGenerator = new ServiceGenerator();
                RegisterAPI api = serviceGenerator.getClient(setUrl).create(RegisterAPI.class);
                Call<Value> call = api.register(username, email, pass, phone);
                call.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        progress.dismiss();
                        String message = response.body().getMessage();
                        if (response.body().getValue().equals("1")) {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();;
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        progress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Jaringan error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

}