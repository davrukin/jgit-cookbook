package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.dstadler.jgit.helper.CookbookHelper.createNewRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.io.File
import java.io.IOException

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */ /**
 * Simple snippet which shows how to list all Tags
 *
 * @author dominik.stadler at gmx.at
 */
object CleanUntrackedFiles {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val localPath = createNewRepository().use { repository ->
			val path = repository.workTree
			println("Repository at " + repository.workTree)
			val untrackedFile = File.createTempFile("untracked", ".txt", repository.workTree)
			val untrackedDir = File.createTempFile("untrackedDir", "", repository.workTree)
			if (!untrackedDir.delete()) {
				throw IOException("Could not delete file $untrackedDir")
			}
			if (!untrackedDir.mkdirs()) {
				throw IOException("Could not create directory $untrackedDir")
			}
			println("Untracked exists: " + untrackedFile.exists() + " Dir: " + untrackedDir.exists() + "/" + untrackedDir.isDirectory)
			Git(repository).use { git ->
				val removed = git.clean().setCleanDirectories(true).call()
				for (item in removed) {
					println("Removed: $item")
				}
				println("Removed " + removed.size + " items")
			}
			println("Untracked after: " + untrackedFile.exists() + " Dir: " + untrackedDir.exists() + "/" + untrackedDir.isDirectory)

			path
		}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}
}