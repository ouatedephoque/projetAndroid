package ch.hearc.arcootest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by jeshon.assuncao on 05.11.2015.
 */
public class activity_calculatorInfo extends Activity {
    public final String EXTRA_DRINKS = "DRINKS";
    public final String EXTRA_SEX = "SEX";
    public final String EXTRA_WEIGHT = "WEIGHT";

    private RadioGroup sexGroup;
    private RadioButton sexButton;
    private EditText weight;
    private ImageButton addDrink;
    private ListView listViewOfDrinked;
    private List<Drink> listOfDrinks; // List of all drinks
    private ArrayList<Drink> listOfDrinked; // List of all drinks drinked
    private ListAdapterDrinks adapterDrinked; // Adapter for listView of cup drinked
    private Button calculate;

    /* variable use in dialog*/
    private Spinner typeOfDrink;
    private EditText volOfAlcool;
    private EditText volIngurgited;
    private TimePicker hourDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_info);

        sexGroup = (RadioGroup)findViewById(R.id.rg_sex);
        sexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sexButton = (RadioButton) findViewById(checkedId);
            }
        });
        weight = (EditText)findViewById(R.id.editText_weight);
        addDrink = (ImageButton)findViewById(R.id.btn_addDrink);
        addDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogDrinks();
            }
        });
        listViewOfDrinked = (ListView)findViewById(R.id.listView_drinks);
        listOfDrinked = ReadDrinks(this);

        adapterDrinked = new ListAdapterDrinks(this, R.layout.drink_list_row, listOfDrinked);
        listViewOfDrinked.setAdapter(adapterDrinked);

        /* Method called when we click on a drink into the ListView */
        listViewOfDrinked.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapter, View view, int position, long id) {
                final int pos = position;
                final Drink drink = (Drink)adapter.getItemAtPosition(pos);

                /* Create a dialog box for delete the drink of the ListView */
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_calculatorInfo.this);

                builder.setMessage("Voulez-vous supprimer la boisson : " + drink.getName() + " ?");

                /* When we click on "yes" for deleting the drink */
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(pos);
                    }
                });

                /* When we click on "No" for deleting the drink */
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        /* Get all the drinks writed into the XML file */
        listOfDrinks = readXML();

        calculate = (Button)findViewById(R.id.btn_calculator_calculate);

        /* Method called when we click on "Calculate" button */
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(sexButton == null || weight.getText().toString().isEmpty())
                {
                    Toast.makeText(activity_calculatorInfo.this, "Veuillez entrez toutes les informatios nécessaires", Toast.LENGTH_LONG).show();
                }
                else
                {
                    /* Write the list of drinked into a txt file */
                    WriteDrinks(activity_calculatorInfo.this, listOfDrinked);

                    /* Call a new activity for show the result */
                    Intent intent = new Intent(activity_calculatorInfo.this, activity_calculatorResult.class);
                    intent.putParcelableArrayListExtra(EXTRA_DRINKS, listOfDrinked);
                    intent.putExtra(EXTRA_SEX, sexButton.getText().toString());
                    intent.putExtra(EXTRA_WEIGHT, Float.parseFloat(weight.getText().toString()));
                    startActivity(intent);
                }
            }
        });
    }

    private void createDialogDrinks()
    {
        final Dialog dialog = new Dialog(activity_calculatorInfo.this); // Create custom dialog object
        dialog.setContentView(R.layout.dialog_calculator_drink); // Include dialog_calculator_drink.xml file
        dialog.setTitle("Qu'avez-vous bu ?"); // Set dialog title

        // set values for custom dialog components
        typeOfDrink = (Spinner) dialog.findViewById(R.id.spinner_drinks);
        volOfAlcool = (EditText) dialog.findViewById(R.id.editText_volume);
        volIngurgited = (EditText) dialog.findViewById(R.id.editText_volumeIngurgited);
        hourDrink = (TimePicker) dialog.findViewById(R.id.timePicker_hourDrink);
        hourDrink.setIs24HourView(true);
        hourDrink.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        typeOfDrink.setAdapter(createAdapter()); // Create and set the adapter

        dialog.show();

        typeOfDrink.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                volOfAlcool.setText(listOfDrinks.get(position).getVolume() + "");
                volIngurgited.setText(listOfDrinks.get(position).getQuantity() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {/*Nothing*/}
        });

        Button confirmButton = (Button) dialog.findViewById(R.id.btn_calculator_validDrink);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = typeOfDrink.getSelectedItem().toString();
                float volume = Float.parseFloat(volOfAlcool.getText().toString());
                float quantity = Float.parseFloat(volIngurgited.getText().toString());
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                date.setHours(hourDrink.getCurrentHour());
                date.setMinutes(hourDrink.getCurrentMinute());
                calendar.setTime(date);

                addItem(new Drink(name, volume, quantity, calendar));

                dialog.dismiss(); // Close dialog
            }
        });

        Button declineButton = (Button) dialog.findViewById(R.id.btn_calculator_declineDrink);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close dialog
            }
        });
    }

    public void addItem(Drink drink) {
        listOfDrinked.add(drink);
        adapterDrinked.notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        adapterDrinked.remove(listOfDrinked.get(position));
        listViewOfDrinked.setAdapter(adapterDrinked);
    }

    private ArrayAdapter createAdapter()
    {
        List<String> listNameOfDrinks = new ArrayList<String>(listOfDrinks.size());
        for(Drink object: listOfDrinks){
            listNameOfDrinks.add(object.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listNameOfDrinks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    private List<Drink> readXML()
    {
        // Needed for saving the data in a Drink object
        String nameOfDrink = "";
        float volume = -1;
        float quantity = -1;

        List<Drink> listOfDrinks = new ArrayList<Drink>();

        // Parsing
        try {
            // Create the XmlPullParser object
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();

            // Open the file to read
            InputStream file = activity_calculatorInfo.this.getAssets().open("alcools.xml");
            myparser.setInput(file, null);

            // parse the XML
            int event = myparser.getEventType();
            String currentName = "";

            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name = myparser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        currentName = name;
                        break;

                    case XmlPullParser.END_TAG:
                        currentName = "";

                        if(!nameOfDrink.equals("") && volume > 0 && quantity > 0)
                        {
                            listOfDrinks.add(new Drink(nameOfDrink, volume, quantity));

                            nameOfDrink = "";
                            volume = -1;
                            quantity = -1;
                        }

                        break;

                    case XmlPullParser.TEXT:
                        if("name".equals(currentName))
                        {
                            nameOfDrink = myparser.getText();
                        }
                        else if("vol".equals(currentName))
                        {
                            volume = Float.parseFloat(myparser.getText());
                        }
                        else if("qte".equals(currentName))
                        {
                            quantity = Float.parseFloat(myparser.getText());
                        }

                        break;
                }
                event = myparser.next();
            }

        } catch (XmlPullParserException e) {
            Toast.makeText(activity_calculatorInfo.this, "XML problem", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(activity_calculatorInfo.this, "Impossible de lire", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

        if(!listOfDrinks.isEmpty())
        {
            return listOfDrinks;
        }
        return null;
    }

    public void WriteDrinks(Context context, List<Drink> list)
    {
        String data = "";

        for (Drink drink:list)
        {
            data += drink.getName() + ";";
            data += drink.getVolume() + ";";
            data += drink.getQuantity() + ";";
            data += drink.getTimeDrink().getTime().toString();
            data += "\n";
        }

        FileOutputStream fout = null;
        OutputStreamWriter osw = null;

        try{
            fout = context.openFileOutput("drinksBackup.txt", MODE_PRIVATE);
            osw = new OutputStreamWriter(fout);
            osw.write(data);
            osw.flush();

            Log.i("information", "Drinks saved ");
        }
        catch (Exception e) {
            Log.e("error", "Drinks not saved ");
        }
        finally {
            try {
                osw.close();
                fout.close();
            } catch (IOException e) {
                Log.e("error", "Buffer not closed");
            }
        }
    }

    public ArrayList<Drink> ReadDrinks(Context context){
        ArrayList<Drink> listToReturn = new ArrayList<Drink>();

        InputStream fin = null;

        try{
            fin = context.openFileInput("drinksBackup.txt");

            String data = "";

            // prepare the file for reading
            InputStreamReader isr = new InputStreamReader(fin);
            Scanner br = new Scanner(isr);

            while(br.hasNext()){
                data = br.nextLine();

                String[] drinkArray = data.split(";");

                if(drinkArray.length == 4)
                {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                    calendar.setTime(sdf.parse(drinkArray[3])); // all done

                    Drink drink = new Drink(drinkArray[0], Float.parseFloat(drinkArray[1]), Float.parseFloat(drinkArray[2]), calendar);
                    listToReturn.add(drink);
                }
            };

            Log.i("information", "Drinks readed");

            isr.close();
            fin.close();
        }
        catch (Exception e) {
            Log.e("error", "Drinks not readed");
            return new ArrayList<Drink>();
        }


        return listToReturn;
    }
}
