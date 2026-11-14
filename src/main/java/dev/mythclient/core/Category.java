package dev.mythclient.core;

/** Groups modules in the click GUI. Add new categories here. */
public enum Category {
    HUD("HUD"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    MISC("Misc");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
