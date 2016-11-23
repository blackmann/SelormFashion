package co.blackground.selormfashion.extras;

public class FormField {

    private String title;
    private String name;
    private String value;

    public FormField(String title, String name, String value) {
        this.title = title;
        this.name = name;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
