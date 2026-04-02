package jongwon.e_commerce.common.interceptor;

public class TestPhaseContext {

    private static final ThreadLocal<String> phase = new ThreadLocal<>();

    public static void set(String value){
        phase.set(value);
    }

    public static String get(){
        return phase.get();
    }

    public static void clear(){
        phase.remove();;
    }
}
