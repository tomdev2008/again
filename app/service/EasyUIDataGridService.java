package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.modules.morphia.Model.MorphiaQuery;

public class EasyUIDataGridService {
	/**
	 * 生成datagrid表格所需要的数据格式.
	 * @param page 当前页
	 * @param rows 当前页条数
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @param query MorphiaQuery
	 * @return 数据格式
	 */
	public static Map getDataGridMap(final int page,
			final int rows, final String sort, final String order, final MorphiaQuery query) {
		Map map = new HashMap();
		String sortTemp = "-" + sort;
		if ("asc".equals(order)) { //默认倒序
			sortTemp = sort;
		}
		List list = query.offset((page - 1) * rows).limit(rows)//分页
				.order(sortTemp).asList();
		long total = query.count();
		map.put("total", total);
		map.put("rows", list);
		return map;
	}
	
	/**
	 * 生成datagrid表格所需要的数据格式.
	 * @param page 当前页
	 * @param rows 当前页条数
	 * @param sort 排序字段
	 * @param order 排序方式
	 * @param query MorphiaQuery
	 * @return 数据格式
	 */
	public static Map getDataGridMap(final int page,
			final int rows, final MorphiaQuery query) {
		Map map = new HashMap();
		
		List list = query.offset((page - 1) * rows).limit(rows)//分页
				.asList();
		long total = query.count();
		map.put("total", total);
		map.put("rows", list);
		return map;
	}

}
