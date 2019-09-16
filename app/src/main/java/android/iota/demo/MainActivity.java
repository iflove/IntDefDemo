package android.iota.demo;

import android.iota.demo.def.CodeDef;
import android.iota.demo.def.RequestIdDef;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.lang.annotation.iota.IOTA;


@IOTA
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "login: " + RequestIdDef.LOGIN);
        Log.i(TAG, "logout: " + RequestIdDef.LOGOUT);
        Log.i(TAG, "home: " + RequestIdDef.HOME);

//        Log.i(TAG, "one: " + IntConst.ONE);
        Log.i(TAG, "login: " + CodeDef.LOGIN);
    }
}
