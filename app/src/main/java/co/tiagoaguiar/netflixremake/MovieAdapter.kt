package co.tiagoaguiar.netflixremake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Movie

class MovieAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Movie) {
            val imageCover: ImageView = itemView.findViewById(R.id.img_cover)
            //imageCover.setImageResource(item.coverUrl)
        }
    }
}