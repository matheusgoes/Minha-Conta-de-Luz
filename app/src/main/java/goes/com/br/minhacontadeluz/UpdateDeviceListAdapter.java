package goes.com.br.minhacontadeluz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by matheusgoes on 18/07/15.
 */
public class UpdateDeviceListAdapter extends ArrayAdapter<Device> {
    private ArrayList<Device> device_array;
    private Context context;
    int layout = R.layout.device_list_item;
    Database_Acesso db_acesso;
    private int lastPosition = -1;

    ImageView image;
    TextView name_textView;
    TextView power_textView;
    TextView time_textView;
    TextView number_textView;
    TextView comodo_textView;


    public UpdateDeviceListAdapter(Context context, ArrayList<Device> objects, Database_Acesso db_acesso) {
        super(context, R.layout.device_list_item, objects);
        device_array = objects;
        this.context = context;
        this.db_acesso = db_acesso;
    }

    public ArrayList<Device> getDevice_array() {
        return device_array;
    }

    public void setDevice_array(ArrayList<Device> device_array) {
        this.device_array = device_array;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(layout, parent, false);
        image = (ImageView) convertView.findViewById(R.id.device_update_list_image);
        name_textView = (TextView) convertView.findViewById(R.id.device_update_list_name);
        power_textView = (TextView) convertView.findViewById(R.id.device_update_list_pot);
        time_textView = (TextView) convertView.findViewById(R.id.device_update_list_time);
        number_textView = (TextView) convertView.findViewById(R.id.device_update_list_number);
        comodo_textView = (TextView) convertView.findViewById(R.id.device_update_list_comodo);

        Device item = getItem(position);
        if (item!= null) {
            Room r = db_acesso.getRoomByID(item.getRoomID());
            name_textView.setText(String.valueOf(item.getName()));
            power_textView.setText("Potência: "+String.valueOf(item.getPower()) + " Watts");
            double time = Double.parseDouble(item.getTime().toString());
            if (time < 1){
                time = time*60;
                time_textView.setText("Tempo de uso: " + ((int)time) + " minutos");
            }else{
                time_textView.setText("Tempo de uso: " + time + " horas");
            }
            number_textView.setText("Quantidade: " + item.getNumber());
            comodo_textView.setText("Cômodo: " + r.getName());
            image.setImageResource(item.getResource_image());
        }
        //Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.push_left_in : R.anim.push_up_out);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
        convertView.startAnimation(animation);
        lastPosition = position;
        return convertView;
    }
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
