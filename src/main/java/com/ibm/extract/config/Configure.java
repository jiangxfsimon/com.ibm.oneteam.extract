package com.ibm.extract.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.ibm.extract.constant.AppInfo;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 初始化项目的配置信息
 */
@PropertySource(AppInfo.baseProperties)
@Component
public class Configure {
	@Value("${app.appName}")
	private String appName;
	@Value("${app.viewPrefix}")
	private String viewPrefix;
	@Value("${app.viewSuffix}")
	private String viewSuffix;
	@Value("${app.viewError}")
	private String viewError;
	@Value("${app.staticFilePath}")
	private String staticFilePath;
	@Value("${app.defaultEncoding}")
	private String defaultEncoding;
	@Value("${sqlite.driver}")
	private String sqliteDriver;
	@Value("${sqlite.url}")
	private String sqliteUrl;
	@Value("${sqlite.user}")
	private String sqliteUser;
	@Value("${sqlite.password}")
	private String sqlitePassword;
	@Value("${oneteam.db2.driver}")
	private String db2Driver;
	@Value("${oneteam.db2.url}")
	private String db2Url;
	@Value("${oneteam.db2.user}")
	private String db2User;
	@Value("${oneteam.db2.password}")
	private String db2Password;
	@Value("${upload.FilePath}")
	private String uploadFilePath;
	@Value("${upload.maxInMemorySize}")
	private int maxUploadInMemorySize;
	@Value("${upload.maxSizePerFile}")
	private int maxUploadSizePerFile;
	@Value("${upload.maxSize}")
	private int maxUploadSize;
	@Value("${bluepage.api}")
	private String bluePageApi;
	@Value("${bluepage.ldap}")
	private String bluePageLdap;
	@Value("${bluepage.referral}")
	private String bluepageReferral;
	@Value("${bluepage.initial}")
	private String bluePageInitial;
	@Value("${bluepage.url}")
	private String bluePageUrl;
	@Value("${bluepage.ldapBase}")
	private String bluepageLdapBase;
	@Value("${bluepage.ldap.value}")
	private String bluePageLdapValue;
	@Value("${bluepage.referral.value}")
	private String bluepageReferralValue;
	@Value("${bluepage.initial.value}")
	private String bluePageInitialValue;
	@Value("${bluepage.url.value}")
	private String bluePageUrlValue;
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getViewPrefix() {
		return viewPrefix;
	}
	public void setViewPrefix(String viewPrefix) {
		this.viewPrefix = viewPrefix;
	}
	public String getViewSuffix() {
		return viewSuffix;
	}
	public void setViewSuffix(String viewSuffix) {
		this.viewSuffix = viewSuffix;
	}
	public String getViewError() {
		return viewError;
	}
	public void setViewError(String viewError) {
		this.viewError = viewError;
	}
	public String getStaticFilePath() {
		return staticFilePath;
	}
	public void setStaticFilePath(String staticFilePath) {
		this.staticFilePath = staticFilePath;
	}
	public String getDefaultEncoding() {
		return defaultEncoding;
	}
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}
	public String getSqliteDriver() {
		return sqliteDriver;
	}
	public void setSqliteDriver(String sqliteDriver) {
		this.sqliteDriver = sqliteDriver;
	}
	public String getSqliteUrl() {
		return sqliteUrl;
	}
	public void setSqliteUrl(String sqliteUrl) {
		this.sqliteUrl = sqliteUrl;
	}
	public String getSqliteUser() {
		return sqliteUser;
	}
	public void setSqliteUser(String sqliteUser) {
		this.sqliteUser = sqliteUser;
	}
	public String getSqlitePassword() {
		return sqlitePassword;
	}
	public void setSqlitePassword(String sqlitePassword) {
		this.sqlitePassword = sqlitePassword;
	}
	public String getDb2Driver() {
		return db2Driver;
	}
	public void setDb2Driver(String db2Driver) {
		this.db2Driver = db2Driver;
	}
	public String getDb2Url() {
		return db2Url;
	}
	public void setDb2Url(String db2Url) {
		this.db2Url = db2Url;
	}
	public String getDb2User() {
		return db2User;
	}
	public void setDb2User(String db2User) {
		this.db2User = db2User;
	}
	public String getDb2Password() {
		return db2Password;
	}
	public void setDb2Password(String db2Password) {
		this.db2Password = db2Password;
	}
	public String getUploadFilePath() {
		return uploadFilePath;
	}
	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}
	public int getMaxUploadInMemorySize() {
		return maxUploadInMemorySize;
	}
	public void setMaxUploadInMemorySize(int maxUploadInMemorySize) {
		this.maxUploadInMemorySize = maxUploadInMemorySize;
	}
	public int getMaxUploadSizePerFile() {
		return maxUploadSizePerFile;
	}
	public void setMaxUploadSizePerFile(int maxUploadSizePerFile) {
		this.maxUploadSizePerFile = maxUploadSizePerFile;
	}
	public int getMaxUploadSize() {
		return maxUploadSize;
	}
	public void setMaxUploadSize(int maxUploadSize) {
		this.maxUploadSize = maxUploadSize;
	}
	public String getBluePageApi() {
		return bluePageApi;
	}
	public void setBluePageApi(String bluePageApi) {
		this.bluePageApi = bluePageApi;
	}
	public String getBluePageLdap() {
		return bluePageLdap;
	}
	public void setBluePageLdap(String bluePageLdap) {
		this.bluePageLdap = bluePageLdap;
	}
	public String getBluepageReferral() {
		return bluepageReferral;
	}
	public void setBluepageReferral(String bluepageReferral) {
		this.bluepageReferral = bluepageReferral;
	}
	public String getBluePageInitial() {
		return bluePageInitial;
	}
	public void setBluePageInitial(String bluePageInitial) {
		this.bluePageInitial = bluePageInitial;
	}
	public String getBluePageUrl() {
		return bluePageUrl;
	}
	public void setBluePageUrl(String bluePageUrl) {
		this.bluePageUrl = bluePageUrl;
	}
	public String getBluepageLdapBase() {
		return bluepageLdapBase;
	}
	public void setBluepageLdapBase(String bluepageLdapBase) {
		this.bluepageLdapBase = bluepageLdapBase;
	}
	public String getBluePageLdapValue() {
		return bluePageLdapValue;
	}
	public void setBluePageLdapValue(String bluePageLdapValue) {
		this.bluePageLdapValue = bluePageLdapValue;
	}
	public String getBluepageReferralValue() {
		return bluepageReferralValue;
	}
	public void setBluepageReferralValue(String bluepageReferralValue) {
		this.bluepageReferralValue = bluepageReferralValue;
	}
	public String getBluePageInitialValue() {
		return bluePageInitialValue;
	}
	public void setBluePageInitialValue(String bluePageInitialValue) {
		this.bluePageInitialValue = bluePageInitialValue;
	}
	public String getBluePageUrlValue() {
		return bluePageUrlValue;
	}
	public void setBluePageUrlValue(String bluePageUrlValue) {
		this.bluePageUrlValue = bluePageUrlValue;
	}
}
