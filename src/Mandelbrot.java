import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator {

    public static final int MAX_I = 2000;

    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -1.5;
        range.width = 3;
        range.height = 3;
    }



    @Override
    public int numIterations(double x, double y) {

        double re = 0; // real -> действительная часть
        double im = 0; // imaginary -> мнимая часть

        for (int i = 0; i < MAX_I; i++){
            double reNew = re * re - im * im + x; // x - действ.
            double imNew = 2 * re * im + y; // y - мним.
            re = reNew;
            im = imNew;

            if (re * re + im * im > 2 * 2){
                return i;
            }
        }

        return -1;
    }

    public String toString() {
        return "Mandelbrot";
    }
}
