package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import play.modules.morphia.Model;

import java.util.Set;

/**
 * 统一后台管理角色模型类.
 * @author jiwei
 * @since 2013-7-14
 */
@Entity
public class AdminRole extends Model {
    private static final long serialVersionUID = 6473628138478841839L;
    /**
	 * 角色名称.
	 */
	public String roleName;
	/**
	 * 角色描述.
	 */
	public String roleDesc;
	/**
	 * 角色等级.
	 */
	public int roleLevel;
	/**
	 * 该角色可以访问的菜单树列表.
	 */
	@Reference(ignoreMissing = true)
	public Set<Menu> menuList;
}