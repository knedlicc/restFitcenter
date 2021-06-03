package cz.cvut.kbss.ear.model;

public enum Roles {
    ADMIN("ROLE_ADMIN"), USER("ROLE_USER"), GUEST("ROLE_GUEST");

    private final String name;

    Roles(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
