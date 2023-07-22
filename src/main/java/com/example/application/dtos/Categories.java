package com.example.application.dtos;

public enum Categories {
    Food_And_Drinks,
    Services,
    Transport,
    Distractions,
    Others;

    @Override
    public String toString() {
        return super.toString().replace("_", " ");
    }
}
