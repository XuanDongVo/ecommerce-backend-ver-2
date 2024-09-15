package xuandong.ecommerce_ver_2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value =  HttpStatus.NOT_FOUND)
public class StorageException extends RuntimeException {
	public StorageException(String mess) {
		super(mess);
	}

	
}
