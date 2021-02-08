import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
public class SeleniumTest {
	
	//Properties 설정
	public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH = "C:/EUNHAE/chromedriver.exe";
	static //5*150 
	List<TestCaseData> list = new ArrayList<>();
	
	
	//각 dbms별 json 파일 가져오기 
	public static String[] fname={
			"C:\\EUNHAE\\eclipse-workspace\\Selenium_TEST\\src\\MySQL.json",
			"C:\\EUNHAE\\eclipse-workspace\\Selenium_TEST\\src\\Oracle.json",
			"C:\\EUNHAE\\eclipse-workspace\\Selenium_TEST\\src\\PostgreSQL.json",
			"C:\\EUNHAE\\eclipse-workspace\\Selenium_TEST\\src\\Redshift.json",
			"C:\\EUNHAE\\eclipse-workspace\\Selenium_TEST\\src\\SQLserver.json",
			"C:\\EUNHAE\\eclipse-workspace\\Selenium_TEST\\src\\SohaDB.json"
			};
	
	public static void main(String[] args) throws Exception {
			
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		TestServiceImpl service = new TestServiceImpl();
		
		//가져온 파일을 list에 넣기 
		for(int h=0; h<fname.length; h++) {
			List<JSONObject> list =check(fname[h]);		
			//파일 내보내기 
			File file = new File("C:\\EUNHAE\\DataStudioTestCase"+(h+1)+".txt");
			FileWriter writer = null;
			try {
				writer = new FileWriter(file, true);
				for(int i=0; i<list.size(); i++) {
					JSONObject data = (JSONObject) list.get(i);
					Set<String> setkey=(Set<String>) data.keySet();
					for(String a:setkey) {
						if(!a.equals("OPER_LIST")) {
							writer.write(a+" : "+ data.get(a)+"\n");}
						}
					service.oper((JSONArray)data.get("OPER_LIST"), h);
					if(service.Comment !="")
					{
						writer.write("Comment: "+service.Comment+"\n");
						service.Comment = "";
						}
					writer.write("Result: "+service.Result+"\n\n");
					}
				writer.flush();
				}catch (Exception e) {
					e.printStackTrace();
					} finally {
						try {
							if (writer !=null)
								writer.close();
							}catch (Exception e) {
								e.printStackTrace();
								}
						}
			}
		}
	
	
	public static String jdriver = null;
	
	/**
	 * 
	 * @param s String 
	 * @return jsonArray return
	 * @throws Exception
	 */
    public static JSONArray check(String s) throws Exception{
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(s));
        JSONArray jo = (JSONArray) obj;

        for(int i=0; i<jo.size(); i++) {
        }
        return jo;
    }

    public static void setDriver(String driver){
        jdriver = driver;
    }
    public String getDriver(){
        return jdriver;
    }
    
}




