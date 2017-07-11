package com.ibm.retain.utils;

import com.ibm.retain.sdi.dataflags.CallFlags;
import com.ibm.retain.sdi.dataflags.PmrFlags;

public class Flags {

	public CallFlags callflags = new CallFlags();
	public PmrFlags pmrFlags = new PmrFlags();

	public Flags() {
		super();
		setUpCallFlags();
		setUpPmrFlags();
	}

	public void setUpCallFlags() {
		callflags.PmrNoFlag = true;
		callflags.BranchFlag = true;
		callflags.CountryFlag = true;

		callflags.CommentLineFlag = true;

		callflags.CenterFlag = true;
		callflags.QueueFlag = true;
		callflags.TimePutOnQueueFlag = true;
		callflags.TimePutOnQueueHexFlag = true;

		callflags.SeverityFlag = true;

		callflags.CreateDateFlag = true;
		callflags.AlterDateFlag = true;

		callflags.OwnerEmployeeNumberFlag = true;
		callflags.ResolverIdFlag = true;
		callflags.AdditionalResolver5Flag = true;

		callflags.CustomerNoFlag = true;
		callflags.CustomerNameFlag = true;

		callflags.CriticalSituationFlag = true;

		callflags.PrimarySecondaryFlag = true;
		
		callflags.CallerTimeZoneCorrectionFlag = true;
	}

	public void setUpPmrFlags() {
		pmrFlags.PmrNoFlag = true;
		pmrFlags.BranchFlag = true;
		pmrFlags.CountryFlag = true;
		
		pmrFlags.CenterFlag = true;
		pmrFlags.QueueFlag = true;

		pmrFlags.TextFlag = true;	
		
		pmrFlags.APARNumberFlag = true;
		
		pmrFlags.TimeZoneAdjustFlag = true;
		pmrFlags.DaylightSavingsTimeIndFlag = true;
	}
}