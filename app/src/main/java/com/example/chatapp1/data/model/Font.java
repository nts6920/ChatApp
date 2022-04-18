package com.example.chatapp1.data.model;

public class Font {
    private String font;
    private String name;

    public Font(String font, String name) {
        this.font = font;
        this.name = name;
    }


    public Font(String font) {
        this.font = font;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
