package no.ftl.abakaffe;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fredrik on 29.11.13. Updated by Khrall on 1.12.13
 */

public class AbakaffeFragment extends Fragment {

	private static final String TAG = "AbakaffeFragment";
	protected Context context;
	private TextView statusText, footerText;
	private RelativeLayout statusField;
	private int statusFieldPosition = 0;

	public AbakaffeFragment() {
		context = getActivity();
	}

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

		ImageView coffee_cup = (ImageView) view.findViewById(R.id.coffee_cup);
		slideInCoffeeCup(coffee_cup);
		CoffeeTouchListener coffeeTouch = new CoffeeTouchListener();
		coffeeTouch.setReloadIcon((ImageView) view.findViewById(R.id.reload_icon));
		coffee_cup.setOnTouchListener(coffeeTouch);

		Typeface openSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Bold.ttf");
		statusText = (TextView) view.findViewById(R.id.status_text);
		statusText.setTypeface(openSans);
		footerText = (TextView) getActivity().findViewById(R.id.footer_text);
		statusField = (RelativeLayout) view.findViewById(R.id.status_field);

		return view;
	}

	private void slideInCoffeeCup(ImageView coffee_cup) {

		coffee_cup.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		float width = coffee_cup.getMeasuredWidth();
		coffee_cup.setTranslationX(-width);
		coffee_cup.animate().translationX(0).setDuration(1000);
	}

	private void updateView(JSONObject coffee) {

		if (statusFieldPosition == 0) {
			statusFieldPosition = statusField.getTop();
		}

		if (coffee != null) {
			try {
				boolean status = coffee.getBoolean("status");

				SpannableStringBuilder stringBuilder = new SpannableStringBuilder(getText(R.string.power));
				if (status) {
					stringBuilder.append(" PÅ");
					stringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green)), stringBuilder.length() - 2,
							stringBuilder.length(), 0);
				} else {
					stringBuilder.append(" AV");
					stringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), stringBuilder.length() - 2,
							stringBuilder.length(), 0);
				}

				statusText.setText(stringBuilder, TextView.BufferType.SPANNABLE);
				statusField.animate().y(statusFieldPosition).setDuration(1000);

				String statusText = "";
				Resources res = getResources();

                long hours = coffee.getJSONObject("time_since").getLong("hours");
                long minutes = coffee.getJSONObject("time_since").getLong("minutes");

				if (hours >= 24) {
					statusText = res.getString(R.string.turnedOnMoreThanDay);
				} else if (hours > 0) {
					statusText = res.getString(R.string.turnedOnMoreThanHour);
				} else if (minutes == 1) {
					statusText = res.getString(R.string.turnedOnOneMinue);
				} else {
					statusText = String.format(res.getString(R.string.turnedOnMinutes), minutes);
				}

				footerText.setText(statusText);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			footerText.setText(getText(R.string.defaultFooterText));
		}
	}

	private class CoffeeTouchListener implements View.OnTouchListener {

		private ImageView reloadIcon;
		private float originY, offsetY, startY;
		private boolean dragging = false;

		public void setReloadIcon(ImageView reloadIcon) {
			this.reloadIcon = reloadIcon;
		}

		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {

			switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!dragging) {
					reloadIcon.animate().alpha(0).rotation(0).setDuration(0);
					originY = view.getTop();
					startY = motionEvent.getRawY();
					offsetY = startY - originY;
					dragging = true;
					Log.i("Coffee", "Started dragging");
					return true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (dragging) {
					float dy = motionEvent.getRawY();
					float alpha = (float) (1 - 1000 / Math.pow(dy - startY, 2));
					view.animate().y(dy - offsetY).setDuration(0);
					reloadIcon.animate().alpha(alpha).rotation(dy).setDuration(0);
					return true;
				}
				break;

			case MotionEvent.ACTION_UP:
				if (dragging) {
					dragging = false;
					float angle = reloadIcon.getRotation();
					view.animate().y(originY).setDuration(300);
					reloadIcon.animate().alpha(0).rotation(angle + 180).setDuration(300);
					Log.i("Coffee", "Stopped dragging");

					if (Math.abs(motionEvent.getRawY() - startY) > 200) {
						Log.i("Coffee", "Attempted to update text");
						statusField.animate().y(statusFieldPosition + 1000).setDuration(0);
						new UpdateStatusTask().execute();
					}
					return true;
				}
			}
			return false;
		}
	}

	private class UpdateStatusTask extends AsyncTask<Void, Object, JSONObject> {

		protected JSONObject doInBackground(final Void... params) {
			return NetworkOperations.updateStatus();
		}

		protected void onPostExecute(JSONObject result) {
			updateView(result);
		}
	}
}
