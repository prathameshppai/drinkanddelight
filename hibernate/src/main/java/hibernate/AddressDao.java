package hibernate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import hibernate.Address;
import updateorder.HibernateUtill;;
public class AddressDao {

    public void saveAddress(Address address) {
        Transaction transaction = null;
        try (Session session = HibernateUtill.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the student object
            session.save(address);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    public List < Address > getAddress() {
        try (Session session = HibernateUtill.getSessionFactory().openSession()) {
            return session.createQuery("from Address", Address.class).list();
        }
    }
}

