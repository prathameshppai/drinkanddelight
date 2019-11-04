package updateorder;
import javax.persistence.Entity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class ProductDao {
	

	public void updateProductOrder(String orderId,String deliveryStatus)  {
		
        Transaction transaction = null;
        try (Session session = HibernateUtill.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            ProductOrder product = (ProductOrder)session.get(ProductOrder.class,Integer.parseInt(orderId));
            product.setDeliveryStatus(deliveryStatus ); 
            session.save(product);
            // commit transaction
            transaction.commit();
            //int result = query.executeUpdate();
        } catch (Exception e) {
            if (transaction != null) {
            	transaction.rollback();
            }

            
            
        }
	}
}


