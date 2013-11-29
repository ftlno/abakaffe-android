package no.ftl.abakaffe;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fredrik on 29.11.13.
 */

public class AbakaffeFragment extends Fragment {

    protected Context context;
    private Button updateButton;
    private TextView statusTextView, powerTextView;


    public AbakaffeFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        new UpdateStatusTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        statusTextView = (TextView) view.findViewById(R.id.status_textview);
        powerTextView = (TextView) view.findViewById(R.id.power_textview);

        updateButton = (Button) view.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdateStatusTask().execute();
            }
        });

        return view;
    }

    private class UpdateStatusTask extends AsyncTask<Void, Object, JSONObject> {

        protected JSONObject doInBackground(final Void... params) {

            return NetworkOperations.updateStatus();
        }

        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    boolean status = result.getBoolean("status");
                    if (status) {
                        powerTextView.setText(getText(R.string.power_on));
                    } else {
                        powerTextView.setText(getText(R.string.power_off));
                    }
                    String last_start = result.getString("last_start");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    Date now = new Date();
                    Date last = df.parse(last_start);
                    long seconds = (now.getTime() - last.getTime()) / 1000;
                    long mins = seconds / 60;
                    long hours = seconds / 3600;
                    statusTextView.setText(Utilities.formatStatus(last_start));


                } catch (JSONException e) {
                } catch (ParseException e){
                }
            }
        }
    }
}
