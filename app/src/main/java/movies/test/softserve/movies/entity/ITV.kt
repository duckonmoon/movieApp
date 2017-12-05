package movies.test.softserve.movies.entity

/**
 * Created by rkrit on 10.11.17.
 */

interface ITV {
    val id: Int?
    val posterPath: String?
    val voteAverage: Double?
    val voteCount: Int?
    val title: String?
    val overview: String?
    val genreIds: List<Int>?
}
