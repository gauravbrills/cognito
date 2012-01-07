package com.liferay.socialnetworking.meetups.asset;

import javax.portlet.PortletURL;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.BaseAssetRendererFactory;
import com.liferay.socialnetworking.meetups.cognito.util.CognitoEventsUtil;
import com.liferay.socialnetworking.meetups.util.WebKeys;
import com.liferay.socialnetworking.model.MeetupsEntry;
import com.liferay.socialnetworking.service.MeetupsEntryLocalServiceUtil;

/**
 * @author Gaurav
 * 
 */
public class MeetupsEntryAssetRendererFactory extends BaseAssetRendererFactory {

	public static final String CLASS_NAME = MeetupsEntry.class.getName();

	public static final String TYPE = "meetups";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liferay.portlet.asset.model.AssetRendererFactory#getAssetRenderer
	 * (long, int)
	 */
	public AssetRenderer getAssetRenderer(long classPK, int type)
			throws PortalException, SystemException {

		MeetupsEntry entry = MeetupsEntryLocalServiceUtil
				.getMeetupsEntry(classPK);

		return new MeetupsEntryAssetRenderer(entry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liferay.portlet.asset.model.AssetRendererFactory#getClassName()
	 */
	public String getClassName() {
		return CLASS_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liferay.portlet.asset.model.AssetRendererFactory#getType()
	 */
	public String getType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liferay.portlet.asset.model.BaseAssetRendererFactory#getURLAdd(com
	 * .liferay.portal.kernel.portlet.LiferayPortletRequest,
	 * com.liferay.portal.kernel.portlet.LiferayPortletResponse)
	 */
	public PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		PortletURL portletURL = null;
		// Get url for add meetups entry
		try {
			portletURL = CognitoEventsUtil.getMeetupsPortletUrl(themeDisplay,
					liferayPortletRequest.getHttpServletRequest());
			portletURL.setParameter("jspPage", "/meetups/edit_entry.jsp");

		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return portletURL;
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