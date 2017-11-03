package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.AppToken

/**
 * Created by rkrit on 03.11.17.
 */
interface OnAppTokenGetListener {
    fun onAppTokenGet(appToken: AppToken)
}