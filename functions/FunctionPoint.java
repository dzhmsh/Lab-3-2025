package functions;

public class FunctionPoint {
    private double x, y;

    // getter'y & setter'y

    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // создаёт объект точки с теми же координатами, что у указанной точки;
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    // – создаёт точку с координатами (0; 0).
    public FunctionPoint() {
        this.x = 0;
        this.y = 0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

}