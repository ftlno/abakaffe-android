package no.ftl.abakaffe;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fredrik on 29.11.13.
 */

public class AbakaffeFragment extends Fragment {

	private static final String TAG = "AbakaffeFragment";
	protected Context context;
	private Button updateButton;
	private TextView statusTextView, powerTextView, hoursTextView, minutesTextView, secondsTextView;

	public AbakaffeFragment(Context context) {
		this.context = context;
	}

	@Override
	public void onResume() {
		super.onResume();
		new UpdateStatusTask().execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_main, container, false);

		statusTextView = (TextView) view.findViewById(R.id.status_textview);
		powerTextView = (TextView) view.findViewById(R.id.power_textview);

		hoursTextView = (TextView) view.findViewById(R.id.hours);
		minutesTextView = (TextView) view.findViewById(R.id.minutes);
		secondsTextView = (TextView) view.findViewById(R.id.seconds);

		updateButton = (Button) view.findViewById(R.id.update_button);
		updateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new UpdateStatusTask().execute();
			}
		});

		Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto-li.ttf");
		powerTextView.setTypeface(roboto);
		hoursTextView.setTypeface(roboto);
		minutesTextView.setTypeface(roboto);
		secondsTextView.setTypeface(roboto);

		return view;
	}

	private void updatePowerStatus(boolean status) {

		SpannableStringBuilder stringBuilder = new SpannableStringBuilder(getText(R.string.power));
		if (status) {
			stringBuilder.append(" på");
			stringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green)), stringBuilder.length() - 2,
					stringBuilder.length(), 0);
		} else {
			stringBuilder.append(" av");
			stringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), stringBuilder.length() - 2,
					stringBuilder.length(), 0);
		}

		powerTextView.setText(stringBuilder, TextView.BufferType.SPANNABLE);
	}

	private void updateTimeLeft(String last_start) {

		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			Date now = new Date();
			Date last = df.parse(last_start);
			long diff = (now.getTime() - last.getTime()) / 1000;
			long hours = diff / 3600;
			diff -= hours * 3600;
			long mins = diff / 60;
			diff -= mins * 60;
			long seconds = diff;

			hoursTextView.setText(hours + "");
			minutesTextView.setText(mins + "");
			secondsTextView.setText(seconds + "");
			statusTextView.setText(getText(R.string.timesince));

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void updateView(JSONObject coffee) {

		try {
			updatePowerStatus(coffee.getBoolean("status"));
			updateTimeLeft(coffee.getString("last_start"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private class UpdateStatusTask extends AsyncTask<Void, Object, JSONObject> {

		protected JSONObject doInBackground(final Void... params) {
			return NetworkOperations.updateStatus();
		}

		protected void onPostExecute(JSONObject result) {
			if (result != null) {
				updateView(result);
			}
		}
	}
}
