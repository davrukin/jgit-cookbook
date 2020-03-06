package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.diff.RawTextComparator
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

// Simple example that shows how to get the Blame-information for a file
object BlameFile {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repo ->
			val list = File(".").list()
					?: throw IllegalStateException("Did not find any files at " + File(".").absolutePath)
			val DATE_FORMAT = SimpleDateFormat("YYYY-MM-dd HH:mm")
			for (file in list) {
				if (File(file).isDirectory) {
					continue
				}
				println("Blaming $file")
				val result = Git(repo).blame().setFilePath(file)
						.setTextComparator(RawTextComparator.WS_IGNORE_ALL).call()
				val rawText = result.resultContents
				for (i in 0 until rawText.size()) {
					val sourceAuthor = result.getSourceAuthor(i)
					val sourceCommit = result.getSourceCommit(i)
					println(sourceAuthor.name +
							(if (sourceCommit != null) " - " + DATE_FORMAT.format(sourceCommit.commitTime.toLong() * 1000) +
									" - " + sourceCommit.name else "") +
							": " + rawText.getString(i))
				}
			}
		}
	}
}