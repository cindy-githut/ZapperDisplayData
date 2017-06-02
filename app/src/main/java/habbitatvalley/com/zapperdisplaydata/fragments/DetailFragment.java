package habbitatvalley.com.zapperdisplaydata.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import habbitatvalley.com.zapperdisplaydata.Networks;
import habbitatvalley.com.zapperdisplaydata.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String USER_ID = "user_id";

    // TODO: Rename and change types of parameters
    private int userId;
    private TextView lname, fname, age, favcolour;
    private OnFragmentInteractionListener mListener;
    private OkHttpClient client;

    public DetailFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(int userId) {

        Bundle args = new Bundle();
        args.putInt(USER_ID, userId);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        lname = (TextView) view.findViewById(R.id.lastname);
        fname = (TextView) view.findViewById(R.id.firstname);
        age = (TextView) view.findViewById(R.id.age);
        favcolour = (TextView) view.findViewById(R.id.favouriteColour);

        //retrieving data using bundle

        populateListWithData(Networks.getUserDetailedInfor(userId));

        return view;
    }

    public void populateListWithData(String url) {

        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception exc){

                }

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String responseString = response.body().string();

                try{

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (response.isSuccessful()) {

                                //convert string response to a JSONArray since the data we get from the database is retrieved in an array
                                try {

                                    if(response.code() == 200){

                                        JSONObject people = new JSONObject(responseString);
                                        handleServerResponse(people);

                                    }else{
                                        Toast.makeText(getActivity(), getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });

                }catch(Exception ignored){

                }
            }
        });
    }
    private void handleServerResponse(JSONObject person) throws JSONException {


        if(person.has("firstName") && person.getString("firstName") != null){
            fname.setText(person.getString("firstName"));

        }else{
            fname.setText(getString(R.string.fname_not_found));

        }
        if(person.has("lastName") && person.getString("lastName") != null){
            lname.setText(person.getString("lastName"));

        }else{
            lname.setText(getString(R.string.lname_not_found));

        }
        if(person.has("age") && person.getString("age") != null){

            age.setText(String.valueOf(person.getInt("age")));

        }else{
            age.setText(getString(R.string.age_not_found));

        }
        if(person.has("favouriteColour") && person.getString("favouriteColour") != null){

            favcolour.setText(person.getString("favouriteColour"));

        }else{
            favcolour.setText(getString(R.string.favcolour_not_found));

        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
