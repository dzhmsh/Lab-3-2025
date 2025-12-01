package functions;

//исключение выхода за границы набора точек при обращении к ним по номеру
public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {

    public FunctionPointIndexOutOfBoundsException() {
        super();
    }

    public FunctionPointIndexOutOfBoundsException(String s) {
        super(s);
    }

}
