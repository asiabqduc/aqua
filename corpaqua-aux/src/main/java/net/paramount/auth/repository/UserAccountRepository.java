/*
* Copyright 2017, Bui Quy Duc
* by the @authors tag. See the LICENCE in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package net.paramount.auth.repository;

import org.springframework.stereotype.Repository;

import net.paramount.auth.entity.SecurityAccountProfile;
import net.paramount.framework.repository.BaseRepository;

/**
 * Repository interface for the {@link SecurityAccountProfile} entity. It contains methods for
 * regular <code>CRUD</code> operations
 * 
 * @author bqduc
 *
 */
@Repository
public interface UserAccountRepository extends BaseRepository<SecurityAccountProfile, Long> {
	/**
	 * Retrieves a {@link SecurityAccountProfile} entity from the underlying data store by its
	 * ResetKey
	 * 
	 * @param ssoId
	 *            the login
	 * @return a User entity
	 * @see SecurityAccountProfile#getResetKey()
	 */
	SecurityAccountProfile findBySsoId(String ssoId);

	/**
	 * Retrieves a {@link SecurityAccountProfile} entity from the underlying datastore by its
	 * Email
	 * 
	 * @param email
	 *            the User's email
	 * @return a User entity
	 * @see SecurityAccountProfile#getEmail()
	 */
	SecurityAccountProfile findByEmail(String email);

	/**
	 * Retrieves the number of entities from the underlying data store by its
	 * login
	 * 
	 * @param login
	 *            the user name
	 * @return a User entity
	 * @see SecurityAccountProfile#getLogin()
	 */
	Long countBySsoId(String login);
	
	/**
	 * Check if a user with the email exists in the system
	 * 
	 * @param email
	 *            the user account's email
	 * @return a true if exists
	 */
	boolean existsByEmail(String email);

	/**
	 * Check if a user with the ssoId exists in the system
	 * 
	 * @param ssoId
	 *            the user account's ssoId
	 * @return a true if exists
	 */
	boolean existsBySsoId(String ssoId);
}
