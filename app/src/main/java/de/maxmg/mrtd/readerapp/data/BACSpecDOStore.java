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

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB-Interface to deal with BACSpecDOs
 * 
 * 
 * @author Max Guenther
 *
 */
public class BACSpecDOStore {
	
	private static final String DB_NAME = "mrtd.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_BAC = "bac";
	
	private static final String COL_DOC_NUM = "DOCNUM";
	private static final String COL_DOB = "DOB";
	private static final String COL_DOE = "DOE";
	
	private DatabaseHelper helper;
	
	
	
	public BACSpecDOStore(Context context) {
		super();
		helper = new DatabaseHelper(context);
	}
	
    private boolean has( String docNumber ) {
    	SQLiteDatabase db = helper.getWritableDatabase();
    	String where = COL_DOC_NUM+"='"+docNumber+"'";
    	Cursor c = db.query(TABLE_BAC, null, where, null, null, null, null);
    	int count =  c.getCount(); 
        c.close();
        
        return count > 0;
    }

	public Vector<BACSpecDO> getAll() {
		SQLiteDatabase db = helper.getWritableDatabase();
    	Cursor c = db.query(TABLE_BAC, null, null, null, null, null, null);
    	Vector<BACSpecDO> result = new Vector<BACSpecDO>();
    	if (c.getCount() > 0) {
            c.moveToFirst();
            do
            {
            	result.add( new BACSpecDO( getDocumentNumber(c), getDateOfBirth(c), getDateOfExpiry(c) ) );
            } while( c.moveToNext() );
        }
        c.close();
        
        return result;
	}
	
	private String getDocumentNumber( Cursor c ) {
		return c.getString( c.getColumnIndex(COL_DOC_NUM) );
	}
	
	private String getDateOfBirth( Cursor c ) {
		return c.getString( c.getColumnIndex(COL_DOB) );
	}
	
	private String getDateOfExpiry( Cursor c ) {
		return c.getString( c.getColumnIndex(COL_DOE) );
	}
	
	public void save( BACSpecDO b ) {
		if( has(b.getDocumentNumber()) )
		{
			update( b );
		}
		else
		{
			insert( b );
		}
	}
	
	private void insert(BACSpecDO b) {
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put( COL_DOC_NUM, b.getDocumentNumber() );
		values.put( COL_DOB, b.getDateOfBirth() );
		values.put( COL_DOE, b.getDateOfExpiry() );
		db.insert( TABLE_BAC, null, values);

        db.close();
	}

	private void update(BACSpecDO b) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		String where = COL_DOC_NUM+"='"+b.getDocumentNumber()+"'";
		
		ContentValues values = new ContentValues();
		values.put( COL_DOC_NUM, b.getDocumentNumber() );
		values.put( COL_DOB, b.getDateOfBirth() );
		values.put( COL_DOE, b.getDateOfExpiry() );
		db.update( TABLE_BAC, values, where, null);

        db.close();
	}
	
	public void delete(BACSpecDO b) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String where = COL_DOC_NUM+"='"+b.getDocumentNumber()+"'";
		db.delete(TABLE_BAC, where, null);
        db.close();
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_BAC + " ("
                    + COL_DOC_NUM + " STRING PRIMARY KEY,"
                    + COL_DOB + " STRING,"
                    + COL_DOE + " STRING"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_BAC);
            onCreate(db);
        }
    }

	
}
