package com.vargas.basicweatherapp.services.models;

public class Root {
    private Main main;
    private Weather weather;
    public Root(){

    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
