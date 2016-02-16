package com.example.rss;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.feedparser.network.ResourceRequest;
import org.apache.commons.feedparser.network.ResourceRequestFactory;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.xml.sax.InputSource;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

public class RssProvider implements RssManage {

	private SyndFeed feed = null;
	private String resource = null;

	private RssProvider(String resource, SyndFeed feed) {
		super();
		this.feed = feed;
		this.resource = resource;
	}

	public static RssManage builder(String resource) throws IOException,
			IllegalArgumentException, FeedException {
		ResourceRequest request = ResourceRequestFactory
				.getResourceRequest(resource);
		InputStream in = request.getInputStream();
		SyndFeed feed = builder(in);
		return new RssProvider(resource, feed);
	}

	private static SyndFeed builder(InputStream in)
			throws IllegalArgumentException, FeedException {
		SyndFeedInput input = new SyndFeedInput();
		InputSource is = new InputSource(in);
		return input.build(is);
	}

	public String getChannelTitle() {
		return this.feed.getTitle();
	}

	public String getChannelDescribe() {
		return this.feed.getDescription();
	}

	public Date getChannelPubishDate() {
		return this.feed.getPublishedDate();
	}

	public String getChannelPubishDate(String dateFormat) {
		return DateFormatUtils.format(this.feed.getPublishedDate(), dateFormat);
	}

	public String getChannelLink() {
		return this.feed.getLink();
	}

	public String getResource() {
		return resource;
	}

	public Channel getChannel() {
		return (Channel) this.feed.originalWireFeed();
	}

	public Image getImage() {
		Image image = new Image();
		SyndImage simage = this.feed.getImage();
		image.setDescription(simage.getDescription());
		return image;
	}

	public List<Item> getItems() {
		List<Item> items = new ArrayList<Item>();

		return items;
	}

}
