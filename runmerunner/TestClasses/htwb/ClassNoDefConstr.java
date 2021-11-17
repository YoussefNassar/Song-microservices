package htwb;

import htwb.ai.RunMe;

public class ClassNoDefConstr {
    
    private int luckyNumber;
    
    public ClassNoDefConstr(int arg) {
        this.luckyNumber = arg;
    }
    
    @RunMe
    public boolean testMethod1() {
        System.out.println("Ich bin protected!");
        return true;
    }

    public int getLuckyNumber () { return luckyNumber;} 
}
