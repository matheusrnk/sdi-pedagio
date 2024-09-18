package udesc.trabalho.producer;

import udesc.trabalho.Tag;

public class Car {

    private String plate = "";
    private boolean payed = false;
    private Tag tag;

    public Car(String plate, boolean payed, Tag tag) {
        this.plate = plate;
        this.payed = payed;
        this.tag = tag;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Car [plate=" + plate + ", payed=" + payed + ", tag=" + tag.getName() + "]";
    }

}
