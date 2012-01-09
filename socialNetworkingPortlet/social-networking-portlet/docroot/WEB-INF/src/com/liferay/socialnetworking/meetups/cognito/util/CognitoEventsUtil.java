package com.liferay.socialnetworking.meetups.cognito.util;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.socialnetworking.meetups.util.MeetupsConstants;

/**
 * The Class CognitoEventsUtil.
 * 
 * @author Gaurav
 */
public class CognitoEventsUtil {

	/**
	 * Gets the meetups portlet url.
	 * 
	 * @param themeDisplay
	 *            the theme display
	 * @param httpServletRequest
	 *            the http servlet request
	 * @return the meetups portlet url
	 * @throws PortalException
	 *             the portal exception
	 * @throws SystemException
	 *             the system exception
	 */
	public static PortletURL getMeetupsPortletUrl(ThemeDisplay themeDisplay,
			HttpServletRequest httpServletRequest) throws PortalException,
			SystemException {
		PortletURL portletURL = null;
		long plid = PortalUtil.getPlidFromPortletId(
				themeDisplay.getScopeGroupId(),
				MeetupsConstants.MEETUPS_PORTLET_PPID);
		portletURL = PortletURLFactoryUtil.create(httpServletRequest,
				MeetupsConstants.MEETUPS_PORTLET_PPID, plid,
				PortletRequest.RENDER_PHASE);

		return portletURL;
	}

}
