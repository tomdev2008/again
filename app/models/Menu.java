package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import play.modules.morphia.Model;

/**
 * 统一后台管理菜单模型类.
 * @author jiwei
 * @since 2013-7-14
 */
@Entity
public class Menu extends Model {
    private static final long serialVersionUID = 4249887676256386150L;
    /**
	 * 菜单名称.
	 */
	public String menuName;
	
	/**
	 * 该菜单指向的controller的method方法.
	 */
	public String url;
	/**
	 * 菜单顺序.
	 */
	public long order;
	/**
	 * 该菜单的父节点 如果为总节点，则为空.
	 */
	@Reference(ignoreMissing = true)
	public Menu parent;
}