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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Date;

import de.maxmg.mrtd.readerapp.data.BACSpecDO;


/**
 * Edit/Create a BACSpecDO
 * 
 * The controls will be populated with the Intents Extra BACSpecDO (if present)
 * If the 'ok' Button is pressed, an Back-Intent will be created, containing a BACSpecDO with the controls values.
 * 
 * 
 * @author Max Guenther
 *
 */
public class BacEditorAct extends Activity implements OnClickListener {

	private static final int DIALOG_ID_DOB = 0;
	private static final int DIALOG_ID_DOE = 1;
	private EditText docNumW;
	private Button selectDobW;
	private Button selectDoeW;
	private Button okW;
	private Button cancelW;

	private Date dob;
	private Date doe;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bac_editor);

		prepareWidgets();
		resolveIntent( getIntent() );
	}

	private void resolveIntent(Intent intent) {
		if( intent.getExtras() != null )
		{
			BACSpecDO bac = intent.getExtras().getParcelable( BACSpecDO.EXTRA_BAC );
			if( bac != null )
			{
				docNumW.setText( bac.getDocumentNumber() );
				try {
					dob = BACSpecDO.SDF.parse(bac.getDateOfBirth());
					doe = BACSpecDO.SDF.parse(bac.getDateOfExpiry());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				
				updateDisplay();
			}
		}
		if( dob == null ) dob = new Date();
		if( doe == null ) doe = new Date();
	}

	private void updateDisplay() {
		String dobLabel = getResources().getString( R.string.selectDOB );
		if( dob != null )
			dobLabel += " " + BACSpecDO.SDF.format(dob);

		selectDobW.setText( dobLabel );

		String doeLabel = getResources().getString( R.string.selectDOE );
		if( doe != null )
			doeLabel += " " + BACSpecDO.SDF.format(doe);

		selectDoeW.setText( doeLabel );

	}

	private void prepareWidgets() {
		docNumW = (EditText) findViewById(R.id.docNum);

		selectDobW = (Button) findViewById(R.id.selectDOB);
		selectDoeW = (Button) findViewById(R.id.selectDOE);

		okW = (Button) findViewById(R.id.bacEditor_okW);
		cancelW = (Button) findViewById(R.id.bacEditor_cancelW);


		selectDobW.setOnClickListener(this);
		selectDoeW.setOnClickListener(this);
		okW.setOnClickListener(this);
		cancelW.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if( v == selectDobW )
		{
			showDialog(DIALOG_ID_DOB);
		}
		else if( v == selectDoeW )
		{
			showDialog(DIALOG_ID_DOE);
		}
		else if( v == cancelW )
		{
			setResult(RESULT_CANCELED);
			finish();
		}
		else if( v == okW )
		{
			BACSpecDO b = new BACSpecDO( docNumW.getText().toString(), dob, doe);
			Intent i = new Intent()
						.putExtra(BACSpecDO.EXTRA_BAC, b);
			
			setResult(RESULT_OK, i);
			finish();
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ID_DOB) {
			return createDatePickerDialog(dob);
		}
		if (id == DIALOG_ID_DOE) {
			return createDatePickerDialog(doe);
		}
		return null;
	}

	private Dialog createDatePickerDialog(Date d ) {
		return new DatePickerDialog(this, new OnDateSetListener(d), d.getYear()+1900, d.getMonth(), d.getDate());
	}

	private class OnDateSetListener implements DatePickerDialog.OnDateSetListener {
		private Date subject;
		
		public OnDateSetListener(Date subject) {
			this.subject = subject;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			subject.setYear(year);
			subject.setMonth(monthOfYear);
			subject.setDate(dayOfMonth);
			updateDisplay();
		}
		
	}
}
