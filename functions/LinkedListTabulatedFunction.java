package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {

    // внутренний класс узла списка
    private static class FunctionNode {
        public FunctionPoint val; // Значение точки
        public FunctionNode prev; // Ссылка на предыдущий элемент
        public FunctionNode next; // Ссылка на следующий элемент

        // конструктор для фиктивной головы
        public FunctionNode() {
            this.val = null;
            this.prev = this;
            this.next = this;
        }

        // конструктор с данными
        public FunctionNode(FunctionPoint point) {
            this.val = point;
            this.prev = null;
            this.next = null;
        }
    }

    private FunctionNode head; // ссылка на голову
    private int count; // количество элементов в списке
    private static final double EPSILON = 1e-9;

    /*
     * Конструктор, создающий табулированную функцию по границам и количеству точек.
     * Значения Y по умолчанию равны 0.
     */
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX || Math.abs(leftX - rightX) < EPSILON) {
            throw new IllegalArgumentException("The left boundary is bigger than the right");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Point number must be bigger than 2");
        }

        // инициализация фиктивной головы
        head = new FunctionNode();
        count = 0;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            addNodeToTail().val = new FunctionPoint(x, 0);
        }
    }

    // Конструктор, создающий табулированную функцию по границам и массиву значений
    // Y.
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX || Math.abs(leftX - rightX) < EPSILON) {
            throw new IllegalArgumentException("The left boundary is bigger than the right");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Point number must be bigger than 2");
        }

        // инициализация фиктивной головы
        head = new FunctionNode();
        count = 0;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + step * i;
            addNodeToTail().val = new FunctionPoint(x, values[i]);
        }
    }

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

        // поиск
        if (index < count / 2) {
            // Идем от начала (head.next - это первый реальный элемент)
            current = head.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            // идем с конца
            current = head.prev;
            for (int i = count - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    // Добавляет новый (пустой) элемент в конец списка и возвращает ссылку на него.
    public FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode();
        FunctionNode last = head.prev;

        // вставка между last и head
        newNode.prev = last;
        newNode.next = head;
        last.next = newNode;
        head.prev = newNode;

        count++;
        return newNode;
    }

    /*
     * Добавляет новый элемент в указанную позицию списка.
     * Индекс 0 — вставка перед первым значащим элементом.
     */
    public FunctionNode addNodeByIndex(int index) {
        // находим узел, перед которым будем вставлять
        // если index == count, вставляем перед head (то есть в хвост)
        FunctionNode nextNode = (index == count) ? head : getNodeByIndex(index);

        FunctionNode newNode = new FunctionNode();
        FunctionNode prevNode = nextNode.prev;

        prevNode.next = newNode;
        newNode.prev = prevNode;
        newNode.next = nextNode;
        nextNode.prev = newNode;

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

        FunctionNode prevNode = nodeToDelete.prev;
        FunctionNode nextNode = nodeToDelete.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        // обнуляем ссылки узла
        nodeToDelete.prev = null;
        nodeToDelete.next = null;

        count--;
        return data;
    }

    public double getLeftDomainBorder() {
        if (count == 0)
            throw new IllegalStateException("List is empty");
        return head.next.val.getX();
    }

    public double getRightDomainBorder() {
        if (count == 0)
            throw new IllegalStateException("List is empty");
        return head.prev.val.getX();
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

        double left = getLeftDomainBorder();
        double right = getRightDomainBorder();

        if (x < left - EPSILON || x > right + EPSILON) {
            return Double.NaN;
        }

        // если x совпадает с границами
        if (Math.abs(x - left) < EPSILON)
            return head.next.val.getY();
        if (Math.abs(x - right) < EPSILON)
            return head.prev.val.getY();

        // проход по списку
        FunctionNode current = head.next;
        while (current != head.prev) {
            double currentX = current.val.getX();
            double nextX = current.next.val.getX();

            if (Math.abs(currentX - x) < EPSILON)
                return current.val.getY();

            if (currentX < x && nextX > x) {
                return linearInterpolation(x, currentX, current.val.getY(), nextX, current.next.val.getY());
            }
            current = current.next;
        }

        // проверка последнего узла
        if (Math.abs(head.prev.val.getX() - x) < EPSILON)
            return head.prev.val.getY();

        return Double.NaN;
    }

    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).val);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        // проверка на нарушение упорядоченности
        double prevX = (node.prev == head) ? Double.NEGATIVE_INFINITY : node.prev.val.getX();
        double nextX = (node.next == head) ? Double.POSITIVE_INFINITY : node.next.val.getX();
        double newX = point.getX();

        if (newX <= prevX + EPSILON || newX >= nextX - EPSILON) {
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

        double prevX = (node.prev == head) ? Double.NEGATIVE_INFINITY : node.prev.val.getX();
        double nextX = (node.next == head) ? Double.POSITIVE_INFINITY : node.next.val.getX();

        if (x <= prevX + EPSILON || x >= nextX - EPSILON) {
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
        if (count == 0) {
            FunctionNode newNode = addNodeToTail();
            newNode.val = new FunctionPoint(point);
            return;
        }

        FunctionNode current = head.next;
        int index = 0;

        // ищем позицию для вставки
        while (current != head && current.val.getX() < point.getX()) {
            current = current.next;
            index++;
        }

        // проверка на дубликат X
        if (current != head && Math.abs(current.val.getX() - point.getX()) < EPSILON) {
            throw new InappropriateFunctionPointException("Point with this X already exists");
        }

        // вставляем новую точку по индексу перед current
        FunctionNode newNode = addNodeByIndex(index);
        newNode.val = new FunctionPoint(point);
    }
}