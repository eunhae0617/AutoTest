import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestServiceImpl{
	
	public String editorid = "";
	public static List<Map<String, Object>> mapList = new ArrayList<>();
	
	WebDriver driver = null;
	String url = "http://192.168.10.94:8080/petra/init.do";
	//String url = "http://106.244.173.20:8888/petra/init.do ";
	
	public String Comment = "" ; 
	public int Result;
	public TestServiceImpl() {
		//Driver SetUp
		ChromeOptions options = new ChromeOptions();
		options.setCapability("ignoreProtectedModeSettings", true);
		driver = new ChromeDriver(options);
		
		driver.get(url);
		driver.manage().window().maximize();    		           
		
		testMap();
	}
	
	/**
	 * Map.json Array로 가져와서 mapList로 저장 
	 */
	public static void testMap() {
		try {
			JSONParser parser=new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("C:\\EUNHAE\\eclipse-workspace\\Selenium_TEST\\src\\Map.json"));
			for(int i = 0; i < jsonArray.size(); i++) {
				JSONObject obj = (JSONObject) jsonArray.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				Set<String> seta=(Set<String>) obj.keySet();
				for(String a:seta) {
					map.put(a, obj.get(a));
					}
				mapList.add(map);
				}
			} catch (Exception e) {
				e.printStackTrace();
				}
		}
	
	/**
	 * json에서 map변수 사용 시 ${} 
	 * @param sr String 
	 * @param map
	 * @return
	 */
	private String findvalue(String sr, Map<String, Object> map) {
		int idx = sr.indexOf("${");
		String result=""; //결과값을 담을 변수
		while(idx>-1&&idx<sr.length()-1){ //주어진 selector길이 만큼 찾는다.
			idx=sr.indexOf("${"); 
			if(idx<0) break;
			result+=sr.substring(0,idx);
			int endIdx=sr.substring(idx).indexOf("}"); //해당하는 변수의 마지막 인덱스까지 구한다.
			if(endIdx>0){
				String s=sr.substring(idx+2, idx+endIdx); //첫 인덱스에서 마지막 인덱스까지의 값을 s에 넣고
				//System.out.println(map.get(s)); //map에서 해당하는 값을 가져온다.
				result+=map.get(s);
				idx+=endIdx+1; //다음 변수를 찾기위해 마지막 인덱스+1를 idx에 넣는다.
				sr=sr.substring(idx);  
				idx = 0;
				}
			}
		result+=sr; 
		sr=result;
		return sr;
		}
	
	/**
	 * 
	 * @param list JSONObject data 얻어올 list
	 * @param h 몇번째 파일인지 MapList에서 얻기위한 번호
	 */
	public void oper(JSONArray list, int h) {
		Map<String, Object> map=mapList.get(h);
		Result = 0;
		try {
			for(int i = 0; i < list.size(); i++) {
				if (Result != 1) {
					Result=0;
					};
					JSONObject data = (JSONObject) list.get(i);
					int type = Integer.parseInt(data.get("TYPE")+"");
					String selector="";
					String value="";
					if(data.containsKey("SELECTOR")){
						selector=data.get("SELECTOR").toString();
						selector=findvalue(selector,map);
						}
					if(data.containsKey("VALUE")){
						value=data.get("VALUE").toString();
						value=findvalue (value, map);
						}	
					if(type == 0) {
						elementSendKey(selector, value, Integer.parseInt(data.get("MS")+""));
					}else if(type == 1) {
						elementClick(selector, Integer.parseInt(data.get("MS")+""));
					}else if(type == 2) {
						jsscript(selector, Integer.parseInt(data.get("MS")+""));
					}else if(type == 3) {
						Wait(selector);
					}else if(type == 4) {
						map.put(data.get("KEY").toString(), id());
					}else if(type == 5) {
						StringSelector(selector,data.get("STRING").toString(), Integer.parseInt(data.get("MS")+""));					
					}
				
				}	
				}catch (Exception e) {
				e.printStackTrace();
				Result=1;
			} finally {
				//driver.close();
					
			}
		//}
	}
	
	/**
	 * key입력
	 * @param selector css 셀렉터
	 * @param value sendkey의 변수 
	 * @param ms 
	 */
	private void elementSendKey(String selector, String value, int ms){
		WebElement element = driver.findElement(By.cssSelector(selector));
		element.sendKeys(value);
		
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Result=1;
		}
	}
	
	/**
	 * 클릭
	 * @param selector css 셀렉터
	 * @param ms
	 */
	private void elementClick(String selector, int ms){
		WebElement element = driver.findElement(By.cssSelector(selector));
		element.click();
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Result=1;
		}
	}
	
	/**
	 * String 긁어오기 
	 * @param selector css 셀렉터 
	 * @param string 긁어온 String 저장
	 * @param ms
	 */
	private void StringSelector(String selector,String string, int ms) {
		WebElement element = driver.findElement(By.cssSelector(selector));
		string = element.getText();
		System.out.println("### String ### : \n" + string + "\n");
		Comment =  "\n" + string ;		
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Result=1;
		}
	}
	
	/**
	 * script 수행
	 * @param selector css 셀렉터
	 * @param ms
	 */
	private void jsscript(String selector,int ms) {
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript(selector);
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Result=1;
		}
	}
	
	/**
	 * 대기 
	 * @param selector 대기 중 찾을 css 셀렉터 
	 */
	private void Wait(String selector){
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
	}
	
	/**
	 * editor id 가져오기 
	 * @return editor id 
	 */
	private String id() {
		return driver.findElement(By.cssSelector("#editorTab > li:nth-child(2)>a")).getAttribute("href").split("tab_")[1];
	}
	
}

