package TestHikariPool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class TestHikariPoolApplication {
	private static Logger logger = LoggerFactory.getLogger(TestHikariPoolApplication.class);

	static final String sqlCreateTable = "create table if not exists balance " +
			"						 (id bigserial,\n" +
			"                         order_ids varchar(255),\n" +
			"                         order_last_update_date_time_utc timestamp,\n" +
			"                         order_line_balance_change decimal,\n" +
			"                         order_line_quantity decimal,\n" +
			"                         order_line_price_of_line decimal,\n" +
			"                         customer_discount_card_number varchar(255),\n" +
			"                         primary key (id));";

	public static void main(String[] args) throws SQLException, InterruptedException {
		SpringApplication.run(TestHikariPoolApplication.class, args);

		List<String> list = new ArrayList<>();

		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
		hikariConfig.setUsername("postgres");
		hikariConfig.setPassword("postgres");
		hikariConfig.setPoolName("HikariTestPool");

		for (int i = 1; i <= 20; i++) {

			logger.info("start test pool: {}", i);

			hikariConfig.setMinimumIdle(i);
			hikariConfig.setMaximumPoolSize(i);
			HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

			Connection connection =  hikariDataSource.getConnection();
			Statement statement = connection.createStatement();
			statement.execute("drop table if exists balance;");
			statement.close();
			statement = connection.createStatement();
			statement.execute(sqlCreateTable);
			statement.close();
			connection.close();

			long startTime = System.currentTimeMillis();
			SaveService saveService = new SaveService(hikariDataSource);
			saveService.downloadAndSaveBalance();
			long time = (System.currentTimeMillis() - startTime) / 1_000;
			list.add("Time for pool:" + i + " - " + time + " second" );

			connection =  hikariDataSource.getConnection();
			statement = connection.createStatement();
			statement.execute("select count(*) count from balance;");
			ResultSet resultSet= statement.getResultSet();
			resultSet.next();
			if (resultSet.getInt("count") != SaveService.limit) {
				logger.error("{} row write", resultSet.getInt("count"));
			}
			statement.close();
			statement = connection.createStatement();
			statement.execute("drop table if exists balance;");
			statement.close();
			connection.close();
			hikariDataSource.close();
		}
		list.forEach(System.out::println);
	}
}
