package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import play.modules.morphia.Model;
import tools.SequenceUtils;
import utils.EasyMap;

public class Paper extends BaseModel{

	public String name;
	public int year;
	public Course course ;
	public SubjectSource source;
	public Set<City> cities = new HashSet<City>();
	public Map<Tag ,Integer> bigTag = new EasyMap<Tag ,Integer>();
	public Set<Tag> tags = new HashSet<Tag>();
}
