package sernet.gs.reveng;

// Generated Jun 5, 2015 1:28:30 PM by Hibernate Tools 3.4.0.CR1

/**
 * MbGefaehrskatId generated by hbm2java
 */
public class MbGefaehrskatId implements java.io.Serializable {

	private int gfkImpId;
	private int gfkId;

	public MbGefaehrskatId() {
	}

	public MbGefaehrskatId(int gfkImpId, int gfkId) {
		this.gfkImpId = gfkImpId;
		this.gfkId = gfkId;
	}

	public int getGfkImpId() {
		return this.gfkImpId;
	}

	public void setGfkImpId(int gfkImpId) {
		this.gfkImpId = gfkImpId;
	}

	public int getGfkId() {
		return this.gfkId;
	}

	public void setGfkId(int gfkId) {
		this.gfkId = gfkId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof MbGefaehrskatId))
			return false;
		MbGefaehrskatId castOther = (MbGefaehrskatId) other;

		return (this.getGfkImpId() == castOther.getGfkImpId())
				&& (this.getGfkId() == castOther.getGfkId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getGfkImpId();
		result = 37 * result + this.getGfkId();
		return result;
	}

}