package cl.otaku.library.video.videolibrary.services;

import cl.otaku.library.video.videolibrary.models.SeriesDataModel;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface VideoStreamService {

    ResponseEntity<InputStreamResource> loadVideo(String folderName, String fileName, String range);

    List<String> getAvailableVideos();

    List<String> getAvailableSeries();

    SeriesDataModel getSeriesData(Long id);

    List<SeriesDataModel> getAvailableSeriesData();

    List<SeriesDataModel> getHighlightedMedia();

    SeriesDataModel getDetails(String seriesName);
}
