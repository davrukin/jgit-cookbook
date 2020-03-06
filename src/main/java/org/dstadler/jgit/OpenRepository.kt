package org.dstadler.jgit

import org.apache.commons.io.FileUtils
import org.dstadler.jgit.helper.CookbookHelper
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
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
 * Simple snippet which shows how to open an existing repository
 *
 * @author dominik.stadler at gmx.at
 */
object OpenRepository {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	@JvmOverloads
	fun main(args: Array<String>? = null) {
		// first create a test-repository, the return is including the .get directory here!
		val repoDir = createSampleGitRepo()

		// now open the resulting repository with a FileRepositoryBuilder
		val builder = FileRepositoryBuilder()
		builder.setGitDir(repoDir)
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build().use { repository ->
					println("Having repository: " + repository.directory)

					// the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
					val head = repository.exactRef("refs/heads/master")
					println("Ref of refs/heads/master: $head")
				}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(repoDir.parentFile)
	}

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun createSampleGitRepo(): File {
		CookbookHelper.createNewRepository().use { repository ->
			println("Temporary repository at " + repository.directory)

			// create the file
			val myFile = File(repository.directory.parent, "testfile")
			if (!myFile.createNewFile()) {
				throw IOException("Could not create file $myFile")
			}
			Git(repository).use { git ->
				git.add()
						.addFilepattern("testfile")
						.call()


				// and then commit the changes
				git.commit()
						.setMessage("Added testfile")
						.call()
			}
			println("Added file " + myFile + " to repository at " + repository.directory)
			return repository.directory
		}
	}
}