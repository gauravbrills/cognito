
<%
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
%>

<%@ include file="/init.jsp"%>
<%@ page import="com.liferay.portal.kernel.workflow.WorkflowConstants"%>
<%
	String tabs1 = ParamUtil.getString(request, "tabs1", "all-meetups");

	List<MeetupsEntry> meetupsEntries = null;

	if (tabs1.equals("all-meetups")) {
		meetupsEntries = MeetupsEntryLocalServiceUtil
				.getMeetupsEntriesByCompany(themeDisplay.getCompanyId());
	} else if (tabs1.equals("my-meetups")) {
		meetupsEntries = MeetupsEntryLocalServiceUtil
				.getMeetupsEntriesByUser(PortalUtil.getUserId(request));
	}
%>

<c:if
	test="<%= permissionChecker.isCompanyAdmin(company.getCompanyId()) %>">

	<%
		PortletURL portletURL = renderResponse.createRenderURL();
	%>

	<liferay-ui:tabs names="all-meetups,my-meetups" param="tabs1"
		url="<%= portletURL.toString() %>" />

	<%
		PortletURL addMeetupsEntryURL = renderResponse
					.createRenderURL();

			addMeetupsEntryURL.setWindowState(WindowState.MAXIMIZED);

			addMeetupsEntryURL.setParameter("jspPage",
					"/meetups/edit_entry.jsp");
			addMeetupsEntryURL.setParameter("redirect", currentURL);
	%>

	<input type="button" value='<liferay-ui:message key="add-meetup" />'
		onClick='location.href = "<%=addMeetupsEntryURL.toString()%>"' />

	<br />
	<br />
</c:if>

<table class="lfr-table" width="100%">

	<%
		for (int i = 0; i < meetupsEntries.size(); i++) {
			MeetupsEntry meetupsEntry = meetupsEntries.get(i);
			Boolean isVisible = false;
			if (meetupsEntry.getStatus() == WorkflowConstants.STATUS_APPROVED)
				isVisible = true;

			int yesTotal = MeetupsRegistrationLocalServiceUtil
					.getMeetupsRegistrationsCount(
							meetupsEntry.getMeetupsEntryId(),
							MeetupsConstants.STATUS_YES);
	%>
	<c:if test="<%=isVisible%>">
		<tr>
			<td align="center" valign="top">
				<%
					PortletURL viewMeetupsEntryURL = renderResponse
									.createRenderURL();

							viewMeetupsEntryURL.setWindowState(WindowState.MAXIMIZED);

							viewMeetupsEntryURL.setParameter("jspPage",
									"/meetups/view_entry.jsp");
							viewMeetupsEntryURL.setParameter("meetupsEntryId",
									String.valueOf(meetupsEntry.getMeetupsEntryId()));

							String thumbnailURL = null;

							if (meetupsEntry.getThumbnailId() == 0) {
								thumbnailURL = request.getContextPath()
										+ "/meetups/images/calendar.png";
							} else {
								thumbnailURL = themeDisplay.getPathImage()
										+ "/meetups?img_id="
										+ meetupsEntry.getThumbnailId()
										+ "&t="
										+ ImageServletTokenUtil.getToken(meetupsEntry
												.getThumbnailId());
							}
				%> <a href="<%=viewMeetupsEntryURL%>"><img
					alt="<liferay-ui:message key="view-meetup" />"
					src="<%=thumbnailURL%>" /></a>
			</td>
			<td valign="top" width="99%">
				<div>
					<%=meetupsEntry.getTitle()%>
				</div> <br />

				<div>
					<%=meetupsEntry.getDescription()%>
				</div> <br />

				<div>
					<label>Tags</label>
					<liferay-ui:asset-tags-summary
						className="<%= MeetupsEntry.class.getName() %>"
						classPK="<%= meetupsEntry.getPrimaryKey() %>" />
				</div> <br /> <c:if test="<%= yesTotal > 1 %>">
					<div>
						<%=LanguageUtil.format(pageContext,
								"x-people-are-planning-to-attend-this-meetup",
								String.valueOf(yesTotal))%>
					</div>

					<br />
				</c:if> <liferay-ui:icon-list>

					<c:if
						test="<%= permissionChecker.isCompanyAdmin(company.getCompanyId()) %>">

						<%
							PortletURL editMeetupsEntryURL = renderResponse
													.createRenderURL();

											editMeetupsEntryURL
													.setWindowState(WindowState.MAXIMIZED);

											editMeetupsEntryURL.setParameter("jspPage",
													"/meetups/edit_entry.jsp");
											editMeetupsEntryURL.setParameter("redirect",
													currentURL);
											editMeetupsEntryURL.setParameter("meetupsEntryId",
													String.valueOf(meetupsEntry
															.getMeetupsEntryId()));
						%>

						<liferay-ui:icon image="edit"
							url="<%= editMeetupsEntryURL.toString() %>" method="get" />
					</c:if>
					<%
						MeetupsRegistration meetupsRegistration = null;
									try {
										meetupsRegistration = MeetupsRegistrationLocalServiceUtil
												.getMeetupsRegistration(
														PortalUtil.getUserId(request),
														meetupsEntry.getMeetupsEntryId());
									} catch (NoSuchMeetupsRegistrationException nsmre) {

									}
									Boolean registered = false;
									if (null != meetupsRegistration) {
										if (meetupsRegistration.getStatus() != MeetupsConstants.STATUS_NO)
											registered = true;
									}
					%>
					<c:if test="<%=!registered %>">
						<liferay-ui:icon image="join" message="register"
							url="<%= viewMeetupsEntryURL.toString() %>" method="get" />
					</c:if>
					<c:if
						test="<%= permissionChecker.isCompanyAdmin(company.getCompanyId()) %>">
						<%
							PortletURL leaveMeetupsEntryURL = renderResponse
													.createActionURL();

											leaveMeetupsEntryURL
													.setWindowState(WindowState.MAXIMIZED);

											leaveMeetupsEntryURL.setParameter(
													ActionRequest.ACTION_NAME,
													"updateMeetupsRegistration");
											leaveMeetupsEntryURL.setParameter("redirect",
													currentURL);
											leaveMeetupsEntryURL.setParameter("meetupsEntryId",
													String.valueOf(meetupsEntry
															.getMeetupsEntryId()));
											leaveMeetupsEntryURL.setParameter("status", Integer
													.toString(MeetupsConstants.STATUS_NO));
						%>
						<c:if test="<%=registered %>">
							<liferay-ui:icon image="leave" message="cognito.event.leave"
								url="<%= leaveMeetupsEntryURL.toString() %>" method="get" />
						</c:if>
						<%
							PortletURL deleteMeetupsEntryURL = renderResponse
													.createActionURL();

											deleteMeetupsEntryURL
													.setWindowState(WindowState.MAXIMIZED);

											deleteMeetupsEntryURL.setParameter(
													ActionRequest.ACTION_NAME,
													"deleteMeetupsEntry");
											deleteMeetupsEntryURL.setParameter("redirect",
													currentURL);
											deleteMeetupsEntryURL.setParameter(
													"meetupsEntryId", String
															.valueOf(meetupsEntry
																	.getMeetupsEntryId()));
						%>

						<liferay-ui:icon-delete
							url="<%= deleteMeetupsEntryURL.toString() %>" />
					</c:if>
				</liferay-ui:icon-list>
			</td>
		</tr>
	</c:if>
	<c:if test="<%= (i + 1) < meetupsEntries.size() %>">
		<tr>
			<td colspan="2">
				<div class="separator">
					<!-- -->
				</div>
			</td>
		</tr>
	</c:if>

	<%
		}
	%>

</table>