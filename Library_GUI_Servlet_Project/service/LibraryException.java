package service;
public class LibraryException extends Exception {
    public LibraryException(String msg) { super(msg); }
    public LibraryException(String msg, Throwable t) { super(msg, t); }
}
