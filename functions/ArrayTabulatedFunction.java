package functions;

public class ArrayTabulatedFunction implements TabulatedFunction {

    private FunctionPoint[] arrayOfPoints;
    private int pointCount; // счетчик реальных точек
    private static final double EPSILON = 1e-9;

    /*
     * IllegalArgumentException, если левая граница области определения больше или
     * равна правой, а также если предлагаемое количество точек меньше двух.
     */

    // первый конструктор
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX || Math.abs(leftX - rightX) < EPSILON) {
            throw new IllegalArgumentException("\nThe left boundary is bigger than the right\n");
        }

        if (pointsCount < 2) {
            throw new IllegalArgumentException("\nPoint number must be bigger than 2\n");
        }
        this.arrayOfPoints = new FunctionPoint[pointsCount];
        this.pointCount = pointsCount;
        double step = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            arrayOfPoints[i] = new FunctionPoint(leftX + step * i, 0);
        }
    }

    // конструктор - вместо количества точек получает значения функции в виде
    // массива
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX || Math.abs(leftX - rightX) < EPSILON) {
            throw new IllegalArgumentException("\nThe left boundary is bigger than the right\n");
        }

        if (values.length < 2) {
            throw new IllegalArgumentException("\nPoint number must be bigger than 2\n");
        }
        int len = values.length;
        this.arrayOfPoints = new FunctionPoint[len];
        this.pointCount = values.length;
        double step = (rightX - leftX) / (len - 1);

        for (int i = 0; i < len; i++) {
            arrayOfPoints[i] = new FunctionPoint(leftX + step * i, values[i]);
        }
    }

    // возвращает значение левой границы
    public double getLeftDomainBorder() {
        return arrayOfPoints[0].getX();
    }

    // значение правой границы
    public double getRightDomainBorder() {
        return arrayOfPoints[pointCount - 1].getX();
    }

    // линейная интерполяция - вспомогательная функция
    private double linearInterpolation(double x, double x0, double y0, double x1, double y1) {
        double k = (y1 - y0) / (x1 - x0);
        double b = y0 - k * x0;
        double y = k * x + b;

        return y;
    }

    // должен возвращать значение функции в точке x
    public double getFunctionValue(double x) {
        if ((x > getRightDomainBorder() && Math.abs(x - getRightDomainBorder()) > EPSILON) ||
                (x < getLeftDomainBorder() && Math.abs(x - getLeftDomainBorder()) > EPSILON))
            return Double.NaN;

        if (Math.abs(x - arrayOfPoints[0].getX()) < EPSILON)
            return arrayOfPoints[0].getY();

        for (int i = 1; i < pointCount; i++) {
            if (Math.abs(arrayOfPoints[i].getX() - x) < EPSILON)
                return arrayOfPoints[i].getY();

            if (arrayOfPoints[i].getX() > x) {
                return linearInterpolation(x,
                        arrayOfPoints[i - 1].getX(),
                        arrayOfPoints[i - 1].getY(),
                        arrayOfPoints[i].getX(),
                        arrayOfPoints[i].getY());
            }
        }

        return Double.NaN;
    }

    // должен возвращать количество точек
    public int getPointsCount() {
        return pointCount;
    }

    /*
     * должны выбрасывать исключение FunctionPointIndexOutOfBoundsException, если
     * переданный в метод номер выходит за границы набора точек.
     */

    // должен возвращать копию точки, соответствующей переданному индексу
    public FunctionPoint getPoint(int index) {
        if (index >= this.pointCount || index < 0) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return new FunctionPoint(arrayOfPoints[index]);
    }

    // должен заменять указанную на копию переданной точки
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        // проверка индекса
        if (index < 0 || index >= pointCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        double newX = point.getX();

        // проверка нарушения упорядоченности (слева и справа)
        // используем EPSILON для сравнения
        if (index > 0 && (newX < arrayOfPoints[index - 1].getX()
                || Math.abs(newX - arrayOfPoints[index - 1].getX()) < EPSILON)) {
            throw new InappropriateFunctionPointException();
        }
        if (index < pointCount - 1 && (newX > arrayOfPoints[index + 1].getX()
                || Math.abs(newX - arrayOfPoints[index + 1].getX()) < EPSILON)) {
            throw new InappropriateFunctionPointException();
        }

        // создаем копию и присваиваем
        arrayOfPoints[index] = new FunctionPoint(point);
    }

    // должен возвращать значение абсциссы точки с указанным номером.
    public double getPointX(int index) {
        if (index >= this.pointCount || index < 0) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return arrayOfPoints[index].getX();
    }

    // должен возвращать значение ординаты точки с указанным номером
    public double getPointY(int index) {
        if (index >= this.pointCount || index < 0) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return arrayOfPoints[index].getY();
    }

    // должен изменять значение абсциссы точки с указанным номером.
    // не должен изменять точку, если новое значение попадает в другой интервал
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index >= this.pointCount || index < 0) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        if (pointCount == 1) {
            arrayOfPoints[index].setX(x);
            return;
        }

        // Проверка границ с EPSILON
        double leftBound = (index == 0) ? Double.NEGATIVE_INFINITY : arrayOfPoints[index - 1].getX();
        double rightBound = (index == pointCount - 1) ? Double.POSITIVE_INFINITY : arrayOfPoints[index + 1].getX();

        if (x > leftBound + EPSILON && x < rightBound - EPSILON) {
            arrayOfPoints[index].setX(x);
        } else {
            throw new InappropriateFunctionPointException();
        }
    }

    // должен изменять значение ординаты точки с указанным номером.
    public void setPointY(int index, double y) {
        if (index >= this.pointCount || index < 0) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        arrayOfPoints[index].setY(y);
    }

    // должен удалять заданную точку табулированной функции.
    public void deletePoint(int index) {
        if (pointCount < 3) {
            throw new IllegalStateException("Number of points is less than 3");
        }
        if (index >= this.pointCount || index < 0) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        if (pointCount - index - 1 > 0) {
            System.arraycopy(arrayOfPoints, index + 1, arrayOfPoints, index, pointCount - index - 1);
        }

        arrayOfPoints[pointCount - 1] = null;
        pointCount--;
    }

    // добавляем точку
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на совпадение X с EPSILON
        for (int i = 0; i < pointCount; i++) {
            if (Math.abs(arrayOfPoints[i].getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException("Point with this X already exists");
            }
        }

        FunctionPoint newPoint = new FunctionPoint(point);

        int insertIndex = 0;
        while (insertIndex < pointCount && arrayOfPoints[insertIndex].getX() < newPoint.getX()) {
            insertIndex++;
        }

        if (pointCount == arrayOfPoints.length) {
            FunctionPoint[] newArray = new FunctionPoint[arrayOfPoints.length * 2 + 1];
            System.arraycopy(arrayOfPoints, 0, newArray, 0, pointCount);
            arrayOfPoints = newArray;
        }

        if (pointCount - insertIndex > 0) {
            System.arraycopy(arrayOfPoints, insertIndex, arrayOfPoints, insertIndex + 1, pointCount - insertIndex);
        }

        arrayOfPoints[insertIndex] = newPoint;
        pointCount++;
    }

}