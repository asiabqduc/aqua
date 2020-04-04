package net.paramount.auth.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import net.paramount.auth.comp.JwtTokenProvider;
import net.paramount.auth.domain.SecurityPrincipalProfile;
import net.paramount.auth.entity.Authority;
import net.paramount.auth.entity.SecurityAccountProfile;
import net.paramount.auth.model.AuthorityGroup;
import net.paramount.auth.repository.UserAccountRepository;
import net.paramount.auth.service.AuthorityService;
import net.paramount.auth.service.UserAccountService;
import net.paramount.common.CommonBeanUtils;
import net.paramount.common.CommonUtility;
import net.paramount.common.DateTimeUtility;
import net.paramount.exceptions.EcosExceptionCode;
import net.paramount.exceptions.EcosphereAuthException;
import net.paramount.exceptions.ObjectNotFoundException;
import net.paramount.framework.repository.BaseRepository;
import net.paramount.framework.service.GenericServiceImpl;


@Service
public class UserAccountServiceImpl extends GenericServiceImpl<SecurityAccountProfile, Long> implements UserAccountService {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6033439932741319171L;

	@Inject
	private AuthorityService authorityService;

	@Autowired
  private UserAccountRepository repository;

	@Inject
	private PasswordEncoder passwordEncoder;

	@Inject
	private JwtTokenProvider tokenProvider;

	@Override
  protected BaseRepository<SecurityAccountProfile, Long> getRepository() {
      return repository;
  }

	@Override
	public SecurityAccountProfile get(String username) throws ObjectNotFoundException {
		return repository.findBySsoId(username);
	}

	@Override
	public UserDetails loadUserByUsername(String login) throws EcosphereAuthException {
		log.debug("Authenticating {}", login);
		String lowercaseLogin = login;//.toLowerCase();
		SecurityAccountProfile userFromDatabase = repository.findBySsoId(login);

		if (null==userFromDatabase)
			throw new EcosphereAuthException(EcosExceptionCode.ERROR_INVALID_PROFILE, String.format("User %s was not found in the database", lowercaseLogin));

		if (Boolean.FALSE.equals(userFromDatabase.isActivated()))
			throw new EcosphereAuthException(EcosExceptionCode.ERROR_PROFILE_INACTIVATE, String.format("User %s is not activated", lowercaseLogin));

		List<GrantedAuthority> grantedAuthorities = userFromDatabase.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(userFromDatabase.getSsoId(), userFromDatabase.getPassword(), grantedAuthorities);
	}

	@Override
	public UserDetails loadUserByEmail(String email) {
		SecurityAccountProfile userFromDatabase = repository.findByEmail(email);
		//TODO: Remove after then
		if (null == userFromDatabase) {
			throw new UsernameNotFoundException(String.format("User with email %s was not found in the database", email));
			//return this.buildDummyUser(email);
		}

		if (!Boolean.TRUE.equals(userFromDatabase.isActivated()))
				throw new EcosphereAuthException(String.format("User with email %s is not activated", email));
		
		return this.buildUserDetails(userFromDatabase);
	}

	@Override
	public SecurityPrincipalProfile register(SecurityAccountProfile userAccount) throws EcosphereAuthException {
		SecurityAccountProfile updatedUserAccount = null;
		SecurityPrincipalProfile registrationProfile = null;
		try {
			updatedUserAccount = (SecurityAccountProfile)CommonBeanUtils.clone(userAccount);
			updatedUserAccount.setRegisteredDate(DateTimeUtility.getSystemDateTime());

			updatedUserAccount.setPassword(passwordEncoder.encode(updatedUserAccount.getPassword()));
			updatedUserAccount = this.save(updatedUserAccount);

			updatedUserAccount.setActivationKey(tokenProvider.generateToken(updatedUserAccount));
			updatedUserAccount = this.saveOrUpdate(updatedUserAccount);

			registrationProfile = SecurityPrincipalProfile.builder()
					.displayName(updatedUserAccount.getDisplayName())
					.userAccount(updatedUserAccount)
					.build();
		} catch (Exception e) {
			throw new EcosphereAuthException(e);
		}
		return registrationProfile;
	}

	/*private boolean hasBeenEncoded(String password) {
		final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z] {53}");

		if (BCRYPT_PATTERN.matcher(password).matches())
			return true;

		return false;
	}*/

	@Override
	public void updateUser(SecurityAccountProfile user) {
		this.repository.saveAndFlush(user);
	}

	@Override
	public void deleteUser(String username) {
		SecurityAccountProfile removedObject = this.repository.findBySsoId(username);
		if (null != removedObject) {
			this.repository.delete(removedObject);
		}
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean userExists(String username) {
		SecurityAccountProfile removedObject = this.repository.findBySsoId(username);
		return (null != removedObject);
	}

	@Override
	public int countByLogin(String login) {
		return this.repository.countBySsoId(login).intValue();
	}

	@Override
	protected Page<SecurityAccountProfile> performSearch(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public UserAccount save(UserAccount user) {
		//user.setPassword(virtualEncoder.encode(user.getPassword()));
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		repository.save(user);
		return user;
	}*/

	@Override
	public SecurityAccountProfile getUserAccount(String loginId, String password) throws EcosphereAuthException {
		SecurityAccountProfile authenticatedUser = null;
		UserDetails userDetails = null;
		SecurityAccountProfile repositoryUser = null;
		if (CommonUtility.isEmailAddreess(loginId)){
			repositoryUser = repository.findByEmail(loginId);
		}else{
			repositoryUser = repository.findBySsoId(loginId);
		}

		if (null == repositoryUser)
			throw new EcosphereAuthException(EcosExceptionCode.ERROR_INVALID_PRINCIPAL, "Could not get the user information base on [" + loginId + "]");

		if (!this.passwordEncoder.matches(password, repositoryUser.getPassword()))
			throw new EcosphereAuthException(EcosExceptionCode.ERROR_INVALID_CREDENTIAL, "Invalid password of the user information base on [" + loginId + "]");

		if (!Boolean.TRUE.equals(repositoryUser.isActivated()))
			throw new EcosphereAuthException(EcosExceptionCode.ERROR_PROFILE_INACTIVATE, "Login information is fine but this account did not activated yet. ");

		userDetails = buildUserDetails(repositoryUser);
		authenticatedUser = repositoryUser;
		authenticatedUser.setUserDetails(userDetails);
		return authenticatedUser;
	}

	@Override
	public SecurityAccountProfile getUserAccount(String userToken) throws EcosphereAuthException {
		SecurityAccountProfile repositoryUser = null;
		if (CommonUtility.isEmailAddreess(userToken)){
			repositoryUser = repository.findByEmail(userToken);
		}else{
			repositoryUser = repository.findBySsoId(userToken);
		}

		if (null==repositoryUser){
			throw new EcosphereAuthException(EcosExceptionCode.ERROR_INVALID_PRINCIPAL, "Could not get the user information base on [" + userToken + "]");
		}

		return repositoryUser;
	}

	@Override
	public void initializeMasterData() throws EcosphereAuthException {
		//UserAccount adminUser = null, clientUser = null, user = null;
		Authority clientRoleEntity = null, userRoleEntity = null, adminRoleEntity = null;
		//Setup authorities/roles
		try {
			clientRoleEntity = authorityService.getByName(AuthorityGroup.RoleClient.getCode());
			if (null==clientRoleEntity){
				clientRoleEntity = Authority.builder().name(AuthorityGroup.RoleClient.getCode()).displayName("Client activity. ").build();
				authorityService.save(clientRoleEntity);
			}

			userRoleEntity = authorityService.getByName(AuthorityGroup.RoleUser.getCode());
			if (null==userRoleEntity){
				userRoleEntity = Authority.builder().name(AuthorityGroup.RoleUser.getCode()).displayName("Common activity for normal user. ").build();
				authorityService.saveOrUpdate(userRoleEntity);
			}

			adminRoleEntity = authorityService.getByName(AuthorityGroup.RoleAdmin.getCode());
			if (null==adminRoleEntity){
				adminRoleEntity = Authority.builder().name(AuthorityGroup.RoleAdmin.getCode()).displayName("System Administration. ").build();
				authorityService.saveOrUpdate(adminRoleEntity);
			}

			Set<Authority> adminAuthorities = new HashSet<>();
			adminAuthorities.add(userRoleEntity);
			adminAuthorities.add(clientRoleEntity);
			adminAuthorities.add(adminRoleEntity);

			Set<Authority> clientAuthorities = new HashSet<>();
			clientAuthorities.add(clientRoleEntity);

			Set<Authority> userAuthorities = new HashSet<>();
			userAuthorities.add(userRoleEntity);

			/*
			if (null==repository.findBySsoId(MasterUserGroup.Administrator.getLogin())){
				//adminUser = User.createInstance("administrator@gmail.com", "Administrator", "System", MasterUserGroup.Administrator.getLogin(), passwordEncoder.encode("P@dministr@t0r"), adminAuthorities);
				adminUser = UserAccount.createInstance(
						"administrator@gmail.com", 
						"Administrator", 
						"System", 
						MasterUserGroup.Administrator.getLogin(), 
						virtualEncoder.encode("P@dministr@t0r"), 
						null);
				adminUser.setActivated(true);
				repository.save(adminUser);

				adminUser.setAuthorities(adminAuthorities);
				repository.save(adminUser);
			}

			if (null==repository.findBySsoId(MasterUserGroup.VpexClient.getLogin())){
				//clientUser = User.createInstance("vpexcorpclient@gmail.com", "Corporate client", "Vpex", MasterUserGroup.VpexClient.getLogin(), passwordEncoder.encode("vP3@x5"), clientAuthorities);
				clientUser = UserAccount.createInstance(
						"vpexcorpclient@gmail.com", 
						"Corporate client", 
						"Vpex", 
						MasterUserGroup.VpexClient.getLogin(), 
						virtualEncoder.encode("vP3@x5"), 
						null);
				clientUser.setActivated(true); 
				repository.save(clientUser);

				adminUser.setAuthorities(clientAuthorities);
				repository.save(clientUser);
			}

			if (null==repository.findBySsoId(MasterUserGroup.User.getLogin())){
				//user = User.createInstance("normaluser@gmail.com", "User", "Application", MasterUserGroup.User.getLogin(), passwordEncoder.encode("u63r@x9"), userAuthorities);
				user = UserAccount.createInstance(
						"normaluser@gmail.com", 
						"User", 
						"Application", 
						MasterUserGroup.User.getLogin(), 
						virtualEncoder.encode("u63r@x9"), 
						null);
				user.setActivated(true);
				repository.save(user);

				adminUser.setAuthorities(userAuthorities);
				repository.save(user);
			}*/
		} catch (Exception e) {
			throw new EcosphereAuthException(e);
		}
	}

	@Override
	public SecurityAccountProfile confirm(String confirmedEmail) throws EcosphereAuthException {
		SecurityAccountProfile confirmUser = repository.findByEmail(confirmedEmail);
		if (null == confirmUser)
			throw new EcosphereAuthException("The email not found in database: " + confirmedEmail);

		confirmUser.setActivated(true);
		repository.save(confirmUser);
		return confirmUser;
	}

	private UserDetails buildUserDetails(SecurityAccountProfile userProfile){
		List<GrantedAuthority> grantedAuthorities = userProfile.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(userProfile.getSsoId(), userProfile.getPassword(), grantedAuthorities);
	}

	@Override
	public boolean existsByEmail(String emailAddress) {
		return this.repository.existsByEmail(emailAddress);
	}
}
