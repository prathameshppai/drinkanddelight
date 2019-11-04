package hibernate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "Address")
public class Address {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "AddressID")
	private int addressId;
	@Column(name = "PlotNo")
	private int plotNo;
	@Column(name = "BuildingName")
	private String buildingName;	
	@Column(name = "StreetName")
	private String streetName;
	@Column(name = "Landmark")
	private String landmark;
	@Column(name = "City")
	private String city;
	@Column(name = "PinCode")
	private int pincode;
	@Column(name = "State")
	private String state;
	@Column(name = "defaultAddress")
	private boolean defaultAddress;	
	 
	public Address( int plotNo, String buildingName, String streetName, String landmark, String city,
			int pincode, String state, boolean defaultAddress) {
//		this.addressId=addressId;
		this.plotNo=plotNo;
		this.buildingName=buildingName;
		this.streetName=streetName;
		this.landmark=landmark;
		this.city=city;
		this.pincode=pincode;
		this.state=state;
		this.defaultAddress=defaultAddress;
		
		
		// 
	}
	public int getAddressId() {
		return addressId;
	} 
	public void setAddressId(int addressId) {
		this.addressId = addressId;
	} 
	public int getPlotNo() {
		return plotNo;
	} 
	public void setPlotNo(int plotNo) {
		this.plotNo = plotNo;
	} 
	public String getBuildingName() {
		return buildingName;
	} 
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	} 
	public String getStreetName() {
		return streetName;
	} 
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	} 
	public String getLandmark() {
		return landmark;
	} 
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	} 
	public String getCity() {
		return city;
	} 
	public void setCity(String city) {
		this.city = city;
	} 
	public int getPincode() {
		return pincode;
	} 
	public void setPincode(int pincode) {
		this.pincode = pincode;
	} 
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public boolean isDefaultAddress() {
		return defaultAddress;
	}
	public void setDefaultAddress(boolean defaultAddress) {
		this.defaultAddress = defaultAddress;
	}
	
	@Override
	public String toString() {
		return "Address [addressId=" + addressId + ", plotNo=" + plotNo + ", buildingName=" + buildingName
				+ ", streetName=" + streetName + ", landmark=" + landmark + ", city=" + city + ", pincode=" + pincode
				+ ", state=" + state + ", defaultAddress=" + defaultAddress + "]";
	}
	
}