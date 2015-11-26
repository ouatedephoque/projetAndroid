package ch.hearc.arcootest;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jeshon.assuncao on 18.11.2015.
 */
public class Drink {

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
}
