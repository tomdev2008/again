package models;

import com.google.code.morphia.annotations.Reference;

public class City extends BaseModel{

	@Reference
	public City context;

	
	public String name;
	
	public int index;
}
