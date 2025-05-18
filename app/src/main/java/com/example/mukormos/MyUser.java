package com.example.mukormos;

public class MyUser {
    private String id;
    private String email;
    private String username;
    private Name name;

    public MyUser(String id, String email, String username, Name name) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.name = name;
    }

    // Getterek és setterek
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    // Belső Name osztály
    public static class Name {
        private String firstname;
        private String lastname;

        public Name(String firstname, String lastname) {
            this.firstname = firstname;
            this.lastname = lastname;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }
    }
}
