package goes.com.br.minhacontadeluz;

public class Device {
    private int id=0;
    private String name;
    private  Double power;
    private  Double time;
    private Double period;
    private int number;
    private int resource_image;
    private int roomID;

    public Device(int id,String name, Double power, Double time, Double period,int number,int resource_image, int roomID) {
        this.name = name;
        this.power = power;
        this.time = time;
        this.period = period;
        this.resource_image = resource_image;
        this.id = id;
        this.number = number;
        this.roomID = roomID;
    }

    public Device(Device device) {
        this.name = device.getName();
        this.power = device.getPower();
        this.time = device.getTime();
        this.period = device.getPeriod();
        this.number = device.getNumber();
        this.resource_image = device.getResource_image();;
        this.id = device.getId();
        this.roomID = device.roomID;
    }

    public Device() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public int getResource_image() {
        return resource_image;
    }

    public void setResource_image(int resource_image) {
        this.resource_image = resource_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getPeriod() {
        return period;
    }

    public void setPeriod(Double period) {
        this.period = period;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }
}
