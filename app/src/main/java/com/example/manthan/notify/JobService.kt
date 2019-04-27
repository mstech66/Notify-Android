package com.example.manthan.notify

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

class JobService : JobService() {
    override fun onStartJob(job: JobParameters?): Boolean {
        return false
    }

    override fun onStopJob(job: JobParameters?): Boolean {
        return false
    }
}