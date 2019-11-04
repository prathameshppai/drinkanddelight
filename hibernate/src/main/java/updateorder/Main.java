package updateorder;


public class Main {

	public static void main(String[] args) {
            ProductDao productdao=new ProductDao();
		   productdao.updateProductOrder("4","Dispatched");
	}

}
