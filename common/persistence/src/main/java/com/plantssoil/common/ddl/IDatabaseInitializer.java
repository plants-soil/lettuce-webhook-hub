package com.plantssoil.common.ddl;

import com.plantssoil.common.config.IConfigurable;

/**
 * RDBMS Initializer<br/>
 * Will execute all DDL to create tables, indexes, constraints, etc.<br/>
 * Will initialize all basic data which used by system functions.<br/>
 * Will automatic to record the latest version<br/>
 * 
 * @author danialdy
 *
 */
public interface IDatabaseInitializer extends IConfigurable {
	/**
	 * Do the initialization<br/>
	 * In order to improve the initialization speed, will automatic record the
	 * latest version. If the version in DB >= the version parameter, the
	 * initialization will be skipped
	 * 
	 * @param version current version (format: major.minor.building, e.g: 1.1.0)
	 */
	public void initialize(String version);
}
