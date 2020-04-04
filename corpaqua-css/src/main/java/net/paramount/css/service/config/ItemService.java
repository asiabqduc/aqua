package net.paramount.css.service.config;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.paramount.domain.entity.general.Language;
import net.paramount.entity.general.GeneralCatalogue;
import net.paramount.entity.general.LocalizedItem;
import net.paramount.exceptions.ObjectNotFoundException;
import net.paramount.framework.service.GenericService;

public interface ItemService extends GenericService<GeneralCatalogue, Long>{

  /**
   * Get one item with the provided code.
   * 
   * @param code The item code
   * @return The item
   * @throws ObjectNotFoundException If no such account exists.
   */
	GeneralCatalogue getOne(String code) throws ObjectNotFoundException;

  /**
   * Get one item with the provided code.
   * 
   * @param name The item name
   * @return The item
   * @throws ObjectNotFoundException If no such account exists.
   */
	GeneralCatalogue getByName(String name) throws ObjectNotFoundException;

	Page<LocalizedItem> searchLocalizedItems(String keyword, String languageCode, Pageable pageable);

	LocalizedItem getLocalizedItem(GeneralCatalogue item, Language language);

	LocalizedItem saveLocalizedItem(LocalizedItem localizedItem);
	
	List<LocalizedItem> getLocalizedItems(String subtype, Language language);
}
