package org.dstadler.jgit.unfinished

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
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
 * Simple snippet which shows how to pull from a remote repository from a remote source
 *
 * @author dominik.stadler at gmx.at
 */
object PullRemoteRepository {
	private const val REMOTE_URL = "https://github.com/github/testrepo.git"

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val localPath = cloneRepository().use { repository ->
			val path = repository.workTree
			println("Having repository: " + repository.directory + " with head: " +
					repository.findRef(Constants.HEAD) + "/" + repository.resolve("HEAD") + "/" +
					repository.resolve("refs/heads/master"))
			Git(repository).use { git ->
				val call = git.pull().call()
				println("Pulled from the remote repository: $call")
			}
			path
		}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}

	@Throws(IOException::class, GitAPIException::class)
	private fun cloneRepository(): Repository {
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
					// Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
					return result.repository
				}
	}
}