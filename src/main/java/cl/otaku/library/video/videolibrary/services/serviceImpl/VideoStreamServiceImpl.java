package cl.otaku.library.video.videolibrary.services.serviceImpl;

import cl.otaku.library.video.videolibrary.services.VideoStreamService;
import io.minio.*;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoStreamServiceImpl implements VideoStreamService {

    private static final Logger logger = LoggerFactory.getLogger(VideoStreamServiceImpl.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    public VideoStreamServiceImpl(
            @Value("${minio.url}") String url,
            @Value("${minio.accessKey}") String accessKey,
            @Value("${minio.secretKey}")String secretKey
    ) {
        this.minioClient = MinioClient.builder()
                                   .endpoint(url)
                                   .credentials(accessKey, secretKey)
                                   .build();
    }

    @Override
    public ResponseEntity<InputStreamResource> loadVideo(String fileName, String range) {
        try {
            logger.info("Loading video file: " + fileName);
            // get the metadata of the object
            var stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(fileName).build()
            );

            long fileSize = stat.size();
            long start = 0;
            long end = fileSize - 1;

            if (range != null && range.startsWith("bytes=")) {
                String[] parts = range.substring(6).split("-");
                start = Long.parseLong(parts[0]);
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    end = Long.parseLong(parts[1]);
                }
            }

            long contentLength = end - start + 1;

            // get only the required range
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .offset(start)
                            .length(contentLength)
                            .build()
            );

            logger.info("loaded video file: " + fileName);

            return ResponseEntity.status(range != null ? 206 : 200)
                           .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                           .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                           .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                           .header(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileSize))
                           .body(new InputStreamResource(stream));

        } catch (Exception e) {
            logger.error("Error while loading video file: " + fileName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getAvailableVideos() {

        List<String> videoNames = new ArrayList<>();

        try {
            logger.info("Getting available videos...");
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                videoNames.add(item.objectName());
            }

            logger.info("available videos: " + videoNames);

        } catch (Exception e) {
            logger.error("Error while getting available videos", e);
            throw new RuntimeException(e);
        }
        return videoNames;
    }
}
