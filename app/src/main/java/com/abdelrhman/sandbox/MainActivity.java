package com.abdelrhman.sandbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abdelrhman.validator.annotaions.Max;
import com.abdelrhman.validator.annotaions.Min;
import com.abdelrhman.validator.annotaions.NotEmpty;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @NotEmpty(errorMessage = "Enter Text")
    EditText edittext0;
    @Min(2)
    EditText edittext1;
    @Min(value = 3, errorMessage = "text must be at least 3 chars long")
    @Max(10)
    EditText edittext2;
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
                if (MainActivity_Validator.validate(MainActivity.this)) {
                    // do something
                    Toast.makeText(MainActivity.this, "Yay! It's valid", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
