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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.maxmg.mrtd.readerapp.data.BACSpecDO;
import de.maxmg.mrtd.readerapp.data.BACSpecDOStore;

/**
 * Display List of BACS
 * 
 * @author Max Guenther
 *
 */
public class BacsAct extends Activity implements OnClickListener, OnItemClickListener {

	private static final String TAG = "BacsAct";
	
	private static final String ACTION_LABEL_READ = "read";
	private static final String ACTION_LABEL_EDIT = "edit";
	private static final String ACTION_LABEL_DELETE = "delete";
	private static final String[] ACTION_LABELS = {ACTION_LABEL_READ,ACTION_LABEL_EDIT, ACTION_LABEL_DELETE};
	
	private static final int REQ_EDIT_NEW_BAC = 1;
	private static final int REQ_EDIT_BAC = 2;
	private static final int REQ_READ_PP = 3;
	private Button readNewW;
	private Button tryAllW;

	private BACSpecDOStore bacs;
	private ArrayAdapter<BACSpecDO> listA;
	
	private BACSpecDO selectedBac;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v( TAG, "onCreate" );
        
        
        setContentView(R.layout.bacs);
        
        bacs = new BACSpecDOStore(this);
        prepareWidgets();
    }
	
	private void prepareWidgets() {
        readNewW = (Button) findViewById(R.id.readNewW);
        readNewW.setOnClickListener(this);
        
        tryAllW = (Button) findViewById(R.id.tryAll);
        tryAllW.setOnClickListener(this);

		ListView listW = (ListView) findViewById(R.id.listW);
        listA = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bacs.getAll());
        listW.setAdapter(listA);
        listW.setOnItemClickListener( this );
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.v( TAG, "onResume" );
		
		
		resolveIntent( getIntent() );
	}

	private void resolveIntent(Intent intent) {
		if (intent != null && NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Tag t = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			ArrayList<BACSpecDO> bacsArrayList = new ArrayList<>();
			bacsArrayList.addAll(bacs.getAll());
			read(bacsArrayList, t);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v( TAG, "onActivityResult" );

		setIntent(data);
		
		if( requestCode == REQ_EDIT_NEW_BAC )
		{
			if( resultCode == RESULT_OK )
			{
				BACSpecDO bac = data.getExtras().getParcelable( BACSpecDO.EXTRA_BAC );
				bacs.save(bac);
				refreshAdapter();
				read(bac);
			}
		}
		else if( requestCode == REQ_EDIT_BAC )
		{
			if( resultCode == RESULT_OK )
			{
				BACSpecDO bac = data.getExtras().getParcelable( BACSpecDO.EXTRA_BAC );
				bacs.save(bac);
				refreshAdapter();
			}
		}
		else if( requestCode == REQ_READ_PP )
		{
			if( resultCode == RESULT_OK )
			{
				Intent i = new Intent(this, PPDisplayAct.class)
							.putExtras(data.getExtras());
				startActivity(i);
			}
			else if( resultCode == RESULT_CANCELED )
			{
				toastIt("error");
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if( v == readNewW )
		{
			startActivityForResult( new Intent(this,BacEditorAct.class), REQ_EDIT_NEW_BAC);
		}
		else if( v == tryAllW )
		{
			ArrayList<BACSpecDO> bacsArrayList = new ArrayList<>();
			bacsArrayList.addAll( bacs.getAll() );
			read( bacsArrayList );
		}
	}
	
	private void read(BACSpecDO b)
	{
		ArrayList<BACSpecDO> bacs = new ArrayList<>();
		bacs.add(b);
		read( bacs );
	}
	private void read(ArrayList<BACSpecDO> bs)
	{
		Intent i = new Intent( BacsAct.this, ReadAct.class )
		.putParcelableArrayListExtra(BACSpecDO.EXTRA_BAC_COL, bs )
		.setAction( ReadAct.ACTION_READ );
		startActivityForResult(i, REQ_READ_PP);
	}
	private void read(ArrayList<BACSpecDO> bs, Tag tag)
	{
		Intent i = new Intent( BacsAct.this, ReadAct.class )
		.putParcelableArrayListExtra(BACSpecDO.EXTRA_BAC_COL, bs )
		.putExtra( NfcAdapter.EXTRA_TAG, tag )
		.setAction( ReadAct.ACTION_READ );
		startActivityForResult(i, REQ_READ_PP);
	}
	
	
	private void delete(BACSpecDO b)
	{
		bacs.delete(b);
		refreshAdapter();
	}
	private void edit(BACSpecDO b)
	{
		Intent i = new Intent(this,BacEditorAct.class)
					.putExtra(BACSpecDO.EXTRA_BAC, b);
		startActivityForResult( i, REQ_EDIT_BAC);
	}
	
	private void toastIt( String msg )
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}


	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectedBac = listA.getItem(position);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Actions");
		builder.setItems( ACTION_LABELS, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	switch (item) {
				case 0: read(selectedBac); break;
				case 1: edit(selectedBac); break;
				case 2: delete(selectedBac); break;

				default:
					break;
				}
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options, menu);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_opt_info) {
			startActivity(new Intent(this, InfoAct.class));
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	private void refreshAdapter() {
		listA.clear();
		for(BACSpecDO b : bacs.getAll())
			listA.add(b);
		
	}
	
}