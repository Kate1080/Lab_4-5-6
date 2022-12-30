import java.awt.geom.Rectangle2D;

public class Tricorn extends FractalGenerator{
    public static final int MAX_ITERATIONS = 2000; //Максимальное количество итераций в цикле

    // Начальный диапозон (-2-2i)-(2+2i)
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2;
        range.width = 4;
        range.height = 4;
    }


    // z(n) = (z(n-1))^2 + c
    // если |z| > 2 (точка находится не во множестве Мандельброта) -> выходим из цикла
    public int numIterations(double x, double y) {

        double re = 0;
        double im = 0;

        for (int count = 0; count < MAX_ITERATIONS; count++) {
            double reNew = re * re - im * im + x; // x - действ.
            double imNew = -2 * re * im + y; // y - мним. (перед 2 минус т.к. компл. сопряжение)
            re = reNew;
            im = imNew;

            if (re * re + im * im > 2 * 2) {
                return count;
            }
        }
        return -1;
    }

    public String toString() {
        return "Tricorn";
    }
}
