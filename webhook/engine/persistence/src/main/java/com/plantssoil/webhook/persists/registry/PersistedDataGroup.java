package com.plantssoil.webhook.persists.registry;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.webhook.beans.DataGroup;
import com.plantssoil.webhook.core.IDataGroup;

/**
 * The data group implementation with persistence<br/>
 * Used by {@link PersistedRegistry}<br/>
 * 
 * @author danialdy
 *
 */
public class PersistedDataGroup implements IDataGroup {
	private DataGroup dataGroupBean;

	/**
	 * Default Constructor
	 */
	public PersistedDataGroup() {
	}

	/**
	 * Constructor base on data group bean, used for query result
	 * 
	 * @param dataGroupBean the data group bean
	 */
	public PersistedDataGroup(DataGroup dataGroupBean) {
		this.dataGroupBean = dataGroupBean;
	}

	/**
	 * Get data group bean
	 * 
	 * @return data group bean
	 */
	DataGroup getDataGroupBean() {
		if (this.dataGroupBean == null) {
			this.dataGroupBean = new DataGroup();
			this.dataGroupBean.setDataGroupId(EntityUtils.getInstance().createUniqueObjectId());
		}
		return this.dataGroupBean;
	}

	/**
	 * Set publisher id which data group belongs to
	 * 
	 * @param publisherId publisher id
	 */
	public void setPublisherId(String publisherId) {
		getDataGroupBean().setPublisherId(publisherId);
	}

	/**
	 * Get publisher id which data group belongs to
	 * 
	 * @return publisher id
	 */
	public String getPublisherId() {
		return getDataGroupBean().getPublisherId();
	}

	@Override
	public void setDataGroup(String dataGroup) {
		getDataGroupBean().setDataGroup(dataGroup);
	}

	@Override
	public void setAccessToken(String accessToken) {
		getDataGroupBean().setAccessToken(accessToken);
	}

	@Override
	public void setRefreshToken(String refreshToken) {
		getDataGroupBean().setRefreshToken(refreshToken);
	}

	@Override
	public String getDataGroup() {
		return getDataGroupBean().getDataGroup();
	}

	@Override
	public String getAccessToken() {
		return getDataGroupBean().getAccessToken();
	}

	@Override
	public String getRefreshToken() {
		return getDataGroupBean().getRefreshToken();
	}
}
