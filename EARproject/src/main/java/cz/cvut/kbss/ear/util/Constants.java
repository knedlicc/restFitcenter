package cz.cvut.kbss.ear.util;

import cz.cvut.kbss.ear.model.Roles;

public final class Constants {

    /**
     * Default user role.
     */
    public static final Roles DEFAULT_ROLE = Roles.USER;

    public static final Roles ADMIN_ROLE = Roles.ADMIN;

    private Constants() {
        throw new AssertionError();
    }
}

