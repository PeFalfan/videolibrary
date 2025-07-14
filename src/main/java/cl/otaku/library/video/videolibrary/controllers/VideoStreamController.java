package cl.otaku.library.video.videolibrary.controllers;

import cl.otaku.library.video.videolibrary.models.SeriesDataModel;
import cl.otaku.library.video.videolibrary.services.VideoStreamService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/videos")
public class VideoStreamController {

    private final VideoStreamService videoStreamService;

    public VideoStreamController(VideoStreamService videoStreamService) {
        this.videoStreamService = videoStreamService;
    }

    @GetMapping("/playVideo/{folderName}/{fileName}")
    public ResponseEntity<InputStreamResource> streamVideo(
            @PathVariable String fileName,
            @PathVariable String folderName,
            @RequestHeader(value = "Range", required = false) String range) {
        try {

            return videoStreamService.loadVideo(folderName, fileName, range);

        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/getAvailableVideos")
    public List<String> getAvailableVideos() {
        try{
            return videoStreamService.getAvailableVideos();

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/getAvailableSeries")
    public List<String> getAvailableSeries() {
        return videoStreamService.getAvailableSeries();
    }

    @GetMapping("/testVideoService")
    public String testVideoService() {
        return "Video service is working";
    }

    @GetMapping("/getSeriesData")
    public List<SeriesDataModel> getSeriesData() {
        return videoStreamService.getAvailableSeriesData();
    }

    @GetMapping("/getHighlightedMedia")
    public List<SeriesDataModel> getHighlightedMedia() {
        return videoStreamService.getHighlightedMedia();
    }

    // this method to get the details for one selected series.
    // we need the name of the series
    @GetMapping("/getDetails{seriesDetails}")
    public SeriesDataModel getSeriesDetails(@PathVariable String seriesDetails) {
        return videoStreamService.getDetails(seriesDetails);
    }
}
