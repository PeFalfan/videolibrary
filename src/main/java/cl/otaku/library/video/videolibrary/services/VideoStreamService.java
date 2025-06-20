package cl.otaku.library.video.videolibrary.services;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface VideoStreamService {

    ResponseEntity<InputStreamResource> loadVideo(String fileName, String range);

    List<String> getAvailableVideos();
}
