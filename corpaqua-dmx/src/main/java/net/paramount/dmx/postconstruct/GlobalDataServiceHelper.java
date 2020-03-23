/**
 * 
 */
package net.paramount.dmx.postconstruct;

import javax.inject.Inject;

import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import net.paramount.auth.domain.BaseACL;
import net.paramount.auth.entity.AccessDecisionPolicy;
import net.paramount.auth.entity.Authority;
import net.paramount.auth.entity.UserAccount;
import net.paramount.auth.service.AccessDecisionPolicyService;
import net.paramount.auth.service.AuthorityService;
import net.paramount.auth.service.UserAccountService;
import net.paramount.common.CommonUtility;
import net.paramount.css.service.config.ConfigurationService;
import net.paramount.css.service.config.LanguageService;
import net.paramount.dmx.globe.DmxConstants;
import net.paramount.domain.entity.general.Language;
import net.paramount.entity.config.Configuration;
import net.paramount.framework.component.ComponentBase;
import net.paramount.global.GlobalConstants;
import net.paramount.lingual.helper.LingualHelper;

/**
 * @author ducbq
 *
 */
@Component
public class GlobalDataServiceHelper extends ComponentBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3228342136333869857L;

	@Inject
  private Environment environment;

	@Inject
	private LanguageService languageService;

	@Inject
	private LingualHelper lingualHelper;

	@Inject
	private ConfigurationService configurationService;

	@Inject
	private AuthorityService authorityService;

	@Inject
	private AccessDecisionPolicyService accessDecisionPolicyService;

	@Inject 
	private PasswordEncoder passwordEncoder;

	@Inject
	private UserAccountService userAccountService;

	public void initialize() {
		initLanguages();
		initCountries();
	}

	public void initiateGlobalData() {
		initiateApplicatonProfile();
		setupMasterAuthorizations();
	}

	private void setupMasterAuthorizations() {
		setupMasterAuthorities();
		initAccessDecisionPolicies();
		setupMasterUsers();
	}

	private void initAccessDecisionPolicies() {
		String propAccessPattern = "accessPattern";
		
		if (!accessDecisionPolicyService.exists(propAccessPattern, BaseACL.ADMINISTRATOR.getAntMatcher())) {
			accessDecisionPolicyService.saveOrUpdate(AccessDecisionPolicy.builder().accessPattern(BaseACL.ADMINISTRATOR.getAntMatcher()).authority(authorityService.getByName(BaseACL.ADMINISTRATOR.getAuthority())).build());
		}

		if (!accessDecisionPolicyService.exists(propAccessPattern, BaseACL.COORDINATOR.getAntMatcher())) {
			accessDecisionPolicyService.saveOrUpdate(AccessDecisionPolicy.builder().accessPattern(BaseACL.COORDINATOR.getAntMatcher()).authority(authorityService.getByName(BaseACL.COORDINATOR.getAuthority())).build());
		}

		if (!accessDecisionPolicyService.exists(propAccessPattern, BaseACL.CRSX.getAntMatcher())) {
			accessDecisionPolicyService.saveOrUpdate(AccessDecisionPolicy.builder().accessPattern(BaseACL.CRSX.getAntMatcher()).authority(authorityService.getByName(BaseACL.CRSX.getAuthority())).build());
		}

		if (!accessDecisionPolicyService.exists(propAccessPattern, BaseACL.MANAGER.getAntMatcher())) {
			accessDecisionPolicyService.saveOrUpdate(AccessDecisionPolicy.builder().accessPattern(BaseACL.MANAGER.getAntMatcher()).authority(authorityService.getByName(BaseACL.MANAGER.getAuthority())).build());
		}

		if (!accessDecisionPolicyService.exists(propAccessPattern, BaseACL.OSX.getAntMatcher())){
			accessDecisionPolicyService.saveOrUpdate(AccessDecisionPolicy.builder().accessPattern(BaseACL.OSX.getAntMatcher()).authority(authorityService.getByName(BaseACL.OSX.getAuthority())).build());
		}
	
		if (!accessDecisionPolicyService.exists(propAccessPattern, BaseACL.SUBSCRIBER.getAntMatcher())) {
			accessDecisionPolicyService.saveOrUpdate(AccessDecisionPolicy.builder().accessPattern(BaseACL.SUBSCRIBER.getAntMatcher()).authority(authorityService.getByName(BaseACL.SUBSCRIBER.getAuthority())).build());
		}
	}

	private void setupMasterAuthorities() {
		String propName = "name";
		//Setup master data for authorities
		if (!authorityService.exists(propName, BaseACL.ADMINISTRATOR.getAuthority())) {
			authorityService.saveOrUpdate(Authority.builder().name(BaseACL.ADMINISTRATOR.getAuthority()).displayName(BaseACL.ADMINISTRATOR.getAuthority()).build());
		}

		if (!authorityService.exists(propName, BaseACL.COORDINATOR.getAuthority())) {
			authorityService.saveOrUpdate(Authority.builder().name(BaseACL.COORDINATOR.getAuthority()).displayName(BaseACL.COORDINATOR.getAuthority()).build());
		}

		if (!authorityService.exists(propName, BaseACL.CRSX.getAuthority())){
			authorityService.saveOrUpdate(Authority.builder().name(BaseACL.CRSX.getAuthority()).displayName(BaseACL.CRSX.getAuthority()).build());
		}

		if (!authorityService.exists(propName, BaseACL.MANAGER.getAuthority())){
			authorityService.saveOrUpdate(Authority.builder().name(BaseACL.MANAGER.getAuthority()).displayName(BaseACL.MANAGER.getAuthority()).build());
		}

		if (!authorityService.exists(propName, BaseACL.OSX.getAuthority())){
			authorityService.saveOrUpdate(Authority.builder().name(BaseACL.OSX.getAuthority()).displayName(BaseACL.OSX.getAuthority()).build());
		}

		if (!authorityService.exists(propName, BaseACL.SUBSCRIBER.getAuthority())) {
			authorityService.saveOrUpdate(Authority.builder().name(BaseACL.SUBSCRIBER.getAuthority()).displayName(BaseACL.SUBSCRIBER.getAuthority()).build());
		}
	}

	private void setupMasterUsers() {
		String propSsoId = "ssoId";
		if (!this.userAccountService.exists(propSsoId, BaseACL.ADMINISTRATOR.getUser())) {
			this.userAccountService.saveOrUpdate(
		  		UserAccount.getInsance(
		  				BaseACL.ADMINISTRATOR.getFirstName(), 
		  				BaseACL.ADMINISTRATOR.getLastName(), 
		  				BaseACL.ADMINISTRATOR.getUser(), 
		  				passwordEncoder.encode(BaseACL.ADMINISTRATOR.getUser()), 
		  				BaseACL.ADMINISTRATOR.getEmail(), 
		  				new Authority[] {authorityService.getByName(BaseACL.ADMINISTRATOR.getAuthority())}));
		}

		if (!this.userAccountService.exists(propSsoId, BaseACL.MANAGER.getUser())) {
			this.userAccountService.saveOrUpdate(
		  		UserAccount.getInsance(
		  				BaseACL.MANAGER.getFirstName(), 
		  				BaseACL.MANAGER.getLastName(), 
		  				BaseACL.MANAGER.getUser(), 
		  				passwordEncoder.encode(BaseACL.MANAGER.getUser()), 
		  				BaseACL.MANAGER.getEmail(), 
		  				new Authority[] {authorityService.getByName(BaseACL.MANAGER.getAuthority())}));
		}

		if (!this.userAccountService.exists(propSsoId, BaseACL.COORDINATOR.getUser())) {
			this.userAccountService.saveOrUpdate(
		  		UserAccount.getInsance(
		  				BaseACL.COORDINATOR.getFirstName(), 
		  				BaseACL.COORDINATOR.getLastName(), 
		  				BaseACL.COORDINATOR.getUser(), 
		  				passwordEncoder.encode(BaseACL.COORDINATOR.getUser()), 
		  				BaseACL.COORDINATOR.getEmail(), 
		  				new Authority[] {authorityService.getByName(BaseACL.COORDINATOR.getAuthority())}));
		}

		if (!this.userAccountService.exists(propSsoId, BaseACL.SUBSCRIBER.getUser())) {
			this.userAccountService.saveOrUpdate(
		  		UserAccount.getInsance(
		  				BaseACL.SUBSCRIBER.getFirstName(), 
		  				BaseACL.SUBSCRIBER.getLastName(), 
		  				BaseACL.SUBSCRIBER.getUser(), 
		  				passwordEncoder.encode(BaseACL.SUBSCRIBER.getUser()), 
		  				BaseACL.SUBSCRIBER.getEmail(), 
		  				new Authority[] {authorityService.getByName(BaseACL.SUBSCRIBER.getAuthority())}));
		}

		if (!this.userAccountService.exists(propSsoId, BaseACL.CRSX.getUser())) {
			this.userAccountService.saveOrUpdate(
		  		UserAccount.getInsance(
		  				BaseACL.CRSX.getFirstName(), 
		  				BaseACL.CRSX.getLastName(), 
		  				BaseACL.CRSX.getUser(), 
		  				passwordEncoder.encode(BaseACL.CRSX.getUser()), 
		  				BaseACL.CRSX.getEmail(), 
		  				new Authority[] {authorityService.getByName(BaseACL.CRSX.getAuthority())}));
		}

		if (!this.userAccountService.exists(propSsoId, BaseACL.OSX.getUser())) {
			this.userAccountService.saveOrUpdate(
		  		UserAccount.getInsance(
		  				BaseACL.OSX.getFirstName(), 
		  				BaseACL.OSX.getLastName(), 
		  				BaseACL.OSX.getUser(), 
		  				passwordEncoder.encode(BaseACL.OSX.getUser()), 
		  				BaseACL.OSX.getEmail(), 
		  				new Authority[] {authorityService.getByName(BaseACL.OSX.getAuthority())}));
		}
	}

	private void initiateApplicatonProfile() {
		final String defaultProductiveLink = "https://paramounts.herokuapp.com";
		final String defaultDevelopmentLink = "http://localhost:8080";
		
		String[] activeProfiles = null;
		String runningProfile = null;
		if (false==this.configurationService.isExistsByName(GlobalConstants.CONFIG_APP_ACCESS_URL)) {
			activeProfiles = environment.getActiveProfiles();
			if (CommonUtility.isNotEmpty(activeProfiles)) {
				runningProfile = activeProfiles[0];
			}

			this.configurationService.save(Configuration.builder()
					.group(GlobalConstants.CONFIG_GROUP_APP)
					.name(GlobalConstants.CONFIG_APP_ACCESS_URL)
					.value((runningProfile.contains("postgres") || runningProfile.contains("develop"))?defaultDevelopmentLink:defaultProductiveLink)
					.build());
		}
	}

	private void initLanguages() {
		log.info("Enter languagues intialize");
		try {
			if (1 > this.languageService.count("code", DmxConstants.LANGUAGE_CODE_VIETNAM)) {
				this.languageService.saveOrUpdate(Language.builder().code(DmxConstants.LANGUAGE_CODE_VIETNAM).name("Vietnamese").build());
			}

			if (1 > this.languageService.count("code", DmxConstants.LANGUAGE_CODE_ENGLISH)) {
				this.languageService.saveOrUpdate(Language.builder().code(DmxConstants.LANGUAGE_CODE_ENGLISH).name("English").build());
			}
		} catch (Exception e) {
			log.error(e);
		}
		log.info("Leave languagues intialize");
	}

	private void initCountries() {
		log.info("Enter countries intialize");
		this.lingualHelper.initAvailableCountries();
		log.info("Leave countries intialize");
	}

}
