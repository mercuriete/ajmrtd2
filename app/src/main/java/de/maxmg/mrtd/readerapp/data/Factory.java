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

import net.sf.scuba.data.Gender;

import org.jmrtd.lds.icao.MRZInfo;

import android.graphics.Bitmap;


/**
 * Creates PassportDOs
 * 
 * 
 * @author Max Guenther
 *
 */
public class Factory {

	private Factory() {
		throw new IllegalStateException("Utility class");
	}

	public static PassportDO createPassport(MRZInfo m, Bitmap face )
	{
		PassportDO d = new PassportDO();
		d.setDocumentCode( m.getDocumentCode() );
		d.setDocumentNumber( m.getDocumentNumber() );
		d.setDateOfBirth( m.getDateOfBirth() );
		d.setDateOfExpiry( m.getDateOfExpiry() );
		d.setGender( genderToString( m.getGender() ) );
		d.setIssuingState( m.getIssuingState() );
		d.setNationality( m.getNationality() );
		d.setPersonalNumber( m.getPersonalNumber() );
		d.setPrimaryIdentifier( m.getPrimaryIdentifier() );
		d.setSecondaryIdentifiers( m.getSecondaryIdentifierComponents() );
		d.setFace(face);
		
		return d;
	}
	
	private static String genderToString( Gender g ) {
		switch ( g ) {
		case MALE : return "m";
		case FEMALE : return "f";
		default:
			return "?";
		}
	}
}
