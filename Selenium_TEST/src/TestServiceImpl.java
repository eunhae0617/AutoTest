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
	 * Map.json Array�� �����ͼ� mapList�� ���� 
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
	 * json���� map���� ��� �� ${} 
	 * @param sr String 
	 * @param map
	 * @return
	 */
	private String findvalue(String sr, Map<String, Object> map) {
		int idx = sr.indexOf("${");
		String result=""; //������� ���� ����
		while(idx>-1&&idx<sr.length()-1){ //�־��� selector���� ��ŭ ã�´�.
			idx=sr.indexOf("${"); 
			if(idx<0) break;
			result+=sr.substring(0,idx);
			int endIdx=sr.substring(idx).indexOf("}"); //�ش��ϴ� ������ ������ �ε������� ���Ѵ�.
			if(endIdx>0){
				String s=sr.substring(idx+2, idx+endIdx); //ù �ε������� ������ �ε��������� ���� s�� �ְ�
				//System.out.println(map.get(s)); //map���� �ش��ϴ� ���� �����´�.
				result+=map.get(s);
				idx+=endIdx+1; //���� ������ ã������ ������ �ε���+1�� idx�� �ִ´�.
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
	 * @param list JSONObject data ���� list
	 * @param h ���° �������� MapList���� ������� ��ȣ
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
	 * key�Է�
	 * @param selector css ������
	 * @param value sendkey�� ���� 
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
	 * Ŭ��
	 * @param selector css ������
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
	 * String �ܾ���� 
	 * @param selector css ������ 
	 * @param string �ܾ�� String ����
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
	 * script ����
	 * @param selector css ������
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
	 * ��� 
	 * @param selector ��� �� ã�� css ������ 
	 */
	private void Wait(String selector){
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
	}
	
	/**
	 * editor id �������� 
	 * @return editor id 
	 */
	private String id() {
		return driver.findElement(By.cssSelector("#editorTab > li:nth-child(2)>a")).getAttribute("href").split("tab_")[1];
	}
	
}

