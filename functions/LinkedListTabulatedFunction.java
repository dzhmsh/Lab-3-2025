package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {

    // Внутренний класс узла списка
    private static class FunctionNode {
        public FunctionPoint val; // Значение точки (данные)
        public FunctionNode prev; // Ссылка на предыдущий элемент
        public FunctionNode next; // Ссылка на следующий элемент

        // Конструктор по умолчанию
        public FunctionNode() {
            this.val = null;
            this.prev = null;
            this.next = null;
        }

        // Конструктор с данными
        public FunctionNode(FunctionPoint point) {
            this.val = point;
            this.prev = null;
            this.next = null;
        }
    }

    private FunctionNode head; // Ссылка на "голову" списка (первый элемент)
    private int count; // Количество элементов в списке

    // -------------------------------------------------------------------------
    // КОНСТРУКТОРЫ
    // -------------------------------------------------------------------------

    /*
     * Конструктор, создающий табулированную функцию по границам и количеству точек.
     * Значения Y по умолчанию равны 0.
     */
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("The left boundary is bigger than the right");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Point number must be bigger than 2");
        }

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            // Создаем точку и добавляем её в конец списка
            double x = leftX + step * i;
            FunctionNode node = addNodeToTail();
            node.val = new FunctionPoint(x, 0);
        }
    }

    /*
     * Конструктор, создающий табулированную функцию по границам и массиву значений
     * Y.
     */
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("The left boundary is bigger than the right");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Point number must be bigger than 2");
        }

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + step * i;
            FunctionNode node = addNodeToTail();
            node.val = new FunctionPoint(x, values[i]);
        }
    }

    // -------------------------------------------------------------------------
    // ВНУТРЕННИЕ МЕТОДЫ РАБОТЫ СО СПИСКОМ
    // -------------------------------------------------------------------------

    /*
     * Возвращает ссылку на объект элемента списка по его номеру.
     * Реализована оптимизация: поиск с головы или с хвоста в зависимости от
     * индекса.
     */
    public FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        FunctionNode current;

        // Оптимизация: если индекс в первой половине списка — идем с начала, иначе с
        // конца
        if (index < count / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = head.prev; // Последний элемент
            for (int i = count - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    /*
     * Добавляет новый (пустой) элемент в конец списка и возвращает ссылку на него.
     */
    public FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode();

        if (head == null) {
            // Если список пуст, создаем первый элемент, замкнутый сам на себя
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            // Вставляем между последним (head.prev) и первым (head)
            FunctionNode last = head.prev;
            newNode.prev = last;
            newNode.next = head;
            last.next = newNode;
            head.prev = newNode;
        }
        count++;
        return newNode;
    }

    /*
     * Добавляет новый элемент в указанную позицию списка.
     * Индекс 0 — вставка перед головой (смещение головы).
     */
    public FunctionNode addNodeByIndex(int index) {
        // Если вставка в конец (или список пуст) — используем addNodeToTail
        if (index == count) {
            return addNodeToTail();
        }

        // Находим узел, который сейчас стоит на этом месте
        FunctionNode currentNode = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode();

        // Вставка перед currentNode
        FunctionNode prevNode = currentNode.prev;

        prevNode.next = newNode;
        newNode.prev = prevNode;
        newNode.next = currentNode;
        currentNode.prev = newNode;

        // Если вставили на место 0, обновляем head
        if (index == 0) {
            head = newNode;
        }

        count++;
        return newNode;
    }

    /*
     * Удаляет элемент списка по номеру и возвращает объект-точку удаленного
     * элемента.
     */
    public FunctionPoint deleteNodeByIndex(int index) {
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException();
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);
        FunctionPoint data = nodeToDelete.val;

        // Если это единственный элемент в списке
        if (count == 1) {
            head = null;
            count = 0;
            return data;
        }

        FunctionNode prevNode = nodeToDelete.prev;
        FunctionNode nextNode = nodeToDelete.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        // Если удаляем голову, смещаем указатель head
        if (index == 0) {
            head = nextNode;
        }

        count--;
        return data; // Возвращаем данные удаленного узла
    }

    // -------------------------------------------------------------------------
    // МЕТОДЫ TABULATED FUNCTION
    // -------------------------------------------------------------------------

    public double getLeftDomainBorder() {
        if (head == null)
            throw new IllegalStateException("List is empty");
        return head.val.getX();
    }

    public double getRightDomainBorder() {
        if (head == null)
            throw new IllegalStateException("List is empty");
        return head.prev.val.getX(); // head.prev — это хвост списка
    }

    public int getPointsCount() {
        return count;
    }

    private double linearInterpolation(double x, double x0, double y0, double x1, double y1) {
        return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
    }

    public double getFunctionValue(double x) {
        if (count == 0)
            return Double.NaN;
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        // Проход по списку для поиска интервала
        FunctionNode current = head;
        // Проверяем все точки
        for (int i = 0; i < count; i++) {
            if (current.val.getX() == x) {
                return current.val.getY();
            }
            // Если x попал в интервал между current и current.next
            // (при условии, что мы не на последнем элементе)
            if (i < count - 1 && current.val.getX() < x && current.next.val.getX() > x) {
                return linearInterpolation(x,
                        current.val.getX(), current.val.getY(),
                        current.next.val.getX(), current.next.val.getY());
            }
            current = current.next;
        }
        return Double.NaN;
    }

    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).val); // Возвращаем копию
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        // Проверка на нарушение упорядоченности
        double prevX = (index == 0) ? Double.NEGATIVE_INFINITY : node.prev.val.getX();
        double nextX = (index == count - 1) ? Double.POSITIVE_INFINITY : node.next.val.getX();

        if (point.getX() <= prevX || point.getX() >= nextX) {
            throw new InappropriateFunctionPointException();
        }

        node.val = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        return getNodeByIndex(index).val.getX();
    }

    public double getPointY(int index) {
        return getNodeByIndex(index).val.getY();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        double prevX = (index == 0) ? Double.NEGATIVE_INFINITY : node.prev.val.getX();
        double nextX = (index == count - 1) ? Double.POSITIVE_INFINITY : node.next.val.getX();

        if (x <= prevX || x >= nextX) {
            throw new InappropriateFunctionPointException();
        }
        node.val.setX(x);
    }

    public void setPointY(int index, double y) {
        getNodeByIndex(index).val.setY(y);
    }

    public void deletePoint(int index) {
        if (count < 3) {
            throw new IllegalStateException("Number of points is less than 3");
        }
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Если список пуст (теоретически невозможно при count >= 2, но для
        // безопасности)
        if (head == null) {
            FunctionNode node = addNodeToTail();
            node.val = new FunctionPoint(point);
            return;
        }

        FunctionNode current = head;
        int index = 0;

        // Ищем позицию для вставки
        while (index < count && current.val.getX() < point.getX()) {
            current = current.next;
            index++;
        }

        // Если нашли точку с таким же X
        if (index < count && current.val.getX() == point.getX()) {
            // Но если мы вышли из цикла потому что прошли весь список (index == count),
            // то current указывает на head (из-за цикличности), нужно проверить последний
            // элемент
            if (index == count) {
                // вставка в самый конец
            } else {
                throw new InappropriateFunctionPointException();
            }
        }

        // Проверка дубликата X при вставке в конец (нужна отдельная, т.к. цикл while
        // может закончиться)
        if (index == count && head.prev.val.getX() == point.getX()) {
            throw new InappropriateFunctionPointException();
        }

        // Вставляем новую точку по индексу
        FunctionNode newNode = addNodeByIndex(index);
        newNode.val = new FunctionPoint(point);
    }

}