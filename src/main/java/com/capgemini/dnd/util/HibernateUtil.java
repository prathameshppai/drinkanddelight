package com.capgemini.dnd.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
//import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import com.capgemini.dnd.entity.EmployeeCredentialEntity;
import com.capgemini.dnd.entity.ProductOrdersEntity;
import com.capgemini.dnd.entity.ProductStockEntity;
import com.capgemini.dnd.entity.RawMaterialOrderEntity;

import com.capgemini.dnd.entity.RawMaterialStockEntity;


public class HibernateUtil {
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
		Configuration config = new Configuration().configure()
				.addPackage("com.capgemini.dnd.entity")
				.addAnnotatedClass(ProductStockEntity.class)
				.addAnnotatedClass(EmployeeCredentialEntity.class)
				.addAnnotatedClass(ProductOrdersEntity.class)
				.addAnnotatedClass(RawMaterialStockEntity.class)
				.addAnnotatedClass(RawMaterialOrderEntity.class);
		ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		SessionFactory sf = config.buildSessionFactory(registry);   
		return sf.openSession();
	}
}
