package xuandong.ecommerce_ver_2.service.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileService {
	@Value("${upload-file.base-path}")
	private String basePath;

	 public String store(MultipartFile file) throws URISyntaxException, IOException {
	        // create unique filename
	        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
	        String encodedPath = URLEncoder.encode(finalName, StandardCharsets.UTF_8.toString());
	        URI uri = new URI(basePath + "/" + encodedPath);
	        Path path = Paths.get(uri);
	        try (InputStream inputStream = file.getInputStream()) {
	            Files.copy(inputStream, path,
	                    StandardCopyOption.REPLACE_EXISTING);
	        }
	        return finalName;
	    }

	
}
