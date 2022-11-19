package scc.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a User, as stored in the database
 */
public class UserDAO {
	private String _rid;
	private String _ts;
	private String id;
	private String name;
	private String pwd;
	private String photoId;
	private Set<String> auctionsIds;
	private Set<String> bidsIds;

	public UserDAO() {
	}
	public UserDAO( User u) {
		this(u.getId(), u.getName(), u.getPwd(), u.getPhotoId(), u.getAuctionsIds(), u.getBidsIds());
	}
	public UserDAO(String id, String name, String pwd, String photoId, Set<String> auctionsIds, Set<String> bidsIds) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
		this.auctionsIds = auctionsIds;
		this.bidsIds = bidsIds;
	}
	public String get_rid() {
		return _rid;
	}
	public void set_rid(String _rid) {
		this._rid = _rid;
	}
	public String get_ts() {
		return _ts;
	}
	public void set_ts(String _ts) {
		this._ts = _ts;
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
	public Set<String> getBidsIds() {
		return bidsIds == null ? new HashSet<String>() : bidsIds ;
	}
	public void setBidsIds(Set<String> bidsIds) {
		this.bidsIds = bidsIds;
	}

}
