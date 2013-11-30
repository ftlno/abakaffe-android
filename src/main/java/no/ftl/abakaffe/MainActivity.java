package no.ftl.abakaffe;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by fredrik on 29.11.13.
 */

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new AbakaffeFragment(getApplicationContext())).commit();
		}
	}

}
