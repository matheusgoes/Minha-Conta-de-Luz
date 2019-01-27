package goes.com.br.minhacontadeluz;

/**
 * Created by matheusgoes on 26/07/15.
 */
public class Bill {
    private int month;
    private int year;
    private double value;

    public Bill(int month, int year, double value) {
        this.month = month;
        this.year = year;
        this.value = value;
    }

    public Bill() {
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
