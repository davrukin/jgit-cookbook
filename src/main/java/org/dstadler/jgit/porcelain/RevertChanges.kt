package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.dstadler.jgit.helper.CookbookHelper.createNewRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/**
 * Simple snippet which shows how to set a modified tracked file back to its state
 * in the most recent commit. This does not make a new commit that reverts a previous commit;
 * this reverts a modified file back to its unmodified state (according to most recent commit)
 *
 * @author JordanMartinez
 */
object RevertChanges {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val localPath = createNewRepository().use { repository ->
			val path = repository.workTree
			println("Listing local branches:")
			Git(repository).use { git ->
				// set up a file
				val fileName = "temptFile.txt"
				val tempFile = File(repository.directory.parentFile, fileName)
				if (!tempFile.createNewFile()) {
					throw IOException("Could not create temporary file $tempFile")
				}
				val tempFilePath = tempFile.toPath()

				// write some initial text to it
				val initialText = "Initial Text"
				println("Writing text [$initialText] to file [$tempFile]")
				Files.write(tempFilePath, initialText.toByteArray())

				// add the file and commit it
				git.add().addFilepattern(fileName).call()
				git.commit().setMessage("Added untracked file " + fileName + "to repo").call()

				// modify the file
				Files.write(tempFilePath, "Some modifications".toByteArray(), StandardOpenOption.APPEND)

				// assert that file's text does not equal initialText
				check(initialText != getTextFromFilePath(tempFilePath)) {
					"Modified file's text should not equal " +
							"its original state after modification"
				}
				println("File now has text [" + getTextFromFilePath(tempFilePath) + "]")

				// revert the changes
				git.checkout().addPath(fileName).call()

				// text should no longer have modifications
				check(initialText == getTextFromFilePath(tempFilePath)) { "Reverted file's text should equal its initial text" }
				println("File modifications were reverted. " +
						"File now has text [" + getTextFromFilePath(tempFilePath) + "]")
			}
			path
		}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}

	@Throws(IOException::class)
	private fun getTextFromFilePath(file: Path): String {
		val bytes = Files.readAllBytes(file)
		val chars = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes))
		return chars.toString()
	}
}