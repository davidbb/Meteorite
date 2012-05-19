package ca.carleton.ccsl.meteorite;

import android.app.Activity;
import android.os.Bundle;

/**
 * The MeteoriteActivity displays a welcome screen layout.
 * There is no functionality in this activity, it merely exists to display
 * information about the use/purpose of Meteorite to users that have installed
 * and launched the Meteorite app.
 * @author dbarrera
 */
public class MeteoriteActivity extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
