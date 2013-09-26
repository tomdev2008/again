package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jxl.Workbook;
import jxl.format.*;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class YtkAnalysis {

	public static List<Question> qlist = new ArrayList<Question>();

	public static void main(String[] args) throws UnsupportedEncodingException {
		File file = new File("C:\\Users\\jiwei\\Desktop\\ytk\\2013-cailiao.txt");
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String html = "";
		try {
			html = new String(filecontent, "utf-8");
		} catch (UnsupportedEncodingException e) {
			System.err.println("The OS does not support utf-8");
			e.printStackTrace();
		}

		Document doc = Jsoup.parse(html);
		// Elements navElements
		// =doc.getElementsByClass("nav-underline").get(0).children();

		// //解析分类导航
		// for(org.jsoup.nodes.Element el:navElements){
		// Elements liElements =el.getElementsByTag("li");
		// for(org.jsoup.nodes.Element li:liElements){
		// Elements as =li.getElementsByTag("a");
		// Element a = as.get(0);
		// System.out.println(a.attr("href"));
		// }
		// }
		processMaterialSubject(doc);
		exportExcel("C:\\Users\\jiwei\\Desktop\\ytk\\test.xls",qlist);
		// 导出到excel

	}

	public static void processSubject(Document doc) {
		Elements questionElements = doc.getElementsByClass("chapter-wrap")
				.get(0).children();
		getQA(questionElements, null);
	}

	public static void getQA(Elements questionElements, Question big) {
		for (org.jsoup.nodes.Element el : questionElements) {
			// index
			if (el.hasClass("question-with-solution-wrap")) {
				Question item = new Question();
				Element index = el.getElementsByClass("question").get(0)
						.getElementsByClass("index").get(0);
				System.out.println("index=" + index.text());
				item.setIndex(index.text());
				Element question = el.getElementsByClass("question").get(0)
						.getElementsByClass("overflow").get(0);
				// type
				Element type = question.getElementsByClass("question-type-tip")
						.get(0);
				System.out.println("type=" + type.text());
				if (big == null) {
					item.setType(type.text());
				} else {
					item.setSubType(type.text());
				}
				Elements contents = question.getElementsByTag("p");
				String content = "";
				for (org.jsoup.nodes.Element con : contents) {
					content = content + con.text();
				}
				System.out.println("content=" + content);
				item.title = content;
				// option
				Elements options = el.getElementsByClass("option");
				for (org.jsoup.nodes.Element op : options) {
					System.out.println("op=" + op.text());
					item.options.add(op.text());

				}

				// anower
				Element answer = el.getElementsByClass("answer-meta").get(0);
				System.out.println("answer=" + answer.text());

				// solution overflow

				Elements ktag = el.getElementsByClass("solution-item").get(0)
						.getElementsByClass("text-label-inner");
				Elements solutions = el.getElementsByClass("solution-item")
						.get(1).getElementsByClass("overflow").get(0)
						.getElementsByTag("p");

				System.out.println("ktag=" + getTxt(ktag, ","));
				item.setTag(getTxt(ktag, ","));
				System.out.println("solutions=" + getTxt(solutions, ""));
				item.solution = getTxt(solutions, "");
				if (big != null) {
					big.subs.add(item);
				} else {
					qlist.add(item);
				}
			}
		}
	}

	public static String getTxt(Elements els, String split) {
		String txt = "";
		for (Element el : els) {
			String tmp = "";
			if ("考点".equals(el.text())) {
				continue;
			}
			if (StringUtils.isBlank(el.text())) {
				tmp = el.html();
			} else {
				tmp = el.text();
			}
			txt = txt + tmp + split;
		}
		if (StringUtils.isBlank(split)) {
			return txt;
		} else {
			return txt.substring(0, txt.lastIndexOf(','));
		}

	}

	public static void processMaterialSubject(Document doc) {
		Elements questionElements = doc.getElementsByClass("material-wrap");
		for (org.jsoup.nodes.Element el : questionElements) {
			// index
			if (el.hasClass("material-wrap")) {
				Elements title = el.getElementsByClass("material-content")
						.get(0).getElementsByTag("p");
				System.out.println(getTxt(title, ""));
				Question big = new Question();
				big.title = getTxt(title, "");
				big.type ="Material";
				getQA(el.children(), big);
				qlist.add(big);
			}

		}
	}

	/** */
	/**
	 * 导出数据为XLS格式
	 * 
	 * @param fileName
	 *            文件的名称，可以设为绝对路径，也可以设为相对路径
	 * @param content
	 *            数据的内容
	 */
	public static void exportExcel(String fileName, List<Question> content) {
		WritableWorkbook wwb;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fileName);
			wwb = Workbook.createWorkbook(fos);
			WritableSheet ws = wwb.createSheet("单选题列表", 10); // 创建一个工作表
			WritableSheet wsBig = wwb.createSheet("多选题列表", 10);
			// 设置单元格的文字格式
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLUE);
			WritableCellFormat wcf = new WritableCellFormat(wf);
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			wcf.setAlignment(Alignment.CENTRE);
			ws.setRowView(0, 500);
			wsBig.setRowView(0, 500);
			// 填充数据的内容
			Question[] p = new Question[content.size()];
			int bigCnt = 0;
			for (int i = 0; i < content.size(); i++) {
				p[i] = (Question) content.get(i);
				if(p[i].type.equals("Material")){
					
					//题干
					wsBig.addCell(new Label(0, bigCnt +1, String.valueOf(p[i].id), wcf));
					wsBig.addCell(new Label(1, bigCnt + 1, p[i].title, wcf));
					wsBig.addCell(new Label(2, bigCnt + 1, p[i].type, wcf));
					bigCnt++;
					for(Question q:p[i].subs){
						wsBig.addCell(new Label(0, bigCnt + 1, String.valueOf(q.id), wcf));
						wsBig.addCell(new Label(1, bigCnt + 1, q.title, wcf));
						wsBig.addCell(new Label(2, bigCnt + 1, q.type, wcf));
						wsBig.addCell(new Label(3, bigCnt + 1, q.tags, wcf));
						wsBig.addCell(new Label(4, bigCnt + 1, q.solution, wcf));
						for( int j=0;j<q.options.size();j++){
							String s = q.options.get(j);
							wsBig.addCell(new Label(5+j, bigCnt + 1, s, wcf));
						}
						bigCnt++;
					}
					if (i == 0)
						wcf = new WritableCellFormat();
					
				}else{

					ws.addCell(new Label(0, i + 1, String.valueOf(p[i].id), wcf));
					ws.addCell(new Label(1, i + 1, p[i].title, wcf));
					ws.addCell(new Label(2, i + 1, p[i].type, wcf));
					ws.addCell(new Label(3, i + 1, p[i].tags, wcf));
					ws.addCell(new Label(4, i + 1, p[i].solution, wcf));
					for( int j=0;j<p[i].options.size();j++){
						String s = p[i].options.get(j);
						ws.addCell(new Label(5+j, i + 1, s, wcf));
					}
					
					if (i == 0)
						wcf = new WritableCellFormat();
				}
				
			}
			wwb.write();
			wwb.close();

		} catch (IOException e) {
		} catch (RowsExceededException e) {
		} catch (WriteException e) {
		}
	}
}
