import java.io.File;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.client.j2se.*; // Import the correct class
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class CouponGenerator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Please select the barcode images format:");
        System.out.println("1. JPG. 2. SVG 3. Generate the Coupon Pack");
        int option = input.nextInt();

        switch(option) {
            case 1:
                try {
                    // Path to the folder containing barcode JPG images
                    String barcodeFolder = "material/barcode_images/";

                    // Path to the background image
                    String backgroundImagePath = "background.jpg";

                    // Create the output folder for coupon images
                    File outputFolder = new File("output/coupon_images");
                    outputFolder.mkdirs();

                    // List files in the barcode folder
                    File[] barcodeImageFiles = new File(barcodeFolder).listFiles();

                    if (barcodeImageFiles != null) {
                        for (File barcodeImageFile : barcodeImageFiles) {
                            if (barcodeImageFile.isFile() && barcodeImageFile.getName().toLowerCase().endsWith(".jpg")) {
                                // Load the barcode image with transparency (retain alpha channel)
                                BufferedImage barcodeImage = loadTransparentImage(barcodeImageFile);

                                // Load the background image
                                BufferedImage backgroundImage = ImageIO.read(new File(backgroundImagePath));

                                // Create a coupon image by adding the barcode image to the background
                                BufferedImage couponImage = createCouponImage_jpg(backgroundImage, barcodeImage);

                                // Save the coupon image with a unique name
                                String barcodeFilename = barcodeImageFile.getName();
                                String couponFilename = "coupon_" + barcodeFilename;
                                File couponFile = new File(outputFolder, couponFilename);
                                ImageIO.write(couponImage, "jpg", couponFile);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    // Path to the folder containing SVG barcode files
                    String svgFolder = "barcode_svgs/";

                    // Path to the background image
                    String backgroundImagePath = "background.jpg";

                    // Create the output folder for coupon images
                    File outputFolder = new File("coupon_images");
                    outputFolder.mkdirs();

                    // List files in the SVG barcode folder
                    File[] svgFiles = new File(svgFolder).listFiles();

                    if (svgFiles != null) {
                        for (File svgFile : svgFiles) {
                            if (svgFile.isFile() && svgFile.getName().toLowerCase().endsWith(".svg")) {
                                // Convert the SVG to a raster (PNG) image
                                BufferedImage barcodeImage = convertSvgToImage(svgFile);

                                // Load the background image
                                BufferedImage backgroundImage = ImageIO.read(new File(backgroundImagePath));

                                // Create a coupon image by adding the barcode image to the background
                                BufferedImage couponImage = createCouponImage_svg(backgroundImage, barcodeImage);

                                // Save the coupon image with a unique name
                                String svgFilename = svgFile.getName();
                                String couponFilename = "coupon_" + svgFilename.replace(".svg", ".jpg");
                                File couponFile = new File(outputFolder, couponFilename);
                                ImageIO.write(couponImage, "jpg", couponFile);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                String sourceFolder1 = "Coupons/Bar_Pacific_UNIQUE";
                String sourceFolder2 = "Coupons/Wacoal_UNIQUE";
                String sourceFolder3 = "Coupons/OGAWA_UNIQUE";
                String destinationFolder = "output/CouponPack";
                String pdfFolder = "Coupons/PDF";

                File sourceDir1 = new File(sourceFolder1);
                File sourceDir2 = new File(sourceFolder2);
                File sourceDir3 = new File(sourceFolder3);
                File destDir = new File(destinationFolder);
                File pdfDir = new File(pdfFolder);

                if (!sourceDir1.exists() || !sourceDir2.exists() || !destDir.exists() || !pdfDir.exists()) {
                    System.out.println("Source, destination, or PDF folder does not exist.");
                    return;
                }

                File[] jpgFiles1 = sourceDir1.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));
                File[] jpgFiles2 = sourceDir2.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));
                File[] jpgFiles3 = sourceDir3.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));
                File[] pdfFiles = pdfDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

                if (jpgFiles1 == null || jpgFiles1.length == 0 || jpgFiles2 == null || jpgFiles2.length == 0 || jpgFiles3 == null
                        || jpgFiles3.length == 0 ||pdfFiles == null || pdfFiles.length == 0) {
                    System.out.println("No JPG or PDF files found in one or more of the folders.");
                    return;
                }

                int packCount = 1500;
                int jpgCount1 = jpgFiles1.length;
                int jpgCount2 = jpgFiles2.length;
                int jpgCount3 = jpgFiles3.length;

                for (int i = 1; i <= packCount; i++) {
                    // Create the CouponPack folder
                    String packFolderName = destinationFolder + File.separator + "CouponPack" + i;
                    File packFolder = new File(packFolderName);
                    packFolder.mkdirs();

                    // Get JPG files one by one from each source folder.
                    int index1 = (i - 1) % jpgCount1;
                    int index2 = (i - 1) % jpgCount2;
                    int index3 = (i - 1) % jpgCount3;

                    File sourceFile1 = jpgFiles1[index1];
                    File sourceFile2 = jpgFiles2[index2];
                    File sourceFile3 = jpgFiles3[index3];

                    File destFile1 = new File(packFolderName + File.separator + sourceFile1.getName());
                    File destFile2 = new File(packFolderName + File.separator + sourceFile2.getName());
                    File destFile3 = new File(packFolderName + File.separator + sourceFile3.getName());

                    try {
                        Files.move(sourceFile1.toPath(), destFile1.toPath());
                        Files.move(sourceFile2.toPath(), destFile2.toPath());
                        Files.move(sourceFile3.toPath(), destFile3.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Copy PDF files to the pack folder.
                    for (File pdfFile : pdfFiles) {
                        File destPDFFile = new File(packFolderName + File.separator + pdfFile.getName());
                        try {
                            Files.copy(pdfFile.toPath(), destPDFFile.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("Created " + packCount + " coupon packs with PDF files.");
                break;
        }

    }

    private static BufferedImage loadTransparentImage(File imageFile) throws Exception {
        BufferedImage image = ImageIO.read(imageFile);
        BufferedImage transparentImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        transparentImage.getGraphics().drawImage(image, 0, 0, null);
        return transparentImage;
    }

    private static BufferedImage createCouponImage_jpg(BufferedImage backgroundImage, BufferedImage barcodeImage) {
        // Create a new image with the same dimensions as the background image
        BufferedImage couponImage = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Draw the background image onto the coupon image
        couponImage.createGraphics().drawImage(backgroundImage, 0, 0, null);

        // Calculate the position to paste the barcode image (right-hand bottom corner)
        int xPosition = couponImage.getWidth() - barcodeImage.getWidth() - 10; // Adjust the right margin as needed
        int yPosition = couponImage.getHeight() - barcodeImage.getHeight() - 10; // Adjust the bottom margin as needed

        // Overlay the barcode image onto the coupon image at the specified position
        couponImage.createGraphics().drawImage(barcodeImage, xPosition, yPosition, null);

        return couponImage;
    }

    private static BufferedImage convertSvgToImage(File svgFile) throws Exception {
        // Create a PNGTranscoder
        PNGTranscoder transcoder = new PNGTranscoder();

        // Set hints to ensure transparency is retained
        transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);

        // Create a TranscoderInput from the SVG file
        TranscoderInput input = new TranscoderInput(svgFile.toURI().toURL().toString());

        // Create a ByteArrayOutputStream to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outputStream);

        // Perform the SVG to image conversion
        transcoder.transcode(input, output);

        // Convert the ByteArrayOutputStream to a byte array
        byte[] imageBytes = outputStream.toByteArray();

        // Read the byte array as a BufferedImage
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    private static BufferedImage createCouponImage_svg(BufferedImage backgroundImage, BufferedImage barcodeImage) {
        // Create a new image with the same dimensions as the background image
        BufferedImage couponImage = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Draw the background image onto the coupon image
        couponImage.createGraphics().drawImage(backgroundImage, 0, 0, null);

        // Calculate the position to paste the barcode image (left-hand bottom corner)
        int xPosition = 20; // Adjust the left margin as needed
        int yPosition = couponImage.getHeight() - barcodeImage.getHeight() - 10; // Adjust the bottom margin as needed

        // Overlay the barcode image onto the coupon image at the specified position
        couponImage.createGraphics().drawImage(barcodeImage, xPosition, yPosition, null);

        return couponImage;
    }
}


