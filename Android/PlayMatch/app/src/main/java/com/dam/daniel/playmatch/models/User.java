package com.dam.daniel.playmatch.models;

import java.util.List;

public class User {

    private int id;
    private String telephoneNumber;
    private String nick;
    private String birthdate;
    private int gender;
    private String email;
    private String password;
    private Double lat;
    private Double lng;
    private String city;
    private String description;
    private String job;
    private String company;
    private String school;
    private int maxDistancePreference;
    private int minAgePreference;
    private int maxAgePreference;
    private boolean active;
    private String disabled;
    private List<Image> images;
    private List<GenderPreference> genderPreferences;

    public User() {}

    // No usamos Password en el constructor porque en "UserService" usamos este constructor para parsear los objetos que llegan del servidor, y la contraseña nunca se debe recibir.
    // Para añadir la contraseña al objeto usamos el "setPassword()"
    public User(int id, String telephoneNumber, String nick, String birthdate, int gender, String email, Double lat, Double lng, String city, String description, String job, String company, String school, int maxDistancePreference, int minAgePreference, int maxAgePreference, boolean active, String disabled, List<Image> images, List<GenderPreference> genderPreferences) {
        this.id = id;
        this.telephoneNumber = telephoneNumber;
        this.nick = nick;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        this.city = city;
        this.description = description;
        this.job = job;
        this.company = company;
        this.school = school;
        this.maxDistancePreference = maxDistancePreference;
        this.minAgePreference = minAgePreference;
        this.maxAgePreference = maxAgePreference;
        this.active = active;
        this.disabled = disabled;
        this.images = images;
        this.genderPreferences = genderPreferences;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getMaxDistancePreference() {
        return maxDistancePreference;
    }

    public void setMaxDistancePreference(int maxDistancePreference) {
        this.maxDistancePreference = maxDistancePreference;
    }

    public int getMinAgePreference() {
        return minAgePreference;
    }

    public void setMinAgePreference(int minAgePreference) {
        this.minAgePreference = minAgePreference;
    }

    public int getMaxAgePreference() {
        return maxAgePreference;
    }

    public void setMaxAgePreference(int maxAgePreference) {
        this.maxAgePreference = maxAgePreference;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDisabled() {
        return disabled;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<GenderPreference> getGenderPreferences() {
        return genderPreferences;
    }

    public void setGenderPreferences(List<GenderPreference> genderPreferences) {
        this.genderPreferences = genderPreferences;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                ", nick='" + nick + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", city='" + city + '\'' +
                ", description='" + description + '\'' +
                ", job='" + job + '\'' +
                ", company='" + company + '\'' +
                ", school='" + school + '\'' +
                ", maxDistancePreference=" + maxDistancePreference +
                ", minAgePreference=" + minAgePreference +
                ", maxAgePreference=" + maxAgePreference +
                ", active=" + active +
                ", disabled='" + disabled + '\'' +
                ", images=" + images +
                ", genderPreferences=" + genderPreferences +
                '}';
    }
}