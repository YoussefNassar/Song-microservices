package htwb.ai;

public class MeanTestMethods {

    private String text;

    public MeanTestMethods(String text) {
        this.text = text;
    }


    public MeanTestMethods() {
        this.text = "default constructor was used";
    }

    public void printa() {
        System.out.println("a");
    }

    private void private_printa() {
        System.out.println("a");
    }

    public void argument_printa(String b){
        System.out.println("a" + b);
    }

    @RunMe
    private void runme_private_printa(){
        System.out.println("a");
    }

    @RunMe
    public void runme_public_printa(){
        System.out.println("a");
    }

    @RunMe
    public void runme_public_argument_printa(String b){
        System.out.println("a" + b);
    }

    @RunMe
    private void runme_private_argument_printa(String b){
        System.out.println("a" + b);
    }

    @RunMe
    public static void runme_static_public_printa(){
        System.out.println("a");
    }

}
