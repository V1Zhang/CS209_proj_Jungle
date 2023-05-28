package controller;


public class AI {
    public static boolean mode;
    public static int difficulty;
    public static void startAIMode(){
        mode=true;
    }
    public static void quitAIMode(){
        mode=false;
    }
    public static void random(){
        difficulty=1;
    }
    public static void strategy(){
        difficulty=2;
    }
}






