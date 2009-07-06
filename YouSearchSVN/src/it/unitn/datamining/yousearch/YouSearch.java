package it.unitn.datamining.yousearch;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.Query.CategoryFilter;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.Category;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.util.ServiceException;

public class YouSearch {

	private String keyword;
	private FileWriter wrt;
	private YouTubeService service;
	private VideoFeed videoCurrent;
	private YouTubeQuery query;
	private CategoryFilter filterTag;
	private YouSearchResult resultDTO;
	private static YouSearch instance;

	private int maxTotPageRes = 19; // google api allows less then 1000 items
	private int maxResPerPage = 50;
	private int currPage = 0; // 0-based

	private boolean needDump = false;

	public static YouSearch getInstance() {
		if (instance == null)
			instance = new YouSearch();

		return instance;
	}

	public void search(String keyword, boolean dumpFile) {
		this.keyword = keyword;
		this.filterTag = new CategoryFilter(new Category(keyword));
		this.needDump = dumpFile;

		if (this.needDump) {
			try {
				wrt = new FileWriter("dump_" + this.keyword.replace(' ', '_'));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		this.service = new YouTubeService(
				"ytapi-AlessandroScipio-DataMiningTest01-im13a1kh-0",
				"AI39si7hFFsKY44Z_zrZ27F73VPrKDMOqX4fv5ARhY4Mksq-B48x8GhHh9ONuubRThmpApT9mfgce9PmXv1UMDdkkDLsyO_AlQ");

		try {
			this.query = new YouTubeQuery(new URL(
					"http://gdata.youtube.com/feeds/api/videos"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (this.keyword != null)
				this.searchByKeyword();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public YouTubeService getYouTubeService() {
		return this.service;
	}

	private void searchByKeyword() throws MalformedURLException {
		this.resultDTO = new YouSearchResult(this.keyword);
		// search for puppies and include restricted content in the search
		// results
		this.query.setFullTextQuery(this.keyword);
		this.query.setSafeSearch(YouTubeQuery.SafeSearch.NONE);
		this.query.setLanguageRestrict("en");
		this.query.setOrderByRelevanceForLanguage("en");
		this.query.addCategoryFilter(this.filterTag);

		System.out.println(query.getFeedUrl() + query.getQueryUri().toString()
				+ "\n");
		try {
			if (this.needDump)
				this.wrt
						.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<result id=\"result\" keyword=\""
								+ this.keyword + "\">\n");

			for (int i = 1; i <= this.maxTotPageRes; i++) {
				this.currPage = i;
				this.query.setMaxResults(this.maxResPerPage);
				this.query
						.setStartIndex((this.maxResPerPage * this.currPage - 50) + 1);
				try {
					videoCurrent = service.getFeed(query, VideoFeed.class);
				} catch (ServiceException sEx) {
				}

				if (this.needDump)
					this.dumpToFile(videoCurrent);

				this.appendResult(videoCurrent);

				System.out.println("PageCount: " + i + " resPerPage: "
						+ this.maxResPerPage + " URI: "
						+ query.getQueryUri().toString());
				Thread.sleep(10);
			}

			if (this.needDump) {
				this.wrt.write("</result>");
				this.wrt.close();
			}

		} catch (IOException e) {
			// e.printStackTrace();
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}
	}

	public YouSearchResult getResult() {
		System.out.println(this.resultDTO.getResult().toString());
		return this.resultDTO;
	}

	private void appendResult(VideoFeed videoFeed) {
		List<String> videoKeywords;
		List<VideoEntry> x = videoFeed.getEntries();

		for (int i = 0; i < x.size(); i++) {
			String tmpTag = new String();
			YouSearchEntry newItem = new YouSearchEntry();
			videoKeywords = x.get(i).getMediaGroup().getKeywords()
					.getKeywords();
			newItem.setTitle(x.get(i).getTitle().getPlainText().replaceAll("&",
					"&amp;"));
			newItem.setVideoId(x.get(i).getMediaGroup().getVideoId());

			for (int j = 0; j < videoKeywords.size(); j++)
				tmpTag += " " + videoKeywords.get(j).replaceAll("&", "&amp;");

			newItem.setTags(tmpTag);

			this.resultDTO.addItem(newItem);
		}
	}

	private void dumpToFile(VideoFeed videoFeed) {
		String toFile = "";
		List<String> videoKeywords;
		if (videoFeed == null)
			return;

		List<VideoEntry> x = videoFeed.getEntries();
		for (int i = 0; i < x.size(); i++) {
			videoKeywords = x.get(i).getMediaGroup().getKeywords()
					.getKeywords();
			toFile += "\t<video id=\"" + x.get(i).getMediaGroup().getVideoId()
					+ "\">\n";
			toFile += "\t\t<title>"
					+ x.get(i).getTitle().getPlainText().replaceAll("&",
							"&amp;") + "</title>\n";
			toFile += "\t\t<keywords>\n";
			for (int j = 0; j < videoKeywords.size(); j++) {
				String tmpTag = videoKeywords.get(j).replaceAll("&", "&amp;");
				String[] tmpTagArr = tmpTag.split(" ");
				for (int k = 0; k < tmpTagArr.length; k++)
					toFile += "\t\t\t<key>" + tmpTagArr[k] + "</key>\n";
			}

			toFile += "\t\t</keywords>\n";
			toFile += "\t</video>\n";
		}
		try {
			this.wrt.write(toFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
