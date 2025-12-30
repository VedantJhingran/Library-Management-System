package model;
public abstract class Item {
    public long id;
    public String title;
    public Item(long id, String title) {
        this.id = id;
        this.title = title;
    }
    public abstract String getType();
}
