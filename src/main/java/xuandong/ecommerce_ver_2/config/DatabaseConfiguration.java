package xuandong.ecommerce_ver_2.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {
	

	@Bean
	public DataSource dataSource() {
		DataSourceBuilder<?> dataSource = DataSourceBuilder.create();
		dataSource.driverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.url("jdbc:mysql://localhost:3306/ecommerce_ver_2");
		dataSource.username("root");
		dataSource.password("dong14052004");
		return dataSource.build();
		
	}

}
