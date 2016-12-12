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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Passport
 * 
 * 
 * @author Max Guenther
 *
 */
public class PassportDO implements Parcelable {
	
	public static final String EXTRA_PASSPORTDO = "EXTRA_PASSPORTDO";
	
	private String documentCode;
	private String issuingState;
	private String primaryIdentifier;
	private String[] secondaryIdentifiers;
	private String nationality;
	private String documentNumber;
	private String personalNumber;
	private String dateOfBirth;
	private String gender;
	private String dateOfExpiry;
	private Bitmap face;
	
	public Bitmap getFace() {
		return face;
	}

	public void setFace(Bitmap face) {
		this.face = face;
	}

	public PassportDO(){}
	
	public String getDocumentCode() {
		return documentCode;
	}
	public void setDocumentCode(String documentCode) {
		this.documentCode = documentCode;
	}
	public String getIssuingState() {
		return issuingState;
	}
	public void setIssuingState(String issuingState) {
		this.issuingState = issuingState;
	}
	public String getPrimaryIdentifier() {
		return primaryIdentifier;
	}
	public void setPrimaryIdentifier(String primaryIdentifier) {
		this.primaryIdentifier = primaryIdentifier;
	}
	public String[] getSecondaryIdentifiers() {
		return secondaryIdentifiers;
	}
	public void setSecondaryIdentifiers(String[] secondaryIdentifiers) {
		this.secondaryIdentifiers = secondaryIdentifiers;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public String getPersonalNumber() {
		return personalNumber;
	}
	public void setPersonalNumber(String personalNumber) {
		this.personalNumber = personalNumber;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDateOfExpiry() {
		return dateOfExpiry;
	}
	public void setDateOfExpiry(String dateOfExpiry) {
		this.dateOfExpiry = dateOfExpiry;
	}
	
	
	

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(documentCode);
		out.writeString(issuingState);
		out.writeString(primaryIdentifier);
		out.writeInt( secondaryIdentifiers.length );
		out.writeStringArray(secondaryIdentifiers);
		out.writeString(nationality);
		out.writeString(documentNumber);
		out.writeString(personalNumber);
		out.writeString(dateOfBirth);
		out.writeString(gender);
		out.writeString(dateOfExpiry);
		out.writeParcelable(face, flags);
	}

	public static final Parcelable.Creator<PassportDO> CREATOR = new Parcelable.Creator<PassportDO>() {
		public PassportDO createFromParcel(Parcel in) {
			return new PassportDO(in);
		}

		public PassportDO[] newArray(int size) {
			return new PassportDO[size];
		}
	};
	
	private PassportDO(Parcel in) {
		documentCode = in.readString();
		issuingState = in.readString();
		primaryIdentifier = in.readString();
		int secondaryIdentifiersLength = in.readInt();
		secondaryIdentifiers = new String[secondaryIdentifiersLength];
		in.readStringArray(secondaryIdentifiers);
		nationality = in.readString();
		documentNumber = in.readString();
		personalNumber = in.readString();
		dateOfBirth = in.readString();
		gender = in.readString();
		dateOfExpiry = in.readString();
		face = in.readParcelable(null);
	}
	
	
}
