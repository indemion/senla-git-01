// Вариант 4: сборочная линия автоматических шариковых ручек (корпус, пружина, стержень)

public class PenAssemblyLineTesting {
    public static void main(String[] args) {
        PenAssemblyLine penAssemblyLine = new PenAssemblyLine(new PenBodyBuilder(), new PenSpringBuilder(), new PenRodBuilder());
        Pen pen = new Pen();
        penAssemblyLine.assembleProduct(pen);
        System.out.println("Pen assembly completed");
    }
}
