import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class FirstInterface extends JFrame {

    private static final long serialVersionUID = 1L;
    public static final String RESULT_IMAGE_FILE_NAME = "atlas_texture.png";
    public static final String ATLAS_STRUCTURE_FILE_NAME = "atlas_structure.txt";
    JTextField sizeField, rowsField;
    int frameWidth, frameHeight;
    Font font;
    ArrayList<String> fileNames;
    ArrayList<BufferedImage> bufferedImages;
    JTextArea outputArea;

    private FirstInterface(){
        super("Atlas texture creator by yiotro");

        frameWidth = 400;
        frameHeight = 300;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        MyButtonListener myButtonListener = new MyButtonListener(this);

        font = new Font("Segoe UI", Font.PLAIN, 14);
        setFont(font);

        setLayout(null);

        outputArea = new JTextArea("Program successfully started\n");
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollPane2 = new JScrollPane(outputArea);
        scrollPane2.setBounds(5, 5, frameWidth - 20, frameHeight - 75);
        add(scrollPane2);

        JLabel sizeLabel = new JLabel("Сжатие");
        sizeLabel.setBounds(5, frameHeight - 65, 50, 30);
        sizeLabel.setFont(font);
        add(sizeLabel);

        sizeField = new JTextField("3");
        sizeField.setFont(font);
        sizeField.setBounds(60, frameHeight - 65, 30, 30);
        add(sizeField);

        JLabel rowsLabel = new JLabel("Колонки");
        rowsLabel.setBounds(100, frameHeight - 65, 55, 30);
        rowsLabel.setFont(font);
        add(rowsLabel);

        rowsField = new JTextField("3");
        rowsField.setFont(font);
        rowsField.setBounds(160, frameHeight - 65, 30, 30);
        add(rowsField);

        JButton generateButton = new JButton("Создать atlas");
        generateButton.setActionCommand("create atlas texture");
        generateButton.setBounds(frameWidth - 135, frameHeight - 65, 120, 30);
        generateButton.addActionListener(myButtonListener);
        generateButton.setFont(font);
        add(generateButton);

        JButton makeLowButton = new JButton("Low");
        makeLowButton.setActionCommand("low");
        makeLowButton.setBounds(frameWidth - 200, frameHeight - 65, 60, 30);
        makeLowButton.addActionListener(myButtonListener);
        makeLowButton.setFont(font);
        add(makeLowButton);

//        JButton makeLowestButton = new JButton("Lowest");
//        makeLowestButton.setActionCommand("lowest");
//        makeLowestButton.setBounds(frameWidth - 285, frameHeight - 65, 80, 30);
//        makeLowestButton.addActionListener(myButtonListener);
//        makeLowestButton.setFont(font);
//        add(makeLowestButton);

        setSize(frameWidth, frameHeight);
        setVisible(true);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);

        loadEverything();
    }

    void loadEverything() {
        loadImageFilesList();
        loadBufferedImages();
        loadCompressionRate();
        loadRowsNumber();
        say("ready");
    }

    int getResultWidth() {
        int sum = 0, currentSum = 0;
        int columnIndex = 0;
        int columnLimit = getColumnLimit();
        for (BufferedImage image : bufferedImages) {
            currentSum += image.getWidth();
            if (columnIndex >= columnLimit) {
                columnIndex = 0;
                if (currentSum > sum) sum = currentSum;
                currentSum = 0;
            } else {
                columnIndex++;
            }
        }
        return sum;
    }

    int getResultHeight() {
        int maxRowHeight = 0;
        int sum = 0;
        int columnIndex = 0;
        int columnLimit = getColumnLimit();
        for (BufferedImage image : bufferedImages) {
            if (image.getHeight() > maxRowHeight) maxRowHeight = image.getHeight();
            if (columnIndex >= columnLimit) {
                sum += maxRowHeight;
                maxRowHeight = 0;
                columnIndex = 0;
            } else {
                columnIndex++;
            }
        }
        sum += maxRowHeight;
        return sum;
    }

    public int getColumnLimit() {
        return 3 * Integer.valueOf(rowsField.getText()) - 1;
    }

    boolean isValidPngFile(File file) {
        return file.getName().contains(".png") && !file.getName().equals(RESULT_IMAGE_FILE_NAME);
    }

    boolean isLowImage(int index) {
        return fileNames.get(index).contains("_low.png");
    }

    boolean isLowestImage(int index) {
        return fileNames.get(index).contains("_lowest.png");
    }

    boolean isNormalImage(int index) {
        return !isLowestImage(index) && !isLowImage(index);
    }

    void loadBufferedImages() {
        bufferedImages = new ArrayList<>();
        File folder = new File(System.getProperty("user.dir"));
        for (final File fileEntry : folder.listFiles()) {
            if (isValidPngFile(fileEntry)) {
                try {
                    BufferedImage image = ImageIO.read(fileEntry);
                    bufferedImages.add(image);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        say("finished loading images");
    }

    void loadRowsNumber() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ATLAS_STRUCTURE_FILE_NAME));
            String line = br.readLine();
            while (line != null) {
                if (!line.contains("rows=")) {
                    line = br.readLine();
                    continue;
                }
                int charPos = line.indexOf("=");
                String compRate = line.substring(charPos + 1, line.length());
                rowsField.setText(compRate);
                break;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void loadCompressionRate() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ATLAS_STRUCTURE_FILE_NAME));
            String line = br.readLine();
            while (line != null) {
                if (!line.contains("compression=")) {
                    line = br.readLine();
                    continue;
                }
                int charPos = line.indexOf("=");
                String compRate = line.substring(charPos + 1, line.length());
                sizeField.setText(compRate);
                break;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        say("finished loading compression rate");
    }

    void loadImageFilesList() {
        File folder = new File(System.getProperty("user.dir"));
        fileNames = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (isValidPngFile(fileEntry)) {
                fileNames.add(fileEntry.getName());
//                System.out.println(fileEntry.getName());
//                try {
//                    BufferedImage image = ImageIO.read(fileEntry);
//                    Graphics2D g = (Graphics2D)image.getGraphics();
//                    g.drawLine(0, 0, 100, 100);
//                    ImageIO.write(image, "png", new File("result.png"));
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
            }
        }
        say("created file list (" + fileNames.size() + ")");
    }

    public void say(String message) {
        System.out.println(message);
        if (outputArea != null) {
            String text = outputArea.getText();
            String newText = text + message + "\n";
            outputArea.setText(newText);
            outputArea.update(outputArea.getGraphics());
        }
    }

    public static void main(String args[]){
        JFrame f = new FirstInterface();
        f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(0);
            }
        });
    }
}

class MyButtonListener implements ActionListener  {

    private final FirstInterface firstInterface;
    ArrayList<Integer> verticalPos, horizontalPos;

    MyButtonListener(FirstInterface firstInterface) {
        this.firstInterface = firstInterface;
    }

    void saveTextFile() {
        BufferedWriter writer = null;
        try {
            File txtFile = new File(FirstInterface.ATLAS_STRUCTURE_FILE_NAME);
            writer = new BufferedWriter(new FileWriter(txtFile));
            String str;
            BufferedImage image;
            writer.write("compression=" + Double.valueOf(firstInterface.sizeField.getText()));
            writer.newLine();
            writer.write("rows=" + Integer.valueOf(firstInterface.rowsField.getText()));
            writer.newLine();
            for (int i=0; i< firstInterface.fileNames.size(); i++) {
                str = firstInterface.fileNames.get(i);
                image = firstInterface.bufferedImages.get(i);
                writer.write(str + "#" + horizontalPos.get(i) + " " + verticalPos.get(i) + " " + image.getWidth() + " " + image.getHeight());
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {}
        }
        firstInterface.say("finished saving text file");
    }

    void saveImageFile() {
        verticalPos = new ArrayList<>();
        horizontalPos = new ArrayList<>();
        int resultWidth = firstInterface.getResultWidth();
        int resultHeight = firstInterface.getResultHeight();
        BufferedImage resultImage = new BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_ARGB);
        int currentX = 0;
        int maxRowHeight = 0;
        int currentY = 0;
        int columnIndex = 0;
        int columnLimit = firstInterface.getColumnLimit();
        Graphics2D graphics2D = (Graphics2D) resultImage.getGraphics();
        for (BufferedImage image : firstInterface.bufferedImages) {
            graphics2D.drawImage(image, currentX, currentY, null);
            verticalPos.add(new Integer(currentY));
            horizontalPos.add(new Integer(currentX));
            currentX += image.getWidth();
            if (image.getHeight() > maxRowHeight) maxRowHeight = image.getHeight();
            if (columnIndex >= columnLimit) {
                currentY += maxRowHeight;
                maxRowHeight = 0;
                columnIndex = 0;
                currentX = 0;
            } else {
                columnIndex++;
            }
        }
        try {
            ImageIO.write(resultImage, "png", new File(FirstInterface.RESULT_IMAGE_FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        firstInterface.say("finished saving image file");
    }

    String getLowFileName(String srcName) {
        StringBuffer stringBuffer = new StringBuffer();
        int index = srcName.indexOf(".");
        String name = srcName.substring(0, index);
        String ext = srcName.substring(index + 1, srcName.length());
        stringBuffer.append(name);
        stringBuffer.append("_low.");
        stringBuffer.append(ext);
        return stringBuffer.toString();
    }

    String getLowestFileName(String srcName) {
        StringBuffer stringBuffer = new StringBuffer();
        int index = srcName.indexOf(".");
        String name = srcName.substring(0, index);
        String ext = srcName.substring(index + 1, srcName.length());
        stringBuffer.append(name);
        stringBuffer.append("_lowest.");
        stringBuffer.append(ext);
        return stringBuffer.toString();
    }

    void createLowImageFiles() {
        BufferedImage image;
        for (int i=0; i<firstInterface.bufferedImages.size(); i++) {
            if (!firstInterface.isNormalImage(i)) continue;
            image = firstInterface.bufferedImages.get(i);
            double compressionRate = Double.valueOf(firstInterface.sizeField.getText());
            int imageWidth = getNewImageWidth(image, compressionRate);
            int imageHeight = getNewImageHeight(image, compressionRate);
            BufferedImage resultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = (Graphics2D) resultImage.getGraphics();
            graphics2D.drawImage(image, 0, 0, imageWidth, imageHeight, null);
            try {
                ImageIO.write(resultImage, "png", new File(getLowFileName(firstInterface.fileNames.get(i))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        firstInterface.loadEverything();
        firstInterface.say("finished creating low images");
    }


    private int getNewImageHeight(BufferedImage image, double compressionRate) {
        int imageHeight = (int)(image.getHeight() / compressionRate);
        if (imageHeight < 1) imageHeight = 1;
        return imageHeight;
    }


    private int getNewImageWidth(BufferedImage image, double compressionRate) {
        int imageWidth = (int)(image.getWidth() / compressionRate);
        if (imageWidth < 1) imageWidth = 1;
        return imageWidth;
    }


    void createLowestImageFiles() {
        BufferedImage image;
        for (int i=0; i<firstInterface.bufferedImages.size(); i++) {
            if (!firstInterface.isNormalImage(i)) continue;
            image = firstInterface.bufferedImages.get(i);
            double compressionRate = Double.valueOf(firstInterface.sizeField.getText());
            compressionRate *= 2;
            int imageWidth = getNewImageWidth(image, compressionRate);
            int imageHeight = getNewImageHeight(image, compressionRate);
            BufferedImage resultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = (Graphics2D) resultImage.getGraphics();
            graphics2D.drawImage(image, 0, 0, imageWidth, imageHeight, null);
            try {
                ImageIO.write(resultImage, "png", new File(getLowestFileName(firstInterface.fileNames.get(i))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        firstInterface.loadEverything();
        firstInterface.say("finished creating lowest images");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("create atlas texture")) {
            saveImageFile();
            saveTextFile();
            firstInterface.say("creation of atlas texture finished");
        } else if (e.getActionCommand().equals("low")) {
            createLowImageFiles();
            createLowestImageFiles();
        }
    }
}