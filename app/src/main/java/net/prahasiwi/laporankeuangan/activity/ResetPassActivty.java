package net.prahasiwi.laporankeuangan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
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

public class ResetPassActivty extends AppCompatActivity {

    EditText etEmail, etPass, etConfirmPass;
    Button btnReset;
    CheckBox cbxShowPass;
    String setUrl;
    ScrollView scrollView;
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass_activty);

        scrollView =findViewById(R.id.scrollview);
        etEmail = findViewById(R.id.reset_emailid);
        etPass = findViewById(R.id.reset_password);
        etConfirmPass = findViewById(R.id.reset_confirmpassword);
        btnReset = findViewById(R.id.resetBtn);
        cbxShowPass = findViewById(R.id.cbx_show_pass);
        dbHelper = new DbHelper(ResetPassActivty.this);
        setUrl = dbHelper.getUrlServer();

        cbxShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    cbxShowPass.setText(R.string.hide_pwd);
                    etPass.setInputType(InputType.TYPE_CLASS_TEXT);
                    etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                    etConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT);
                    etConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                } else {
                    cbxShowPass.setText(R.string.show_pwd);
                    etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password
                    etConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password
                }
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
    }

    private void checkValidation() {
        String getEmailId = etEmail.getText().toString();
        String getPassword = etPass.getText().toString();
        String getConfirmPassword = etConfirmPass.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(getEmailId);
        // Check if all strings are null or not
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("") || getConfirmPassword.length() == 0) {

            Snackbar.make(scrollView, "Incomplete Field, try again.", Snackbar.LENGTH_LONG).show();

            // Check if email id valid or not
        } else if (!m.find()) {

            Snackbar.make(scrollView, "Your Email invalid.", Snackbar.LENGTH_LONG).show();
            // Check if both password should be equal
        } else if (!getConfirmPassword.equals(getPassword)) {
            Snackbar.make(scrollView, "Both password doesn't match.", Snackbar.LENGTH_LONG).show();
            // Make sure user should check Terms and Conditions checkbox
        } else {
            loadserver();
        }

    }

    private void loadserver() {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();
        String confpass = etConfirmPass.getText().toString();
        if (!pass.equals(confpass)) {
            Toast.makeText(ResetPassActivty.this, "Konfirmasi Password Salah", Toast.LENGTH_SHORT).show();
        } else {
            ServiceGenerator serviceGenerator = new ServiceGenerator();
            RegisterAPI api = serviceGenerator.getClient(setUrl).create(RegisterAPI.class);
            Call<Value> call = api.reset(email, pass);
            call.enqueue(new Callback<Value>() {
                @Override
                public void onResponse(Call<Value> call, Response<Value> response) {
                    String message = response.body().getMessage();
                    if (response.body().getValue().equals("1")) {
                        Intent intent = new Intent(ResetPassActivty.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {

                    }
                    Toast.makeText(ResetPassActivty.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Value> call, Throwable t) {
                    Toast.makeText(ResetPassActivty.this, "Jaringan Error ...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

