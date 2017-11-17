package movies.test.softserve.movies.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import movies.test.softserve.movies.R
import movies.test.softserve.movies.entity.Genre


class ViewAdapter(private val mValues: List<Genre>,private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<ViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_genre, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].name

        holder.mView.setOnClickListener {
            onItemClickListener.onItemClick(mValues[position])
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.findViewById(R.id.id)
        var mItem: Genre? = null

    }

    interface OnItemClickListener{
        fun onItemClick(genre: Genre)
    }
}
