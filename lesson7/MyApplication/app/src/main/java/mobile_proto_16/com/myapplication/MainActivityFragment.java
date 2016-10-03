package mobile_proto_16.com.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @BindView(R.id.button) Button button;
    @BindView(R.id.input) EditText input;
    @BindView(R.id.price) TextView price;

    private final String TAG = this.getClass().getName();

    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                //remove the first 3 characters and parse it into JSONArray
                JSONArray jsonArray = new JSONArray(response.substring(3));
                // get the price and show it in the TextView
                price.setText(extractPriceFromJSON(jsonArray));
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "A VolleyError occurred.");
            error.printStackTrace();
        }
    };

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view); // bind the current view

        final Context c = this.getContext();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // build the url from input
                String url = buildSearchURL(input.getText().toString()); // build the url from input
                // create new string request
                StringRequest stringRequest = new StringRequest(url,responseListener,errorListener);
                // add the string request into the queue
                MySingleton.getInstance(c).addToRequestQueue(stringRequest);
            }
        });

        return view;
    }

    private String buildSearchURL(String companyTicker) {
        // build the url using uri builder
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("finance.google.com")
                .appendPath("finance")
                .appendPath("info")
                .appendQueryParameter("client", "iq")
                .appendQueryParameter("q", companyTicker);
        String myUrl = builder.build().toString();
        return myUrl; // return a url
    }

    private String extractPriceFromJSON(JSONArray array) throws JSONException {
        JSONObject result = (JSONObject) array.get(0); // get the json object from the array
        return result.getString("l"); // return the price
    }

}
