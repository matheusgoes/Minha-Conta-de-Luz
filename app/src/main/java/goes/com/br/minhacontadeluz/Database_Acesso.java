package goes.com.br.minhacontadeluz;

/**
 * Created by matheusgoes on 11/07/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class Database_Acesso{
    private static SQLiteDatabase db;
    private static final String table1 = "device";
    private static final String table2 = "rooms";
    private static final String table3 = "bills";
    private static final String table4 = "device_bill";


    final String[] colunas_table1 =
            new String[]{"id", "name", "power", "time","period","number","res_img", "room_id"};
    final String[] colunas_table2 =
            new String[]{"id", "name"};
    final String[] colunas_table3 =
            new String[]{"month", "year", "value"};
    final String[] colunas_table4 =
            new String[]{"device_id", "bill_month", "bill_year"};
    //construtor
    public Database_Acesso(Context context) {
        Database aux_db = new Database(context);
        //um objeto dessa classe é um banco de dados onde se é possível escrever
        db = aux_db.getWritableDatabase();
        db.enableWriteAheadLogging();
    }

    public void initializeRooms(){
        ContentValues valores = new ContentValues();
        valores.put("id",0);
        valores.put("name","Sala");
        db.insert(table2, null, valores);
        valores = new ContentValues();
        valores.put("id",1);
        valores.put("name", "Quarto");
        db.insert(table2, null, valores);
        valores = new ContentValues();
        valores.put("id",2);
        valores.put("name","Cozinha");
        db.insert(table2, null, valores);
        valores = new ContentValues();
        valores.put("id",3);
        valores.put("name","Banheiro");
        db.insert(table2, null, valores);
    }

    public void insert_device (Device device) {
        ContentValues valores = new ContentValues();
        valores.put("id", device.getId());
        valores.put("name",device.getName());
        valores.put("power",device.getPower());
        valores.put("time", device.getTime());
        valores.put("period", device.getPeriod());
        valores.put("number",device.getNumber());
        valores.put("res_img", device.getResource_image());
        valores.put("room_id", device.getRoomID());
        db.insert(table1, null, valores);
    }

    public void insert_room (Room room) {
        ContentValues valores = new ContentValues();
        valores.put("id", room.getId());
        valores.put("name", room.getName());
        db.insert(table2, null, valores);
    }

    public void insert_bill (int year, int month, double value) {
        ContentValues valores = new ContentValues();
        valores.put("month",month);
        valores.put("year",year);
        valores.put("value",value);
        Log.i("insert_bill", "Month: " + month + " - year" + year + " - Value: " + value);
        db.insert(table3, null, valores);
        update_bill(value, month, year);
    }

    public void insert_bill_device (int device_id, int bill_month, int bill_year) {
        ContentValues valores = new ContentValues();
        valores.put("bill_year", bill_year);
        valores.put("bill_month", bill_month);
        valores.put("device_id", device_id);
        db.insert(table4, null, valores);
    }

    public void insert_bill_device (ArrayList<Device> device, int bill_month, int bill_year) {
        ContentValues valores = new ContentValues();
        for (int i = 0; i < device.size(); i++) {
            valores = new ContentValues();
            valores.put("bill_year",bill_year);
            valores.put("bill_month",bill_month);
            valores.put("device_id",device.get(i).getId());
            db.insert(table4, null, valores);
        }
    }



    void update_device(Device device){
        ContentValues valores = new ContentValues();
        valores.put("power",device.getPower());
        valores.put("time",device.getTime());
        valores.put("period",device.getPeriod());
        valores.put("number", device.getNumber());
        valores.put("room_id", device.getRoomID());
        valores.put("res_img", device.getResource_image());
        db.update(table1, valores, "id=?", new String[]{String.valueOf(device.getId())});
    }

    void update_bill(double value, int month, int year){
        ContentValues valores = new ContentValues();
        valores.put("value",value);
        db.update(table3, valores, "month="+ month + " and year="+year, null);
    }

    public ArrayList<Device> getDevices () {
        ArrayList<Device> lista = new ArrayList<>();
        Cursor cursor = db.query(table1, colunas_table1, null, null, null, null,"id ASC");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Device d = new Device();
                d.setId(cursor.getInt(0));
                d.setName(cursor.getString(1));
                d.setPower(cursor.getDouble(2));
                d.setTime(cursor.getDouble(3));
                d.setPeriod(cursor.getDouble(4));
                d.setNumber(cursor.getInt(5));
                d.setResource_image(cursor.getInt(6));
                d.setRoomID(cursor.getInt(7));
                lista.add(d);
            } while (cursor.moveToNext());
        }

        return lista;
    }

    public ArrayList<Room> getRooms () {
        ArrayList<Room> lista = new ArrayList<>();

        Cursor cursor = db.query(table2, colunas_table2, null, null, null, null,"id ASC");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Room r = new Room();
                r.setId(cursor.getInt(0));
                r.setName(cursor.getString(1));
                lista.add(r);
            } while (cursor.moveToNext());
        }

        return lista;
    }

    public Device getDeviceByID(int id){
        Device d = new Device();
        Cursor cursor = db.query(table1, colunas_table1, "id="+id, null, null, null,null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            d.setId(cursor.getInt(0));
            d.setName(cursor.getString(1));
            d.setPower(cursor.getDouble(2));
            d.setTime(cursor.getDouble(3));
            d.setPeriod(cursor.getDouble(4));
            d.setNumber(cursor.getInt(5));
            d.setResource_image(cursor.getInt(6));
            d.setRoomID(cursor.getInt(7));
        }
        return d;
    }

    public ArrayList<Device> getDevicesByID(ArrayList<Integer> ids){
        ArrayList<Device> lista = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Cursor cursor = db.query(table1, colunas_table1, "id="+ids.get(i), null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Device d = new Device();
                d.setId(cursor.getInt(0));
                d.setName(cursor.getString(1));
                d.setPower(cursor.getDouble(2));
                d.setTime(cursor.getDouble(3));
                d.setPeriod(cursor.getDouble(4));
                d.setNumber(cursor.getInt(5));
                d.setResource_image(cursor.getInt(6));
                d.setRoomID(cursor.getInt(7));
                lista.add(d);
            }
        }
        return lista;
    }

    public ArrayList<Integer> getDevicesIDFromDevice_Bill() {
        ArrayList<Integer> lista = new ArrayList<>();
        Cursor cursor = db.query(table4, colunas_table4 , null, null, null, null,"device_id ASC");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                int i = cursor.getInt(0);
                lista.add(i);
            } while (cursor.moveToNext());
        }
        return lista;
    }

    public ArrayList<Integer> getDevicesIDFromDevice_Bill(int month, int year) {
        String[] colunas = new String[]{"device_id"};
        ArrayList<Integer> lista = new ArrayList<>();
        Cursor cursor = db.query(table4, colunas , "bill_month="+month +" and  bill_year="+year, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                int i = cursor.getInt(0);
                lista.add(i);
            } while (cursor.moveToNext());
        }
        return lista;
    }

    public ArrayList<Device> getDevicesByRoomID(int id){
        ArrayList<Device> lista = new ArrayList<>();
        Cursor cursor = db.query(table1, colunas_table1, "room_id="+id, null, null, null,null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Device d = new Device();
                d.setId(cursor.getInt(0));
                d.setName(cursor.getString(1));
                d.setPower(cursor.getDouble(2));
                d.setTime(cursor.getDouble(3));
                d.setPeriod(cursor.getDouble(4));
                d.setNumber(cursor.getInt(5));
                d.setResource_image(cursor.getInt(6));
                d.setRoomID(cursor.getInt(7));
                lista.add(d);
            } while (cursor.moveToNext());
        }
        return lista;
    }

    public ArrayList<Device> getDevicesByRoomIDAndDate(int id, int month, int year){
        ArrayList<Device> lista = new ArrayList<>();
        //Cursor cursor = db.query(table1, colunas_table1, "room_id="+id, null, null, null,null);
        Cursor cursor = db.rawQuery("SELECT id, name, power, time, period, number, res_img, room_id " +
                "FROM "+ table1 + " INNER JOIN "+ table4 + " ON "+table1+".id="+table4+".device_id"
                + " WHERE "+table4+".bill_month="+month+ " AND "+table4+".bill_year="+year
                + " AND "+table1+".id="+id, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Device d = new Device();
                d.setId(cursor.getInt(0));
                d.setName(cursor.getString(1));
                d.setPower(cursor.getDouble(2));
                d.setTime(cursor.getDouble(3));
                d.setPeriod(cursor.getDouble(4));
                d.setNumber(cursor.getInt(5));
                d.setResource_image(cursor.getInt(6));
                d.setRoomID(cursor.getInt(7));
                lista.add(d);
            } while (cursor.moveToNext());
        }
        return lista;
    }

    public Room getRoomByID(int id){
        Room r = new Room();
        Cursor cursor = db.query(table2, colunas_table2, "id="+id, null, null, null,null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            r.setId(cursor.getInt(0));
            r.setName(cursor.getString(1));
        }
        return r;
    }

    public ArrayList<Bill> getBills(){
        ArrayList<Bill> lista = new ArrayList<>();
        Cursor cursor = db.query(table3, colunas_table3, null, null, null, null,"month ASC, year ASC");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Bill b = new Bill();
                b.setMonth(cursor.getInt(0));
                b.setYear(cursor.getInt(1));
                b.setValue(cursor.getDouble(2));
                lista.add(b);
            } while (cursor.moveToNext());
        }
        return lista;
    }

    public ArrayList<Device> getDevicesOrderedByCost () {
        ArrayList<Device> lista = new ArrayList<>();
        Cursor cursor = db.query(table1, colunas_table1, null, null, null, null,"power ASC, period ASC, time ASC,id ASC");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Device d = new Device();
                d.setId(cursor.getInt(0));
                d.setName(cursor.getString(1));
                d.setPower(cursor.getDouble(2));
                d.setTime(cursor.getDouble(3));
                d.setPeriod(cursor.getDouble(4));
                d.setNumber(cursor.getInt(5));
                d.setResource_image(cursor.getInt(6));
                d.setRoomID(cursor.getInt(7));
                lista.add(d);
            } while (cursor.moveToNext());
        }

        return lista;
    }

    public int getMaxDeviceId(){
        String[] colunas = new String[]{"max(id)"};
        int id=0;
        Cursor cursor = db.query(table1, colunas, null, null, null, null,null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }
        return id;
    }

    public int getMaxRoomId(){
        String[] colunas = new String[]{"max(id)"};
        int id=0;
        Cursor cursor = db.query(table2, colunas, null, null, null, null,null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }
        return id;
    }

    //função clear
    //deleta todos os dados salvos no banco de dados.
    void remove_all(){
        db.delete("contact", null, null);
    }
    void remove_device(int id){
       // db.delete("contact", "id=?", new String [] { id });
        String sql = "DELETE FROM "+ table1 +" WHERE id=" + id;
        db.execSQL(sql);
    }

    boolean roomDeletableCheck(int id){
        String[] colunas = new String[]{"count(id)"};
        Cursor cursor = db.query(table2, colunas, "id="+id, null , null, null,null);
        if (cursor.getCount() > 0) {
            return false;
        }else{
            return true;
        }
    }

    void remove_room(int id){
        // db.delete("contact", "id=?", new String [] { id });
        String sql = "DELETE FROM "+ table2 +" WHERE id=" + id;
        db.execSQL(sql);
    }

    void remove_device_bill(int deviceID, int bill_month, int bill_year){
        Log.i("REMOVE DEVICE BILL", "ID " + deviceID + " mes " + bill_month + " ano " + bill_year);
        String sql = "DELETE FROM "+ table4 +" WHERE device_id=" + deviceID
                +" AND bill_month="+ bill_month + " AND bill_year="+bill_year;
        db.execSQL(sql);
    }

    void closeDB(){
        db.close();
    }
}
