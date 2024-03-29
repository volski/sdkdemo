package com.zhj.bluetooth.sdkdemo.ui;

import android.text.TextUtils;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhj.bluetooth.sdkdemo.R;
import com.zhj.bluetooth.sdkdemo.base.BaseActivity;
import com.zhj.bluetooth.sdkdemo.ui.adapter.SleepsHistoryAdapter;
import com.zhj.bluetooth.sdkdemo.ui.adapter.StepsHistoryAdapter;
import com.zhj.zhjsdkcustomized.bean.HealthSleepItem;
import com.zhj.zhjsdkcustomized.bean.HealthSportItem;
import com.zhj.zhjsdkcustomized.ble.BleCallback;
import com.zhj.zhjsdkcustomized.ble.BleSdkWrapper;
import com.zhj.zhjsdkcustomized.ble.HandlerBleDataResult;
import com.zhj.zhjsdkcustomized.ble.bluetooth.OnLeWriteCharacteristicListener;
import com.zhj.zhjsdkcustomized.ble.bluetooth.exception.WriteBleException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RestingCalorieActivity extends BaseActivity {

    @BindView(R.id.etHeight)
    EditText etHeight;
    @BindView(R.id.etWeight)
    EditText etWeight;
    @BindView(R.id.etSex)
    EditText etSex;
    @BindView(R.id.etAge)
    EditText etAge;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;


    @Override
    protected int getContentView() {
        return R.layout.activity_resting_cal;
    }

    @Override
    protected void initView() {
        super.initView();
        titleName.setText(getResources().getString(R.string.cal_rating_title));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.btnSub)
    void submitData(){
        String height = etHeight.getText().toString();
        String weight = etWeight.getText().toString();
        String age = etAge.getText().toString();
        String sex = etSex.getText().toString();
        if(TextUtils.isEmpty(height) || TextUtils.isEmpty(weight) ||
                TextUtils.isEmpty(age) || TextUtils.isEmpty(sex)){
            showToast(getResources().getString(R.string.toast_info));
            return;
        }
        sportItemsAll.clear();
        syncStepHistory(true,Integer.parseInt(height),Integer.parseInt(weight),Integer.parseInt(age),Integer.parseInt(sex));
    }
    List<HealthSportItem> sportItemsAll = new ArrayList<>();//Historical step data
    List<HealthSleepItem> sleepItemsAll = new ArrayList<>();//History sleep
    Calendar calendar;
    StepsHistoryAdapter stepsHistoryAdapter;

    //Historical sleep data (rest calculated calorie data SEX: 0 male 1 female height: cm unit weight: KG unit)
    private void syncStepHistory(boolean isFirst,int height,int weight,int age,int sex){
        if (isFirst) {
            calendar = Calendar.getInstance();
        }
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH)+1;
        final int day=calendar.get(Calendar.DATE);
    }
}
