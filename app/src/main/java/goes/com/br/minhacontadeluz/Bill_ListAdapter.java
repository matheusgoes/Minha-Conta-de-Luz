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

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by matheusgoes on 20/07/15.
 */
public class Bill_ListAdapter extends ArrayAdapter<Device> {
    private ArrayList<Device> device_array;
    private Context context;
    Database_Acesso db_acesso;
    TextView name;
    TextView power;
    TextView time;
    TextView period;
    TextView cost;
    ImageView image;
    TextView number_textView;
    TextView comodo;
    Double preco_kwh;
    private int lastPosition = -1;

    public Bill_ListAdapter(Context context, ArrayList<Device> objects, Database_Acesso db_acesso, double preco_kwh) {
        super(context, R.layout.fragment_bill_list_item, objects);
        device_array = objects;
        this.context = context;
        this.db_acesso = db_acesso;
        this.preco_kwh = preco_kwh;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.fragment_bill_list_item, parent, false);
        name = (TextView) convertView.findViewById(R.id.bill_list_name);
        power = (TextView) convertView.findViewById(R.id.bill_list_pot);
        time = (TextView) convertView.findViewById(R.id.bill_list_time);
        cost = (TextView) convertView.findViewById(R.id.bill_list_cost);
        period = (TextView) convertView.findViewById(R.id.bill_list_consume);
        image = (ImageView) convertView.findViewById(R.id.bill_list_image);
        number_textView = (TextView) convertView.findViewById(R.id.bill_list_number);
        comodo = (TextView) convertView.findViewById(R.id.bill_list_comodo);

        Device item = getItem(position);
        if (item!= null) {
            Room r = db_acesso.getRoomByID(item.getRoomID());
            name.setText(item.getName());
            period.setText(((int)Double.parseDouble(item.getPeriod().toString())) + " dias");
            power.setText(item.getPower().toString()+ " Watts");
            double time = Double.parseDouble(item.getTime().toString());
            double costValue = ((double)Math.round(((time * item.getPower() * item.getPeriod())/1000) * preco_kwh * 100) / 100) * item.getNumber();
            cost.setText("Custo: R$"+ costValue);
            if (time < 1){
                time = time*60;
                this.time.setText("Tempo de uso: " + ((int)time) + " minutos");
            }else{
                this.time.setText("Tempo de uso: "+ time + " horas");
            }
            DecimalFormat df = new DecimalFormat();
            df.setDecimalSeparatorAlwaysShown(true);
            df.setMaximumFractionDigits(2);
            number_textView.setText(" - Qtd: " + item.getNumber());
            comodo.setText(" - " + r.getName());
            image.setImageResource(item.getResource_image());
        }
        //Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.push_left_in : R.anim.push_up_out);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
        lastPosition = position;
        return convertView;
    }
}
