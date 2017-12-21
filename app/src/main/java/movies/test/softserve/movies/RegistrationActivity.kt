package movies.test.softserve.movies

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registration.*
import movies.test.softserve.movies.controller.MainController

class RegistrationActivity : AppCompatActivity() {

    private var controller = MainController.getInstance()

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = controller.getmAuth()

        btn_login.setOnClickListener({
            spinner.visibility = View.VISIBLE
            btn_login.visibility = View.GONE

            if (password.text.toString().length > 5 && repeat_password.text.toString() == password.text.toString()) {
                try {
                    mAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                            .addOnCompleteListener(this@RegistrationActivity) { task ->
                                spinner.visibility = View.GONE
                                btn_login.visibility = View.VISIBLE
                                if (task.isSuccessful) {
                                    controller.user = mAuth.currentUser
                                    controller.user.sendEmailVerification()
                                    Toast.makeText(applicationContext, "Verification email was sent to " + mAuth.currentUser!!.email, Toast.LENGTH_LONG).show()
                                    onBackPressed()
                                } else {
                                    Snackbar.make(container, "Fail", Snackbar.LENGTH_LONG).show()
                                }
                            }
                } catch (e: Exception) {
                    Snackbar.make(container, "Fail", Snackbar.LENGTH_LONG).show()
                }

            } else {
                Snackbar.make(container, "Fail", Snackbar.LENGTH_LONG).show()
            }


        })

        btn_signup.setOnClickListener({ onBackPressed() })
    }
}
