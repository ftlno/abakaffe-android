package no.ftl.abakaffe;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by fredrik on 29.11.13. Updated by Khrall on 1.12.13.
 */

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LinearLayout header = (LinearLayout) findViewById(R.id.header);
		FrameLayout footer = (FrameLayout) findViewById(R.id.footer);

		header.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		footer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

		float headerHeight = header.getMeasuredHeight();
		float footerHeight = footer.getMeasuredHeight();

        header.setTranslationY(-headerHeight);
        footer.setTranslationY(footerHeight);

		header.animate().translationY(0).setDuration(700);
		footer.animate().translationY(0).setDuration(700);

		TextView headerText = (TextView) findViewById(R.id.header_text);
		TextView footerText = (TextView) findViewById(R.id.footer_text);

		Typeface openSans = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
		headerText.setTypeface(openSans);
		footerText.setTypeface(openSans);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.content, new AbakaffeFragment(getApplicationContext())).commit();
		}
	}
}
