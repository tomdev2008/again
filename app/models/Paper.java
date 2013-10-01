package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import play.modules.morphia.Model;
import tools.SequenceUtils;

public class Paper extends BaseModel{

	public int year;
	public Course course ;
	public SubjectSource source;
	public Set<City> cities = new HashSet<City>();
	
}
