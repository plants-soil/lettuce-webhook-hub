package com.plantssoil.webhook.core;

/**
 * The organization interface, publisher or subscriber are all belong to
 * IOrganization
 * 
 * @author danialdy
 * @Date 13 Nov 2024 1:43:39 pm
 */
public interface IOrganization {
    /**
     * The organization identity
     * 
     * @return organization id
     */
    public String getOrganizationId();

    /**
     * The organization name
     * 
     * @return organization name
     */
    public String getOrganizationName();

    /**
     * The website of the organization
     * 
     * @return organization website
     */
    public String getWebsite();

    /**
     * The logo URL link of the organization
     * 
     * @return logo URL link
     */
    public String getLogoLink();

    /**
     * The secret key which is used to be Token or generate signature<br/>
     * <p>
     * This key will be as the token which is added in callback request
     * ({@link IOrganization#getWebhookUrl()}) http header, if subscriber choose
     * {@link SecurityStrategy#TOKEN} as the security strategy
     * ({@link IOrganization#getSecurityStrategy()})
     * </p>
     * <p>
     * The key is used to generate signature, the signature will be added in
     * callback request ({@link IOrganization#getWebhookUrl()}) http header, if
     * subscriber choose {@link SecurityStrategy#SIGNATURE} as the security strategy
     * ({@link IOrganization#getSecurityStrategy()})
     * </p>
     * 
     * @return secret key
     */
    public String getSecretKey();

    /**
     * The trusted IP list, if the URL domain is not one of the trusted, will fail
     * to access
     * 
     * @return trusted IP list
     */
    public String[] getTrustedIps();
}
