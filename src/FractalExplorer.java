import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionListener;

public class FractalExplorer {

    private int displaySize;
    private JImageDisplay jImageDisplay; // Обновление отображения
    private FractalGenerator fractalGenerator;
    private Rectangle2D.Double range; // Диапозон комплексной плоскости для вывода на экран
    private JComboBox ComboBox; // Для управления коллекцией объекта
    private int rowsRemain; // количество оставшихся строк, которые должны быть завершены
    private JButton reset; // кнопка сброса изображения
    private JButton save; // кнопка сохранения изображения


    //Сохраняет размер изображения
    public FractalExplorer(int size) {
        this.displaySize = size;
        this.fractalGenerator = new Mandelbrot();
        this.range = new Rectangle2D.Double();
        this.fractalGenerator.getInitialRange(this.range);

    }

    public static void main (String[] args) {
        FractalExplorer fractalExplorer = new FractalExplorer(800);
        fractalExplorer.createAndShowGUI();
        fractalExplorer.drawFractal();
    }

    public void createAndShowGUI(){
        jImageDisplay = new JImageDisplay(displaySize, displaySize);
        JFrame frame = new JFrame("Fractal Explorer"); // инициализация (создание) окна
        frame.setLayout(new BorderLayout());
        frame.add(jImageDisplay, BorderLayout.CENTER); // отображение изображения в центре

        JPanel buttons = new JPanel(); // панель с кнопками


        // СБрос отображения
        reset = new JButton("Reset Display");
        buttons.add(reset);
        reset.addActionListener(new buttonReset());


        //Сохраняем изображение
        save = new JButton("Save Image");
        buttons.add(save);
        save.addActionListener(new buttonSave());

        frame.add(buttons, BorderLayout.SOUTH);

        // Мышка
        jImageDisplay.addMouseListener(new MouseListener());

        // Выбор фрактала
        ComboBox = new JComboBox<>();
        ComboBox.addItem(new Mandelbrot());
        ComboBox.addItem(new Tricorn());
        ComboBox.addItem(new BurningShip());

        // Пояснение к выпадающему списку
        JPanel Panel = new JPanel();
        JLabel label = new JLabel("Choose fractal");
        Panel.add(label);
        Panel.add(ComboBox);
        frame.add(Panel, BorderLayout.NORTH);

        // взаимодействие со списком
        ComboBox.addActionListener(new ComboActionListener());


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack(); // правильно размещает содержимле окна
        frame.setVisible(true); // делает окно видимым
        frame.setResizable(false); // запрет изменения размеров окна
    }

    private void drawFractal(){
        enableUI (false); // отключаем все элементы пользовательского интерфейса во время рисования
        rowsRemain = displaySize; // сколько строк нужно нарисовать
        // проходим через каждую строку в отображении
        for (int i = 0; i < displaySize; i++){
            FractalWorker row = new FractalWorker(i); // создаём отдельный рабочий объект
            row.execute(); // запускаем фоновый поток и задачу в фоновом режиме
        }
    }
    private void enableUI(boolean val) {
        reset.setEnabled(val);
        save.setEnabled(val);
        ComboBox.setEnabled(val);
    }


    private class buttonReset implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fractalGenerator.getInitialRange(range); //сброс диапазона к начальному
            drawFractal();
        }
    }

    // для сохранения изображения
    private class buttonSave implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
            chooser.setFileFilter(filter);
            // средство выбора не разрешит пользователю использование отличных от png форматов
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showSaveDialog(jImageDisplay) == JFileChooser.APPROVE_OPTION) {
                try {
                    // сохранение
                    ImageIO.write(jImageDisplay.picture, "png", chooser.getSelectedFile());
                } catch (Exception ee) {
                    // сообщение об ошибке
                    JOptionPane.showMessageDialog(jImageDisplay, ee.getMessage(),
                            "Cannot Save Image", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    // обработка события о щелчке мыши (щелчком увеличиваем изображение)
    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e){
            // если значение rows remaining не равно нулю, сразу возвращаемся в предыдущее состояние
            if (rowsRemain != 0){
                return;
            }
            int x = e.getX();
            int y = e.getY();
            double xCoord = FractalGenerator.getCoord (range.x, range.x + range.width, displaySize, x);
            double yCoord = FractalGenerator.getCoord (range.y, range.y + range.height, displaySize, y);
            fractalGenerator.recenterAndZoomRange(range, xCoord, yCoord, 0.5);
            drawFractal();
        }
    }

    private class ComboActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fractalGenerator = (FractalGenerator) ComboBox.getSelectedItem();
            fractalGenerator.getInitialRange(range); //сброс диапазона к начальному
            drawFractal(); // перерисовываем фрактал
        }
    }


    // класс для вычисление значений цвета для одной строки фрактала
    private class FractalWorker extends SwingWorker<Object, Object> {
        private int yCoordinate; // целочисленная y-координата вычисляемой строки
        private int[] color; // массив для хранения вычисленных значений RGB для каждого пикселя в этой строке

        public FractalWorker(int y){
            this.yCoordinate = y;
        }


        public Object doInBackground(){
            color = new int[displaySize]; // выделение памяти для массив целых чисел
            int rgbColor;
            // проходим через каждый пиксель в отображении
            for (int x = 0; x < displaySize; x++){

                //x - пиксельная координата; xCoord - координата в пространстве фрактала
                double xCoord = FractalGenerator.getCoord
                        (range.x, range.x + range.width, displaySize, x);
                double yCoord = FractalGenerator.getCoord
                        (range.y,range.y + range.height, displaySize, yCoordinate);

                //количество итераций для соответствующих координат в области отображения фрактала
                int numIters = fractalGenerator.numIterations(xCoord, yCoord);

                if (numIters == -1){
                    // если точка не выходит за границы (число итераций == -1), красим пиксель в чёрный
                    rgbColor = 0;
                } else {
                    float hue = 0.7f + (float) numIters / 200f;
                    rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                }
                color[x] = rgbColor;
            }
            return null;
        }

        public void done(){
            // рисуем пиксели в строке
            for (int x = 0; x < displaySize; x++) {
                jImageDisplay.drawPixel(x, yCoordinate, color[x]);
            }
            // перерисовываем указанную область после того, как строка будет вычислена
            jImageDisplay.repaint(0, 0, yCoordinate, displaySize, 1);
            rowsRemain --; // уменьшаем на единицу
            if (rowsRemain == 0){
                enableUI (true);
            }
        }
    }
}





