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
 * Simple snippet which shows how to commit a file
 *
 * @author dominik.stadler at gmx.at
 */
object CommitFile {

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

				// run the add
				git.add()
						.addFilepattern("testfile")
						.call()

				// and then commit the changes
				git.commit()
						.setMessage("Added testfile")
						.call()
				println("Committed file " + myFile + " to repository at " + repository.directory)
			}

			path
		}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}
}