package models.enums;

/**
 * 用户角色管理
 * @author zhaojingyu
 * @since 2012-08-30
 */
public enum RoleType {
	/**
	 * 学生，权限为0.
	 */
	STUDENT(0),
	
	/**
	 * 教师，权限为10.
	 */
	TEACHER(10),
	/**
	 * 管理员角色，权限为100.
	 */
	ADMIN(100),
	/**
	 * 财务管理员，权限为200.
	 */
	SUPER(200)
	;
	
	
	public final int level;

	/**
	 * 构造函数.
	 * @param level
	 */
	private RoleType(int level) {
		this.level = level;
	}
}
