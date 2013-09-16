package controllers;

import java.util.List;

import models.Subject;
import play.mvc.Controller;
import play.mvc.With;
@With(Secure.class)
public class Subjects extends Controller {

	public static void index(){
		List<Subject> list = Subject.findAll();
		render(list);
	}
}
