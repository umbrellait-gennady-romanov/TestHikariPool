package TestHikariPool;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SaveService {

    private DataSource dataSource;

    public SaveService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void downloadAndSaveBalance() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors());

        final String sqlBalance = "insert into balance (" +
                "order_ids, " +
                "order_last_update_date_time_utc, " +
                "order_line_balance_change, " +
                "order_line_quantity, " +
                "order_line_price_of_line, " +
                "customer_discount_card_number" +
                ") values ";

        StringBuilder sqlBuilder = new StringBuilder().append(sqlBalance);

        for (int row = 1; row <= 1_000_000; row++) {

            sqlBuilder.append("(")
                    .append("'" + "order_ids_order_id_promotions" + row + "'").append(", ")
                    .append("to_date('01.04.2021','dd.MM.yyyy')").append(", ")
                    .append(row).append(", ")
                    .append(row + 1).append(", ")
                    .append(row + 2).append(", ")
                    .append(row + 3)
                    .append(")");
            if (row % 1_000 != 0) {
                sqlBuilder.append(",");
            } else {
                sqlBuilder.append(";");
                executorService.execute(new SaveWorker(sqlBuilder.toString(), dataSource));
                sqlBuilder.delete(0, sqlBuilder.length());
                sqlBuilder.append(sqlBalance);
                if (row == 1_000_000) {
                    executorService.shutdown();
                    executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
                }
            }
        }
    }
}
