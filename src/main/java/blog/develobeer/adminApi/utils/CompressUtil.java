package blog.develobeer.adminApi.utils;

import org.pngquant.PngQuant;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class CompressUtil {
    private static final int PNG_QUALITY_MIN = 60;
    private static final int PNG_QUALITY_MAX = 65;

    private static final float JPG_QUALITY = 0.6f;

    private static final String JPG_FORMAT = ".jpg";
    private static final String IMG_UPLOAD_TEMP_DIR = "/tmp/uploads/";

    public static MultipartFile compressImage(MultipartFile multipartFile) {
        if (multipartFile.getOriginalFilename().endsWith(".png")) {
            return compressPng(multipartFile);
        } else if (multipartFile.getOriginalFilename().endsWith(".jpg") || multipartFile.getOriginalFilename().endsWith(".jpeg")) {
            return comporessJpgWithExternalLib(multipartFile);
        } else {
            return multipartFile;
        }
    }

    private static MultipartFile comporessJpgWithExternalLib(MultipartFile multipartFile) {
        try {
            if (!new File(IMG_UPLOAD_TEMP_DIR).exists()) {
                new File(IMG_UPLOAD_TEMP_DIR).mkdir();
            }

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String filePath = IMG_UPLOAD_TEMP_DIR + "temp" + now + JPG_FORMAT;

            // temp file create
            Path dest = Paths.get(filePath);
            FileChannel fileChannel = FileChannel.open(dest, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            fileChannel.write(ByteBuffer.wrap(multipartFile.getBytes()));
            fileChannel.close();

            // compression
            ShellUtil.byCommonsExec("jpegoptim", filePath, "-m65", "--strip-all", "--all-progressive");

            // temp compressed image read
            BufferedImage originalImage = ImageIO.read(new File(filePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            // temp file delete
            Files.delete(dest);

            return transToMultipartFile(multipartFile, imageInByte);
        } catch (Exception e) {
            e.printStackTrace();
            return multipartFile;
        }
    }

    /**
     * 이미 잘 압축된 파일 업로드 시 용량이 커지며 열화가 심해진다.
     * <p>
     * 사용하지 않는다.
     *
     * @param multipartFile
     * @return
     */
    private static MultipartFile compressJpgWithJavaLib(MultipartFile multipartFile) {
        try {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            // get all image writers for JPG format
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

            if (!writers.hasNext()) {
                throw new IllegalStateException("No writers found");
            }

            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();

            // compress to a given quality
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            }
            if (param.canWriteProgressive()) {
                param.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
            }
            if (param.canWriteTiles()) {
                param.setTilingMode(ImageWriteParam.MODE_EXPLICIT);
            }

            param.setCompressionQuality(JPG_QUALITY);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            writer.setOutput(ios);
            ImageIO.write(image, "jpg", baos);
            writer.write(null, new IIOImage(image, null, null), param);

            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            writer.dispose();

            return transToMultipartFile(multipartFile, imageInByte);
        } catch (Exception e) {
            e.printStackTrace();
            return multipartFile;
        }
    }

    private static MultipartFile compressPng(MultipartFile multipartFile) {
        try {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);

            PngQuant pngQuant = new PngQuant();
            pngQuant.setQuality(PNG_QUALITY_MIN, PNG_QUALITY_MAX);
            BufferedImage remapped = pngQuant.getRemapped(image);
            ImageIO.write(remapped, "png", ios);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            return transToMultipartFile(multipartFile, imageInByte);
        } catch (Exception e) {
            e.printStackTrace();
            return multipartFile;
        }
    }

    private static MultipartFile transToMultipartFile(MultipartFile multipartFile, byte[] imageInByte) {
        return new MultipartFile() {
            private final byte[] imgContent = imageInByte;

            @Override
            public String getName() {
                return multipartFile.getName();
            }

            @Override
            public String getOriginalFilename() {
                return multipartFile.getOriginalFilename();
            }

            @Override
            public String getContentType() {
                return multipartFile.getContentType();
            }

            @Override
            public boolean isEmpty() {
                return imgContent == null || imgContent.length == 0;
            }

            @Override
            public long getSize() {
                return imgContent.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return imgContent;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(imgContent);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                new FileOutputStream(dest).write(imgContent);
            }
        };
    }
}
