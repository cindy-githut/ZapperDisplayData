package habbitatvalley.com.zapperdisplaydata;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import habbitatvalley.com.zapperdisplaydata.fragments.DetailFragment;
import habbitatvalley.com.zapperdisplaydata.fragments.MasterFragment;

public class MainActivity extends AppCompatActivity implements MasterFragment.OnUserSelected{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check whether the Activity is using the layout verison with the fragment_container
        // FrameLayout and if so we must add the first fragment
        if (findViewById(R.id.fragment_container) != null){

            if (savedInstanceState != null){
                return;
            }

            // Create an Instance of Master Fragment
            MasterFragment versionsFragment = new MasterFragment();
            versionsFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, versionsFragment)
                    .commit();

        }

    }
    @Override
    public void onUserSelected(int userId) {

        DetailFragment detailsFragment = (DetailFragment) getFragmentManager()
                .findFragmentById(R.id.details_fragment);

        if (detailsFragment != null){

            // If detail is available, we are in two pane layout
            detailsFragment.populateListWithData(Networks.getUserDetailedInfor(userId));

        } else {

            DetailFragment newDetailFragment = new DetailFragment();
            Bundle args = new Bundle();

            args.putInt(DetailFragment.USER_ID,userId);
            newDetailFragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the backStack so the User can navigate back
            fragmentTransaction.replace(R.id.fragment_container,newDetailFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

    }
}
