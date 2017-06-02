package habbitatvalley.com.zapperdisplaydata.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import habbitatvalley.com.zapperdisplaydata.Networks;
import habbitatvalley.com.zapperdisplaydata.R;
import habbitatvalley.com.zapperdisplaydata.adapters.PersonAdapter;
import habbitatvalley.com.zapperdisplaydata.models.PersonItem;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MasterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MasterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MasterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private ListView listview;
    OkHttpClient client;
    String url;
    PersonItem person;
    PersonAdapter personAdapter;
    public ArrayList<PersonItem> personItemArray;
    private static final String mypreference = "mypref";

    private OnFragmentInteractionListener mListener;
    private OnUserSelected mListenern;
    Handler handler;


    public MasterFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MasterFragment newInstance() {
        MasterFragment fragment = new MasterFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        if (getActivity() instanceof OnUserSelected) {
            mListenern = (OnUserSelected) getActivity();
        } else {
            throw new ClassCastException(getActivity().toString() + " must implement OnUserSelected.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
        * first call the removeCallbacks to remove any existing callbacks to the handler
        * then call the handler to start the runnable object
        * */
        handler.removeCallbacks(runnableCode);
        handler.postDelayed(runnableCode, 5000);//5 seconds
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnableCode);
    }

    @Override
    public void onPause() {
        super.onPause();

        handler.removeCallbacks(runnableCode);
    }
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {

            //do a periodical fetch from the API endpoint to pull the data and update the UI every after 5 seconds
            populateListWithData(Networks.personBaseEndpoint);
            handler.postDelayed(runnableCode, 5000); //5 seconds

        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListenern = null;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_master, container, false);

        client = new OkHttpClient();
        personItemArray = new ArrayList<>();
        listview = (ListView) view.findViewById(R.id.listview);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(mypreference, MODE_PRIVATE);


        if (sharedPreferences.getString("dataUsers", null)== null) {

            populateListWithData(Networks.personBaseEndpoint);

        }else{

            //populate/displaythe UI with the data stored locally
            try {

                JSONArray people = new JSONArray(sharedPreferences.getString("dataUsers", null));

                handleServerResponse(people);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //go fetch new users data from the API
            populateListWithData(Networks.personBaseEndpoint);

        }

        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void populateListWithData(String url) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception exc){

                }

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String responseString = response.body().string();
                Log.d("RESPONSE", responseString);

                try{

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (response.isSuccessful()) {

                                //convert string response to a JSONArray since the data we get from the database is retrieved in an array
                                try {

                                    JSONArray people = new JSONArray(responseString);

                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(mypreference, MODE_PRIVATE).edit();
                                    editor.putString("dataUsers", responseString);
                                    editor.apply();

                                    //clear the cached data showing on the screen before adding new users
                                    if(personItemArray != null){
                                        personItemArray.clear();
                                    }

                                    handleServerResponse(people);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });

                }catch(Exception exc){

                }

            }
        });
    }

    private void handleServerResponse(JSONArray people) throws JSONException {

        JSONObject jsonObject;

        for (int i = 0; i < people.length(); i++){

            jsonObject = people.getJSONObject(i);

            person = new PersonItem();

            person.setPersonId(jsonObject.getInt("id"));

            //check if it has first name and that it is not a null value
            if(jsonObject.has("firstName") && jsonObject.getString("firstName") != null){
                person.setFirstName(jsonObject.getString("firstName"));

            }else{
                person.setFirstName("");

            }

            //check if it has last name and that it is not a null value
            if(jsonObject.has("lastName") && jsonObject.getString("lastName") != null){
                person.setLastName(jsonObject.getString("lastName"));

            }else{

                person.setLastName("");

            }

            personItemArray.add(person);
        }

        try{

            personAdapter = new PersonAdapter(getActivity(), personItemArray, mListenern);
            listview.setAdapter(personAdapter);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public interface OnUserSelected {
        void onUserSelected(int userId);
    }
}
