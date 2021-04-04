package TestHikariPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SaveWorker implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveWorker.class);
    private final String sql;
    private final DataSource appDataSource;

    public SaveWorker(String sql, DataSource appDataSource) {
        this.sql = sql;
        this.appDataSource = appDataSource;
    }

    @Override
    public void run() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = appDataSource.getConnection();
            st = conn.createStatement();
            st.execute(sql);
            st.close();
            conn.close();
        } catch (SQLException throwables) {
            LOGGER.error("error save sql: {}", sql);
            throwables.printStackTrace();
        }
    }
}
