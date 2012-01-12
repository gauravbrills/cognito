/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.socialnetworking.meetups.asset;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.BaseAssetRenderer;
import com.liferay.socialnetworking.meetups.cognito.util.CognitoEventsUtil;
import com.liferay.socialnetworking.meetups.util.WebKeys;
import com.liferay.socialnetworking.model.MeetupsEntry;

/**
 * @author Jorge Ferrer
 * @author Juan Fernández
 */
public class MeetupsEntryAssetRenderer extends BaseAssetRenderer {

	public MeetupsEntryAssetRenderer(MeetupsEntry entry) {
		_entry = entry;
	}

	public long getClassPK() {
		return _entry.getMeetupsEntryId();
	}

	public String getSummary() {
		return HtmlUtil.stripHtml(_entry.getDescription());
	}

	public String getTitle() {
		return _entry.getTitle();
	}

	public PortletURL getURLEdit(LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse) {
		PortletURL editPortletURL = null;
		ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest
				.getAttribute(WebKeys.THEME_DISPLAY);
		// Get url for add meetups entry
		try {
			editPortletURL = CognitoEventsUtil
					.getMeetupsPortletUrl(themeDisplay,
							liferayPortletRequest.getHttpServletRequest());
			editPortletURL.setParameter("jspPage", "/meetups/edit_entry");
			editPortletURL.setParameter("meetupsEntryId",
					String.valueOf(_entry.getMeetupsEntryId()));
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return editPortletURL;
	}

	public String getUrlTitle() {
		return _entry.getTitle();
	}

	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect) {

		ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		return themeDisplay.getPortalURL() + themeDisplay.getPathMain()
				+ "/meetups/view_entry.jsp?redirect="
				+ HttpUtil.encodeURL(noSuchEntryRedirect) + "&meetupsEntryId="
				+ _entry.getMeetupsEntryId();
	}

	public long getUserId() {
		return _entry.getUserId();
	}

	public String getUuid() {
		String uuID = null;
		try {
			uuID = _entry.getUserUuid();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uuID;
	}

	/*
	 * public boolean hasEditPermission(PermissionChecker permissionChecker) {
	 * return BlogsEntryPermission.contains(permissionChecker, _entry,
	 * ActionKeys.UPDATE); }
	 */

	/*
	 * public boolean hasViewPermission(PermissionChecker permissionChecker) {
	 * return BlogsEntryPermission.contains(permissionChecker, _entry,
	 * ActionKeys.VIEW); }
	 */

	public boolean isPrintable() {
		return true;
	}

	public String render(RenderRequest renderRequest,
			RenderResponse renderResponse, String template) throws Exception {

		if (template.equals(TEMPLATE_FULL_CONTENT)) {
			renderRequest.setAttribute(WebKeys.MEETUPS_ENTRY, _entry);

			return "/meetups/asset/" + template + ".jsp";
		} else {
			return null;
		}
	}

	protected String getIconPath(ThemeDisplay themeDisplay) {
		return themeDisplay.getPathThemeImages() + "/common/date.png";
	}

	private MeetupsEntry _entry;

	@Override
	public long getGroupId() {
		// TODO Auto-generated method stub
		return _entry.getGroupId();
	}

}