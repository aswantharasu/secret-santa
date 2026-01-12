package com.digitalxc.secretsanta.model;

public final class Assignment {

    private final Participant giver;
    private final Participant receiver;

    public Assignment(Participant giver, Participant receiver) {
        this.giver = giver;
        this.receiver = receiver;
    }

    public Participant getGiver() {
        return giver;
    }

    public Participant getReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", giver, receiver);
    }
}
