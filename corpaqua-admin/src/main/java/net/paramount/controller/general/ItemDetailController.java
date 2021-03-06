/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.paramount.controller.general;

import static com.github.adminfaces.template.util.Assert.has;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;

import com.github.adminfaces.template.exception.AccessDeniedException;

import net.paramount.css.service.config.ItemService;
import net.paramount.entity.general.GeneralCatalogue;
import net.paramount.utility.FacesUtilities;

/**
 * @author ducbq
 */
@Named
@ViewScoped
public class ItemDetailController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5751177703135442722L;

	@Inject
	private ItemService businessService;

	@Inject
	private FacesUtilities utils;

	private Long id;
	private GeneralCatalogue businessObject;
	private GeneralCatalogue parent;

	public void init() {
		if (Faces.isAjaxRequest()) {
			return;
		}
		if (has(id)) {
			businessObject = businessService.getObject(id);
		} else {
			businessObject = new GeneralCatalogue();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public GeneralCatalogue getBusinessObject() {
		return businessObject;
	}

	public void setBusinessObject(GeneralCatalogue businessObject) {
		this.businessObject = businessObject;
	}

	public void remove() throws IOException {
		if (!utils.isUserInRole("ROLE_ADMIN")) {
			throw new AccessDeniedException("User not authorized! Only role <b>admin</b> can remove cars.");
		}
		if (has(businessObject) && has(businessObject.getId())) {
			businessService.remove(businessObject);
			utils.addDetailMessage("Business object " + businessObject.getName() + " removed successfully");
			Faces.getFlash().setKeepMessages(true);
			Faces.redirect("user/car-list.jsf");
		}
	}

	public void save() {
		String msg;
		businessObject.setParent(parent);
		if (businessObject.getId() == null) {
			businessService.saveOrUpdate(businessObject);
			msg = "Business object " + businessObject.getName() + " created successfully";
		} else {
			businessService.saveOrUpdate(businessObject);
			msg = "Business object " + businessObject.getName() + " updated successfully";
		}
		utils.addDetailMessage(msg);
	}

	public void clear() {
		businessObject = new GeneralCatalogue();
		id = null;
	}

	public boolean isNew() {
		return businessObject == null || businessObject.getId() == null;
	}

	public void handleParentSelect(SelectEvent event) { 
		Object item = event.getObject(); 
		if (item instanceof GeneralCatalogue) {
			this.parent = (GeneralCatalogue)item;
		}
		//FacesMessage msg = new FacesMessage("Selected", "Item:" + item); 
	}

}
