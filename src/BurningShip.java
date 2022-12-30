import java.awt.geom.Rectangle2D;


public class BurningShip extends FractalGenerator{

    public static final int MAX_I = 2000;

    // начальный диапазон (-2-2.5i)-(2+1.5i)
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2;
        range.width = 4;
        range.height = 4;
    }


    // z(n) = (|re(z(n-1))| + i |im(z(n-1))|)^2 + c
    public int numIterations(double x, double y) {

        double re = 0;
        double im = 0;

        for (int count = 0; count < MAX_I; count++){
            double reNew = re * re - im * im + x;
            double imNew = Math.abs(2 * re * im) + y;
            re = reNew;
            im = imNew;
            if (re * re + im * im > 2 * 2){
                return count;
            }
        }
        return -1;
    }


    public String toString() {
        return "BurningShip";
    }
}
