package ch.hearc.arcootest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jeshon.assuncao on 18.11.2015.
 */
public class ListAdapterDrinks extends ArrayAdapter<Drink> {

    public ListAdapterDrinks(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapterDrinks(Context context, int resource, List<Drink> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(parent.getContext());
            v = vi.inflate(R.layout.drink_list_row, parent, false);

            Drink drink = getItem(position);

            if (drink != null) {
                TextView tv_name = (TextView) v.findViewById(R.id.row_name);
                TextView tv_quantity = (TextView) v.findViewById(R.id.row_quantity);
                TextView tv_volume = (TextView) v.findViewById(R.id.row_volume);
                TextView tv_hour = (TextView) v.findViewById(R.id.row_hour);

                if (tv_name != null) {
                    tv_name.setText(drink.getName());
                }
                if (tv_quantity != null) {
                    tv_quantity.setText(drink.getQuantity() + "dl");
                }
                if (tv_volume != null) {
                    tv_volume.setText(drink.getVolume()+"°");
                }
                if (tv_hour != null) {
                    int hour = drink.getTimeDrink().get(Calendar.HOUR_OF_DAY);
                    int minute = drink.getTimeDrink().get(Calendar.MINUTE);

                    if(minute > 0 && minute < 10)
                    {
                        tv_hour.setText(hour + ":0" + minute);
                    }
                    else
                    {
                        tv_hour.setText(hour + ":" + minute);
                    }
            }
            }
        }

        return v;
    }
}
