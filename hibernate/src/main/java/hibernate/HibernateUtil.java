package hibernate;
import java.io.File;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateUtil {Configuration configuration = new Configuration();
    private static final SessionFactory sessionFactory = buildSessionFactory();
     
    private static SessionFactory buildSessionFactory() {
        try {
             Configuration configuration = new Configuration();
             return configuration.configure(new File("C:\\Users\\akum1031\\eclipse-workspace\\hibernate\\src\\main\\java\\updateorder\\hibernateconfig.xml")).buildSessionFactory();
        }
        
        catch (Throwable ex) { 
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
 
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
 
    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
}


