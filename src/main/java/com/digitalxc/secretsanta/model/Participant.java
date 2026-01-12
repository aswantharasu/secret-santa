package com.digitalxc.secretsanta.model;

import java.util.Objects;

public final class Participant {

    private final String name;
    private final String email; // canonical lower-case email

    public Participant(String name, String email) {
        String e = email == null ? "" : email.trim().toLowerCase();
        if (e.isEmpty()) throw new IllegalArgumentException("email must be provided");
        this.email = e;
        this.name = name == null ? "" : name.trim();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;
        Participant that = (Participant) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return name.isEmpty() ? email : name + " <" + email + ">";
    }
}
