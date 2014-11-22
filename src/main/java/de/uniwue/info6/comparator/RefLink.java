package de.uniwue.info6.comparator;

/**
 * Klasse für MySQL-Referenz-Links.
 * 
 * @author Christian
 *
 */
public class RefLink {
	
	private String name;
	private String url;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public RefLink(String name, String url) {
		this.name = name;
		this.url = url;
	}
	
	public boolean equals(RefLink anotherRefLink){
		return name.equals(anotherRefLink.getName()) && url.equals(anotherRefLink.getUrl());
	}
	
}
