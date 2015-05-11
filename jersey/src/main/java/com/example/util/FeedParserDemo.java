package com.example.util;

import java.util.Date;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.feedparser.DefaultFeedParserListener;
import org.apache.commons.feedparser.FeedParser;
import org.apache.commons.feedparser.FeedParserException;
import org.apache.commons.feedparser.FeedParserFactory;
import org.apache.commons.feedparser.FeedParserListener;
import org.apache.commons.feedparser.FeedParserState;
import org.apache.commons.feedparser.network.ResourceRequest;
import org.apache.commons.feedparser.network.ResourceRequestFactory;

public class FeedParserDemo {

	public static void main(String[] args) throws FeedParserException,
			IOException {
		FeedParser parser = FeedParserFactory.newFeedParser();

		FeedParserListener listener = new DefaultFeedParserListener() {

			public void onChannel(FeedParserState state, String title,
					String link, String description) throws FeedParserException {

				System.out.println("Found a new channel: " + title);

			}

			public void onItem(FeedParserState state, String title,
					String link, String description, String permalink)
					throws FeedParserException {

				System.out.println(String.format("Found a new published article:[%s]:[%s][%s]", title,description,permalink));

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


		String resource = "http://www.duanwenxue.com/data/rss/1.xml";

		if (args.length == 1)
			resource = args[0];

		System.out.println("Fetching resource:" + resource);

		ResourceRequest request = ResourceRequestFactory
				.getResourceRequest(resource);

		InputStream is = request.getInputStream();

		parser.parse(listener, is, resource);
	}

}
