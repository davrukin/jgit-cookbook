package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.dstadler.jgit.helper.CookbookHelper.createNewRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.io.File
import java.io.IOException

/**
 * Simple snippet which shows how to revert a previous commit
 *
 * @author JordanMartinez
 */
object RevertCommit {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val localPath = createNewRepository().use { repository ->
			Git(repository).use { git ->
				val path = repository.workTree
				println("Repository at $path")

				// Create a new file and add it to the index
				val newFile = File(path, "file1.txt")
				FileUtils.writeStringToFile(newFile, "Line 1\r\n", "UTF-8", true)
				git.add().addFilepattern("file1.txt").call()
				val rev1 = git.commit().setAuthor("test", "test@test.com").setMessage("Commit Log 1").call()
				println("Rev1: $rev1")

				// commit some changes
				FileUtils.writeStringToFile(newFile, "Line 2\r\n", "UTF-8", true)
				git.add().addFilepattern("file1.txt").call()
				val rev2 = git.commit().setAll(true).setAuthor("test", "test@test.com").setMessage("Commit Log 2").call()
				println("Rev2: $rev2")

				// commit some changes
				FileUtils.writeStringToFile(newFile, "Line 3\r\n", "UTF-8", true)
				git.add().addFilepattern("file1.txt").call()
				val rev3 = git.commit().setAll(true).setAuthor("test", "test@test.com").setMessage("Commit Log 3").call()
				println("Rev3: $rev3")

				// print logs
				var gitLog = git.log().call()
				for (logMessage in gitLog) {
					println("Before revert: " + logMessage.name + " - " + logMessage.fullMessage)
				}
				val revertCommand = git.revert()
				// revert to revision 2
				revertCommand.include(rev3)
				val revCommit = revertCommand.call()
				println("Reverted: $revCommit")
				println("Reverted refs: " + revertCommand.revertedRefs)
				println("Unmerged paths: " + revertCommand.unmergedPaths)
				println("Failing results: " + revertCommand.failingResult)

				// print logs
				gitLog = git.log().call()
				for (logMessage in gitLog) {
					println("After revert: " + logMessage.name + " - " + logMessage.fullMessage)
				}
				println("File contents: " + FileUtils.readFileToString(newFile, "UTF-8"))

				path
			}
		}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}
}