package ch.hearc.arcootest;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jeshon.assuncao on 05.11.2015.
 */
public class activity_calculatorInfo extends Activity {
    private ImageButton addDrink;
    private ListView listViewOfDrinked;
    private List<Drink> listOfDrinks; // List of all drinks
    private List<Drink> listOfDrinked; // List of all drinks drinked
    private ListAdapterDrinks adapterDrinked; // Adapter for listView of cup drinked

    /* variable use in dialog*/
    private Spinner typeOfDrink;
    private EditText volOfAlcool;
    private EditText volIngurgited;
    private TimePicker hourDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_info);

        addDrink = (ImageButton)findViewById(R.id.btn_addDrink);
        addDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogDrinks();
            }
        });
        listViewOfDrinked = (ListView)findViewById(R.id.listView_drinks);
        listOfDrinked = new ArrayList<Drink>();

        adapterDrinked = new ListAdapterDrinks(this, R.layout.drink_list_row, listOfDrinked);
        listViewOfDrinked.setAdapter(adapterDrinked);

        listOfDrinks = readXML();
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
                Date date = new Date(0, 0, 0, hourDrink.getCurrentHour(), hourDrink.getCurrentMinute()); // Date(int year, int month, int day, int hour, int minute)

                addItem(new Drink(name, volume, quantity, date));

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

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItem(Drink drink) {
        listOfDrinked.add(drink);
        adapterDrinked.notifyDataSetChanged();
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
}
