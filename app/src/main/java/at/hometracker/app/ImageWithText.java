package at.hometracker.app;

public class ImageWithText {

    private byte[] imageData;
    private String name;

    public ImageWithText(byte[] imageData, String name) {
        this.imageData = imageData;
        this.name = name;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
