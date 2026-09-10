package com.airtribe.librarysystem.util;

public final class IdGenerator {

    private static int patronIdCounter = 1;
    private static int branchIdCounter = 1;

    private IdGenerator() {
    }

    public static String getNextPatronId() {
        return "P" + (patronIdCounter++);
    }

    public static String getNextBranchId() {
        return "B" + (branchIdCounter++);
    }
}
