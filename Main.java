import functions.*;

public class Main {
    public static void main(String[] args) {
        // Создаем объект через интерфейс.
        // Для проверки ArrayTabulatedFunction просто раскомментируйте вторую строчку.
        TabulatedFunction fun = new LinkedListTabulatedFunction(0, 20, 5);
        // TabulatedFunction fun = new ArrayTabulatedFunction(0, 20, 5);

        System.out.println("Function created using " + fun.getClass().getSimpleName());

        // пытаемся заполнить y значениями 2*x
        for (int i = 0; i < fun.getPointsCount(); i++) {
            fun.setPointY(i, 2 * fun.getPointX(i));
        }

        System.out.println("Number of points: " + fun.getPointsCount());

        // Вывод точек
        for (int i = 0; i < fun.getPointsCount(); i++) {
            System.out.print("P(" + fun.getPointX(i) + ") = ");
            System.out.println(fun.getPointY(i));
        }

        System.out.println("x belongs to [" + fun.getLeftDomainBorder() + "; " + fun.getRightDomainBorder() + "]");

        // Проверка интерполяции
        System.out.println("\n--- Interpolation Check ---");
        for (double i = 0.0; i <= 1.1; i += 0.5) {
            System.out.println("x = " + i + " y = " + fun.getFunctionValue(i));
        }

        printFunction(fun);

        // ----------------------------------------------------
        // ПРОВЕРКА setPoint
        // ----------------------------------------------------
        System.out.println("\n--- Testing setPoint ---");
        FunctionPoint newPoint = new FunctionPoint(1, 2.005);

        try {
            // Пытаемся вставить точку (1, 2.005) на позицию 1.
            // В исходной функции: 0->0, 5->10. Точка с X=1 допустима между 0 и 5.
            fun.setPoint(1, newPoint);
            System.out.println("Point [1] set successfully.");
        } catch (InappropriateFunctionPointException e) {
            System.err.println("Error setting point: " + e.getMessage());
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.err.println("Index out of bounds: " + e.getMessage());
        }
        printFunction(fun);

        // Попытка вставить некорректную точку (нарушение порядка X)
        System.out.println("Attempting to set invalid point (x=-10 at index 1)...");
        try {
            fun.setPoint(1, new FunctionPoint(-10, 5)); // Должно упасть, т.к. слева x=0
        } catch (InappropriateFunctionPointException e) {
            System.out.println("CAUGHT EXPECTED EXCEPTION: InappropriateFunctionPointException (Order violation)");
        } catch (Exception e) {
            System.err.println("Wrong exception caught: " + e);
        }

        // ----------------------------------------------------
        // ПРОВЕРКА setPointX
        // ----------------------------------------------------
        System.out.println("\n--- Testing setPointX ---");

        // Корректное изменение
        try {
            fun.setPointX(1, 4); // X=4 допустим (между 0 и 5)
            System.out.println("SetPointX point [1] to x=4: Success");
        } catch (InappropriateFunctionPointException e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        printFunction(fun);

        // Некорректное изменение (нарушение порядка)
        System.out.println("Attempting SetPointX [1] to 600 (should fail)...");
        try {
            fun.setPointX(1, 600);
        } catch (InappropriateFunctionPointException e) {
            System.out.println("CAUGHT EXPECTED EXCEPTION: InappropriateFunctionPointException (Order violation)");
        }

        // ----------------------------------------------------
        // ПРОВЕРКА addPoint
        // ----------------------------------------------------
        System.out.println("\n--- Testing addPoint ---");

        FunctionPoint addPt = new FunctionPoint(15.5, 31);
        try {
            fun.addPoint(addPt);
            System.out.println("Added point (15.5, 31)");
        } catch (InappropriateFunctionPointException e) {
            System.err.println("Error adding point: " + e.getMessage());
        }
        printFunction(fun);

        // Попытка добавить существующий X
        System.out.println("Attempting to add duplicate X=15.5...");
        try {
            fun.addPoint(new FunctionPoint(15.5, 100));
        } catch (InappropriateFunctionPointException e) {
            System.out.println("CAUGHT EXPECTED EXCEPTION: InappropriateFunctionPointException (Duplicate X)");
        }

        // ----------------------------------------------------
        // ПРОВЕРКА deletePoint
        // ----------------------------------------------------
        System.out.println("\n--- Testing deletePoint ---");

        // Удаление существующей точки
        fun.deletePoint(1);
        System.out.println("Deleted point at index 1");
        printFunction(fun);

        // Попытка удалить точку с неверным индексом
        System.out.println("Attempting delete at index 100...");
        try {
            fun.deletePoint(100);
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("CAUGHT EXPECTED EXCEPTION: FunctionPointIndexOutOfBoundsException");
        }

        // Попытка удалить точки до количества < 3 (если требуется такая проверка)
        // Сейчас у нас 5 точек (было 5, заменили, добавили 1, удалили 1 -> итого 5)
        // Удалим лишние, чтобы проверить исключение при малом количестве
        try {
            while (fun.getPointsCount() > 2) {
                fun.deletePoint(0);
            }
            System.out.println("Points remaining: " + fun.getPointsCount());
            System.out.println("Attempting to delete one more point...");
            fun.deletePoint(0);
        } catch (IllegalStateException e) {
            System.out.println("CAUGHT EXPECTED EXCEPTION: " + e.getMessage());
        }
    }

    // Метод теперь принимает интерфейс, чтобы работать и с Array, и с LinkedList
    // реализациями
    static private void printFunction(TabulatedFunction func) {
        System.out.print("Function state: [ ");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.print("(" + func.getPointX(i) + ", " + func.getPointY(i) + ") ");
        }
        System.out.println("]");
    }

}