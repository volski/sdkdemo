package com.zhj.bluetooth.sdkdemo.ui;

import static com.zhj.zhjsdkcustomized.ble.BleSdkWrapper.BLUETOOTH_CODE.CODE_GET_HISTORY_TEMP;

import android.bluetooth.BluetoothGattCharacteristic;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhj.bluetooth.sdkdemo.R;
import com.zhj.bluetooth.sdkdemo.base.BaseActivity;
import com.zhj.bluetooth.sdkdemo.ui.adapter.RateHistoryAdapter;
import com.zhj.bluetooth.sdkdemo.ui.adapter.TempHistoryAdapter;
import com.zhj.zhjsdkcustomized.bean.HealthHeartRateItem;
import com.zhj.zhjsdkcustomized.bean.TempInfo;
import com.zhj.zhjsdkcustomized.ble.BleCallback;
import com.zhj.zhjsdkcustomized.ble.BleSdkWrapper;
import com.zhj.zhjsdkcustomized.ble.HandlerBleDataResult;
import com.zhj.zhjsdkcustomized.ble.bluetooth.OnLeWriteCharacteristicListener;
import com.zhj.zhjsdkcustomized.ble.bluetooth.exception.WriteBleException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

public class TempHistoryActivity extends BaseActivity {
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected int getContentView() {
        return R.layout.activity_temp_history;
    }

    @Override
    protected void initView() {
        super.initView();
        titleName.setText(getResources().getString(R.string.main_history_temp));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        syncTempHistory(true);
    }

    List<TempInfo> tempInfoArrayList = new ArrayList<>();
    Calendar calendar;
    //Historical heart rate data
    private void syncTempHistory(boolean isFirst){
        if (isFirst) {
            calendar = Calendar.getInstance();
        }
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH)+1;
        final int day=calendar.get(Calendar.DATE);
        BleSdkWrapper.getHistoryTemp(year, month, day, new OnLeWriteCharacteristicListener() {
            @Override
            public void onSuccess(HandlerBleDataResult handlerBleDataResult) {
                if(handlerBleDataResult.bluetooth_code == CODE_GET_HISTORY_TEMP) {
                    if (handlerBleDataResult.isComplete) {
                        //Is there any more historical data? If so, decrease the number of days by 1 and continue to obtain
                        if (handlerBleDataResult.hasNext) {
                            List<TempInfo> tempInfos = (List<TempInfo>) handlerBleDataResult.data;
                            tempInfoArrayList.addAll(tempInfos);
                            calendar.add(Calendar.DATE, -1);
                            TempHistoryAdapter tempHistoryAdapter = new TempHistoryAdapter(TempHistoryActivity.this, tempInfoArrayList);
                            mRecyclerView.setAdapter(tempHistoryAdapter);
//                        syncTempHistory(false);
                        } else {
                            TempHistoryAdapter tempHistoryAdapter = new TempHistoryAdapter(TempHistoryActivity.this, tempInfoArrayList);
                            mRecyclerView.setAdapter(tempHistoryAdapter);
                        }
                    }
                }
            }

            @Override
            public void onSuccessCharac(BluetoothGattCharacteristic bluetoothGattCharacteristic) {

            }

            @Override
            public void onFailed(WriteBleException e) {

            }
        } );
    }
}
