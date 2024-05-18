package client;

public enum option {
    Receiver, Sender;

    private option() {}

    public String value(){
        return name();
    }

    public static option fromvalue(String v){
        return valueOf(v);
    }
}
