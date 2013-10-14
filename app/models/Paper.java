package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;
import tools.SequenceUtils;
import utils.EasyMap;

@Entity
public class Paper extends BaseModel{

	public String name;
	public int year;
	@Reference
	public Course course ;
	@Reference
	public SubjectSource source;
	@Reference
	public Set<City> cities = new HashSet<City>();
	@Reference
	public Map<Tag ,Integer> bigTag = new EasyMap<Tag ,Integer>();
	@Reference
	public Set<Tag> tags = new HashSet<Tag>();
}
