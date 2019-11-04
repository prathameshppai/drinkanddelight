package hibernate;
import java.util.List;
import hibernate.Address;
import hibernate.AddressDao;
import updateorder.HibernateUtill;
public class Main {
	
	    public static void main(String[] args) {
	    	
	        AddressDao addressDao = new AddressDao();
	         Address address = new Address(400,"DistributorB1","Lake Street","Textile Market","Delhi",12345,"Delhi",true);
	         addressDao.saveAddress(address);
	        List < Address > Addresses = addressDao.getAddress();
	        for(int i=0;i<Addresses.size();i++) {
	        	System.out.println(Addresses.get(i).getAddressId());
	        }
	    }
	}


