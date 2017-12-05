package movies.test.softserve.movies.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import movies.test.softserve.movies.R

import movies.test.softserve.movies.entity.Achievement
import movies.test.softserve.movies.util.AchievementService

class AchievementsRecyclerViewAdapter(private val mValues: List<Achievement>) : RecyclerView.Adapter<AchievementsRecyclerViewAdapter.ViewHolder>() {

    private var service: AchievementService = AchievementService.getInstance()


    companion object {
        val VISIBLE = 1f
        val INVISIBLE = 0.05f
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_achievements, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].title
        holder.mContentView.text = mValues[position].description
        holder.mImageView.setImageResource(mValues[position].resourceId)

        if (service.getAchievementStatus(mValues[position])) {
            holder.mImageView.alpha = VISIBLE
        } else {
            holder.mImageView.alpha = INVISIBLE
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mImageView: ImageView = mView.findViewById(R.id.imageV)
        val mIdView: TextView = mView.findViewById<View>(R.id.id) as TextView
        val mContentView: TextView = mView.findViewById<View>(R.id.content) as TextView
        var mItem: Achievement? = null

        override fun toString(): String = super.toString() + " '" + mContentView.text + "'"
    }
}
