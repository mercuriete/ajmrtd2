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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.jmrtd.lds.icao.MRZInfo;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import de.maxmg.mrtd.readerapp.data.BACSpecDO;
import de.maxmg.mrtd.readerapp.data.Factory;
import de.maxmg.mrtd.readerapp.data.PassportDO;
import de.maxmg.mrtd.readerapp.service.MRTDConnectionResult;
import de.maxmg.mrtd.readerapp.service.IMRTDConnectionProgressListener;
import de.maxmg.mrtd.readerapp.service.MRTDConnection;
import de.maxmg.utils.OnScreenLogger;


/**
 * Read Passport content
 * 
 * 
 * @author Max Guenther
 *
 */
public class ReadAct extends Activity implements IMRTDConnectionProgressListener, OnCancelListener {
	
	
	private static final String TAG = "ReadAct";

	public static final String ACTION_READ = "ACTION_READ";
	
	private TextView console;
	private ScrollView consoleScroll;
	private OnScreenLogger osl;
	private ProgressDialog progressDialog;
	
	
	private MRZInfo mrzInfo;
	
	private long timeStart;
	
	
	private ArrayList<BACSpecDO> bacs;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "onCreate");
        
        setContentView(R.layout.read);
        
        prepareWidgets();
        
        resolveIntent(getIntent());
        
        osl.m("Please provide your Passport .... ");
    }
	
	private void prepareWidgets() {
		console = (TextView) findViewById(R.id.console);
		consoleScroll = (ScrollView) findViewById(R.id.consoleScroll);

		osl = new OnScreenLogger(console, consoleScroll);
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.v(TAG, "onNewIntent");
		setIntent(intent);
		resolveIntent(intent);
	}
	
	private void resolveIntent(Intent intent) {
		Log.v(TAG, "resolveIntent");
		String action = intent.getAction();
		Log.v(TAG, action);

		if( ACTION_READ.equals(action) )
		{
			bacs = intent.getExtras().getParcelableArrayList( BACSpecDO.EXTRA_BAC_COL );
			Tag t = intent.getExtras().getParcelable(NfcAdapter.EXTRA_TAG);
			if( t != null )
			{
				handleIsoDepFound( IsoDep.get(t) );
			}
		}
		else if( NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) )
		{
			Tag t = intent.getExtras().getParcelable(NfcAdapter.EXTRA_TAG);

			if( Arrays.asList( t.getTechList() ).contains( "android.nfc.tech.IsoDep" ) )
			{
				handleIsoDepFound( IsoDep.get(t));
			}
		}
	}
	

	@Override
	public void onResume() 
	{
		super.onResume();
		Log.v(TAG, "onResume");
		enableForegroundDispatch();
	}

	private void enableForegroundDispatch() {
		NfcAdapter a = NfcAdapter.getDefaultAdapter(this);
		if(a == null){
			osl.m("NFC NOT AVAILABLE!!!");
			return;
		}
		Intent in = new Intent(getApplicationContext(), this.getClass());
		in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity( this, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
		String[][] filter = new String[][]{ 
				new String[]{ "android.nfc.tech.IsoDep" }
		};

		a.enableForegroundDispatch( this, pi, null, filter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");
		if (NfcAdapter.getDefaultAdapter(this) != null){
			NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
		}
	}
	
	private void handleIsoDepFound(IsoDep isodep) {
		Log.v(TAG, "handleIsoDepFound");
		
		osl.m("..thx");
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Reading...");
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(this);
		
		try
		{
			progressDialog.show();
		} catch( Exception ex )
		{
			Log.e(TAG, "error", ex);
		}
		
		
		isodep.setTimeout(10000);
		MRTDConnection con = new MRTDConnection(this, isodep, bacs);
		timeStart = System.currentTimeMillis();
		new AsyncRead().execute(con);
		
		
	}

	
	
	private void line( final String s )
	{
		runOnUiThread( new Runnable() {
			@Override
			public void run() {
				osl.nl().m(s).nl();
			}
		});
	}
	
	
	private void simpleErrorHandler(Exception ex) {
		try
		{
			progressDialog.dismiss();
		} catch(Exception e){}
		StringWriter w = new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		line( w.toString() );
	}


	@Override
	public void onProgress(String msg) {
		line(msg);
	}

	@Override
	public void onError(String msg, Exception e) {
		line(msg);
		Log.e(TAG, msg, e);
		simpleErrorHandler(e);
	}
	
	private void handlePassportResult(MRTDConnectionResult result) {
		line("done");
		
		line( "read Passport in " + (System.currentTimeMillis() - timeStart)/1000 + " seconds" );
		
		
		if( checkResult(result) )
		{
			mrzInfo = result.getMRZInfo();
			startBitmapDecode(result);
		}
	}
	
	private boolean checkResult( MRTDConnectionResult result )
	{
		if( result == null )
		{
			Log.e(TAG, "result == null");
			return false;
		}
		else if( result.getMRZInfo() == null )
		{
			Log.e(TAG, "result.getMRZInfo() == null");
			return false;
		}
		else if( result.getFaceInputStream() == null )
		{
			Log.e(TAG, "result.getFaceInputStream() == null");
			return false;
		}
		
		return true;
	}
	
	private void startBitmapDecode(MRTDConnectionResult result) {
		progressDialog.setMessage("Decoding Image...");
		line("start image decoding");
		timeStart = System.currentTimeMillis();
		
		InputStream is = result.getFaceInputStream();
		new AsyncImageDecode(getCacheDir()).execute(is);
	}
	
	private void handleBitmapDecoded(Bitmap result) {
		resultComplete(mrzInfo, result);
	}
	
	private void resultComplete( MRZInfo mrzInfo, Bitmap face ) {
		PassportDO pp = Factory.createPassport(mrzInfo, face);
		
		if( face == null )
		{
			setResult(RESULT_CANCELED);
		}
		else
		{
			Bundle extras = new Bundle();
			extras.putParcelable( PassportDO.EXTRA_PASSPORTDO, pp);
			Intent i = new Intent();
			i.putExtras(extras);
			setResult(RESULT_OK, i );
		}
		
		finish();
	}
	

	@Override
	public void onCancel(DialogInterface arg0) {
		setResult(RESULT_CANCELED);
		finish();
	}
	
	class AsyncRead extends AsyncTask<MRTDConnection, String, MRTDConnectionResult> {

		@Override
		protected MRTDConnectionResult doInBackground(MRTDConnection... params) {
			MRTDConnection con = params[0];
			return con.readPassport();
		}
		
		@Override
		protected void onPostExecute(MRTDConnectionResult result) {
			handlePassportResult(result);
		}
	}
	
	class AsyncImageDecode extends AsyncTask<InputStream, String, Bitmap> {

		private File cacheDir;
		
		public AsyncImageDecode(File cacheDir) {
			super();
			this.cacheDir = cacheDir;
		}

		@Override
		protected Bitmap doInBackground(InputStream... params) {
			InputStream is = params[0];
			Bitmap face = new BitmapFactory().decodeStream(is);
			try
			{
				is.close();
			}
			catch( IOException e ) {}
			return face;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			handleBitmapDecoded(result);
		}

	}
	

}
