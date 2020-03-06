package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.dstadler.jgit.helper.CookbookHelper.createNewRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.io.File
import java.io.IOException
import java.io.PrintWriter

/**
 * Simple snippet which shows how to commit all files
 *
 * @author dominik.stadler@gmx.at
 */
object CommitAll {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val localPath = createNewRepository().use { repository ->
			val path = repository.workTree
			Git(repository).use { git ->
				// create the file
				val myFile = File(repository.directory.parent, "testfile")
				if (!myFile.createNewFile()) {
					throw IOException("Could not create file $myFile")
				}

				// Stage all files in the repo including new files
				git.add().addFilepattern(".").call()

				// and then commit the changes.
				git.commit()
						.setMessage("Commit all changes including additions")
						.call()
				PrintWriter(myFile).use { writer -> writer.append("Hello, world!") }

				// Stage all changed files, omitting new files, and commit with one command
				git.commit()
						.setAll(true)
						.setMessage("Commit changes to all files")
						.call()
				println("Committed all changes to repository at " + repository.directory)
			}

			path
		}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}
}