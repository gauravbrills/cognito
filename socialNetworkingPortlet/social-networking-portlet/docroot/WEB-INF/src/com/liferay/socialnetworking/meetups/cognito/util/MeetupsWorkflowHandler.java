package com.liferay.socialnetworking.meetups.cognito.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.socialnetworking.model.MeetupsEntry;
import com.liferay.socialnetworking.service.impl.MeetupsEntryLocalServiceImpl;

/**
 * @author Gaurav Rawat
 */
public class MeetupsWorkflowHandler extends BaseWorkflowHandler {

	public static final String CLASS_NAME = MeetupsEntry.class.getName();
	public static final String TYPE = "Cognito Event";

	public String getClassName() {
		return CLASS_NAME;
	}

	public String getType(Locale locale) {
		return TYPE;
	}

	public MeetupsEntry updateStatus(int status,
			Map<String, Serializable> workflowContext) throws PortalException,
			SystemException {

		long userId = GetterUtil.getLong((String) workflowContext
				.get(WorkflowConstants.CONTEXT_USER_ID));
		long classPK = GetterUtil.getLong((String) workflowContext
				.get(WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		ServiceContext serviceContext = (ServiceContext) workflowContext
				.get("serviceContext");
		MeetupsEntryLocalServiceImpl localServiceImpl = new MeetupsEntryLocalServiceImpl();
		return localServiceImpl.updateStatus(userId, classPK, status,
				serviceContext);
	}

	protected String getIconPath(ThemeDisplay themeDisplay) {
		return themeDisplay.getPathThemeImages() + "/common/pages.png";
	}

}