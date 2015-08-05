package com.wzq.wheel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.wzq.wheel.models.Address;
import com.github.wzq.wheel.utils.WheelHelper;


public class MainActivity extends AppCompatActivity {

    private WheelHelper wheelHelper;

    private EditText edit1, edit2, edit3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);
        edit3 = (EditText) findViewById(R.id.edit3);
        FrameLayout layout = (FrameLayout) findViewById(R.id.container);
        wheelHelper = new WheelHelper(this, "province_data");
        layout.addView(wheelHelper.getView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickSet(View view){
        wheelHelper.setCurrent(WheelHelper.TYPE_PROVINCE, edit1.getText().toString());
        wheelHelper.setCurrent(WheelHelper.TYPE_CITY, edit2.getText().toString());
        wheelHelper.setCurrent(WheelHelper.TYPE_DISTRICT, edit3.getText().toString());
    }

    public void onClickShow(View view){
        Address address = wheelHelper.getCurrent();
        Toast.makeText(this, address.getProvince()+ " " + address.getCity()+" "+address.getDistrict(), Toast.LENGTH_LONG).show();
    }
}
