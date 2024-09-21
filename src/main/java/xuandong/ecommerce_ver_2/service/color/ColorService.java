package xuandong.ecommerce_ver_2.service.color;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xuandong.ecommerce_ver_2.entity.Color;
import xuandong.ecommerce_ver_2.exception.UserAlreadyExistsException;
import xuandong.ecommerce_ver_2.repository.ColorRepository;

@Service
public class ColorService {

	@Autowired
	private ColorRepository colorRepository;

	public void addColor(String name) {
		Color color = colorRepository.findByName(name).get();

		if (color != null) {
			new UserAlreadyExistsException("Color is already exist");
			return;
		}

		color = new Color();
		color.setName(name);
		colorRepository.save(color);
	}

	public void deleteColor(String name) {
		Color color = colorRepository.findByName(name).get();
		colorRepository.delete(color);
	}
}
