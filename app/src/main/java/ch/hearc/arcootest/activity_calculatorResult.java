package ch.hearc.arcootest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jeshon.assuncao on 03.12.2015.
 */
public class activity_calculatorResult extends Activity {
    public final String EXTRA_DRINKS = "DRINKS";
    public final String EXTRA_SEX = "SEX";
    public final String EXTRA_WEIGHT = "WEIGHT";
    private static final int REQUEST_CODE = 12;
    private static final String EXTRA_RESULT = "TO_DO_CODE";
    public final double COEF_MEN = 0.7;
    public final double COEF_GIRL = 0.6;
    public final double COEF_MUL = 0.8;
    public final double COEF_ABSORPTION_MIN = 0.0025;

    private TimePicker hourNow;
    private TextView alcoholCount;
    private TextView moral;
    private Button goBackToNight;
    private Button goBackToMain;
    private Button newNight;

    private List<Drink> listOfDrinks;
    private String sex;
    private float weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_result);

        hourNow = (TimePicker)findViewById(R.id.timePicker_hourNow);
        hourNow.setIs24HourView(true);
        hourNow.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        hourNow.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calculAlcohol();
            }
        });

        alcoholCount = (TextView)findViewById(R.id.tv_alcoholCount);
        moral = (TextView)findViewById(R.id.tv_moral);

        goBackToNight = (Button)findViewById(R.id.btn_calculator_goBackToNight);
        goBackToNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult(1);
            }
        });

        goBackToMain= (Button)findViewById(R.id.btn_calculator_accueil);
        goBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult(0);
            }
        });

        newNight= (Button)findViewById(R.id.btn_calculator_newNight);
        newNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult(2);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listOfDrinks = extras.getParcelableArrayList(EXTRA_DRINKS);
            sex = extras.getString(EXTRA_SEX);
            weight = extras.getFloat(EXTRA_WEIGHT);
            calculAlcohol();
        }
    }

    private void calculAlcohol()
    {
        double taux_i = 0;
        double tauxTot = 0;

        for (int i=0; i<listOfDrinks.size(); i++)
        {
            Drink drink = listOfDrinks.get(i);

            taux_i = drink.getQuantity() * drink.getVolume() * COEF_MUL;
            if(sex.equals("Homme"))
            {
                taux_i /= COEF_MEN * weight;
            }
            else if(sex.equals("Femme"))
            {
                taux_i /= COEF_GIRL * weight;
            }

            /* Add the rate of the drink to the total rate */
            tauxTot += taux_i;
        }

        double alcoholAbsorbed = susbtractAlcoholAbsorbed(tauxTot);
        tauxTot -= alcoholAbsorbed;

        NumberFormat nf = new DecimalFormat("#.###");
        alcoholCount.setText(nf.format(tauxTot) + " g/l");
        showMoral(tauxTot);
    }

    public double susbtractAlcoholAbsorbed(double tauxTot)
    {
        /* Get the difference between de hour of drink and now in minutes */
        /*Date date = new Date();
        date.setHours(hourNow.getCurrentHour());
        date.setMinutes(hourNow.getCurrentMinute());
        long difference_ms = date.getTime() - drink.getTimeDrink().getTimeInMillis();
        long difference_min = TimeUnit.MILLISECONDS.toMinutes(difference_ms);*/

        /* Subtraction alcohol absorbed by the body during the time elapsed  */
        /*double alcoholAbsorbed = difference_min * COEF_ABSORPTION_MIN;
        if(taux_i - alcoholAbsorbed >= 0)
        {
            taux_i -= difference_min * COEF_ABSORPTION_MIN;
        }
        else
        {
            taux_i = 0;
        }*/

        return 0.0;
    }

    private void showMoral(double tauxTot)
    {
        if(tauxTot >= 0 && tauxTot <= 0.1)
        {
            moral.setText(getString(R.string.moral_0));
        }
        else if(tauxTot > 0.1 && tauxTot <= 0.4)
        {
            moral.setText(getString(R.string.moral_1));
        }
        else if(tauxTot > 0.4 && tauxTot <= 0.7)
        {
            moral.setText(getString(R.string.moral_2));
        }
        else if(tauxTot > 0.7 && tauxTot <= 0.9)
        {
            moral.setText(getString(R.string.moral_3));
        }
        else if(tauxTot > 0.9 && tauxTot <= 1.2)
        {
            moral.setText(getString(R.string.moral_4));
        }
        else if(tauxTot > 1.2 && tauxTot <= 1.6)
        {
            moral.setText(getString(R.string.moral_5));
        }
        else if(tauxTot > 1.6 && tauxTot <= 2.0)
        {
            moral.setText(getString(R.string.moral_6));
        }
        else
        {
            moral.setText(getString(R.string.moral_7));
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
