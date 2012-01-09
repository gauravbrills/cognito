package com.liferay.socialnetworking.meetups.cognito.util;

import com.liferay.portal.kernel.search.HitsOpenSearchImpl;

/**
 * The Class MeetupsOpenSearchImpl.
 * 
 * @author gaurav
 */
public class MeetupsOpenSearchImpl extends HitsOpenSearchImpl {

	/** The Constant SEARCH_PATH. */
	public static final String SEARCH_PATH = "/c/meetups/open_search";

	/** The Constant TITLE. */
	public static final String TITLE = "Cognito Events Search: ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liferay.portal.kernel.search.HitsOpenSearchImpl#getPortletId()
	 */
	public String getPortletId() {
		return CognitoEventIndexer.PORTLET_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liferay.portal.kernel.search.HitsOpenSearchImpl#getSearchPath()
	 */
	public String getSearchPath() {
		return SEARCH_PATH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liferay.portal.kernel.search.HitsOpenSearchImpl#getTitle(java.lang
	 * .String)
	 */
	public String getTitle(String keywords) {
		return TITLE + keywords;
	}

}