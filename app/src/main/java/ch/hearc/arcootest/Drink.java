package ch.hearc.arcootest;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jeshon.assuncao on 18.11.2015.
 */
public class Drink implements Parcelable {

    /* Constructors */
    public Drink(String name, float volume, float quantity, Calendar hourDrink)
    {
        this.name = name;
        this.volume = volume;
        this.quantity = quantity;
        this.hourDrink = hourDrink;
    }

    public Drink(String name, float volume, float quantity)
    {
        this(name, volume, quantity, Calendar.getInstance());
    }

    /* Getters */
    public String getName()
    {
        return name;
    }
    public float getVolume()
    {
        return volume;
    }
    public float getQuantity()
    {
        return quantity;
    }
    public Calendar getTimeDrink() {
        return hourDrink;
    }

    /* Setters */
    public void setName(String name)
    {
        this.name = name;
    }
    public void setVolume(float volume)
    {
        this.volume = volume;
    }
    public void setQuantity(float quantity)
    {
        this.quantity = quantity;
    }
    public void setTimeDrink(Calendar hourDrink) {
        this.hourDrink = hourDrink;
    }

    /* Attributes */
    private String name;
    private float volume;
    private float quantity;
    private Calendar hourDrink;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(volume);
        dest.writeFloat(quantity);
        dest.writeValue(hourDrink);
    }

    private Drink(Parcel in) {
        this.name = in.readString();
        this.volume = in.readFloat();
        this.quantity = in.readFloat();

        this.hourDrink = (GregorianCalendar)in.readValue(GregorianCalendar.class.getClassLoader());
    }

    public static final Creator<Drink> CREATOR = new Creator<Drink>() {
        @Override
        public Drink createFromParcel(Parcel in) {
            return new Drink(in);
        }

        @Override
        public Drink[] newArray(int size) {
            return new Drink[size];
        }
    };

    @Override
    public String toString() {
        return  "[Name : " + this.name +
                " | Quantity : " + this.quantity +
                " | Volume : " + this.volume +
                " | HourDrink : " + this.hourDrink.getTime() + "]";
    }
}
