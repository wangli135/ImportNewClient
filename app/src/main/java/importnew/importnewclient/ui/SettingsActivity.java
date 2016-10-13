package importnew.importnewclient.ui;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import importnew.importnewclient.R;
import importnew.importnewclient.utils.Constants;


public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Switch saveFlowSwitch;
    private TextView feadBackTv;
    private LinearLayout checkUpdateLl;
    private TextView abountTv;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getPreferences(MODE_PRIVATE);

        initViews();
        initListeners();
    }

    private void initViews() {

        saveFlowSwitch = (Switch) findViewById(R.id.save_flow_switch);
        saveFlowSwitch.setChecked(sharedPreferences.getBoolean(Constants.Key.IS_SAVE_FLOW_MODE, false));
        feadBackTv = (TextView) findViewById(R.id.feadback);
        checkUpdateLl = (LinearLayout) findViewById(R.id.check_update);
        abountTv = (TextView) findViewById(R.id.abount);

    }

    private void initListeners() {

        saveFlowSwitch.setOnCheckedChangeListener(this);
        checkUpdateLl.setOnClickListener(this);
        feadBackTv.setOnClickListener(this);
        abountTv.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView.getId() == R.id.save_flow_switch) {
            Constants.IS_SAVE_FLOW = isChecked;
            sharedPreferences.edit().putBoolean(Constants.Key.IS_SAVE_FLOW_MODE, isChecked).apply();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.feadback: {

            }
            break;

            case R.id.check_update: {

            }
            break;

            case R.id.abount: {

            }
            break;

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
