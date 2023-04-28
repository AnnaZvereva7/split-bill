package edu.practicum.splitbill.model;

public class Participant implements Comparable<Participant> {
    private final String name;
    double debt; //expenses-payment


    public Participant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    @Override
    public int compareTo(Participant p) {
        return (int)(this.debt-p.debt);
    }

    @Override
    public String toString() {
        return "name:"+name+ " debt:" + (int) debt;
    }
}
