package controllers;

import play.mvc.Controller;
import play.mvc.With;
//@With(Secure.class)
public class Application extends Controller {

    public static void index() {
        render();
    }
    public static void reg() {
    	render("reg.html");
    }
    public static void doExercise(String courseName) {
    	render(courseName);
    }
    
    public static void result() {
    	render();
    }
}