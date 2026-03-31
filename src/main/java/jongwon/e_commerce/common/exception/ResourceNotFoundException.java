package jongwon.e_commerce.common.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String datasource, long id){
        super("Could not find " + id + " from " + datasource);
    }

    public ResourceNotFoundException(String datasource, String id){
        super("Could not find " + id + " from " + datasource);
    }
}
