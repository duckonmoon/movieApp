package movies.test.softserve.movies.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.transition.Scene
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_password_restore.*
import kotlinx.android.synthetic.main.confirm_registration_layout.*
import movies.test.softserve.movies.R
import movies.test.softserve.movies.controller.MainController


class PasswordRestoreActivity : AppCompatActivity() {

    private var controller = MainController.getInstance()

    private lateinit var mAuth: FirebaseAuth

    private lateinit var scene: Scene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_restore)

        btn_back.setOnClickListener { onBackPressed() }

        mAuth = controller.getmAuth()

        scene = Scene.getSceneForLayout(container, R.layout.confirm_registration_layout, this)

        btn_reset_password.setOnClickListener {
            val emailString = email.text.toString().trim()

            if (TextUtils.isEmpty(emailString)) {
                Toast.makeText(application, R.string.enter_email, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            spinner.visibility = View.VISIBLE
            mAuth.sendPasswordResetEmail(emailString)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            TransitionManager.go(scene)
                            verification_email_sent_message.text = getString(R.string.sent_instruction)
                        } else {
                            Toast.makeText(this@PasswordRestoreActivity, R.string.fail_sent_instructions, Toast.LENGTH_SHORT).show()
                        }

                        spinner.visibility = View.GONE
                    }
        }
    }
}
