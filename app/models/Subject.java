package models;

import java.util.*;

import models.enums.SubjectStatus;
import models.enums.SubjectType;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

import play.modules.morphia.Model;

/**
 * Created with IntelliJ IDEA.
 * User: wji
 * Date: 13-7-11
 * Time: 下午8:25
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Subject extends Model{

    public SubjectStatus status;

    public SubjectType type;
    
    @Reference
    public Tag course;
    
    @Reference
    public Tag grade;
    
    @Reference
    public Set<Tag> tags = new HashSet();

    public String title;
 
    @Reference
    public List<Option> options = new ArrayList();
   
    @Reference
    public List<Option> answer= new ArrayList();

    public String solution;
    
    public int weight=50;

    public int frequency = 14;

    public long  ACR=0;

    public long FCR =0;

    public long SCR =0;

    @Reference
    public User owner;
    
    public Date createAt = new Date();

    public Date updateAt = new Date();
   
}


