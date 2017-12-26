package movies.test.softserve.movies.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.transition.Scene
import android.transition.TransitionManager
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.confirm_registration_layout.*
import movies.test.softserve.movies.R
import movies.test.softserve.movies.controller.MainController
import movies.test.softserve.movies.util.StartActivityClass

class RegistrationActivity : AppCompatActivity() {

    private var controller = MainController.getInstance()

    private lateinit var mAuth: FirebaseAuth

    private lateinit var scene: Scene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = controller.getmAuth()

        btn_reset_password.setOnClickListener { StartActivityClass.passwordRestoreActivityStart(this) }

        scene = Scene.getSceneForLayout(container, R.layout.confirm_registration_layout, this)
        btn_login.setOnClickListener({
            spinner.visibility = View.VISIBLE
            btn_login.visibility = View.GONE

            if (password.text.toString().trim().length > 5 && repeat_password.text.toString().trim() == password.text.trim().toString()) {
                try {
                    mAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                            .addOnCompleteListener(this@RegistrationActivity) { task ->
                                spinner.visibility = View.GONE
                                btn_login.visibility = View.VISIBLE
                                if (task.isSuccessful) {
                                    controller.user = mAuth.currentUser
                                    controller.user.sendEmailVerification()

                                    TransitionManager.go(scene)
                                    verification_email_sent_message.text = getString(R.string.sent_to_email, mAuth.currentUser!!.email)
                                } else {
                                    Snackbar.make(container, R.string.smth_wrong_with_email_or_password, Snackbar.LENGTH_LONG).show()
                                }
                            }
                } catch (e: Exception) {
                    spinner.visibility = View.GONE
                    btn_login.visibility = View.VISIBLE
                    Snackbar.make(container, R.string.email_cant_be_empty, Snackbar.LENGTH_LONG).show()
                }

            } else {
                spinner.visibility = View.GONE
                btn_login.visibility = View.VISIBLE
                Snackbar.make(container, R.string.wrong_password, Snackbar.LENGTH_LONG).show()
            }


        })

        btn_signup.setOnClickListener({ onBackPressed() })
    }
}
