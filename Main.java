import functions.*;

public class Main {
    public static void main(String[] args) {

        // 1. Создание функции
        System.out.println("1. Создаем функцию и заполняем базовыми значениями (y = 2x)");
        TabulatedFunction fun = new LinkedListTabulatedFunction(0, 20, 5);

        for (int i = 0; i < fun.getPointsCount(); i++) {
            fun.setPointY(i, 2 * fun.getPointX(i));
        }

        // Показываем, что получилось
        printFunction(fun);
        System.out.println("Интервал X: от " + fun.getLeftDomainBorder() + " до " + fun.getRightDomainBorder());

        // 2. Проверка вычислений
        System.out.println("\n2. Проверяем вычисление значений (интерполяцию):");
        // Проходимся по значениям x с шагом 0.5, чтобы попасть между точками
        for (double x = 0.0; x <= 1.1; x += 0.5) {
            System.out.printf("При x = %.1f, y = %.4f%n", x, fun.getFunctionValue(x));
        }

        // 3. Изменение существующей точки
        System.out.println("\n3. Тестируем изменение точки (setPoint):");
        FunctionPoint newPoint = new FunctionPoint(1, 2.005);

        try {
            fun.setPoint(1, newPoint);
            System.out.println("Точка с индексом 1 успешно заменена на (1, 2.005).");
        } catch (Exception e) {
            System.err.println("Ошибка при замене точки: " + e.getMessage());
        }
        printFunction(fun);

        // Попытка сломать порядок сортировки
        System.out.println("Пробуем вставить некорректную точку (x=-10 на позицию 1)...");
        try {
            fun.setPoint(1, new FunctionPoint(-10, 5));
            System.err.println("Ошибка! Программа пропустила некорректную точку.");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Все верно, поймали ошибку: нарушение порядка координат.");
        }

        // 4. Изменение только координаты X
        System.out.println("\n4. Тестируем изменение X (setPointX):");
        try {
            fun.setPointX(1, 4); // Меняем x=1 на x=4 (это допустимо, так как порядок сохраняется)
            System.out.println("Координата X точки [1] успешно изменена на 4.");
        } catch (Exception e) {
            System.err.println("Ошибка изменения X: " + e.getMessage());
        }
        printFunction(fun);

        // Попытка задать X, который нарушит сортировку
        System.out.println("Пробуем задать x=600 для точки [1] (должно быть запрещено)...");
        try {
            fun.setPointX(1, 600);
            System.err.println("Ошибка! Некорректный X был принят.");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Система сработала верно, изменение отклонено.");
        }

        // 5. Добавление новой точки
        System.out.println("\n5. Добавляем новую точку (addPoint):");
        try {
            fun.addPoint(new FunctionPoint(15.5, 31));
            System.out.println("Точка (15.5, 31) добавлена.");
        } catch (Exception e) {
            System.err.println("Не удалось добавить точку: " + e.getMessage());
        }
        printFunction(fun);

        // Проверка на дубликаты
        System.out.println("Пробуем добавить точку с уже существующим X (15.5)...");
        try {
            fun.addPoint(new FunctionPoint(15.5, 100));
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Верно, дубликаты X запрещены.");
        }

        // 6. Удаление точек
        System.out.println("\n6. Удаляем точки (deletePoint):");

        // Удаляем корректную точку
        fun.deletePoint(1);
        System.out.println("Точка с индексом 1 удалена.");
        printFunction(fun);

        // Пытаемся удалить несуществующую
        System.out.println("Пробуем удалить точку с индексом 100...");
        try {
            fun.deletePoint(100);
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("Поймали ошибку выхода за границы массива (как и ожидалось).");
        }

        // 7. Тест ограничений списка
        System.out.println("\n7. Тест на минимальное количество точек:");
        System.out.println("Сейчас точек: " + fun.getPointsCount());
        System.out.println("Удаляем точки по одной, пока не сработает ограничение...");

        try {
            while (fun.getPointsCount() > 0) {
                fun.deletePoint(0);
                System.out.println("Удалили точку [0]. Осталось: " + fun.getPointsCount());
            }
        } catch (IllegalStateException e) {
            System.out.println("Стоп. Сработала защита: " + e.getMessage());
        }

        System.out.println("\nТесты завершены.");
    }

    // Простой метод для вывода списка точек
    static private void printFunction(TabulatedFunction func) {
        System.out.print("Текущие точки: ");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.print("(" + func.getPointX(i) + "; " + func.getPointY(i) + ") ");
        }
        System.out.println();
    }

}