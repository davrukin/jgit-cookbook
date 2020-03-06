package org.dstadler.jgit.unfinished

import org.apache.commons.io.FileUtils
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
 * Note: This snippet is not done and likely does not show anything useful yet
 *
 * @author dominik.stadler at gmx.at
 */
object PushToRemoteRepository {
	private const val REMOTE_URL = "https://github.com/github/testrepo.git"

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		// prepare a new folder for the cloned repository
		val localPath = File.createTempFile("TestGitRepository", "")
		if (!localPath.delete()) {
			throw IOException("Could not delete temporary file $localPath")
		}

		// then clone
		println("Cloning from $REMOTE_URL to $localPath")
		Git.cloneRepository()
				.setURI(REMOTE_URL)
				.setDirectory(localPath)
				.call().use { result ->
					// prepare a second folder for the 2nd clone
					val localPath2 = File.createTempFile("TestGitRepository", "")
					if (!localPath2.delete()) {
						throw IOException("Could not delete temporary file $localPath2")
					}

					// then clone again
					println("Cloning from file://$localPath to $localPath2")
					Git.cloneRepository()
							.setURI("file://$localPath")
							.setDirectory(localPath2)
							.call().use { result2 ->
								println("Result: $result2")

								// now open the created repository
								val builder = FileRepositoryBuilder()
								builder.setGitDir(localPath2)
										.readEnvironment() // scan environment GIT_* variables
										.findGitDir() // scan up the file system tree
										.build().use { repository ->
											Git(repository).use { git ->
												git.push()
														.call()
											}
											println("Pushed from repository: " + repository.directory + " to remote repository at " + REMOTE_URL)
										}
							}
				}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}
}