package com.example.util;

import java.util.Date;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.feedparser.DefaultFeedParserListener;
import org.apache.commons.feedparser.FeedParser;
import org.apache.commons.feedparser.FeedParserException;
import org.apache.commons.feedparser.FeedParserFactory;
import org.apache.commons.feedparser.FeedParserListener;
import org.apache.commons.feedparser.FeedParserState;
import org.apache.commons.feedparser.network.NetworkException;
import org.apache.commons.feedparser.network.ResourceRequest;
import org.apache.commons.feedparser.network.ResourceRequestFactory;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.xml.sax.InputSource;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

public class FeedParserDemo {
	
	/**used apache commons-feedparser.jar [ for rss|atom]]
	 * @param rss
	 */
	public static void useFeedparser(String rss){
		try {
			FeedParser parser = FeedParserFactory.newFeedParser();

			FeedParserListener listener = new DefaultFeedParserListener() {

				public void onChannel(FeedParserState state, String title,
						String link, String description) throws FeedParserException {

					System.out.println("Found a new channel: " + title);
					if(state.current.getChild("pubDate")!=null)
						System.out.println("Found a new channel publish datetime:" +state.current.getChild("pubDate").getValue());
				}

				public void onItem(FeedParserState state, String title,
						String link, String description, String permalink)
						throws FeedParserException {

					System.out.println(String.format("Found a new published article:[%s]:[%s][%s] [%s]", title,description,permalink,link));
				}

				public void onCreated(FeedParserState state, Date date)
						throws FeedParserException {
					System.out.println("Which was created on: " + date);
				}
				
				public void onImage(FeedParserState state, String title,
						String link, String url) throws FeedParserException {
					super.onImage(state, title, link, url);
					System.out.println(String.format("Which was a Image: %s",  url));
				}
				
				public void onIssued(FeedParserState state, String content)
						throws FeedParserException {
					super.onIssued(state, content);
					System.out.println(String.format("Which was a Issue: %s",  content));
				}
			};


			System.out.println("Fetching resource:" + rss);

			ResourceRequest request = ResourceRequestFactory.getResourceRequest(rss);
			InputStream is = request.getInputStream();
			parser.parse(listener, is, rss);
			
		} catch (NetworkException e) {
			e.printStackTrace();
		} catch (FeedParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**used rome.jar [ for rss|atom]
	 * @param rss
	 */
	public static void useRome(String rss){
		try {
			ResourceRequest request = ResourceRequestFactory.getResourceRequest(rss);
			InputStream in = request.getInputStream();
			
			SyndFeedInput input = new SyndFeedInput();
			InputSource is = new InputSource(in);
			
			SyndFeed sf = input.build(is);
			System.out.println("title: "+sf.getTitle());
			System.out.println("description: "+sf.getDescription());
			System.out.println("encoding: "+sf.getEncoding());
			System.out.println("link: "+sf.getLink());
			System.out.println("publish date: "+DateFormatUtils.format(sf.getPublishedDate(), "yyyy-MM-dd hh:mm:ss"));
			
			
			SyndImage si=sf.getImage();
			System.out.println("image title: "+si.getTitle());
			System.out.println("image url: "+si.getUrl());
			System.out.println("image link: "+si.getLink());
			
			Iterator<?>  its =sf.getEntries().iterator();
			while(its.hasNext()){
				SyndEntry se =(SyndEntry) its.next();
				String title  = se.getTitle();
				String link = se.getLink();
				String destype=se.getDescription().getType();
				String desvalue = se.getDescription().getValue();
				String pubdate = DateFormatUtils.format(se.getPublishedDate(), "yyyy-MM-dd hh:mm:ss");
				
				System.out.println(String.format("%s %s %s [%s] %s", pubdate,title,link,destype,desvalue));
			}
			
			in.close();
			
		} catch (NetworkException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FeedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		String rss ="http://www.oschina.net/project/rss";// "http://www.duanwenxue.com/data/rss/1.xml";
		useFeedparser(rss);
		useRome(rss);

	}
}
