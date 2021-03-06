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


package de.maxmg.mrtd.readerapp.service;

import java.io.InputStream;

import org.jmrtd.lds.icao.MRZInfo;

public class MRTDConnectionResult {
	
	private MRZInfo mrzInfo;
	private InputStream faceInputStream;
	
	

	public void setMRZInfo(MRZInfo mrzInfo) {
		this.mrzInfo = mrzInfo;
	}

	public void setFaceInputStream(InputStream faceInputStream) {
		this.faceInputStream = faceInputStream;
	}

	public MRZInfo getMRZInfo() {
		return mrzInfo; 
	}

	public InputStream getFaceInputStream() {
		return faceInputStream;
	}
	
	
	
	
	
}
