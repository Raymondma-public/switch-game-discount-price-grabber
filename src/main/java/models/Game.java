package models;

public class Game {
    private String name;
    private Integer price;
    private String url;
    private String windowHandle;

    private Integer minLocalPlayer;
    private Integer maxLocalPlayer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getMinLocalPlayer() {
        return minLocalPlayer;
    }

    public void setMinLocalPlayer(Integer minLocalPlayer) {
        this.minLocalPlayer = minLocalPlayer;
    }

    public Integer getMaxLocalPlayer() {
        return maxLocalPlayer;
    }

    public void setMaxLocalPlayer(Integer maxLocalPlayer) {
        this.maxLocalPlayer = maxLocalPlayer;
    }

    public String getWindowHandle() {
        return windowHandle;
    }

    public void setWindowHandle(String windowHandle) {
        this.windowHandle = windowHandle;
    }
}
