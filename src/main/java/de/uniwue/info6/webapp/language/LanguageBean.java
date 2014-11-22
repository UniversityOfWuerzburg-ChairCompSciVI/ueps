package de.uniwue.info6.webapp.language;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

/**
 * not finished, not used.
 *
 * @author Michael
 */
@ManagedBean(name = "language")
@SessionScoped
public class LanguageBean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String localeCode;

	private static Map<String, Object> countries;

	static {
		countries = new LinkedHashMap<String, Object>();
		countries.put("[DE]", Locale.GERMAN); //label, value
		countries.put("[EN]", Locale.ENGLISH); //label, value
	}

	public Map<String, Object> getCountriesInMap() {
		return countries;
	}

	public String getLocaleCode() {
		if (localeCode == null) {
			localeCode = FacesContext.getCurrentInstance().getApplication().getDefaultLocale().toString();
		}
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		// FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(localeCode));
		this.localeCode = localeCode;
	}

	public void countryLocaleCodeChanged(ValueChangeEvent e) {
		String newLocaleValue = e.getNewValue().toString();
		//loop a map to compare the locale code
		for (Map.Entry<String, Object> entry : countries.entrySet()) {

			if (entry.getValue().toString().equals(newLocaleValue)) {

				FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale) entry.getValue());

			}
		}

	}

}
