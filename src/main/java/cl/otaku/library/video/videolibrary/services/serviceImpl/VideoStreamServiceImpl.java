package cl.otaku.library.video.videolibrary.services.serviceImpl;

import cl.otaku.library.video.videolibrary.models.ChapterModel;
import cl.otaku.library.video.videolibrary.models.SeriesDataModel;
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
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public ResponseEntity<InputStreamResource> loadVideo(String folderName, String fileName, String range) {
        try {
            logger.info("Loading video file: " + fileName + " in folder: " + folderName);
            // get the metadata of the object
            var stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(folderName+"/"+fileName).build()
            );

            // Inferir el tipo MIME
            String mimeType;
            try {
                mimeType = Files.probeContentType(Paths.get(folderName+"/"+fileName));
            } catch (Exception e) {
                mimeType = null;
            }
            if (mimeType == null) {
                if (fileName.endsWith(".mkv")) {
                    mimeType = "video/x-matroska";
                } else if (fileName.endsWith(".mp4")) {
                    mimeType = "video/mp4";
                } else {
                    mimeType = "application/octet-stream";
                }
            }

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
                            .object(folderName+"/"+fileName)
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
                           .contentType(MediaType.parseMediaType(mimeType))
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

    @Override
    public List<String> getAvailableSeries() {

        logger.info("Getting available series...");
        List<String> seriesNames = new ArrayList<>();
        try {
            List<String> allNames = getAvailableVideos();
            logger.info("available series: " + allNames);

            for (String name : allNames) {
                String[] parts = name.split("/");
                if (!seriesNames.contains(parts[0]) ) {
                    seriesNames.add(parts[0]);
                }
            }

            return seriesNames;

        } catch (Exception e) {
            logger.error("Error while getting available series", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public SeriesDataModel getSeriesData(Long id) {
        return null;
    }

    public List<SeriesDataModel> getTestSeriesData() {
        List<SeriesDataModel> seriesDataModels = new ArrayList<>();
        ArrayList<ChapterModel> chapters = new ArrayList<>();

        // de momento es un dummy, mientras se trabaja en la base de datos

        for (int i = 0; i < 24; i++) {
            ChapterModel chapterModel = new ChapterModel();

            chapterModel.setId((long) i + 1 );
            chapterModel.setTitle("Titulo capitulo " + i + 1);
            chapterModel.setChapterNumber(i + 1);
            chapterModel.setChapterDescription("Descripcion del capitulo " + i + 1);
            chapterModel.setChapterThumbnailUrl("https://palomaynacho.com/wp-content/uploads/2025/05/Ado-Historia-1536x864.jpg");

            chapters.add(chapterModel);
        }

        for (int i = 0; i < 200; i++) {
            SeriesDataModel seriesDataModel = new SeriesDataModel();

            seriesDataModel.setId((long) i);
            seriesDataModel.setTitle("Seriessssssssssssssssssssssss " + i);
            seriesDataModel.setCurrentChapters(1);
            seriesDataModel.setTotalChapters(12);
            seriesDataModel.setMainTag("Anime " + i);
            seriesDataModel.setAllTags(new String[]{"tag1", "tag2"});
            seriesDataModel.setOriginalName("Original name " + i);
            seriesDataModel.setDescription("Description " + i);

            seriesDataModel.setChapters(chapters);
            seriesDataModel.setMainImageUrl("https://i.pinimg.com/736x/64/0f/15/640f154748584de5d5f2571dfae07e14.jpg");
            seriesDataModel.setYearOfRelease(2025);

            seriesDataModels.add(seriesDataModel);
        }

        return seriesDataModels;
    }


    @Override
    public List<SeriesDataModel> getAvailableSeriesData() {

        return getTestSeriesData();
    }

    @Override
    public List<SeriesDataModel> getHighlightedMedia() {
        // List<SeriesDataModel> high = getTestSeriesData();

        // cargo las series disponibles en minIO
        List<String> availableSeries = getAvailableSeries();

        // ahora cargo los capitulos disponibles desde minIO
        List<String> highNames = getAvailableVideos();

        // ahora creo el objeto que espero retornar
        List<SeriesDataModel> highlightedSeriesList = new ArrayList<>();

        // ahora, elemento por elemento voy sumando series:
        for (String seriesName : availableSeries) {

            int chapterCount = 0;

            SeriesDataModel seriesDataModel = new SeriesDataModel();
            ArrayList<ChapterModel> chapters = new ArrayList<>();
            seriesDataModel.setId((long) availableSeries.indexOf(seriesName));
            seriesDataModel.setTitle(seriesName);
            seriesDataModel.setChapters(chapters);


            for (String highName : highNames) {
                if (highName.startsWith(seriesName)) {
                    chapterCount++;
                    seriesDataModel.setCurrentChapters(chapterCount);
                    seriesDataModel.setTotalChapters(chapterCount);
                    // De momento, el main tag será anime, se va a cargar un archivo en minIO con esta info a futuro
                    // o cuando exista un backoffice para poder cargar las series en un flujo más definido.
                    // lo mismo para todos los elementos de este cuadro
                    seriesDataModel.setMainTag("Anime");
                    seriesDataModel.setAllTags(new String[]{"Anime", "Seinen"});
                    seriesDataModel.setOriginalName(highName);
                    seriesDataModel.setDescription("place holder description " + highName);
                    // se agrega un capítulo por cada elemento de highName
                    seriesDataModel.getChapters().add(new ChapterModel(
                            (long) highNames.indexOf(highName),
                            getChapterName(highName),
                            highNames.indexOf(highName),
                            "place holder chapter description",
                            "https://i.pinimg.com/736x/64/0f/15/640f154748584de5d5f2571dfae07e14.jpg"
                    ));
                    seriesDataModel.setMainImageUrl("https://i.pinimg.com/736x/64/0f/15/640f154748584de5d5f2571dfae07e14.jpg");
                    seriesDataModel.setYearOfRelease(2025);

                }
            }
            highlightedSeriesList.add(seriesDataModel);
        }

        return highlightedSeriesList;
    }

    public String getSeriesName(String name) {
        String[] parts = name.split("/");
        return parts[0];
    }

    public String getChapterName(String name) {
        String[] parts = name.split("/");
        return parts[1];
    }

    @Override
    public SeriesDataModel getDetails(String seriesName) {

        // ahora cargo los capitulos disponibles desde minIO
        List<String> highNames = getAvailableVideos();

        // ahora, elemento por elemento voy sumando series:
        int chapterCount = 0;

        SeriesDataModel seriesDataModel = new SeriesDataModel();
        ArrayList<ChapterModel> chapters = new ArrayList<>();
        seriesDataModel.setId(1L);
        seriesDataModel.setTitle(seriesName);
        seriesDataModel.setChapters(chapters);
        seriesDataModel.setDescription("place holder description for " + seriesName);

        for (String highName : highNames) {
            if (highName.startsWith(seriesName)) {
                chapterCount++;
                seriesDataModel.setCurrentChapters(chapterCount);
                seriesDataModel.setTotalChapters(chapterCount);
                // De momento, el main tag será anime, se va a cargar un archivo en minIO con esta info a futuro
                // o cuando exista un backoffice para poder cargar las series en un flujo más definido.
                // lo mismo para todos los elementos de este cuadro
                seriesDataModel.setMainTag("Anime");
                seriesDataModel.setAllTags(new String[]{"Anime", "Seinen"});
                seriesDataModel.setOriginalName(highName);

                // se agrega un capítulo por cada elemento de highName
                seriesDataModel.getChapters().add(new ChapterModel(
                        (long) highNames.indexOf(highName),
                        getChapterName(highName),
                        highNames.indexOf(highName),
                        "place holder chapter description",
                        "https://i.pinimg.com/736x/64/0f/15/640f154748584de5d5f2571dfae07e14.jpg"
                ));
                seriesDataModel.setMainImageUrl("https://i.pinimg.com/736x/64/0f/15/640f154748584de5d5f2571dfae07e14.jpg");
                seriesDataModel.setYearOfRelease(2025);

            }
        }

        return seriesDataModel;
    }
}