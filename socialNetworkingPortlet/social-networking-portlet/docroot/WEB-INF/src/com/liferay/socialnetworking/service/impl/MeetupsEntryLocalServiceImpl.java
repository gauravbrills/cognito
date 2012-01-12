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

package com.liferay.socialnetworking.service.impl;

import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ImageLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.calendar.model.CalEvent;
import com.liferay.portlet.expando.DuplicateColumnNameException;
import com.liferay.portlet.expando.NoSuchTableException;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;
import com.liferay.socialnetworking.MeetupsEntryEndDateException;
import com.liferay.socialnetworking.MeetupsEntryStartDateException;
import com.liferay.socialnetworking.model.MeetupsEntry;
import com.liferay.socialnetworking.service.MeetupsEntryLocalServiceUtil;
import com.liferay.socialnetworking.service.base.MeetupsEntryLocalServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 */
public class MeetupsEntryLocalServiceImpl extends
		MeetupsEntryLocalServiceBaseImpl {

	public MeetupsEntry addMeetupsEntry(long userId, String title,
			String description, int startDateMonth, int startDateDay,
			int startDateYear, int startDateHour, int startDateMinute,
			int endDateMonth, int endDateDay, int endDateYear, int endDateHour,
			int endDateMinute, int totalAttendees, int maxAttendees,
			double price, byte[] thumbnail, ServiceContext serviceContext)
			throws PortalException, SystemException {

		User user = userLocalService.getUserById(userId);

		Date startDate = PortalUtil.getDate(startDateMonth, startDateDay,
				startDateYear, startDateHour, startDateMinute,
				user.getTimeZone(), new MeetupsEntryStartDateException());

		Date endDate = PortalUtil.getDate(endDateMonth, endDateDay,
				endDateYear, endDateHour, endDateMinute, user.getTimeZone(),
				new MeetupsEntryEndDateException());

		Date now = new Date();

		long meetupsEntryId = counterLocalService.increment();

		MeetupsEntry meetupsEntry = meetupsEntryPersistence
				.create(meetupsEntryId);

		meetupsEntry.setCompanyId(user.getCompanyId());
		meetupsEntry.setUserId(user.getUserId());
		meetupsEntry.setUserName(user.getFullName());
		meetupsEntry.setCreateDate(now);
		meetupsEntry.setModifiedDate(now);
		meetupsEntry.setTitle(title);
		meetupsEntry.setDescription(description);
		meetupsEntry.setStartDate(startDate);
		meetupsEntry.setEndDate(endDate);
		meetupsEntry.setTotalAttendees(totalAttendees);
		meetupsEntry.setMaxAttendees(maxAttendees);
		meetupsEntry.setPrice(price);
		meetupsEntry.setGroupId(serviceContext.getScopeGroupId());
		// Set status as draft for workflow
		meetupsEntry.setStatus(WorkflowConstants.STATUS_DRAFT);

		if ((thumbnail != null) && (thumbnail.length > 0)) {
			meetupsEntry.setThumbnailId(counterLocalService.increment());
		}

		// Calendar Event

		CalEvent event = calEventLocalService.addEvent(user.getUserId(), title,
				description, startDateMonth, startDateDay, startDateYear,
				startDateHour, startDateMinute, endDateMonth, endDateDay,
				endDateYear, endDateHour, endDateMinute, true, true,
				"Training", false, null, 0, 0, 0, serviceContext);

		// Expando

		// Set ExpandoAttributes
		ExpandoTable table = getExpandoTable(meetupsEntry);
		ExpandoColumn column = getOrAddExpandoColumn(table, "calEventID",
				ExpandoColumnConstants.LONG);
		// Add eventId to expando column calEventID
		ExpandoValueLocalServiceUtil.addValue(table.getClassNameId(),
				table.getTableId(), column.getColumnId(),
				meetupsEntry.getMeetupsEntryId(),
				Long.toString(event.getEventId()));

		meetupsEntryPersistence.update(meetupsEntry, false);

		if ((thumbnail != null) && (thumbnail.length > 0)) {
			ImageLocalServiceUtil.updateImage(meetupsEntry.getThumbnailId(),
					thumbnail);
		}

		// Asset

		updateAsset(userId, meetupsEntry, serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames());

		/*
		 * // Indexer
		 * 
		 * Indexer indexer = IndexerRegistryUtil.getIndexer(MeetupsEntry.class);
		 * 
		 * indexer.reindex(meetupsEntry);
		 */
		// Workflow

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
				meetupsEntry.getCompanyId(), meetupsEntry.getGroupId(), userId,
				MeetupsEntry.class.getName(), meetupsEntry.getPrimaryKey(),
				meetupsEntry, serviceContext);

		return meetupsEntry;
	}

	public void deleteMeetupsEntry(long meetupsEntryId) throws PortalException,
			SystemException {

		MeetupsEntry meetupsEntry = meetupsEntryPersistence
				.findByPrimaryKey(meetupsEntryId);

		meetupsRegistrationPersistence.removeByMeetupsEntryId(meetupsEntryId);

		ImageLocalServiceUtil.deleteImage(meetupsEntry.getThumbnailId());

		// Get expando details
		ExpandoTable table = getExpandoTable(MeetupsEntryLocalServiceUtil
				.getMeetupsEntry(meetupsEntryId));
		ExpandoColumn column = getOrAddExpandoColumn(table, "calEventID",
				ExpandoColumnConstants.LONG);
		// Get event id
		ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(
				table.getTableId(), column.getColumnId(), meetupsEntryId);
		// Delete Calendar Event
		if (null != value) {
			calEventLocalService.deleteEvent(Long.parseLong(value.getData()));
			// Delete Expando Data
			ExpandoValueLocalServiceUtil.deleteExpandoValue(value.getValueId());
		}

		meetupsEntryPersistence.remove(meetupsEntry);

		// Asset
		assetEntryLocalService.deleteEntry(MeetupsEntry.class.getName(),
				meetupsEntryId);

		// Indexer

		Indexer indexer = IndexerRegistryUtil.getIndexer(MeetupsEntry.class);

		indexer.delete(meetupsEntry);
	}

	public List<MeetupsEntry> getMeetupsEntriesByCompany(long companyId)
			throws SystemException {

		return meetupsEntryPersistence.findByCompanyId(companyId);
	}

	public List<MeetupsEntry> getMeetupsEntriesByUser(long userId)
			throws SystemException {

		return meetupsEntryPersistence.findByUserId(userId);
	}

	public MeetupsEntry updateMeetupsEntry(long userId, long meetupsEntryId,
			String title, String description, int startDateMonth,
			int startDateDay, int startDateYear, int startDateHour,
			int startDateMinute, int endDateMonth, int endDateDay,
			int endDateYear, int endDateHour, int endDateMinute,
			int totalAttendees, int maxAttendees, double price,
			byte[] thumbnail, ServiceContext serviceContext)
			throws PortalException, SystemException {

		User user = userLocalService.getUserById(userId);

		Date startDate = PortalUtil.getDate(startDateMonth, startDateDay,
				startDateYear, startDateHour, startDateMinute,
				user.getTimeZone(), new MeetupsEntryStartDateException());

		Date endDate = PortalUtil.getDate(endDateMonth, endDateDay,
				endDateYear, endDateHour, endDateMinute, user.getTimeZone(),
				new MeetupsEntryEndDateException());

		MeetupsEntry meetupsEntry = meetupsEntryPersistence
				.findByPrimaryKey(meetupsEntryId);

		meetupsEntry.setModifiedDate(new Date());
		meetupsEntry.setTitle(title);
		meetupsEntry.setDescription(description);
		meetupsEntry.setStartDate(startDate);
		meetupsEntry.setEndDate(endDate);
		meetupsEntry.setTotalAttendees(totalAttendees);
		meetupsEntry.setMaxAttendees(maxAttendees);
		meetupsEntry.setPrice(price);
		meetupsEntry.setGroupId(serviceContext.getScopeGroupId());
		if ((thumbnail != null) && (thumbnail.length > 0)
				&& (meetupsEntry.getThumbnailId() == 0)) {

			meetupsEntry.setThumbnailId(counterLocalService.increment());
		}

		// Expando
		// Get expando details
		ExpandoTable table = getExpandoTable(meetupsEntry);
		ExpandoColumn column = getOrAddExpandoColumn(table, "calEventID",
				ExpandoColumnConstants.LONG);
		// Get event id
		ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(
				table.getTableId(), column.getColumnId(), meetupsEntryId);
		// Calendar

		calEventLocalService.updateEvent(userId,
				Long.parseLong(value.getData()), meetupsEntry.getTitle(),
				meetupsEntry.getDescription(), startDateMonth, startDateDay,
				startDateYear, startDateHour, startDateMinute, endDateMonth,
				endDateDay, endDateYear, endDateHour, endDateMinute, true,
				true, "Training", false, null, 0, 0, 0, serviceContext);

		meetupsEntryPersistence.update(meetupsEntry, false);

		if ((thumbnail != null) && (thumbnail.length > 0)) {
			ImageLocalServiceUtil.updateImage(meetupsEntry.getThumbnailId(),
					thumbnail);
		}

		// Asset
		updateAsset(userId, meetupsEntry, serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames());

		// Indexer

		Indexer indexer = IndexerRegistryUtil.getIndexer(MeetupsEntry.class);

		indexer.reindex(meetupsEntry);

		return meetupsEntry;
	}

	public void updateAsset(long userId, MeetupsEntry meetupsEntry,
			long[] assetCategoryIds, String[] assetTagNames)
			throws PortalException, SystemException {

		assetEntryLocalService.updateEntry(userId, meetupsEntry.getGroupId(),
				MeetupsEntry.class.getName(), meetupsEntry.getMeetupsEntryId(),
				meetupsEntry.getUserUuid(), assetCategoryIds, assetTagNames,
				true, null, null, null, null, ContentTypes.TEXT_HTML,
				meetupsEntry.getTitle(), meetupsEntry.getDescription(), null,
				null, 0, 0, null, false);
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

	public MeetupsEntry updateStatus(long userId, long classPK, int status,
			ServiceContext serviceContext) throws SystemException,
			PortalException {
		User user = UserLocalServiceUtil.getUser(userId);
		MeetupsEntry entry = MeetupsEntryLocalServiceUtil
				.getMeetupsEntry(classPK);
		entry.setStatus(status);
		entry.setStatusByUserId(userId);
		entry.setStatusByUserName(user.getFullName());
		entry.setStatusDate(serviceContext.getModifiedDate());

		MeetupsEntryLocalServiceUtil.updateMeetupsEntry(entry, false);

		if (status != WorkflowConstants.STATUS_APPROVED) {
			AssetEntryLocalServiceUtil.updateVisible(MeetupsEntry.class.getName(),
					classPK, true);
		} else
			AssetEntryLocalServiceUtil.updateVisible(MeetupsEntry.class.getName(),
					classPK, true);
		// Indexer

		Indexer indexer = IndexerRegistryUtil.getIndexer(MeetupsEntry.class);

		indexer.reindex(entry);

		return entry;
	}

}