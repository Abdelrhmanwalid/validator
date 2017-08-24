package com.abdelrhman.sandbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.abdelrhman.reflectionvalidator.Validator;
import com.abdelrhman.reflectionvalidator.annotaions.NotEmpty;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @NotEmpty(errorMessage = "Enter Text")
    private EditText edittext0;
    @NotEmpty(errorMessage = "can't accept empty string")
    private EditText edittext1;
    @NotEmpty
    private EditText edittext2;
    private Button validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_main);

        edittext0 = (EditText) findViewById(R.id.edittext0);
        edittext1 = (EditText) findViewById(R.id.edittext1);
        edittext2 = (EditText) findViewById(R.id.edittext2);

        validate = (Button) findViewById(R.id.validate);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validator.validate(MainActivity.this);
            }
        });

    }
}
