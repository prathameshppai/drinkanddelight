package com.capgemini.dnd.util;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
//import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.entity.DistributorEntity;
import com.capgemini.dnd.entity.EmployeeCredentialEntity;
import com.capgemini.dnd.entity.ProductOrdersEntity;
import com.capgemini.dnd.entity.ProductStockEntity;
import com.capgemini.dnd.entity.RawMaterialOrderEntity;

import com.capgemini.dnd.entity.RawMaterialStockEntity;


public class HibernateUtil {
	private static Logger logger = Logger.getRootLogger();

	static SessionFactory sf = null;
	static {
		Configuration config = new Configuration().configure().addPackage("com.capgemini.dnd.entity")
				.addAnnotatedClass(ProductStockEntity.class)
				.addAnnotatedClass(EmployeeCredentialEntity.class)
				.addAnnotatedClass(ProductOrdersEntity.class)
				.addAnnotatedClass(RawMaterialOrderEntity.class)
				.addAnnotatedClass(RawMaterialStockEntity.class)
				.addAnnotatedClass(DistributorEntity.class);
		ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		sf = config.buildSessionFactory(registry);
	}

//	private static final SessionFactory sessionFactory = buildSessionFactory();
//	 
//    private static SessionFactory buildSessionFactory() {
//        try {
//            // Create the SessionFactory from hibernate.cfg.xml
//        	System.out.println();
//            return new AnnotationConfiguration().configure(new File("C:\\Users\\gauragai\\eclipse-workspace\\drinkanddelight\\hibernate.cgf.xml")).buildSessionFactory();
// 
//        }
//        catch (Throwable ex) {
//            // Make sure you log the exception, as it might be swallowed
//            System.err.println("Initial SessionFactory creation failed." + ex);
//            throw new ExceptionInInitializerError(ex);
//        }
//    }
// 
//    public static SessionFactory getSessionFactory() {
//        return sessionFactory;
//    }
// 
//    public static void shutdown() {
//    	// Close caches and connection pools
//    	getSessionFactory().close();
//    }

	public static Session getASession() {
		return sf.openSession();
	}
	
	public static void closeSession(Session session) throws BackEndException {
		try {
			session.close();
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BackEndException(exception.getMessage());
		}
	}

	
}
