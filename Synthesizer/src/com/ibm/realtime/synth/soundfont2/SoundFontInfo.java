/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.soundfont2;

/**
 * A class to store all informational/meta data from the soundfont file.
 * 
 * @author florian
 * 
 */
public class SoundFontInfo {
	private int versionMajor;
	private int versionMinor;
	private String soundEngine = "EMU8000";
	protected String name = "(unknown)";
	private String romName;
	private int romVersionMajor;
	private int romVersionMinor;
	private String creationDate;
	private String engineer;
	private String product;
	private String copyright;
	private String comment;
	private String software;

	public int getVersionMajor() {
		return versionMajor;
	}

	public int getVersionMinor() {
		return versionMinor;
	}

	void setVersion(int versionMajor, int versionMinor) {
		this.versionMajor = versionMajor;
		this.versionMinor = versionMinor;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the soundEngine.
	 */
	public String getSoundEngine() {
		return soundEngine;
	}

	/**
	 * @param soundEngine The soundEngine to set.
	 */
	void setSoundEngine(String soundEngine) {
		this.soundEngine = soundEngine;
	}

	/**
	 * @return Returns the romName.
	 */
	public String getRomName() {
		return romName;
	}

	/**
	 * @param romName The romName to set.
	 */
	public void setRomName(String romName) {
		this.romName = romName;
	}

	void setROMVersion(int versionMajor, int versionMinor) {
		this.romVersionMajor = versionMajor;
		this.romVersionMinor = versionMinor;
	}

	public int getROMVersionMajor() {
		return romVersionMajor;
	}

	public int getROMVersionMinor() {
		return romVersionMinor;
	}

	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment The comment to set.
	 */
	void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return Returns the copyright.
	 */
	public String getCopyright() {
		return copyright;
	}

	/**
	 * @param copyright The copyright to set.
	 */
	void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**
	 * @return Returns the creationDate.
	 */
	public String getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate The creationDate to set.
	 */
	void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return Returns the engineer.
	 */
	public String getEngineer() {
		return engineer;
	}

	/**
	 * @param engineer The engineer to set.
	 */
	void setEngineer(String engineer) {
		this.engineer = engineer;
	}

	/**
	 * @return Returns the product.
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @param product The product to set.
	 */
	void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @return Returns the software.
	 */
	public String getSoftware() {
		return software;
	}

	/**
	 * @param software The software to set.
	 */
	void setSoftware(String software) {
		this.software = software;
	}

}
