package vgu.vgu;

public class Student {
	private int id;
	private String name;
	private String img;
	private String year;
	private int exchange;
	
	
	public Student(String name, String img, String year, int exchange) {
		this.name = name;
		this.img = img;
		this.year = year;
		this.exchange = exchange;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImg() {
		return img;
	}
	
	public String getYear() {
		return year;
	}
	
	public int getExchange() {
		return exchange;
	}
}
