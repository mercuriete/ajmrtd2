/*
 * aJMRTD - An Android Client for JMRTD, a Java API for accessing machine readable travel documents.
 *
 * Max Guenther, max.math.guenther@googlemail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */


package de.maxmg.mrtd.readerapp.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Represents a BAC KeySpec
 * 
 * 
 * @author Max Guenther
 *
 */
public class BACSpecDO implements Parcelable {
	
	public static final String EXTRA_BAC = "EXTRA_BAC";
	public static final String EXTRA_BAC_COL = "EXTRA_BAC_COL";
	
	
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyMMdd");

	private String documentNumber;
	private String dateOfBirth;
	private String dateOfExpiry;

	public BACSpecDO(String documentNumber, String dateOfBirth, String dateOfExpiry) {
		this.documentNumber = documentNumber.trim();
		this.dateOfBirth = dateOfBirth;
		this.dateOfExpiry = dateOfExpiry;
		
		
	}

	public BACSpecDO(String documentNumber, Date dateOfBirth, Date dateOfExpiry) {
		this( documentNumber, SDF.format(dateOfBirth), SDF.format(dateOfExpiry));
	}

	

	public String getDocumentNumber() {
		return documentNumber;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public String getDateOfExpiry() {
		return dateOfExpiry;
	}

	public String toString() {
		return documentNumber + ", " + dateOfBirth + ", " + dateOfExpiry;
	}

	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (!o.getClass().equals(this.getClass())) { return false; }
		if (o == this) { return true; }
		BACSpecDO previous = (BACSpecDO)o;
		return documentNumber.equals(previous.documentNumber) &&
		dateOfBirth.equals(previous.dateOfBirth) &&
		dateOfExpiry.equals(previous.dateOfExpiry);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(documentNumber);
		out.writeString(dateOfBirth);
		out.writeString(dateOfExpiry);
	}

	public static final Parcelable.Creator<BACSpecDO> CREATOR
	= new Parcelable.Creator<BACSpecDO>() {
		public BACSpecDO createFromParcel(Parcel in) {
			return new BACSpecDO(in);
		}

		public BACSpecDO[] newArray(int size) {
			return new BACSpecDO[size];
		}
	};
	
	private BACSpecDO(Parcel in) {
		documentNumber = in.readString();
		dateOfBirth = in.readString();
		dateOfExpiry = in.readString();
	}
}
