package semanticweb.hws14.movapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import semanticweb.hws14.activities.R;

/**
 * Created by Frederik on 25.11.2014.
 */

//This is the adapter for the actorlist
    //It is mainly used for customized layout and styling
public class ActorListAdapter extends ArrayAdapter<String> {
    Context context;
    int layoutResourceId;
    ArrayList<String> data;

    public ActorListAdapter(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ActorHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ActorHolder();
            holder.actorName = (TextView)row.findViewById(R.id.listItemActorName);

            row.setTag(holder);
        } else {
            holder = (ActorHolder)row.getTag();
        }
        String actor = data.get(position);
        holder.actorName.setText(actor);
        LinearLayout p = (LinearLayout) holder.actorName.getParent();

        if(position % 2 == 1) {
            p.setBackground(context.getResources().getDrawable(R.drawable.list_item_1));
        } else {
            p.setBackground(context.getResources().getDrawable(R.drawable.list_item_2));
        }
        return row;
    }

    static class ActorHolder {
        TextView actorName;
    }
}
