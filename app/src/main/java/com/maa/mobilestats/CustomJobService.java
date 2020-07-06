package com.maa.mobilestats;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class CustomJobService extends JobService {
    private static  final String TAG = "CustomJobService";
    private boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job Started");
        doBackgroundWork(jobParameters);
        return false;
    }

    private  void doBackgroundWork(final JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    MainActivity fs = new MainActivity();
                    fs.FetchInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (jobCancelled) {
                    return;
                }

                jobFinished(params, false);
            }
        }).start();
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancelled = true;
        return true;
    }
}
