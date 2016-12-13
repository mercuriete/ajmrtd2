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

import android.nfc.tech.IsoDep;
import android.util.Log;

import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;

import org.jmrtd.BACKey;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CVCAFile;
import org.jmrtd.lds.icao.DG11File;
import org.jmrtd.lds.icao.DG12File;
import org.jmrtd.lds.icao.DG14File;
import org.jmrtd.lds.icao.DG15File;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.icao.DG3File;
import org.jmrtd.lds.icao.DG4File;
import org.jmrtd.lds.icao.DG5File;
import org.jmrtd.lds.icao.DG6File;
import org.jmrtd.lds.icao.DG7File;
import org.jmrtd.lds.icao.MRZInfo;
import org.jmrtd.lds.iso19794.FaceInfo;
import org.jmrtd.lds.iso19794.FingerInfo;

import java.io.InputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import de.maxmg.mrtd.readerapp.data.BACSpecDO;
import de.maxmg.utils.MessageBuilder;

public class MRTDConnection {

	
	private static final String TAG = "MRTDConnection";
	
	
	final private IMRTDConnectionProgressListener log;
	final private IsoDep isodep;
	final private BACSpecDO bac;
	
	public MRTDConnection(IMRTDConnectionProgressListener log, IsoDep isodep, BACSpecDO bac) {
		super();
		this.log = log;
		this.isodep = isodep;
		this.bac = bac;
	}

	public MRTDConnectionResult readPassport() {
		Security.addProvider( new SecurityProvider("myProv", 1, "hi"));

		MessageBuilder mb = new MessageBuilder();
		
		progress( "start" );

		try {

			PassportService ps = new PassportService( CardService.getInstance(isodep) );

			ps.doBAC(new BACKey(this.bac.getDocumentNumber(),this.bac.getDateOfBirth(),this.bac.getDateOfExpiry()));
			return this.getPassInfos(ps, false);

		}
		catch (CardServiceException e) {
			e.printStackTrace();
			error("error:" , e);
		}

		return null;
	}

	private MRTDConnectionResult getPassInfos(PassportService passport, boolean isProgressiveMode)
	{

		MRTDConnectionResult res = new MRTDConnectionResult();

		MessageBuilder mbTime = new MessageBuilder();
		
		
		

		List<Short> fileList = new ArrayList<Short>();
		fileList.add(PassportService.EF_COM);
		fileList.add(PassportService.EF_DG1);
		fileList.add(PassportService.EF_DG2);
		fileList.add(PassportService.EF_DG3);

		for (short fid: fileList) {
			
			MessageBuilder mb = new MessageBuilder();
			progress( mb.m( "reading file " + Integer.toHexString(fid) ).toString() );
			try
			{
				InputStream in = passport.getInputStream(fid);
				if (in == null) 
				{ 
					Log.w(TAG,"Got null inputstream while trying to display " + Integer.toHexString(fid & 0xFFFF)); 
				}
				switch (fid) {
				case PassportService.EF_COM:
					/* NOTE: Already processed this one. */
					break;
				case PassportService.EF_DG1:
						InputStream dg1In = passport.getInputStream(PassportService.EF_DG1);
						DG1File dg1 = new DG1File(dg1In);
						MRZInfo mrzInfo = dg1.getMRZInfo();
						
						res.setMRZInfo( mrzInfo );
					break;
				case PassportService.EF_DG2:
					DG2File dg2 = new DG2File(in);
					List<FaceInfo> faces = dg2.getFaceInfos();
//					for (FaceInfo face: faces) 
//					{ 
//						InputStream faceInputStream = face.getInputStream();
//						res.setFaceInputStream( faceInputStream );
//					}
					InputStream faceInputStream = in;
					res.setFaceInputStream( faceInputStream );
					break;
				case PassportService.EF_DG3:
					DG3File dg3 = new DG3File(in);
					List<FingerInfo> fingers = dg3.getFingerInfos();
					for (FingerInfo finger: fingers) 
					{ 
						//						displayPreviewPanel.addDisplayedImage(finger, isProgressiveMode); 
					}
					break;
				case PassportService.EF_DG4:
					DG4File dg4 = new DG4File(in);
					break;
				case PassportService.EF_DG5:
					DG5File dg5 = new DG5File(in);
					break;
				case PassportService.EF_DG6:
					DG6File dg6 = new DG6File(in);
					break;
				case PassportService.EF_DG7:
					DG7File dg7 = new DG7File(in);
//					List<DisplayedImageInfo> infos = dg7.getImages();
//					for (DisplayedImageInfo info: infos) { displayPreviewPanel.addDisplayedImage(info, isProgressiveMode); }
					break;
				case PassportService.EF_DG11:
					DG11File dg11 = new DG11File(in);
					break;
				case PassportService.EF_DG12:
					DG12File dg12 = new DG12File(in);
					break;
				case PassportService.EF_DG14:
					DG14File dg14 = new DG14File(in);
					break;
				case PassportService.EF_DG15:
					DG15File dg15 = new DG15File(in);
					break;
				case PassportService.EF_SOD:
					/* NOTE: Already processed this one above. */
					break;
				case PassportService.EF_CVCA:
					CVCAFile cvca = new CVCAFile(in);
					break;
				default:
					String message = "Displaying of file " + Integer.toHexString(fid) + " not supported!";
					if ((fid & 0x010F) == fid) {
						message = "Displaying of DG" + "xxx" + " not supported!";
					}
				}
		} catch (Exception ioe) {
			String errorMessage = "Exception reading file " + Integer.toHexString(fid) + ": \n"
			+ ioe.getClass().getSimpleName() + "\n" + ioe.getMessage() + "\n";
			Log.w(TAG, errorMessage );
			mb.nl().nl().nl().m(errorMessage).nl().nl();
			continue;
		}
		
		
		}
		
		return res;
	}
	
	private void progress(String msg) {
		log.onProgress(msg);
	}
	
	private void error( String msg, Exception e ) {
		
	}
	
}
