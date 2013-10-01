package service;

import models.City;

import org.apache.commons.lang.StringUtils;

public class CityService {

	
	public static City getCity(String name){
		City city = City.filter("name", StringUtils.trim(name)).first();
		if(city == null){
			city  = new City();
			city.name = name;
			city.save();
		}
		return city;

	}
}
