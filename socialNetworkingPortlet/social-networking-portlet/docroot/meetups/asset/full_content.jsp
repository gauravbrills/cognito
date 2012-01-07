
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
<%@ page
	import="com.liferay.socialnetworking.meetups.cognito.util.CognitoEventsUtil"%>

<%
	MeetupsEntry entry = (MeetupsEntry) request
			.getAttribute(com.liferay.socialnetworking.meetups.util.WebKeys.MEETUPS_ENTRY);
	String thumbnailURL = null;
	if (entry.getThumbnailId() == 0) {
		thumbnailURL = themeDisplay.getPathThemeImages()
				+ "/common/date.png";
		;
	} else {
		thumbnailURL = themeDisplay.getPathImage()
				+ "/meetups?img_id="
				+ entry.getThumbnailId()
				+ "&t="
				+ ImageServletTokenUtil
						.getToken(entry.getThumbnailId());
	}
%>
<td valign="top" width="99%"><div>
		<img alt="<liferay-ui:message key="view-meetup" />"
			src="<%=thumbnailURL%>" />&nbsp;
		<%=entry.getDescription()%>
	</div> <br /> <c:if
		test="<%= permissionChecker.isCompanyAdmin(company.getCompanyId()) %>">

		<%
			PortletURL editMeetupsEntryURL = CognitoEventsUtil
						.getMeetupsPortletUrl(themeDisplay, request);

				editMeetupsEntryURL.setWindowState(WindowState.MAXIMIZED);

				editMeetupsEntryURL.setParameter("jspPage",
						"/meetups/edit_entry.jsp");
				editMeetupsEntryURL.setParameter("redirect", currentURL);
				editMeetupsEntryURL.setParameter("meetupsEntryId",
						String.valueOf(entry.getMeetupsEntryId()));
		%>
		<liferay-ui:icon image="edit"
			url="<%= editMeetupsEntryURL.toString() %>" method="get" />
	</c:if> <%
 	MeetupsRegistration meetupsRegistration = null;
 	PortletURL viewMeetupsEntryURL = CognitoEventsUtil
 			.getMeetupsPortletUrl(themeDisplay, request);

 	viewMeetupsEntryURL.setWindowState(WindowState.MAXIMIZED);

 	viewMeetupsEntryURL.setParameter("jspPage",
 			"/meetups/view_entry.jsp");
 	viewMeetupsEntryURL.setParameter("meetupsEntryId",
 			String.valueOf(entry.getMeetupsEntryId()));
 	try {
 		meetupsRegistration = MeetupsRegistrationLocalServiceUtil
 				.getMeetupsRegistration(PortalUtil.getUserId(request),
 						entry.getMeetupsEntryId());
 	} catch (NoSuchMeetupsRegistrationException nsmre) {

 	}
 	Boolean registered = false;
 	if (null != meetupsRegistration) {
 		if (meetupsRegistration.getStatus() != MeetupsConstants.STATUS_NO)
 			registered = true;
 	}
 %> <c:if test="<%=!registered %>">
		<liferay-ui:icon image="join" message="register"
			url="<%= viewMeetupsEntryURL.toString() %>" method="get" />
	</c:if> <c:if
		test="<%= permissionChecker.isCompanyAdmin(company.getCompanyId()) %>">
		<%
			PortletURL leaveMeetupsEntryURL = CognitoEventsUtil
						.getMeetupsPortletUrl(themeDisplay, request);
				leaveMeetupsEntryURL.setWindowState(WindowState.MAXIMIZED);

				leaveMeetupsEntryURL.setParameter(ActionRequest.ACTION_NAME,
						"updateMeetupsRegistration");
				leaveMeetupsEntryURL.setParameter("redirect", currentURL);
				leaveMeetupsEntryURL.setParameter("meetupsEntryId",
						String.valueOf(entry.getMeetupsEntryId()));
				leaveMeetupsEntryURL.setParameter("status",
						Integer.toString(MeetupsConstants.STATUS_NO));
		%>
		<c:if test="<%=registered %>">
			<liferay-ui:icon image="leave" message="cognito.event.leave"
				url="<%= leaveMeetupsEntryURL.toString() %>" method="get" />
		</c:if>
	</c:if></td>