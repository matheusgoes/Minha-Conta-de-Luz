package goes.com.br.minhacontadeluz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by matheusgoes on 21/07/15.
 */
public class Rooms_ListAdapter extends ArrayAdapter<Room> {

    private ArrayList<Room> device_array;
    private Context context;
    private int lastPosition = -1;
    TextView name;

    public Rooms_ListAdapter(Context context, ArrayList<Room> objects) {
        super(context, R.layout.rooms_list_item, objects);
        device_array = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.rooms_list_item, parent, false);
        name = (TextView) convertView.findViewById(R.id.rooms_name_textView);
        Room item = getItem(position);
        if (item!= null) {
            name.setText(item.getName());
        }
        //Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.push_left_in : R.anim.push_up_out);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
        convertView.startAnimation(animation);
        lastPosition = position;
        return convertView;
    }

}
