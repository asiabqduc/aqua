package net.paramount.css.service.contact;

import org.springframework.data.domain.Page;

import net.paramount.entity.contact.CTAContact;
import net.paramount.exceptions.ObjectNotFoundException;
import net.paramount.framework.model.SearchParameter;
import net.paramount.framework.service.GenericService;

public interface ContactService extends GenericService<CTAContact, Long> {

	/**
	 * Get one Enterprise with the provided code.
	 * 
	 * @param code
	 *            The Enterprise code
	 * @return The Enterprise
	 * @throws ObjectNotFoundException
	 *             If no such Enterprise exists.
	 */
	CTAContact getOne(String code) throws ObjectNotFoundException;

	/**
	 * Get one Enterprises with the provided search parameters.
	 * 
	 * @param searchParameter
	 *            The search parameter
	 * @return The pageable Enterprises
	 */
	Page<CTAContact> getObjects(SearchParameter searchParameter);
}
