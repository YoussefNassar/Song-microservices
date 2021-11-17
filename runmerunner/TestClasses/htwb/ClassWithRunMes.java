package htwb;

import htwb.ai.RunMe;

public class ClassWithRunMes {

    @RunMe
    public static String methodOk1() {
        System.out.println("In methodOk1: ich bin public und static!");
        return "method".toUpperCase();
    }

    @RunMe
    public boolean methodOk2() {
        System.out.println("In methodOk2: ich bin public!");
        return true;
    }
    
    @RunMe
    String methodPackagePrivate() {
        System.out.println("In methodPackagePrivate: ich bin package-private!");
        return "methodPackagePrivate".toUpperCase();
    }

    @RunMe
    protected void methodProtected() {
        System.out.println("In method2: ich bin protected");
    }

    @RunMe
    private boolean methodPrivate() {
        System.out.println("In methodPrivate: ich bin privat!");
        return true;
    }

    @RunMe
    public boolean methodWithArg(String input) {
        System.out.println("In methodWithArg: ich bekomme einen Parameter!");
        return true;
    }

    @RunMe
    public String methodBOOM() {
        System.out.println("In methodBOOM: ich bin kaputt!");
        String npe = null;
        if (npe.startsWith("BOOM")) {
            return "x";
        }
        return "y";
    }

    public void noRmR1() {
        System.out.println("noRmR1: Ich werde nicht aufgerufen!");
    }

    void noRmR2() {
        System.out.println("noRmR2: Ich werde nicht aufgerufen!");
    }

    public static void noRmR3() {
        System.out.println("noRmR3: Ich werde nicht aufgerufen!");
    }
}
