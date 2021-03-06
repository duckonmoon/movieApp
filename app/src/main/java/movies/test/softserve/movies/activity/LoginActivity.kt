package movies.test.softserve.movies.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.transition.Scene
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import movies.test.softserve.movies.R
import movies.test.softserve.movies.controller.MainController
import movies.test.softserve.movies.event.InfoUpToDateListener
import movies.test.softserve.movies.util.StartActivityClass


class LoginActivity : AppCompatActivity() {


    private var controller = MainController.getInstance()

    private lateinit var mAuth: FirebaseAuth
    private var mUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = controller.getmAuth()
        mUser = controller.user

        btn_reset_password.setOnClickListener { StartActivityClass.passwordRestoreActivityStart(this) }

        if (mUser != null && mUser!!.isEmailVerified) {
            if (controller.networkState) {
                val scene = Scene.getSceneForLayout(container, R.layout.loading_screen_layout, this)
                TransitionManager.go(scene)
                controller.getLastUpdates(object : InfoUpToDateListener {
                    override fun upToDate() {
                        StartActivityClass.startMoviesListActivity(this@LoginActivity)
                    }
                })
            } else {
                StartActivityClass.startMoviesListActivity(this@LoginActivity)
            }
            return
        }
        btn_login.setOnClickListener({
            try {
                spinner.visibility = View.VISIBLE
                btn_login.visibility = View.GONE

                val emailString = email.text.toString().trim()
                val passwordString = password.text.toString()
                mAuth.signInWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                mUser = mAuth.currentUser
                                if (mUser!!.isEmailVerified) {
                                    controller.user = mUser

                                    val scene = Scene.getSceneForLayout(container, R.layout.loading_screen_layout, this)
                                    TransitionManager.go(scene)
                                    controller.getLastUpdates(object : InfoUpToDateListener {
                                        override fun upToDate() {
                                            StartActivityClass.startMoviesListActivity(this@LoginActivity)
                                        }
                                    })

                                } else {
                                    Snackbar.make(container, getString(R.string.verification, mUser!!.email), Snackbar.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(this@LoginActivity, R.string.wrong_email_or_password, Toast.LENGTH_LONG).show()
                            }

                            spinner.visibility = View.GONE
                            btn_login.visibility = View.VISIBLE
                        }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, R.string.email_cant_be_empty, Toast.LENGTH_LONG).show()

                spinner.visibility = View.GONE
                btn_login.visibility = View.VISIBLE
            }
        })

        btn_signup.setOnClickListener({
            StartActivityClass.startRegistrationActivity(this@LoginActivity)
        })
    }
}
