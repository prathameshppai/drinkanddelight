package com.capgemini.dnd.util;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {
	private static final SessionFactory sessionFactory = buildSessionFactory();
	 
    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
        	System.out.println();
            return new AnnotationConfiguration().configure(new File("C:\\Users\\akdeep\\eclipse-workspace\\drinkanddelight\\hibernate.cgf.xml")).buildSessionFactory();
 
        }
        catch (Throwable exception) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + exception);
            throw new ExceptionInInitializerError(exception);
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
