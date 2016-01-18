package ch.hearc.arcootest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

/**
 * Created by jeshon.assuncao on 07.01.2016.
 */
public class activity_ethylotest extends Activity implements SensorEventListener {
    public final String EXTRA_LIMIT_EXCEED = "LIMIT_EXCEED";
    private static final int REQUEST_CODE = 12;
    private static final String EXTRA_RESULT = "TO_DO_CODE";

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 40;

    private TextView tv_chronometer;
    private Button btn_startMeasure;
    private Button btn_goToMain;

    private boolean isMeasuring;
    private int limitExceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ethylotest);

        isMeasuring = false;
        limitExceed = 0;

        /* Init component */
        tv_chronometer = (TextView)findViewById(R.id.tv_countDownChrono);
        btn_startMeasure = (Button)findViewById(R.id.btn_ethylotestStartMeasure);
        btn_startMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChrono();
            }
        });
        btn_goToMain = (Button)findViewById(R.id.btn_ethylotestGoToMain);
        btn_goToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* Init the Senson Manager */
        senSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, senSensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER && isMeasuring)
        {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if((curTime - lastUpdate) > 100)
            {
                long diffTime = curTime - lastUpdate;
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                Log.d("speed : ", speed+"");

                if(speed > SHAKE_THRESHOLD)
                {
                    limitExceed++;
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startChrono()
    {
        CountDownTimer cT =  new CountDownTimer(11000, 1000) {
            public void onTick(long millisUntilFinished) {
                String v = String.format("%02d", millisUntilFinished/60000);
                int va = (int)( (millisUntilFinished%60000)/1000);
                tv_chronometer.setText(v + ":" + String.format("%02d", va));
            }

            public void onFinish()
            {
                /* Call a new activity for show the result */
                Intent intent = new Intent(activity_ethylotest.this, activity_ethylotestResult.class);
                intent.putExtra(EXTRA_LIMIT_EXCEED, limitExceed);
                startActivityForResult(intent, REQUEST_CODE);
            }
        };
        cT.start();
        isMeasuring = true;
        btn_startMeasure.setText("Mesure en cours, veuillez patientez...");
        btn_startMeasure.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_CODE:
                if(resultCode == RESULT_OK)
                {
                    Bundle res = data.getExtras();
                    int result = res.getInt(EXTRA_RESULT);

                    if(result == 0)
                    {
                        finish();
                    }
                    else
                    {
                        isMeasuring = false;
                        tv_chronometer.setText("00:10");
                        btn_startMeasure.setText("Commencer la mesure");
                        btn_startMeasure.setEnabled(true);
                    }
                }
                break;
        }
    }
}