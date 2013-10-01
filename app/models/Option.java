package models;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;

import play.modules.morphia.Model;
import tools.SequenceUtils;


@Entity
public class Option extends BaseModel {

	public String content;
}
