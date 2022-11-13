package scc.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a User, as returned to the clients
 */
public class User {
	private String id;
	private String name;
	private String pwd;
	private String photoId;
	private Set<String> auctionsIds;
	public User(String id, String name, String pwd, String photoId, Set<String> auctionsIds) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
		this.auctionsIds = auctionsIds;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getPhotoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}
	public Set<String> getAuctionsIds() {
		return auctionsIds == null ? new HashSet<String>() : auctionsIds ;
	}
	public void setAuctionsIds(Set<String> auctionsIds) {
		this.auctionsIds = auctionsIds;
	}
	/* 
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", pwd=" + pwd + ", photoId=" + photoId + ", auctionsIds="
				+ Arrays.toString(auctionsIds) + "]";
	}*/

}
