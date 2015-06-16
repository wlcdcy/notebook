package com.example.commons;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrelloUtils {

	public static void main(String[] args) {
//		get();
		getBoard(board_id);
	}
	public static Logger logger = LoggerFactory.getLogger(TrelloUtils.class);
	public static String key = "f0b19e018eb3e79393f381e6b73bb687";
	public static String secret = "b97c687707005760d7a7c710a66f28ae9eddb964916decb93e171549f6d4fcd3";
	public static String board_id = "4d5ea62fd76aa1136000000c";
	public static String my_board_id ="FzH7OKZ6";
	
	public static String get(){
		String url="https://api.trello.com/1/board/4d5ea62fd76aa1136000000c";
		String req_url = String.format("%s?key=%s&cards=open&lists=open", url, key);
		String resp_body =NetClientUtils.request(HttpGet.METHOD_NAME, req_url,"");
		logger.info(resp_body);
		return resp_body;
		
	}
	
	public static String getBoard(String id){
		String url ="https://api.trello.com/1/board";
		String req_url = String.format("%s/%s?key=%s&cards=open&lists=open",url, id, key);
		String resp_body =NetClientUtils.request(HttpGet.METHOD_NAME, req_url,"");
		logger.info(resp_body);
		return resp_body;
	}
	

}
