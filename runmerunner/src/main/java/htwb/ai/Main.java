package htwb.ai;

public class Main {

    public static void main(String[] args) {

        if(args.length == 0) {
            System.out.println("Please provide a class!");
            System.exit(0);
        }

        String className = args[0];
        ClassProcessor c = new ClassProcessor(className);
        c.start();

    }

}
