package com.example.rss;

import java.util.Date;
import java.util.List;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;

public interface RssManage {

	public Channel getChannel();

	public Image getImage();

	public List<Item> getItems();

	public String getChannelTitle();

	public String getChannelDescribe();

	public Date getChannelPubishDate();

	public String getChannelPubishDate(String dateFormat);

	public String getChannelLink();

	// public String getItemTitle();
	// public String getItemDescribe();
	// public Date getItemPubishDate();
	// public String getItemPubishDate(String dateFormat);
	// public String getItemLink();

}
