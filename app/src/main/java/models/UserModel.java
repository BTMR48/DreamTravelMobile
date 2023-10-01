package models;



public abstract class UserModel {
    private String userID;
    private String username;
    private String email;
    private String phone;

    private int age;
    private String gender;



    public UserModel() {
    }

    public UserModel(String userID, String username, String email, String phone, int age, String gender) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.phone = phone;

        this.age = age;
        this.gender = gender;


    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }




}
