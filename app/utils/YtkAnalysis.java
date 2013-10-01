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

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.qiniu.api.auth.AuthException;

import tools.UploadUtils;

public class YtkAnalysis {

	public static List<Question> qlist = new ArrayList<Question>();

	public static String source;
	public static String bigTag;
	public static String year;
	public static String area;
	public static void main(String[] args) throws UnsupportedEncodingException {
		File files = new File("/Users/wji/Desktop/ytk/");
		//File files = new File("C:\\Users\\jiwei\\Desktop\\ytk\\");
		if(files.isDirectory()){
			File[] dir = files.listFiles();
			for(File d :dir){
				if(d.getName().indexOf("DS_Store")>0){
					continue;
				}
				String fileName = d.getName();
				String[] fn = fileName.split("-");
				year = fn[0];
				source = fn[1];
				area =fn[2];
					
				File f = new File(d.getPath()+File.separator+"yanyu.txt");
				bigTag ="言语理解与表达";
				if(f.exists()){
					processTxt(f);
				}
				f = new File(d.getPath()+File.separator+"shuliang.txt");
				bigTag ="数量关系";
				if(f.exists()){
					processTxt(f);
				}
				f = new File(d.getPath()+File.separator+"panduan.txt");
				bigTag ="判断推理";
				if(f.exists()){
					processTxt(f);
				}
				f = new File(d.getPath()+File.separator+"changshi.txt");
				bigTag ="常识判断";
				if(f.exists()){
					processTxt(f);
				}
				f = new File(d.getPath()+File.separator+"ziliao.txt");
				bigTag ="资料分析";
				if(f.exists()){
					processTxt(f);
				}
				// 导出到excel
				//files
				exportExcel("/Users/wji/Desktop/ytk/"+fileName+".xls",qlist);		
				//exportExcel("C:\\Users\\jiwei\\Desktop\\ytk\\"+fileName+".xls",qlist);		
			}
		}
	}
	
	public static void processTxt(File f){
		Long filelength = f.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(f);
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
		source =doc.getElementsByClass("exercise-wrap").get(0).attr("data-sheet-name");
		processSubject(doc);
		processMaterialSubject(doc);
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
//				for (org.jsoup.nodes.Element con : contents) {
//					content = content + con.text();
//				}
				if(StringUtils.isBlank(content)){
					content = getTxt(contents,"");	
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
				Element answer = el.getElementsByClass("correct-answer").get(0);
				System.out.println("answer=" + answer.text());
				item.ans = answer.text();
				// solution overflow

				Elements ktag = el.getElementsByClass("solution-item").get(0)
						.getElementsByClass("text-label-inner");
				Elements solutions = el.getElementsByClass("solution-item")
						.get(1).getElementsByClass("overflow").get(0)
						.getElementsByTag("p");

				String tagStr =getTxt(ktag, "");
				item.setTag(tagStr);
				String solutionsStr=getTxt(solutions, "");
				item.solution = solutionsStr;
				item.source = source;
				item.bigTag = bigTag;
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
				Elements imgs = el.getElementsByTag("img");
				for(Element img :imgs){
					String imgUrl = img.attr("src");
					if(imgUrl.indexOf("http") <0){
						continue;
					}
					try {
						String picKey = UploadUtils.copyUrl(imgUrl);
						String newUrl = "http://hizhiti.u.qiniudn.com/"+picKey;
						img.attr("src",newUrl);
					} catch (IOException e) {
						
						e.printStackTrace();
					} catch (AuthException e) {
						
						e.printStackTrace();
					} catch (JSONException e) {
					
						e.printStackTrace();
					}
				}
				
				tmp = el.html();

			} else {
				tmp = el.text();
			}
			txt = txt + tmp + split;
		}
		System.out.println(txt);
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
				Question big = new Question();
				big.title = getTxt(title, "");
				big.type ="MATERIAL";
				big.source = source;
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
			WritableSheet ws = wwb.createSheet("普通题列表", 10); // 创建一个工作表
			WritableSheet wsBig = wwb.createSheet("材料题列表", 10);
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
				System.out.println("=="+i);
				if(i==85){
					System.out.println("==");
				}
				p[i] = (Question) content.get(i);
				if(p[i].type.equals("MATERIAL")){
					
					//题干
					wsBig.addCell(new Label(0, bigCnt +1, String.valueOf(p[i].source), wcf));
					wsBig.addCell(new Label(1, bigCnt +1, String.valueOf(p[i].id), wcf));
					wsBig.addCell(new Label(2, bigCnt + 1, p[i].title, wcf));
					wsBig.addCell(new Label(3, bigCnt + 1, p[i].type, wcf));
					bigCnt++;
					for(Question q:p[i].subs){
						wsBig.addCell(new Label(1, bigCnt + 1, String.valueOf(q.id), wcf));
						wsBig.addCell(new Label(2, bigCnt + 1, q.title, wcf));
						wsBig.addCell(new Label(3, bigCnt + 1, q.type, wcf));
						wsBig.addCell(new Label(4, bigCnt + 1, q.bigTag, wcf));
						wsBig.addCell(new Label(5, bigCnt + 1, q.tags, wcf));
						wsBig.addCell(new Label(6, bigCnt + 1, q.ans, wcf));
						wsBig.addCell(new Label(7, bigCnt + 1, q.solution, wcf));
						for( int j=0;j<q.options.size();j++){
							String s = q.options.get(j);
							wsBig.addCell(new Label(8+j, bigCnt + 1, s, wcf));
						}
						bigCnt++;
					}
					if (i == 0)
						wcf = new WritableCellFormat();
					
				}else{
					ws.addCell(new Label(0, i +1, String.valueOf(p[i].source), wcf));
					ws.addCell(new Label(1, i + 1, String.valueOf(p[i].id), wcf));
					ws.addCell(new Label(2, i + 1, p[i].title, wcf));
					ws.addCell(new Label(3, i + 1, p[i].type, wcf));
					ws.addCell(new Label(4, i + 1, p[i].bigTag, wcf));
					ws.addCell(new Label(5, i + 1, p[i].tags, wcf));
					ws.addCell(new Label(6, i + 1, p[i].ans, wcf));
					ws.addCell(new Label(7, i + 1, p[i].solution, wcf));
					for( int j=0;j<p[i].options.size();j++){
						String s = p[i].options.get(j);
						ws.addCell(new Label(8+j, i + 1, s, wcf));
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
