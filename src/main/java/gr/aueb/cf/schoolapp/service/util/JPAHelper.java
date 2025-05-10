package gr.aueb.cf.schoolapp.service.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class JPAHelper {
    private static EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();
    private static final HikariConfig config = new HikariConfig();
    private static final Map<String, Object> properties = new HashMap<>();
//
    static {
////        config.setJdbcUrl("jdbc:mysql://localhost:3306/school6jaxrshiberdb?serverTimezone=UTC");
////        config.setUsername("school6user");
////        config.setPassword("12345");
//
//        // Use env variables for storing sensitive data for database connectivity
        config.setJdbcUrl(System.getenv("SCHOOL7_DB_HOST") + ":" + System.getenv("SCHOOL7_DB_PORT") +
                "/" + System.getenv("SCHOOL7_DB_DATABASE"));
        config.setUsername(System.getenv("SCHOOL7_DB_USERNAME"));
        config.setPassword(System.getenv("SCHOOL7_DB_PASSWORD"));
////
////      Optional
////        config.setMaximumPoolSize(20);
////        config.setConnectionTimeout(20000);
//
        DataSource dataSource = new HikariDataSource(config);
        properties.put("hibernate.connection.datasource", dataSource);
    }

    private JPAHelper() {

    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory("school7DBContext");
//            emf = Persistence.createEntityManagerFactory("school7DBContext", properties);
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();
        if (em == null || !em.isOpen())  {
            em = getEntityManagerFactory().createEntityManager();
            threadLocal.set(em);
        }

        return em;
    }

    public static void closeEntityManager() {
        getEntityManager().close();
        threadLocal.remove();
    }

    public static void beginTransaction() {
        getEntityManager().getTransaction().begin();
    }

    public static void commitTransaction() {
        getEntityManager().getTransaction().commit();
    }

    public static void rollbackTransaction() {
        getEntityManager().getTransaction().rollback();
    }

    public static void closeEMF() {
        emf.close();
    }
}
