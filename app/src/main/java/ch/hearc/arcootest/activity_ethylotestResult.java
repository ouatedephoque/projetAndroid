package ch.hearc.arcootest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by jeshon.assuncao on 14.01.2016.
 */
public class activity_ethylotestResult extends Activity{
    public final String EXTRA_LIMIT_EXCEED = "LIMIT_EXCEED";
    private static final int REQUEST_CODE = 12;
    private static final String EXTRA_RESULT = "TO_DO_CODE";

    private int limitExceed;

    private TextView alcoholEstimate;
    private TextView moral;
    private Button goBackToMain;
    private Button newNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ethylotest_result);

        /* Init component */
        alcoholEstimate = (TextView)findViewById(R.id.tv_alcoholEstimate);

        goBackToMain = (Button)findViewById(R.id.btn_ethylotest_accueil);
        goBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult(0);
            }
        });

        newNight = (Button)findViewById(R.id.btn_ethylotest_newNight);
        newNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult(1);
            }
        });

        /* Get the informatiosn from activity_ethylotest */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            limitExceed = extras.getInt(EXTRA_LIMIT_EXCEED);
            showResult();
        }
    }

    private void showResult()
    {
        if(limitExceed <= 5)
        {
            alcoholEstimate.setText("0.0\n"+getString(R.string.moral_0));
        }
        else if(limitExceed <= 10)
        {
            alcoholEstimate.setText("0.1 à 0.4\n"+getString(R.string.moral_1));
        }
        else if(limitExceed <= 15)
        {
            alcoholEstimate.setText("0.4 à 0.7\n"+getString(R.string.moral_2));
        }
        else if(limitExceed <= 20)
        {
            alcoholEstimate.setText("0.7 à 0.9\n"+getString(R.string.moral_3));
        }
        else if(limitExceed <= 25)
        {
            alcoholEstimate.setText("0.9 à 1.2\n"+getString(R.string.moral_4));
        }
        else if(limitExceed <= 30)
        {
            alcoholEstimate.setText("1.2 à 1.6\n"+getString(R.string.moral_5));
        }
        else if(limitExceed <= 35)
        {
            alcoholEstimate.setText("1.6 à 2.0\n"+getString(R.string.moral_6));
        }
        else
        {
            alcoholEstimate.setText("Plus que 2.0\n"+getString(R.string.moral_7));
        }
    }

    private void finishWithResult(int codeResult)
    {
        Bundle conData = new Bundle();
        conData.putInt(EXTRA_RESULT, codeResult);
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);
        finish();
    }
}
