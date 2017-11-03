package movies.test.softserve.movies.event

import movies.test.softserve.movies.entity.LoginSession

/**
 * Created by rkrit on 03.11.17.
 */
interface OnLoginSessionGetListener {
    fun OnLoginSessionGet(loginSession: LoginSession)
}