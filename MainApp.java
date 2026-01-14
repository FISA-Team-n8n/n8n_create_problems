import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainApp {

	public static void main(String[] args) {
		// n8n 주소 입력
		String gasUrl = ""; //google app script URL 입력
        String n8nUrl = ""; // n8n URL 입력      
     	String myEmail = ""; // 메일 받고자 하는 이메일 입력
        
        try {
        		// 분석하고 싶은 기사 URL입력
        		String newsArticleUrl = ""; // 뉴스 URL 입력
        		
        		String json = String.format("{\"link\":\"%s\", \"email\":\"%s\", \"gasUrl\":\"%s\"}"
        				, newsArticleUrl, myEmail, gasUrl);		
        		
        		System.out.println("n8n으로 기사 요청 전송: " + json);
        		
        		String response = sendPost(n8nUrl, json);
        		
        		System.out.println("n8n 응답 수신: " + response);
        } catch(Exception e) {
        		System.out.println("에러 발생***");
        		e.printStackTrace();
        }

	}
	
	public static String sendPost(String targetUrl, String data) throws Exception{
		URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json"); 
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        }
        
        Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
	}

}
