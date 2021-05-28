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
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.MRZInfo;

import java.io.InputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import de.maxmg.mrtd.readerapp.data.BACSpecDO;
import de.maxmg.utils.MessageBuilder;

public class MRTDConnection {


    private static final String TAG = "MRTDConnection";


    private final IMRTDConnectionProgressListener log;
    private final IsoDep isodep;
    private final BACSpecDO bac;

    public MRTDConnection(IMRTDConnectionProgressListener log, IsoDep isodep, BACSpecDO bac) {
        super();
        this.log = log;
        this.isodep = isodep;
        this.bac = bac;
    }

    public MRTDConnectionResult readPassport() {
        Security.addProvider(new SecurityProvider("myProv", 1, "hi"));
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

        progress("start");

        try {
            // I didn't know what I did. 1024,1024,false,false are just magic numbers choosen by luck
            PassportService ps = new PassportService(CardService.getInstance(isodep),1024,1024,false,false);
            ps.open();
            ps.sendSelectApplet(false);

            ps.doBAC(new BACKey(this.bac.getDocumentNumber(), this.bac.getDateOfBirth(), this.bac.getDateOfExpiry()));
            MRTDConnectionResult result =  this.getPassInfos(ps, false);
            ps.close();
            progress("finish");
            return result;

        } catch (CardServiceException e) {
            e.printStackTrace();
        }

        return null;
    }

    private MRTDConnectionResult getPassInfos(PassportService passport, boolean isProgressiveMode) {

        MRTDConnectionResult res = new MRTDConnectionResult();

        List<Short> fileList = new ArrayList<>();
        fileList.add(PassportService.EF_DG1);
        //fileList.add(PassportService.EF_DG2);
        //fileList.add(PassportService.EF_DG3);

        for (short fid : fileList) {

            MessageBuilder mb = new MessageBuilder();
            progress(mb.m("reading file " + Integer.toHexString(fid)).toString());
            try {
                InputStream in = passport.getInputStream(fid);
                if (in == null) {
                    Log.w(TAG, "Got null inputstream while trying to display " + Integer.toHexString(fid & 0xFFFF));
                }
                // We are only reading DG1 at this moment
                // if you want to see what code was here you can check this commit
                // git checkout e942341ee1bbda64bb52e06251b1f7ebdc001200
                if (fid == PassportService.EF_DG1) {
                    Log.i(TAG, "Before: DG1");
                    InputStream dg1In = passport.getInputStream(PassportService.EF_DG1);
                    DG1File dg1 = new DG1File(dg1In);
                    MRZInfo mrzInfo = dg1.getMRZInfo();
                    res.setMRZInfo(mrzInfo);
                    Log.i(TAG, "After: DG1: " + dg1.toString());
                }
            } catch (Exception ioe) {
                String errorMessage = "Exception reading file " + Integer.toHexString(fid) + ": \n"
                        + ioe.getClass().getSimpleName() + "\n" + ioe.getMessage() + "\n";
                Log.w(TAG, errorMessage);
                mb.nl().nl().nl().m(errorMessage).nl().nl();
            }

        }

        return res;
    }

    private void progress(String msg) {
        log.onProgress(msg);
    }

}
