package parkingos.com.bolink.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface AdvertisemenetService {
    String upload(HttpServletRequest request, MultipartFile file) throws IOException;

    String addParkId(HttpServletRequest request);
}
