package goes.com.br.minhacontadeluz;

/**
 * Created by matheusgoes on 11/07/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper{
    // Versão do db
    private static final int versao_db = 1;

    // Nome do db
    private static final String nome_db = "MinhaContaDeLuz";

    // Nome da tabela 1
    private static final String table1 = "device";
    // Nomes das colunas_table1 da tabela device
    private static final String id = "id";
    private static final String name = "name";
    private static final String power = "power";
    private static final String time = "time";
    private static final String period = "period";
    private static final String number = "number";
    private static final String resource_image = "res_img";
    private static final String device_room_id = "room_id";

    // Nome da tabela
    private static final String table2 = "rooms";
    // Nomes das colunas_table1 da tabela room
    private static final String room_id = "id";
    private static final String room_name = "name";

    // Nome da tabela
    private static final String table3 = "bills";
    // Nomes das colunas_table1 da tabela bill
    private static final String bill_month = "month";
    private static final String bill_year = "year";
    private static final String bill_value = "value";

    //Nome da tabela
    private static final String table4 = "device_bill";
    // Nomes das colunas_table1 da tabela device_bill
    private static final String device_id = "device_id";
    private static final String billYear = "bill_year";
    private static final String billMonth = "bill_month";


    //construtor do banco
    public Database(Context context) {
        super(context, nome_db, null, versao_db);
    }
    // Criando as tabelas
    //caso a versao seja a mesma da já criada o construtor executará essa função
    @Override
    public void onCreate(SQLiteDatabase db) {
        //tabela phone
        //na String abaixo o comando sql que responsável pela criação da tabela
        String criarTabela1 = "CREATE TABLE " + table1 + "("
                + id + " INTEGER PRIMARY KEY," + name + " TEXT NOT NULL," + power + " REAL NOT NULL," + time + " REAL NOT NULL,"
                + period + " REAL NOT NULL," + number +" INTEGER NOT NULL,"
                + resource_image +" INTEGER NOT NULL,"+ device_room_id +" INTEGER NOT NULL,"
                + " FOREIGN KEY (" + device_room_id +") REFERENCES " + table2 + "(" +room_id+ ")"
                + ");";

        String criarTabela2 = "CREATE TABLE " + table2 + "("
                + room_id + " INTEGER PRIMARY KEY," + room_name + " TEXT NOT NULL)";

        String criarTabela3 = "CREATE TABLE " + table3 + "("
                + bill_month +" INTEGER NOT NULL,"
                + bill_year +" INTEGER NOT NULL," + bill_value +" REAL NOT NULL," +
                " PRIMARY KEY (" + bill_month +","+ bill_year+") )";

        String criarTabela4 = "CREATE TABLE " + table4 + "("
                + device_id +" INTEGER NOT NULL,"
                + billMonth +" INTEGER NOT NULL,"
                + billYear +" INTEGER NOT NULL,"
                + " FOREIGN KEY (" + device_id +") REFERENCES " + table1 + "(" +id+ ")"
                + " FOREIGN KEY (" + billMonth +") REFERENCES " + table3 + "(" +bill_month+ ")"
                + " FOREIGN KEY (" + billYear +") REFERENCES " + table3 + "(" +bill_year+ ")"
                + " PRIMARY KEY ("+device_id+ "," + billMonth +","+ billYear + ")"
                + ");";


        //execução do comando SQL
        db.execSQL(criarTabela1);
        db.execSQL(criarTabela2);
        db.execSQL(criarTabela3);
        db.execSQL(criarTabela4);
    }

    //caso a versão seja diferente da já criada o construtor executa essa função
    @Override
    public void onUpgrade(SQLiteDatabase db, int versao_ant, int versao_nv) {
        //mensagem de log para avisar sobre a mudança da versão
        Log.w(Database.class.getName(),
                "Atualizando o banco de dados da versão " + versao_ant + " para "
                        + versao_nv + ", isso apagará os dados antigos.");
        //comando para deletar a tabela
        db.execSQL("DROP TABLE IF EXISTS " + table1 + ";");
        db.execSQL("DROP TABLE IF EXISTS " + table2 + ";");
        db.execSQL("DROP TABLE IF EXISTS " + table3 + ";");
        db.execSQL("DROP TABLE IF EXISTS " + table4 + ";");
        //criar novamente a tabela
        onCreate(db);
    }


}
