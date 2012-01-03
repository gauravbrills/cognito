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

package com.liferay.socialnetworking.meetups.portlet;

import java.io.File;
import java.util.Calendar;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.calendar.model.CalEvent;
import com.liferay.portlet.calendar.service.CalEventLocalServiceUtil;
import com.liferay.portlet.expando.DuplicateColumnNameException;
import com.liferay.portlet.expando.NoSuchTableException;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;
import com.liferay.socialnetworking.model.MeetupsEntry;
import com.liferay.socialnetworking.service.MeetupsEntryLocalServiceUtil;
import com.liferay.socialnetworking.service.MeetupsRegistrationLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * @author Brian Wing Shun Chan
 */
/**
 * @author Gaurav modified to suit cognito Needs
 */
public class MeetupsPortlet extends MVCPortlet {

	public void deleteMeetupsEntry(ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker = themeDisplay
				.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin()) {
			return;
		}

		long meetupsEntryId = ParamUtil
				.getLong(actionRequest, "meetupsEntryId");
		// Get expando details
		ExpandoTable table = getExpandoTable(MeetupsEntryLocalServiceUtil
				.getMeetupsEntry(meetupsEntryId));
		ExpandoColumn column = getOrAddExpandoColumn(table, "calEventID",
				ExpandoColumnConstants.LONG);
		// Get event id
		ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(
				table.getTableId(), column.getColumnId(), meetupsEntryId);
		// Delete Calendar Event
		CalEventLocalServiceUtil.deleteEvent(Long.parseLong(value.getData()));
		// Delete Expando Data
		ExpandoValueLocalServiceUtil.deleteExpandoValue(value.getValueId());
		// Delete Meetups Entry
		MeetupsEntryLocalServiceUtil.deleteMeetupsEntry(meetupsEntryId);
	}

	public void updateMeetupsEntry(ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		UploadPortletRequest uploadRequest = PortalUtil
				.getUploadPortletRequest(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay) uploadRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker = themeDisplay
				.getPermissionChecker();

		// get selected tags
		String[] assetTagNames = null;
		String assetTagCSV = ParamUtil
				.getString(uploadRequest, "assetTagNames");
		assetTagNames = assetTagCSV != null ? assetTagCSV.split(",") : null;
		if (!permissionChecker.isCompanyAdmin()) {
			return;
		}

		long meetupsEntryId = ParamUtil
				.getLong(uploadRequest, "meetupsEntryId");

		String title = ParamUtil.getString(uploadRequest, "title");
		String description = ParamUtil.getString(uploadRequest, "description");

		int startDateMonth = ParamUtil.getInteger(uploadRequest,
				"startDateMonth");
		int startDateDay = ParamUtil.getInteger(uploadRequest, "startDateDay");
		int startDateYear = ParamUtil
				.getInteger(uploadRequest, "startDateYear");
		int startDateHour = ParamUtil
				.getInteger(uploadRequest, "startDateHour");
		int startDateMinute = ParamUtil.getInteger(uploadRequest,
				"startDateMinute");
		int startDateAmPm = ParamUtil
				.getInteger(uploadRequest, "startDateAmPm");

		if (startDateAmPm == Calendar.PM) {
			startDateHour += 12;
		}

		int endDateMonth = ParamUtil.getInteger(uploadRequest, "endDateMonth");
		int endDateDay = ParamUtil.getInteger(uploadRequest, "endDateDay");
		int endDateYear = ParamUtil.getInteger(uploadRequest, "endDateYear");
		int endDateHour = ParamUtil.getInteger(uploadRequest, "endDateHour");
		int endDateMinute = ParamUtil
				.getInteger(uploadRequest, "endDateMinute");
		int endDateAmPm = ParamUtil.getInteger(uploadRequest, "endDateAmPm");

		if (endDateAmPm == Calendar.PM) {
			endDateHour += 12;
		}

		int totalAttendees = ParamUtil.getInteger(uploadRequest,
				"totalAttendees");
		int maxAttendees = ParamUtil.getInteger(uploadRequest, "maxAttendees");
		double price = ParamUtil.getDouble(uploadRequest, "price");

		File file = uploadRequest.getFile("fileName");
		byte[] bytes = FileUtil.getBytes(file);

		if (meetupsEntryId <= 0) {
			// Add to Calendar
			CalEvent event = CalEventLocalServiceUtil.addEvent(
					themeDisplay.getUserId(), title, description,
					startDateMonth, startDateDay, startDateYear, startDateHour,
					startDateMinute, endDateMonth, endDateDay, endDateYear,
					endDateHour, endDateMinute, true, true, "Training", false,
					null, 0, 0, 0,
					ServiceContextFactory.getInstance(uploadRequest));
			// Add Meetups Entry
			MeetupsEntry entry = MeetupsEntryLocalServiceUtil.addMeetupsEntry(
					themeDisplay.getUserId(), title, description,
					startDateMonth, startDateDay, startDateYear, startDateHour,
					startDateMinute, endDateMonth, endDateDay, endDateYear,
					endDateHour, endDateMinute, totalAttendees, maxAttendees,
					price, bytes);
			// Add Tags if they exist
			if (null != assetTagCSV && !assetTagCSV.equals(""))
				AssetEntryLocalServiceUtil.updateEntry(
						themeDisplay.getUserId(), 0,
						MeetupsEntry.class.getName(), entry.getPrimaryKey(),
						entry.getUserUuid(), null, assetTagNames, true, null,
						null, entry.getStartDate(), null,
						ContentTypes.TEXT_HTML, entry.getTitle(), null,
						"meetupTags", null, 0, 0, null, false);
			// Set ExpandoAttributes
			ExpandoTable table = getExpandoTable(entry);
			ExpandoColumn column = getOrAddExpandoColumn(table, "calEventID",
					ExpandoColumnConstants.LONG);
			// Add eventId to expando column calEventID
			ExpandoValueLocalServiceUtil.addValue(table.getClassNameId(),
					table.getTableId(), column.getColumnId(),
					entry.getMeetupsEntryId(),
					Long.toString(event.getEventId()));
		} else {
			MeetupsEntry entry = MeetupsEntryLocalServiceUtil
					.updateMeetupsEntry(themeDisplay.getUserId(),
							meetupsEntryId, title, description, startDateMonth,
							startDateDay, startDateYear, startDateHour,
							startDateMinute, endDateMonth, endDateDay,
							endDateYear, endDateHour, endDateMinute,
							totalAttendees, maxAttendees, price, bytes);
			// Add Tags if they exist
			if (null != assetTagCSV && !assetTagCSV.equals(""))
				AssetEntryLocalServiceUtil.updateEntry(
						themeDisplay.getUserId(), 0,
						MeetupsEntry.class.getName(), entry.getPrimaryKey(),
						entry.getUserUuid(), null, assetTagNames, true, null,
						null, entry.getStartDate(), null,
						ContentTypes.TEXT_HTML, entry.getTitle(), null,
						"meetupTags", null, 0, 0, null, false);
			// Get expando details
			ExpandoTable table = getExpandoTable(entry);
			ExpandoColumn column = getOrAddExpandoColumn(table, "calEventID",
					ExpandoColumnConstants.LONG);
			// Get event id
			ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(
					table.getTableId(), column.getColumnId(), meetupsEntryId);

			CalEventLocalServiceUtil.updateEvent(themeDisplay.getUserId(),
					Long.parseLong(value.getData()), entry.getTitle(),
					entry.getDescription(), startDateMonth, startDateDay,
					startDateYear, startDateHour, startDateMinute,
					endDateMonth, endDateDay, endDateYear, endDateHour,
					endDateMinute, true, true, "Training", false, null, 0, 0,
					0, ServiceContextFactory.getInstance(uploadRequest));

		}
	}

	/**
	 * @param table
	 * @param columnName
	 * @param colType
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private ExpandoColumn getOrAddExpandoColumn(ExpandoTable table,
			String columnName, int colType) throws PortalException,
			SystemException {
		ExpandoColumn column = null;
		try {
			column = ExpandoColumnLocalServiceUtil.addColumn(
					table.getTableId(), columnName, colType);
		} catch (DuplicateColumnNameException dcne) {
			column = ExpandoColumnLocalServiceUtil.getColumn(
					table.getTableId(), columnName);
		}
		return column;

	}

	/**
	 * @param entry
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private ExpandoTable getExpandoTable(MeetupsEntry entry)
			throws PortalException, SystemException {
		ExpandoTable table = null;
		try {
			table = ExpandoTableLocalServiceUtil.getDefaultTable(
					entry.getCompanyId(), MeetupsEntry.class.getName());
		} catch (NoSuchTableException nste) {
			table = ExpandoTableLocalServiceUtil.addDefaultTable(
					entry.getCompanyId(), MeetupsEntry.class.getName());
		}
		return table;
	}

	public void updateMeetupsRegistration(ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		long meetupsEntryId = ParamUtil
				.getLong(actionRequest, "meetupsEntryId");
		int status = ParamUtil.getInteger(actionRequest, "status");
		String comments = ParamUtil.getString(actionRequest, "comments");

		MeetupsRegistrationLocalServiceUtil.updateMeetupsRegistration(
				themeDisplay.getUserId(), meetupsEntryId, status, comments);
	}

}