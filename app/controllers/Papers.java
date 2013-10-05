package controllers;

import java.util.List;

import models.Paper;
import play.mvc.Controller;

public class Papers extends Controller {

	public static void inde(int psize){
		List<Paper> list = Paper.find().order("-year").asList();
		render(list);
	}

}
