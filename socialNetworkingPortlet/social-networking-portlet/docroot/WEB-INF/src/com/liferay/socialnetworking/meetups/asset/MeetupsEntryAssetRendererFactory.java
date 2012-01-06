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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.BaseAssetRendererFactory;
import com.liferay.socialnetworking.meetups.util.WebKeys;
import com.liferay.socialnetworking.model.MeetupsEntry;
import com.liferay.socialnetworking.service.MeetupsEntryLocalServiceUtil;

/**
 * @author Jorge Ferrer
 * @author Juan Fernández
 * @author Raymond Augé
 */
public class MeetupsEntryAssetRendererFactory extends BaseAssetRendererFactory {

	public static final String CLASS_NAME = MeetupsEntry.class.getName();

	public static final String TYPE = "meetups";

	public AssetRenderer getAssetRenderer(long classPK, int type)
			throws PortalException, SystemException {

		MeetupsEntry entry = MeetupsEntryLocalServiceUtil
				.getMeetupsEntry(classPK);

		return new MeetupsEntryAssetRenderer(entry);
	}

	public String getClassName() {
		return CLASS_NAME;
	}

	public String getType() {
		return TYPE;
	}

	public PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		PortletURL addAssetURL = null;
		/*
		 * if (BlogsPermission.contains(themeDisplay.getPermissionChecker(),
		 * themeDisplay.getScopeGroupId(), ActionKeys.ADD_ENTRY)) {
		 */
		addAssetURL = liferayPortletResponse.createRenderURL();
		// addMeetupsEntryURL.setWindowState(WindowState.MAXIMIZED);

		addAssetURL.setParameter("jspPage", "/meetups/edit_entry.jsp");
		//addMeetupsEntryURL.setParameter("redirect", currentURL);
		addAssetURL.setParameter("struts_action", "/meetups/edit_entry");
		// }
		return addAssetURL;
	}

	/*
	 * public boolean hasPermission(PermissionChecker permissionChecker, long
	 * classPK, String actionId) throws Exception {
	 * 
	 * return MeetupsEntryBlogsEntryPermission.contains(permissionChecker,
	 * classPK, actionId); }
	 */
	protected String getIconPath(ThemeDisplay themeDisplay) {
		return themeDisplay.getPathThemeImages() + "/common/date.png";
	}

}