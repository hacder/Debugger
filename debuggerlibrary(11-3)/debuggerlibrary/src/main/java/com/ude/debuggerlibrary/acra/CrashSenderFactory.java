package com.ude.debuggerlibrary.acra;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.config.ACRAConfiguration;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderFactory;

/**
 * Created by ude on 2017-07-28.
 */

public class CrashSenderFactory implements ReportSenderFactory {
    /**
     * @param context Application context.
     * @param config  Configuration to use when sending reports.
     * @return Fully configured instance of the relevant ReportSender.
     */
    @NonNull
    @Override
    public ReportSender create(@NonNull Context context, @NonNull ACRAConfiguration config) {
        return new MyKeyStoreFactory();
    }
}
