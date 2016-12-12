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


package de.maxmg.mrtd.readerapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.maxmg.mrtd.readerapp.data.PassportDO;
import de.maxmg.utils.ArrayUtil;

/**
 * Display Passport content
 * 
 * 
 * @author Max Guenther
 *
 */
public class PPDisplayAct extends Activity implements OnClickListener {
	
	private ImageView iv;
	private TextView documentNumberW;
	private TextView personalNumberW;
	private TextView issuingStateW;
	private TextView primaryIdentifierW;
	private TextView secondaryIdentifiersW;
	private TextView genderW;
	private TextView nationalityW;
	private TextView dobW;
	private TextView doeW;
	
	private Button savePicW;
	private Button closeW;
	
	private PassportDO pp;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_display);
        
        prepareWidgets();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		this.pp = getIntent().getParcelableExtra( PassportDO.EXTRA_PASSPORTDO ); 
		updateView(pp);
	}

	private void prepareWidgets() {
		iv = (ImageView) findViewById(R.id.pp_display_iv);
		documentNumberW = (TextView) findViewById(R.id.ppd_documentNumberW);
		personalNumberW = (TextView) findViewById(R.id.ppd_personalNumberW);
		issuingStateW = (TextView) findViewById(R.id.ppd_issuingStateW);
		primaryIdentifierW = (TextView) findViewById(R.id.ppd_primaryIdentifierW);
		secondaryIdentifiersW = (TextView) findViewById(R.id.ppd_secondaryIdentifiersW);
		genderW = (TextView) findViewById(R.id.ppd_genderW);
		nationalityW = (TextView) findViewById(R.id.ppd_nationalityW);
		dobW = (TextView) findViewById(R.id.ppd_dateOfBirthW);
		doeW = (TextView) findViewById(R.id.ppd_dateOfExpiryW);
		
		savePicW = (Button) findViewById(R.id.ppd_savePicW);
		savePicW.setOnClickListener(this);
		closeW = (Button) findViewById(R.id.ppd_closeW);
		closeW.setOnClickListener(this);
	}
	
	private void updateView( PassportDO pp ) {
		iv.setImageBitmap( pp.getFace() );
		documentNumberW.setText( pp.getDocumentNumber() );
		personalNumberW.setText( pp.getPersonalNumber() );
		issuingStateW.setText( pp.getIssuingState() );
		primaryIdentifierW.setText( pp.getPrimaryIdentifier() );
		secondaryIdentifiersW.setText( ArrayUtil.join(pp.getSecondaryIdentifiers()," ") );
		genderW.setText( pp.getGender() );
		nationalityW.setText( pp.getNationality() );
		dobW.setText( pp.getDateOfBirth() );
		doeW.setText( pp.getDateOfExpiry() );
	}

	@Override
	public void onClick(View v) {
		if( v == closeW ) 
		{
			finish();
		}
		else if( v == savePicW )
		{
			Toast.makeText(this, "not available in this version", Toast.LENGTH_SHORT).show();
			//createExternalStoragePublicPicture( pp.getFace() );
		}
	}
	
	private void createExternalStoragePublicPicture(Bitmap b) {
	    File path = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES);
	    File file = new File(path, "PassportPic.jpg");

	    try {
	        // Make sure the Pictures directory exists.
	        path.mkdirs();

	        OutputStream os = new FileOutputStream(file);
	        b.compress( Bitmap.CompressFormat.JPEG, 100, os);
	        os.close();

	        // Tell the media scanner about the new file so that it is
	        // immediately available to the user.
	        MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null, 
	        		new MediaScannerConnection.OnScanCompletedListener() {
			        	@Override
			        	public void onScanCompleted(String path, Uri uri) {
			                Log.i("ExternalStorage", "Scanned " + path + ":");
			                Log.i("ExternalStorage", "-> uri=" + uri);
			            }
			        });
	    } catch (IOException e) {
	        // Unable to create file, likely because external storage is not currently mounted.
	        Log.w("ExternalStorage", "Error writing " + file, e);
	    }
	}
}
