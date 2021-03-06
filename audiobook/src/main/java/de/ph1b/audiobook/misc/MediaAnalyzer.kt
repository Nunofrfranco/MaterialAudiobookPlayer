package de.ph1b.audiobook.misc

import e
import io.reactivex.Single
import java.io.File
import javax.inject.Inject

/**
 * Analyzes media files for meta data and duration
 *
 * @author Paul Woitaschek
 */
class MediaAnalyzer @Inject constructor(
    private val exoPlayerDurationParser: DurationAnalyzer,
    private val metaDataAnalyzer: MetaDataAnalyzer) {

  fun analyze(file: File): Single<Result> = exoPlayerDurationParser.duration(file)
      .map { duration ->
        assembleMetaData(duration, file)
      }

  private fun assembleMetaData(duration: Int, file: File) = if (duration > 0) {
    metaDataAnalyzer.prepare(file)
    val chapterName = metaDataAnalyzer.parseChapterName()
    val author = metaDataAnalyzer.parseAuthor()
    val bookName = metaDataAnalyzer.parseBookName()
    Result.Success(duration, chapterName, author, bookName)
  } else {
    e { "Could not parse duration for $file" }
    Result.Failure
  }


  sealed class Result {
    data class Success(val duration: Int, val chapterName: String, val author: String?, val bookName: String?) : Result() {
      init {
        require(duration > 0)
      }
    }

    object Failure : Result()
  }
}

