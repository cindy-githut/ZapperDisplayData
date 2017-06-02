package habbitatvalley.com.zapperdisplaydata.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import habbitatvalley.com.zapperdisplaydata.R;
import habbitatvalley.com.zapperdisplaydata.fragments.MasterFragment;
import habbitatvalley.com.zapperdisplaydata.models.PersonItem;

public class PersonAdapter extends BaseAdapter {

    private ArrayList<PersonItem> listFriends;
    LayoutInflater inflater;
    Activity activity;
    private MasterFragment.OnUserSelected mListenern;


    public PersonAdapter(Activity activity, ArrayList<PersonItem> listFriends, MasterFragment.OnUserSelected mListenern) {

        this.listFriends = listFriends;
        this.inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.mListenern = mListenern;

    }

    @Override
    public int getCount() {
        return listFriends.size();
    }

    @Override
    public PersonItem getItem(int position) {
        return listFriends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = this.inflater.inflate(R.layout.person_item_layout,
                    parent, false);

            holder.txtFullNames = (TextView) convertView
                    .findViewById(R.id.fullname);
            holder.userLayout = (RelativeLayout) convertView
                    .findViewById(R.id.userLayout);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        // Current person
        holder.txtFullNames.setText(listFriends.get(position).getFirstName() + " " + listFriends.get(position).getLastName());

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mListenern !=null){

                    mListenern.onUserSelected(listFriends.get(position).getPersonId());

                }

            }
        });

        return convertView;

    }

    private class ViewHolder {

        private TextView txtFullNames;
        private RelativeLayout userLayout;

    }
}


