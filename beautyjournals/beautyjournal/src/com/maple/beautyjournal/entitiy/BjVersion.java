package com.maple.beautyjournal.entitiy;

import java.io.Serializable;

public class BjVersion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2392282376771325044L;

	public int version_id;
	
	public String version_num;
	
	public String version_changelog;
	
	public String update_hint;
	
	public String download_link;
	
	public boolean force_update;
	
	public boolean for_release;
	
	public String time_stamp;
}
