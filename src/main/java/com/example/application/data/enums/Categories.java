package com.example.application.data.enums;

public enum Categories {
    Food_And_Drinks,
    Services,
    Transport, // TODO: This will THROW error, because it doesn't exists in database
    Distractions,
    Others;

    @Override
    public String toString() {
        return super.toString().replace("_", " ");
    }
}
